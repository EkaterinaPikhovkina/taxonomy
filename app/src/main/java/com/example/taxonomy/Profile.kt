package com.example.taxonomy

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
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

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            iconResId = R.drawable.profile,
            titleText = navData.email,
            navController = navController,
            profileIcon = null,
            navData = ProfileObject(navData.uid, navData.email)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 30.dp, top = 30.dp, end = 30.dp, bottom = 30.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "New Project",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSecondary,
                modifier = Modifier.clickable {
                    navController.navigate(DocumentCorpusObject(navData.uid))
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
            Text(
                text = "Log Out",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSecondary,
                modifier = Modifier.clickable {
                    signOut(auth)
                    navController.navigate(LoginScreenObject)
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