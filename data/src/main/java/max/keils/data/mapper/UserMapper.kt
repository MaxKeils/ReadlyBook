package max.keils.data.mapper

import com.google.firebase.auth.FirebaseUser
import max.keils.domain.entity.UserData
import javax.inject.Inject

class UserMapper @Inject constructor() {

    fun mapFirebaseUserToUserEntity(firebaseUser: FirebaseUser) = UserData(
        id = firebaseUser.uid,
        email = firebaseUser.email,
        photoUrl = firebaseUser.photoUrl?.toString(),
        name = firebaseUser.displayName,
        isEmailVerified = firebaseUser.isEmailVerified
    )

}