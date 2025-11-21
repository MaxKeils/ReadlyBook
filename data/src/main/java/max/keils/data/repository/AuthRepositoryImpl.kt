package max.keils.data.repository

import com.google.firebase.auth.FirebaseUser
import max.keils.data.mapper.FirebaseMapper
import max.keils.data.source.remote.FirebaseRemoteDataSource
import max.keils.domain.entity.UserData
import max.keils.domain.repository.AuthRepository
import javax.inject.Inject

class AuthRepositoryImpl @Inject private constructor(
    private val firebaseRemoteDataSource: FirebaseRemoteDataSource,
    private val firebaseMapper: FirebaseMapper
) : AuthRepository {
    override suspend fun signIn(email: String, password: String): Result<UserData> = performAuth {
        firebaseRemoteDataSource.signIn(email = email, password = password)
    }

    override suspend fun signUp(email: String, password: String): Result<UserData> = performAuth {
        firebaseRemoteDataSource.signUp(email = email, password = password)
    }

    override fun signOut(): Result<Unit> = runCatching {
        firebaseRemoteDataSource.signOut()
    }.fold(
        onSuccess = Result.Companion::success,
        onFailure = Result.Companion::failure
    )

    override fun getCurrentUserId(): String? = firebaseRemoteDataSource.getCurrentUserId()

    private suspend fun performAuth(
        block: suspend () -> FirebaseUser
    ): Result<UserData> = runCatching {
        val firebaseUser = block()
        firebaseMapper.mapFirebaseUserToUserEntity(firebaseUser)
    }.fold(Result.Companion::success, Result.Companion::failure)

}