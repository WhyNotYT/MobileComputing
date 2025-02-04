package com.example.mobilecomputing.screens

import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.activity.compose.BackHandler
import coil.compose.AsyncImage
import kotlinx.coroutines.launch
import com.example.mobilecomputing.data.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun SecondScreen(onNavigateBack: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var username by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<String?>(null) }
    val database = AppDatabase.getDatabase(context)

    BackHandler {
        onNavigateBack()
    }

    LaunchedEffect(Unit) {
        scope.launch(Dispatchers.IO) {
            try {
                database.userProfileDao().getProfile()?.let { userProfile ->
                    withContext(Dispatchers.Main) {
                        username = userProfile.username
                        imageUri = userProfile.imageUri
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Profile Details",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        imageUri?.let { uri ->
            AsyncImage(
                model = uri,
                contentDescription = "Profile Picture",
                modifier = Modifier.size(120.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Username: $username",
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(onClick = onNavigateBack) {
            Text("Back to Main Screen")
        }
    }
}