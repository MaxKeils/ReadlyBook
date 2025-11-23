package max.keils.readlybook.ui.screen.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import max.keils.readlybook.R
import max.keils.readlybook.ui.components.ReadlyTextField
import max.keils.readlybook.ui.theme.ReadlyBookTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun AuthScreen(
    state: AuthState,
    selectedTab: AuthTab,
    onTabChange: (AuthTab) -> Unit,
    onLoginClick: (email: String, password: String) -> Unit,
    onRegisterClick: (email: String, password: String, repeatPassword: String) -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var repeatPassword by remember { mutableStateOf("") }

    val snackBarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state) {
        if (state is AuthState.Error) {
            snackBarHostState.showSnackbar(state.message, withDismissAction = true)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    val text =
                        if (selectedTab == AuthTab.Login) stringResource(R.string.sign_in)
                        else stringResource(R.string.sign_up)
                    Text(text = text)
                },
                actions = {
                    TextButton(onClick = {
                        onTabChange(
                            if (selectedTab == AuthTab.Login) AuthTab.Register else AuthTab.Login
                        )
                    }) {
                        val text =
                            if (selectedTab == AuthTab.Login) stringResource(R.string.sign_up)
                            else stringResource(R.string.sign_in)
                        Text(text = text)
                    }
                }
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackBarHostState)
        }
    ) { paddingValues ->
        Column(
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            AnimatedVisibility(
                visible = true
            ) {
                Column {
                    LoginForm(
                        email = email,
                        password = password,
                        onEmailChange = { email = it },
                        onPasswordChange = { password = it },
                        onSubmit = {
                            if (selectedTab == AuthTab.Login)
                                onLoginClick(email, password)
                            else if (repeatPassword.isNotEmpty())
                                onRegisterClick(email, password, repeatPassword)
                        }
                    )

                    AnimatedVisibility(
                        visible = selectedTab == AuthTab.Register
                    ) {
                        RegisterForm(
                            repeatPassword = repeatPassword,
                            onRepeatPasswordChange = { repeatPassword = it },
                            onSubmit = {
                                onRegisterClick(email, password, repeatPassword)
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    AuthSubmitButton(
                        loading = state is AuthState.Loading,
                        selectedTab = selectedTab,
                        onClick = {
                            if (selectedTab == AuthTab.Login)
                                onLoginClick(email, password)
                            else
                                onRegisterClick(email, password, repeatPassword)

                        }
                    )
                }
            }
        }
    }

}

@Composable
private fun AuthSubmitButton(loading: Boolean, selectedTab: AuthTab, onClick: () -> Unit) {
    Button(
        modifier = Modifier.fillMaxWidth(),
        enabled = !loading,
        onClick = onClick
    ) {
        if (loading)
            CircularProgressIndicator()
        else Text(
            if (selectedTab == AuthTab.Login)
                stringResource(R.string.sign_in)
            else stringResource(
                R.string.sign_up
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun AuthSubmitButtonLoadingPreview() {
    ReadlyBookTheme {
        AuthSubmitButton(loading = true, selectedTab = AuthTab.Login) { }
    }
}

@Preview(showBackground = true)
@Composable
private fun AuthSubmitButtonPreview() {
    ReadlyBookTheme {
        AuthSubmitButton(loading = false, selectedTab = AuthTab.Login) { }
    }
}


@Composable
private fun LoginForm(
    email: String,
    password: String,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onSubmit: () -> Unit
) {
    val focusManager = LocalFocusManager.current

    Column(modifier = Modifier.fillMaxWidth()) {
        ReadlyTextField(
            value = email,
            onValueChange = onEmailChange,
            label = stringResource(R.string.email),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Down) }
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        ReadlyTextField(
            value = password,
            onValueChange = onPasswordChange,
            label = stringResource(R.string.password),
            isPassword = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    focusManager.clearFocus()
                    onSubmit()
                }
            ),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(12.dp))
    }
}

@Composable
fun RegisterForm(
    repeatPassword: String,
    onRepeatPasswordChange: (String) -> Unit,
    onSubmit: () -> Unit
) {
    val focusManager = LocalFocusManager.current

    Spacer(Modifier.height(12.dp))

    ReadlyTextField(
        value = repeatPassword,
        onValueChange = onRepeatPasswordChange,
        label = stringResource(R.string.repeat_password),
        isPassword = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password,
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(
            onDone = {
                focusManager.clearFocus()
                onSubmit()
            }
        ),
        modifier = Modifier.fillMaxWidth()
    )
}

@Preview
@Composable
private fun AuthScreenLoginTabPreview() {
    ReadlyBookTheme {
        AuthScreen(
            state = AuthState.Idle,
            selectedTab = AuthTab.Login,
            onLoginClick = { _, _ -> },
            onRegisterClick = { _, _, _ -> },
            onTabChange = {}
        )
    }
}

@Preview
@Composable
private fun AuthScreenLoginRegisterPreview() {
    ReadlyBookTheme {
        AuthScreen(
            state = AuthState.Idle,
            selectedTab = AuthTab.Register,
            onLoginClick = { _, _ -> },
            onRegisterClick = { _, _, _ -> },
            onTabChange = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun LoginFormPreview() {
    ReadlyBookTheme {
        LoginForm(
            email = "",
            password = "",
            onEmailChange = {},
            onPasswordChange = {},
            onSubmit = {}
        )
    }
}