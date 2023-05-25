package com.example.chitchat.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ConversationsPage(modifier: Modifier) {
    Column(modifier = Modifier.padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally)
    {

        Text(text = "Chats",
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold)

        ConvoCard()
    }
}

@Composable 
fun ConvoCard(modifier: Modifier = Modifier) {
    Card(modifier = Modifier
        .padding(4.dp)
        .fillMaxWidth()) {
        Column() {
            Text(text = "Testing card",
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.displayMedium)
        }

    }
}
@Preview(showSystemUi = true)
@Composable
fun previewConversationsPage()
{

}