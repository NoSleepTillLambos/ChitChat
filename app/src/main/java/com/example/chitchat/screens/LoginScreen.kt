package com.example.chitchat.screens


import android.os.Build
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.chitchat.CAViewModel
import com.example.chitchat.DestinationScreen
import com.example.chitchat.ProgressSpinner
import com.example.chitchat.R
import com.example.chitchat.checkIfSignedIn
import com.example.chitchat.navigateTo


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    navController: NavController,
    vm: CAViewModel,
    modifier: Modifier = Modifier
) {

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = {
            Log.d(" AA check ", it.toString())
        })

    checkIfSignedIn(vm = vm, navController = navController)
    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .wrapContentHeight()
                .verticalScroll(
                    rememberScrollState()
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        )
        {
            val emailState = remember { mutableStateOf(TextFieldValue()) }
            val passwordState = remember { mutableStateOf(TextFieldValue()) }

            val focus = LocalFocusManager.current
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = null,
                modifier = modifier
                    .width(100.dp)
                    .padding(top = 16.dp)
                    .padding(8.dp)
            )
            Text(text = "Login", modifier = Modifier.padding(8.dp), fontSize = 18.sp)

            OutlinedTextField(
                value = emailState.value,
                onValueChange = { emailState.value = it },
                modifier.padding(9.dp),
                label = { Text(text = "Email") },
                trailingIcon = {
                    Icon(imageVector = Icons.Default.Email, contentDescription = null)
                },
            )
            OutlinedTextField(
                value = passwordState.value,
                onValueChange = { passwordState.value = it },
                modifier.padding(9.dp),
                label = { Text(text = "Password") },
                trailingIcon = {
                    Icon(imageVector = Icons.Default.Star, contentDescription = null)
                },
                visualTransformation = PasswordVisualTransformation()
            )

            Button(
                onClick = {
                    focus.clearFocus(force = true)
                    vm.login(
                        emailState.value.text,
                        passwordState.value.text
                    )
                },
                modifier = Modifier.padding(8.dp)
            ) {
                Text(text = "Login here")
            }

            Text(text = "Need an account, head to Sign up!",
                color = Color.Black,
                modifier = Modifier
                    .padding(6.dp)
                    .clickable {
                        navigateTo(navController, DestinationScreen.RegisterScreen.route)
                    }
            )
        }

        // when signing a user up the spinner is active
        val isLoading = vm.isInProgress.value
        if (isLoading)
            ProgressSpinner()


        // LAUNCHING NOTIFICATION
        LaunchedEffect(key1 = permissionLauncher) {
            Log.d("AA launche", "YES! WE HAVE LAUNCHED")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                permissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
            }
        }

    }
}
