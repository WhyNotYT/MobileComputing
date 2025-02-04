package com.example.mobilecomputing.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobilecomputing.data.AppDatabase
import com.example.mobilecomputing.UserProfile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID


class UserProfileViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)

    private val _username = MutableStateFlow("")
    val username: StateFlow<String> = _username

    private val _profileImageUri = MutableStateFlow<Uri?>(null)
    val profileImageUri: StateFlow<Uri?> = _profileImageUri

    init {
        viewModelScope.launch(Dispatchers.IO) {
            val profile = database.userProfileDao().getProfile()
            _username.value = profile?.username ?: ""
            _profileImageUri.value = profile?.imageUri?.let { Uri.parse(it) }
        }
    }

    fun saveImageToInternalStorage(uri: Uri) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val inputStream = getApplication<Application>().contentResolver.openInputStream(uri)
                val file = File(getApplication<Application>().filesDir, "profile_${UUID.randomUUID()}.jpg")
                val imagePath = file.absolutePath

                inputStream?.use { input ->
                    file.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }

                database.userProfileDao().insertProfile(
                    UserProfile(
                        username = _username.value,
                        imageUri = imagePath
                    )
                )

                _profileImageUri.value = Uri.parse(imagePath)

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun saveProfile(username: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                database.userProfileDao().insertProfile(
                    UserProfile(
                        username = username,
                        imageUri = _profileImageUri.value?.toString()
                    )
                )
                _username.value = username 
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
