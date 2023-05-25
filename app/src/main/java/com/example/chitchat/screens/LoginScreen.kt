package com.example.chitchat.screens



import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chitchat.R
import com.example.chitchat.ui.theme.ChitChatTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(navToRegister:() -> Unit,
                modifier: Modifier = Modifier) {

    // state variables for logging in and signing up
    var email by remember {
        mutableStateOf("")}
    var password by remember {
        mutableStateOf("")}

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally) {

        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = null,
            modifier = Modifier.size(100.dp)
        )
        Spacer(modifier = Modifier.size(20.dp))
        Text(
            text = "Login screen",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Normal,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer (modifier = Modifier.size(20.dp))
        OutlinedTextField(
            value = email,
            onValueChange = {email = it} ,
            label = {Text("Email")},
            leadingIcon = { Icon(
                imageVector = Icons.Default.Email,
                contentDescription = null,
        )
                          },

        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp)
        )

        OutlinedTextField(
            value = password,
            onValueChange = {password = it} ,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            label = {Text("Password")},
            leadingIcon = { Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = null,
            )
                          },

            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp)
        )
        Button(onClick = { /*TODO*/ },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(5.dp)) {
            Text(text = "Login", fontSize = 19.sp,
            modifier = Modifier.padding(4.dp))
        }

        Row (modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center) {
            Text(text = "Don't have an account? ")

            TextButton(onClick = { navToRegister.invoke()}) {
                Text(text = "Register Here")
            }
        }

    }
}
@Preview (showSystemUi = true)
@Composable
fun PreviewLoginScreen() {
    ChitChatTheme() {
        LoginScreen(navToRegister = {})
    }
}