package com.example.mobilecomputing.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import com.example.mobilecomputing.R
import com.example.mobilecomputing.Message
import com.example.mobilecomputing.SampleData
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.shape.CircleShape
import coil.compose.AsyncImage
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.layout.ContentScale
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mobilecomputing.viewmodel.UserProfileViewModel
import com.example.mobilecomputing.SensorBackgroundService

@Composable
fun MainScreen(
    onNavigateToSecond: () -> Unit,
    onNavigateToSensor: () -> Unit,
    viewModel: UserProfileViewModel = viewModel()
) {
    var username by remember { mutableStateOf("") }

    val savedUsername by viewModel.username.collectAsState()
    val savedProfileImageUri by viewModel.profileImageUri.collectAsState()

    LaunchedEffect(savedUsername) {
        if (savedUsername.isNotBlank()) {
            username = savedUsername
        }
    }

    val photoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let {
            viewModel.saveImageToInternalStorage(it) // Save new image
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .clickable {
                        photoPicker.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    }
            ) {
                AsyncImage(
                    model = savedProfileImageUri ?: R.drawable.laundrylablogo, // Use saved image
                    contentDescription = "Profile Picture",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Username") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = {
                        if (username.isNotBlank()) {
                            viewModel.saveProfile(username)
                            onNavigateToSecond()
                        }
                    }
                ) {
                    Text("Save Profile")
                }

                Button(
                    onClick = onNavigateToSensor
                ) {
                    Text("Sensor Data")
                }
            }
        }

        ConversationWithUserProfile(
            messages = SampleData.conversationSample.map { message ->
                message.copy(author = if (username.isNotBlank()) username else message.author)
            },
            userImage = savedProfileImageUri
        )
    }
}

@Composable
fun ConversationWithUserProfile(
    messages: List<Message>,
    userImage: Uri?
) {
    LazyColumn {
        items(messages) { message ->
            MessageCardWithUserProfile(message = message, userImage = userImage)
        }
    }
}

@Composable
fun MessageCardWithUserProfile(
    message: Message,
    userImage: Uri?
) {
    Row(modifier = Modifier.padding(all = 8.dp)) {
        AsyncImage(
            model = userImage ?: R.drawable.laundrylablogo,
            contentDescription = null,
            modifier = Modifier
                .size(80.dp)
                .clip(RoundedCornerShape(10))
                .border(1.5.dp, MaterialTheme.colorScheme.secondary, RoundedCornerShape(10)),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.width(8.dp))

        var isExpanded by remember { mutableStateOf(false) }
        val surfaceColor by animateColorAsState(
            if (isExpanded) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
        )

        Column(modifier = Modifier.clickable { isExpanded = !isExpanded }) {
            Text(
                text = message.author,
                color = MaterialTheme.colorScheme.secondary,
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(4.dp))

            Surface(
                shape = MaterialTheme.shapes.medium,
                shadowElevation = 1.dp,
                color = surfaceColor,
                modifier = Modifier
                    .animateContentSize()
                    .padding(1.dp)
            ) {
                Text(
                    text = message.body,
                    modifier = Modifier.padding(all = 4.dp),
                    maxLines = if (isExpanded) Int.MAX_VALUE else 1,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}