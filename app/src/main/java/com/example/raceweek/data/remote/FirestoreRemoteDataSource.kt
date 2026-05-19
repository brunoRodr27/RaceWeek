package com.example.raceweek.data.remote

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

private const val TAG = "FirestoreDS"

data class RemoteCategory(
    val name: String,
    val description: String
)

data class RemoteRaceSession(
    val key: String,
    val timestampMillis: Long
)

data class RemoteUpcomingRace(
    val id: String,
    val flagResName: String,
    val categoryDescription: String,
    val name: String,
    val country: String,
    val location: String,
    val raceTimestampMillis: Long,
    val timezone: String = "UTC",
    val laps: Int? = null,
    val lat: Double? = null,
    val lon: Double? = null,
    val sessions: List<RemoteRaceSession> = emptyList()
)

class FirestoreRemoteDataSource @Inject constructor() {

    private val db = Firebase.firestore

    suspend fun fetchCategories(): Result<List<RemoteCategory>> = runCatching {
        suspendCancellableCoroutine { continuation ->
            db.collection("categories")
                .get()
                .addOnSuccessListener { snapshot ->
                    val categories = snapshot.documents.map { doc ->
                        RemoteCategory(
                            name = doc.id,
                            description = doc.getString("description") ?: ""
                        )
                    }
                    continuation.resume(categories)
                }
                .addOnFailureListener { continuation.resumeWithException(it) }
        }
    }

    // Todas as corridas ordenadas por data. A filtragem de "futuras" é feita no app após
    // reanchorar o timestamp pelo fuso correto da corrida — não pode ser feita no Firestore
    // porque o timestamp armazenado representa o horário local como se fosse UTC-3.
    suspend fun fetchAllRaces(): Result<List<RemoteUpcomingRace>> =
        buildRaceList(
            db.collectionGroup("extra").orderBy("race")
        )

    private suspend fun buildRaceList(query: Query): Result<List<RemoteUpcomingRace>> {
        return try {
            val scheduleSnapshot = query.get().await()

            // Estrutura intermediária para guardar os dados extraídos de cada schedule doc.
            data class RaceCandidate(
                val raceTimestamp: Timestamp,
                val infoDoc: DocumentSnapshot,
                val raceCollectionId: String,
                val categoryDocPath: String,
                val categoryDocRef: com.google.firebase.firestore.DocumentReference,
                val sessions: List<RemoteRaceSession>
            )

            // Lê todos os info docs em paralelo — elimina N leituras sequenciais.
            val candidates: List<RaceCandidate> = coroutineScope {
                scheduleSnapshot.documents.map { scheduleDoc ->
                    async {
                        val raceTimestamp = scheduleDoc.getTimestamp("race") ?: return@async null
                        val infoDocRef = scheduleDoc.reference.parent.parent ?: return@async null
                        val raceCollectionRef = infoDocRef.parent
                        val categoryDocRef = raceCollectionRef.parent ?: return@async null

                        val infoDoc = infoDocRef.get().await()
                        if (!infoDoc.exists()) return@async null

                        val sessions = scheduleDoc.data?.entries?.mapNotNull { (key, value) ->
                            val ts = (value as? Timestamp)?.toDate()?.time ?: return@mapNotNull null
                            RemoteRaceSession(key = key, timestampMillis = ts)
                        }?.sortedBy { it.timestampMillis } ?: emptyList()

                        RaceCandidate(
                            raceTimestamp = raceTimestamp,
                            infoDoc = infoDoc,
                            raceCollectionId = raceCollectionRef.id,
                            categoryDocPath = categoryDocRef.path,
                            categoryDocRef = categoryDocRef,
                            sessions = sessions
                        )
                    }
                }.awaitAll().filterNotNull()
            }

            // Lê categorias únicas (cache evita reads duplicados por categoria).
            val categoryCache = mutableMapOf<String, DocumentSnapshot>()
            val races = candidates.mapNotNull { candidate ->
                val categoryDoc = categoryCache[candidate.categoryDocPath]
                    ?: candidate.categoryDocRef.get().await()
                        .also { categoryCache[candidate.categoryDocPath] = it }
                if (!categoryDoc.exists()) return@mapNotNull null

                RemoteUpcomingRace(
                    id = "${categoryDoc.id}_${candidate.raceCollectionId}",
                    flagResName = candidate.infoDoc.getString("flag") ?: "",
                    categoryDescription = categoryDoc.getString("description") ?: categoryDoc.id,
                    name = candidate.infoDoc.getString("name") ?: "",
                    country = candidate.infoDoc.getString("country") ?: "",
                    location = candidate.infoDoc.getString("location") ?: "",
                    raceTimestampMillis = candidate.raceTimestamp.toDate().time,
                    timezone = candidate.infoDoc.getString("timezone") ?: "UTC",
                    laps = candidate.infoDoc.getLong("laps")?.toInt(),
                    lat = candidate.infoDoc.safeDouble("lat"),
                    lon = candidate.infoDoc.safeDouble("lon"),
                    sessions = candidate.sessions
                )
            }

            Result.success(races)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "buildRaceList erro inesperado: ${e.message}", e)
            Result.failure(e)
        }
    }

    // getDouble() do Firestore lança RuntimeException se o campo for String.
    // Esta extensão trata os dois tipos que podem vir do Firestore (Number ou String).
    private fun DocumentSnapshot.safeDouble(field: String): Double? =
        when (val v = get(field)) {
            is Number -> v.toDouble()
            is String -> v.toDoubleOrNull()
            else -> null
        }
}
