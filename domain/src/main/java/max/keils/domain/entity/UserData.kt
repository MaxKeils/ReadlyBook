package max.keils.domain.entity


data class UserData(
    val id: String,
    val email: String?,
    val photoUrl: String?,
    val name: String?,
    val isEmailVerified: Boolean
)
