package max.keils.data.repository

import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.StateFlow
import max.keils.data.mapper.UserMapper
import max.keils.data.source.remote.FirebaseAuthRemoteDataSource
import max.keils.domain.entity.UserData
import max.keils.domain.repository.AuthRepository
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuthRemoteDataSource: FirebaseAuthRemoteDataSource,
    private val userMapper: UserMapper
) : AuthRepository {

    override val currentUserId: StateFlow<String?>
        get() = firebaseAuthRemoteDataSource.currentUserId

    override suspend fun signIn(email: String, password: String): Result<UserData> = performAuth {
        firebaseAuthRemoteDataSource.signIn(email = email, password = password)
    }

    override suspend fun signUp(email: String, password: String): Result<UserData> = performAuth {
        firebaseAuthRemoteDataSource.signUp(email = email, password = password)
    }

    override fun signOut(): Result<Unit> = runCatching {
        firebaseAuthRemoteDataSource.signOut()
    }.fold(
        onSuccess = { Result.success(it) },
        onFailure = { Result.failure(it) }
    )

    override suspend fun getCurrentUser(): Result<UserData?> = runCatching {
        val firebaseUser = firebaseAuthRemoteDataSource.getCurrentUser()
        firebaseUser?.let { userMapper.mapFirebaseUserToUserEntity(it) }
    }.fold(
        onSuccess = { Result.success(it) },
        onFailure = {
            Result.failure(it)
        }
    )

    override suspend fun updateUserProfile(
        displayName: String?,
        photoUrl: String?
    ): Result<UserData> = performAuth {
        firebaseAuthRemoteDataSource.updateUserProfile(displayName, photoUrl)
    }

    override suspend fun uploadUserPhoto(photoBytes: ByteArray, userId: String): Result<String> =
        runCatching {
            firebaseAuthRemoteDataSource.uploadUserPhoto(photoBytes, userId)
        }.fold(
            onSuccess = { Result.success(it) },
            onFailure = {
                Result.failure(it)
            }
        )


    private suspend fun performAuth(
        block: suspend () -> FirebaseUser
    ): Result<UserData> = runCatching {
        val firebaseUser = block()
        userMapper.mapFirebaseUserToUserEntity(firebaseUser)
    }.fold(
        onSuccess = { Result.success(it) },
        onFailure = {
            Result.failure(it)
        }
    )

}