package com.example.chitchat.screens



import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.chitchat.CAViewModel
import com.example.chitchat.DestinationScreen
import com.example.chitchat.ProgressSpinner
import com.example.chitchat.R
import com.example.chitchat.navigateTo
import com.example.chitchat.ui.theme.ChitChatTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    navController: NavController,
    vm: CAViewModel,
    modifier: Modifier = Modifier) {

    Box(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier
            .fillMaxSize()
            .wrapContentHeight()
            .verticalScroll(
                rememberScrollState()
            ),
            horizontalAlignment = Alignment.CenterHorizontally)
        {
            val nameState = remember { mutableStateOf(TextFieldValue())}
            val numberState = remember { mutableStateOf(TextFieldValue())}
            val emailState = remember { mutableStateOf(TextFieldValue())}
            val passwordState = remember { mutableStateOf(TextFieldValue())}

            val focus  = LocalFocusManager.current
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = null,
                modifier = modifier
                    .width(100.dp)
                    .padding(top = 16.dp)
                    .padding(8.dp)
            )
            Text(text = "Sign up below", modifier = Modifier.padding(8.dp), fontSize = 26.sp, fontFamily = FontFamily.Serif)

            OutlinedTextField(value = nameState.value,
                onValueChange = {nameState.value = it},
                modifier.padding(9.dp),
                label = { Text(text = "Name")}
            )
            OutlinedTextField(value = numberState.value,
                onValueChange = {numberState.value = it},
                modifier.padding(9.dp),
                label = { Text(text = "Number")}
            )
            OutlinedTextField(value = emailState.value,
                onValueChange = {emailState.value = it},
                modifier.padding(9.dp),
                label = { Text(text = "Email")}
            )
            OutlinedTextField(value = passwordState.value,
                onValueChange = {passwordState.value = it},
                modifier.padding(9.dp),
                label = { Text(text = "Password")},
                visualTransformation = PasswordVisualTransformation()
            )

            Button(onClick = {
                focus.clearFocus(force = true)

                // getting the view model for auth
                vm.signUp(
                    nameState.value.text,
                    numberState.value.text,
                    emailState.value.text,
                    passwordState.value.text
                )
            },
            modifier = Modifier.padding(8.dp)) {
                Text(text = "Sign up here")
            }

            Text(text = "Do you already have an account?",
            color = Color.Black,
                modifier = Modifier
                    .padding(6.dp)
                    .clickable {
                        navigateTo(navController, DestinationScreen.Login.route)
                    }
            )
        }

        // when signing a user up the spinner is active
        val isLoading = vm.isInProgress.value
        if (isLoading)
            ProgressSpinner()


    }

}
@Preview (showSystemUi = true)
@Composable
fun PreviewRegisterScreen() {
    ChitChatTheme() {

    }
}