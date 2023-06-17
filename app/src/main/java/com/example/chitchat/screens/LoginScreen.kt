package com.example.chitchat.screens




import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.chitchat.ui.theme.ChitChatTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen() {
}
@Preview (showSystemUi = true)
@Composable
fun PreviewLoginScreen() {
    ChitChatTheme {
        LoginScreen()
    }
}