package max.keils.data.source.remote

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseAuthRemoteDataSource @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) {

    private val _currentUserId = MutableStateFlow(getCurrentUserId())
    val currentUserId
        get() = _currentUserId.asStateFlow()


    init {
        firebaseAuth.addAuthStateListener { auth ->
            _currentUserId.value = auth.currentUser?.uid
        }
    }

    suspend fun signIn(email: String, password: String): FirebaseUser {
        if (email.isEmpty() || password.isEmpty()) {
            throw IllegalArgumentException("Email and password must not be empty")
        }

        val authResult = firebaseAuth.signInWithEmailAndPassword(email, password)
            .await()

        return authResult.user
            ?: throw IllegalStateException("Firebase signIn succeeded, but user object is null")
    }

    suspend fun signUp(email: String, password: String): FirebaseUser {
        if (email.isEmpty() || password.isEmpty()) {
            throw IllegalArgumentException("Email and password must not be empty")
        }

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