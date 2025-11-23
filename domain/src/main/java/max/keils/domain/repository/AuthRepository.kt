package max.keils.domain.repository

import kotlinx.coroutines.flow.StateFlow
import max.keils.domain.entity.UserData

interface AuthRepository {

    val currentUserId: StateFlow<String?>

    suspend fun signIn(email: String, password: String): Result<UserData>

    suspend fun signUp(email: String, password: String): Result<UserData>

    fun signOut(): Result<Unit>

    suspend fun getCurrentUser(): Result<UserData?>

    suspend fun updateUserProfile(displayName: String?, photoUrl: String?): Result<UserData>

    suspend fun uploadUserPhoto(photoBytes: ByteArray, userId: String): Result<String>
}