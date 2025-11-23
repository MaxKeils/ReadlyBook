package max.keils.readlybook.ui.components

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.compose.currentBackStackEntryAsState
import max.keils.readlybook.ui.navigation.NavigationItem
import max.keils.readlybook.ui.navigation.NavigationState

@Composable
internal fun NavigationBar(
    items: List<NavigationItem>,
    navigationState: NavigationState
) {
    NavigationBar {
        val navBackStackEntry by navigationState.navHostController.currentBackStackEntryAsState()
        items.forEach { item ->
            NavigationBarItem(
                selected = navBackStackEntry?.destination?.route == item.screen.route,
                onClick = {
                    navigationState.navigateTo(item.screen)
                },
                icon = {
                    Icon(
                        painter = painterResource(item.imageResId),
                        contentDescription = null
                    )
                },
                label = {
                    Text(text = stringResource(item.labelResId))
                }
            )
        }
    }
}