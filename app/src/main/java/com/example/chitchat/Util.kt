package com.example.chitchat

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter

fun navigateTo(navController: NavController, route: String) {
    navController.navigate(route) {
        // if there are x we just remove everything until that route, keeps everything clean
        // less memory
        popUpTo(route)
        launchSingleTop = true
    }
}

@Composable
fun ProgressSpinner() {
    Row(modifier = Modifier
        .alpha(0.2f)
        .background(Color.Gray)
        .clickable(enabled = false) {}
        .fillMaxSize(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {

        // here we can define the progress spinner
        CircularProgressIndicator()
    }

}

@Composable
fun notificationMessage(vm: CAViewModel) {
    val notifyOfState = vm.popUpNotify.value
    val notifyMessage = notifyOfState?.getContentOrNull()
    if (!notifyMessage.isNullOrEmpty())
        Toast.makeText(LocalContext.current, notifyMessage, Toast.LENGTH_SHORT).show()
}
@Composable
fun checkIfSignedIn(vm: CAViewModel, navController: NavController) {
    val isSignedIn = remember { mutableStateOf(false) }
    val signedIn = vm.isSignedIn.value
    if (signedIn && !isSignedIn.value) {
        isSignedIn.value = true
        navController.navigate(DestinationScreen.Profile.route) {
            popUpTo(0)
        }
    }
}

@Composable
fun SectionDivider() {

    Divider(
        modifier = Modifier
            .alpha(0.2f)
            .padding(top = 25.dp, bottom = 5.dp),
        thickness = 0.8.dp,
    )
}

@Composable
fun Image(
    data: String?,
    modifier: Modifier = Modifier.wrapContentHeight(),
    contentScale: ContentScale = ContentScale.Crop
){
    val painter = rememberAsyncImagePainter(model = data)
    androidx.compose.foundation.Image(painter = painter, contentDescription = null, modifier = modifier,
    contentScale = contentScale)
    if (painter.state is AsyncImagePainter.State.Loading)
        ProgressSpinner()

}

@Composable
fun RowCommon(imageUrl: String?, name: String?, onItemClick: () -> Unit) {
    Row(modifier = Modifier
        .fillMaxWidth()
        .height(90.dp)
        .clickable { onItemClick.invoke() },
        verticalAlignment = Alignment.CenterVertically)
    {
        Image(
            data = imageUrl,
            modifier = Modifier.padding(10.dp)
                .size(70.dp)
                .clip(CircleShape)
                .background(Color.Black)
        )
        Text(text = name ?: "--",
        fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 6.dp)
        )

    }
}