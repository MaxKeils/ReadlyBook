package max.keils.data.source.remote

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

internal class FirebaseRemoteDataSource @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) {

    suspend fun signIn(email: String, password: String): FirebaseUser {
        val authResult = firebaseAuth.signInWithEmailAndPassword(email, password)
            .await()

        return authResult.user
            ?: throw IllegalStateException("Firebase signIn succeeded, but user object is null")
    }

    suspend fun signUp(email: String, password: String): FirebaseUser {
        val authResult = firebaseAuth.createUserWithEmailAndPassword(email, password)
            .await()

        return authResult.user
            ?: throw IllegalStateException("Firebase signUp succeeded, but user object is null")
    }

    fun signOut() {
        firebaseAuth.signOut()
    }

    fun getCurrentUserId(): String? = firebaseAuth.currentUser?.uid

}