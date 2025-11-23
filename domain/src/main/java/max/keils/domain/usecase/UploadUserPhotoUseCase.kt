package max.keils.domain.usecase

import max.keils.domain.repository.AuthRepository

class UploadUserPhotoUseCase(private val repository: AuthRepository) {

    suspend operator fun invoke(photoBytes: ByteArray, userId: String): Result<String> =
        repository.uploadUserPhoto(photoBytes, userId)

}

