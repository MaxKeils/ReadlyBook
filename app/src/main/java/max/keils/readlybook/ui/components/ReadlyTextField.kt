package max.keils.readlybook.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import max.keils.readlybook.ui.theme.ReadlyBookTheme

@Composable
internal fun ReadlyTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    singleLine: Boolean = true,
    isError: Boolean = false,
    errorMessage: String? = null,
    shape: Shape = RoundedCornerShape(16.dp),
    isPassword: Boolean = false,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default
) {
    var passwordVisible by remember { mutableStateOf(false) }

    Column(modifier = modifier) {

        Surface(
            shape = shape,
            tonalElevation = 2.dp,
            shadowElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            TextField(
                value = value,
                onValueChange = onValueChange,
                singleLine = singleLine,
                textStyle = LocalTextStyle.current.copy(
                    fontSize = 16.sp
                ),
                label = {
                    val fontSize = if (value.isEmpty()) 16.sp else 14.sp
                    Text(
                        label,
                        style = LocalTextStyle.current.copy(fontSize = fontSize)
                    )
                },
                isError = isError,
                visualTransformation = if (isPassword && !passwordVisible)
                    PasswordVisualTransformation()
                else
                    VisualTransformation.None,
                trailingIcon = if (isPassword) {
                    {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible)
                                    Icons.Default.Visibility
                                else
                                    Icons.Default.VisibilityOff,
                                contentDescription = if (passwordVisible)
                                    "Hide password"
                                else
                                    "Show password"
                            )
                        }
                    }
                } else null,
                keyboardOptions = keyboardOptions,
                keyboardActions = keyboardActions,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    disabledContainerColor = MaterialTheme.colorScheme.surface,

                    focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                    unfocusedIndicatorColor = MaterialTheme.colorScheme.outlineVariant,
                    errorIndicatorColor = MaterialTheme.colorScheme.error,

                    cursorColor = MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 56.dp)
            )
        }

        if (isError && errorMessage != null) {
            Spacer(Modifier.height(4.dp))
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                fontSize = 12.sp
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ReadlyTextFieldPreview() {
    ReadlyBookTheme {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            ReadlyTextField(
                value = "Достоевский",
                onValueChange = {},
                label = "Автор"
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ReadlyEmptyTextFieldPreview() {
    ReadlyBookTheme {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            ReadlyTextField(
                value = "",
                onValueChange = {},
                label = "Автор"
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ReadlyPasswordFieldPreview() {
    ReadlyBookTheme {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            ReadlyTextField(
                value = "mypassword123",
                onValueChange = {},
                label = "Пароль",
                isPassword = true
            )
        }
    }
}

