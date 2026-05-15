package com.example.raceweek.data.remote

import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

data class RemoteCategory(
    val name: String,
    val description: String
)

data class RemoteHeroRace(
    val flagResName: String,
    val name: String,
    val country: String,
    val location: String,
    val raceTimestampMillis: Long
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
                .addOnFailureListener { exception ->
                    continuation.resumeWithException(exception)
                }
        }
    }

    // Navega pelo collectionGroup "extra" para encontrar o documento "schedule" com o
    // campo "race" (Timestamp) mais próximo da data atual entre todos os campeonatos/corridas.
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

                    // Caminho: categories/{cat}/{race}/info/extra/schedule
                    // parent       -> extra (coleção)
                    // parent.parent -> info (documento)
                    val infoDocRef = scheduleDoc.reference.parent.parent
                    if (infoDocRef == null) {
                        continuation.resume(null)
                        return@addOnSuccessListener
                    }

                    infoDocRef.get()
                        .addOnSuccessListener { infoDoc ->
                            continuation.resume(
                                RemoteHeroRace(
                                    flagResName = infoDoc.getString("flag") ?: "",
                                    name = infoDoc.getString("name") ?: "",
                                    country = infoDoc.getString("country") ?: "",
                                    location = infoDoc.getString("location") ?: "",
                                    raceTimestampMillis = raceTimestamp.toDate().time
                                )
                            )
                        }
                        .addOnFailureListener { continuation.resumeWithException(it) }
                }
                .addOnFailureListener { continuation.resumeWithException(it) }
        }
    }
}
