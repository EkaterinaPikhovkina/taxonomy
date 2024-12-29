package com.example.taxonomy.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import com.example.taxonomy.R
import com.example.taxonomy.ui.data.DocumentCorpusObject
import com.example.taxonomy.ui.data.LoginScreenObject
import com.example.taxonomy.ui.data.ProfileObject
import com.example.taxonomy.ui.data.ProjectsObject
import com.google.firebase.auth.FirebaseAuth


@Composable
fun Profile(
    navController: NavHostController,
    navData: ProfileObject,
) {
    val auth = FirebaseAuth.getInstance()
    var email by remember { mutableStateOf("") }
    var isLoggedIn by remember { mutableStateOf(true) }
    var isLoading by remember { mutableStateOf(true) }
    val errorState = remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        val user = auth.currentUser
        if (user != null) {
            email = user.email ?: ""
        } else {
            errorState.value = "User not logged in"
            isLoggedIn = false
        }
        isLoading = false
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primary)
                .padding(start = 30.dp, top = 30.dp, end = 30.dp, bottom = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(R.drawable.profile)
                        .decoderFactory(SvgDecoder.Factory())
                        .build(),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    contentScale = ContentScale.Fit
                )
                Spacer(modifier = Modifier.width(10.dp))
                if (isLoading) {
                    Text("Loading...", style = MaterialTheme.typography.headlineLarge, color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Text(
                        text = email,
                        color = MaterialTheme.colorScheme.onPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.headlineLarge
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 30.dp, top = 30.dp, end = 30.dp, bottom = 30.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (errorState.value.isNotEmpty()) {
                Text(
                    text = errorState.value,
                    style = MaterialTheme.typography.bodyLarge,
                )
            }
            if (isLoggedIn) {
                Text(
                    text = "New Project",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSecondary,
                    modifier = Modifier.clickable {
                        navController.navigate(
                            DocumentCorpusObject(navData.uid)
                        )
                    }
                )
                Text(
                    text = "My Projects",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSecondary,
                    modifier = Modifier.clickable {
                        navController.navigate(ProjectsObject(navData.uid))
                    }
                )
            }
                Text(
                    text = "Log Out",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSecondary,
                    modifier = Modifier.clickable {
                        if (isLoggedIn) {
                            signOut(auth)
                            navController.navigate(LoginScreenObject) {
                                popUpTo(0) { inclusive = true }
                            }
                        } else {
                            navController.navigate(LoginScreenObject) {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    }
                )
        }
    }
}


private fun signOut(
    auth: FirebaseAuth,
) {
    auth.signOut()
}