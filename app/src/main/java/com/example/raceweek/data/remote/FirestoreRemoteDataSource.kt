package com.example.raceweek.data.remote

import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

data class RemoteCategory(
    val name: String,
    val description: String
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
}
