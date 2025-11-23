package max.keils.readlybook.ui.navigation

import max.keils.readlybook.R

internal sealed class NavigationItem(
    val screen: Screen,
    val labelResId: Int,
    val imageResId: Int,
) {

    data object BookList : NavigationItem(
        screen = Screen.BookList,
        labelResId = R.string.my_books,
        imageResId = R.drawable.ic_book_list
    )

    data object BookLoading : NavigationItem(
        screen = Screen.BookLoading,
        labelResId = R.string.navigation_book_loading,
        imageResId = R.drawable.ic_book_loading
    )

    data object Profile : NavigationItem(
        screen = Screen.Profile,
        labelResId = R.string.navigation_profile,
        imageResId = R.drawable.ic_person
    )

}