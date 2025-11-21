package max.keils.domain.repository

import max.keils.domain.entity.UserData

interface AuthRepository {

    suspend fun signIn(email: String, password: String): Result<UserData>

    suspend fun signUp(email: String, password: String): Result<UserData>

    fun signOut(): Result<Unit>

    fun getCurrentUserId(): String?

}