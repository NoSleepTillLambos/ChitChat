import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.chitchat.CAViewModel
import com.example.chitchat.Image
import com.example.chitchat.data.Message

@Composable
fun IndividualChat(navController: NavController, vm: CAViewModel, chatId: String) {


    LaunchedEffect(key1 = Unit) {
        vm.populateChat(chatId)
    }
    BackHandler {
        vm.depopulateChat()
    }

    var reply by rememberSaveable { mutableStateOf("") }
    val currentChat = vm.chats.value.first { it.chatId == chatId }
    val myUser = vm.userData.value
    val chatUser = if (myUser?.userId == currentChat.user1.userId) currentChat.user2
    else currentChat.user1
    val onSendReply = {
        vm.onSendReply(chatId, reply)
        reply = ""
    }
    val chatMessages = vm.chatMessages

    Column(modifier = Modifier.fillMaxSize()) {
        // Chat header
        ChatHeader(name = chatUser.name ?: "", imageUrl = chatUser.imageUrl ?: "") {
            navController.popBackStack()
            vm.depopulateChat()
        }

        // Messages
        Messages(
            modifier = Modifier.weight(1f),
            chatMessages.value,
            myUser?.userId ?: ""
        )

        // Reply box
        ReplyBox(reply = reply, onReplyChange = { reply = it }, onSendReply = onSendReply)
    }
}

@Composable
fun ChatHeader(name: String, imageUrl: String, onBackClicked: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            Icons.Rounded.ArrowBack,
            contentDescription = null,
            modifier = Modifier
                .clickable { onBackClicked.invoke() }
                .padding(8.dp)
        )
        Image(
            data = imageUrl,
            modifier = Modifier
                .padding(8.dp)
                .size(50.dp)
                .clip(CircleShape)
        )
        Text(
            text = name,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 4.dp)
        )
    }
    Divider()
}

@Composable
fun Messages(modifier: Modifier, chatMessages: List<Message>, currentUserId: String) {
    LazyColumn(modifier = modifier) {
        items(chatMessages) { msg ->
            msg.message?.let {
                val alignment = if (msg.sentBy == currentUserId) Alignment.End
                else Alignment.Start
                val color = if (msg.sentBy == currentUserId) Color(0xFFF7CF4A)
                else Color(0xFFF93B6D9)

                /*0xF7C4A*/
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalAlignment = alignment
                ) {
                    Text(
                        text = msg.message,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(color)
                            .padding(12.dp),
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReplyBox(reply: String, onReplyChange: (String) -> Unit, onSendReply: () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Divider()
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TextField(value = reply, onValueChange = onReplyChange, maxLines = 3)
            Button(onClick = onSendReply) {
                Text(text = "Send")
            }
        }
    }
}