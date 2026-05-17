package com.example.raceweek.data.remote

import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

data class RemoteCategory(
    val name: String,
    val description: String
)

data class RemoteRaceSession(
    val key: String,
    val timestampMillis: Long
)

data class RemoteHeroRace(
    val id: String,
    val flagResName: String,
    val name: String,
    val country: String,
    val location: String,
    val raceTimestampMillis: Long,
    val timezone: String = "UTC"
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

    // Navega pelo collectionGroup "extra" para encontrar o documento "schedule" com o
    // campo "race" (Timestamp) mais próximo da data atual.
    suspend fun fetchNextRace(): Result<RemoteHeroRace?> = runCatching {
        val now = Timestamp.now()
        suspendCancellableCoroutine { continuation ->
            db.collectionGroup("extra")
                .whereGreaterThanOrEqualTo("race", now)
                .orderBy("race")
                .limit(1)
                .get()
                .addOnSuccessListener { scheduleSnapshot ->
                    val scheduleDoc = scheduleSnapshot.documents.firstOrNull()
                    if (scheduleDoc == null) {
                        continuation.resume(null)
                        return@addOnSuccessListener
                    }

                    val raceTimestamp = scheduleDoc.getTimestamp("race")
                    if (raceTimestamp == null) {
                        continuation.resume(null)
                        return@addOnSuccessListener
                    }

                    // Caminho: categories/{cat}/{raceId}/info/extra/schedule
                    val infoDocRef = scheduleDoc.reference.parent.parent
                    if (infoDocRef == null) {
                        continuation.resume(null)
                        return@addOnSuccessListener
                    }

                    // Constrói o id igual ao usado em fetchUpcomingRaces()
                    val raceCollectionId = infoDocRef.parent.id
                    val categoryId = infoDocRef.parent.parent?.id ?: ""
                    val raceId = "${categoryId}_${raceCollectionId}"

                    infoDocRef.get()
                        .addOnSuccessListener { infoDoc ->
                            continuation.resume(
                                RemoteHeroRace(
                                    id = raceId,
                                    flagResName = infoDoc.getString("flag") ?: "",
                                    name = infoDoc.getString("name") ?: "",
                                    country = infoDoc.getString("country") ?: "",
                                    location = infoDoc.getString("location") ?: "",
                                    raceTimestampMillis = raceTimestamp.toDate().time,
                                    timezone = infoDoc.getString("timezone") ?: "UTC"
                                )
                            )
                        }
                        .addOnFailureListener { continuation.resumeWithException(it) }
                }
                .addOnFailureListener { continuation.resumeWithException(it) }
        }
    }

    // Busca todas as corridas futuras ordenadas por data crescente, incluindo todas
    // as sessões do documento schedule (practiceone, qualifying, race, etc.).
    // Usa cache de categorias para evitar leituras repetidas do mesmo campeonato.
    suspend fun fetchUpcomingRaces(): Result<List<RemoteUpcomingRace>> {
        return try {
            val now = Timestamp.now()

            val scheduleSnapshot = db.collectionGroup("extra")
                .whereGreaterThanOrEqualTo("race", now)
                .orderBy("race")
                .get()
                .await()

            val categoryCache = mutableMapOf<String, DocumentSnapshot>()
            val races = mutableListOf<RemoteUpcomingRace>()

            for (scheduleDoc in scheduleSnapshot.documents) {
                val raceTimestamp = scheduleDoc.getTimestamp("race") ?: continue

                // categories/{cat}/{raceId}/info/extra/schedule
                val infoDocRef = scheduleDoc.reference.parent.parent ?: continue
                val raceCollectionRef = infoDocRef.parent
                val categoryDocRef = raceCollectionRef.parent ?: continue

                val infoDoc = infoDocRef.get().await()
                if (!infoDoc.exists()) continue

                val categoryDoc = categoryCache[categoryDocRef.path]
                    ?: categoryDocRef.get().await().also { categoryCache[categoryDocRef.path] = it }
                if (!categoryDoc.exists()) continue

                // Parseia todos os campos do schedule como sessões (todos são Timestamps)
                val sessions = scheduleDoc.data?.entries?.mapNotNull { (key, value) ->
                    val ts = (value as? Timestamp)?.toDate()?.time ?: return@mapNotNull null
                    RemoteRaceSession(key = key, timestampMillis = ts)
                }?.sortedBy { it.timestampMillis } ?: emptyList()

                races.add(
                    RemoteUpcomingRace(
                        id = "${categoryDoc.id}_${raceCollectionRef.id}",
                        flagResName = infoDoc.getString("flag") ?: "",
                        categoryDescription = categoryDoc.getString("description") ?: categoryDoc.id,
                        name = infoDoc.getString("name") ?: "",
                        country = infoDoc.getString("country") ?: "",
                        location = infoDoc.getString("location") ?: "",
                        raceTimestampMillis = raceTimestamp.toDate().time,
                        timezone = infoDoc.getString("timezone") ?: "UTC",
                        laps = infoDoc.getLong("laps")?.toInt(),
                        sessions = sessions
                    )
                )
            }

            Result.success(races)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
