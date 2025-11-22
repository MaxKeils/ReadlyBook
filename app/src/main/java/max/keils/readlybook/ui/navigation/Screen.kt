package max.keils.readlybook.ui.navigation

import kotlinx.serialization.Serializable

internal sealed class Screen {

    val route: String
        get() = this::class.qualifiedName ?: error("Unknown route")

    @Serializable
    object Auth : Screen()

    @Serializable
    object Main : Screen()

    @Serializable
    object BookList : Screen()

    @Serializable
    object BookLoading : Screen()

    @Serializable
    object Profile : Screen()

    @Serializable
    object ReaderScreen : Screen()

}