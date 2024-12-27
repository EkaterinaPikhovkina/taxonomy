package com.example.taxonomy

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.taxonomy.ui.data.ProfileObject
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

@Composable
fun LoginScreen(
    onNavigateToProfileObject: (ProfileObject) -> Unit
) {
    val auth = remember {
        Firebase.auth
    }

    val emailState = remember { mutableStateOf("katerina32909@gmail.com") }
    val passwordState = remember { mutableStateOf("1234567890") }
    val errorState = remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        TopAppBar(
            iconResId = R.drawable.taxonomy,
            titleText = "Taxonomy Generator",
            navController = null,
            profileIcon = null,
            navData = null
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(start = 30.dp, top = 0.dp, end = 30.dp, bottom = 0.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            AuthTextField(
                value = emailState.value,
                onValueChange = { emailState.value = it },
                label = { Text("Email") }
            )
            Spacer(modifier = Modifier.height(10.dp))
            AuthTextField(
                value = passwordState.value,
                onValueChange = { passwordState.value = it },
                label = { Text("Password") }
            )
            if (errorState.value.isNotEmpty()) {
                Text(
                    text = errorState.value,
                    style = MaterialTheme.typography.bodyLarge,
                )
            }
        }
        Column(
            modifier = Modifier
                .padding(start = 30.dp, top = 30.dp, end = 30.dp, bottom = 30.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            ButtonWithoutIcon(
                onClick = {
                    signIn(
                        auth,
                        emailState.value,
                        passwordState.value,
                        onSignInSuccess = { navData ->
                            onNavigateToProfileObject(navData)
                        },
                        onSignInFailure = { error ->
                            errorState.value = error
                        }
                    )            },
                text = "Sing In"
            )
            OutlinedButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    signUp(
                        auth,
                        emailState.value,
                        passwordState.value,
                        onSignUpSuccess = { navData ->
                            onNavigateToProfileObject(navData)
                        },
                        onSignUpFailure = { error ->
                            errorState.value = error
                        }
                    )
                },
            ) {
                Text(
                    text = "Sing Up",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSecondary
                )
            }
        }
    }
}

private fun signUp(
    auth: FirebaseAuth,
    email: String,
    password: String,
    onSignUpSuccess: (ProfileObject) -> Unit,
    onSignUpFailure: (String) -> Unit
) {
    if (email.isBlank() || password.isBlank()) {
        onSignUpFailure("Email and password cannot be empty")
        return
    }

    auth.createUserWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) onSignUpSuccess(
                ProfileObject(
                    task.result.user?.uid!!,
                    task.result.user?.email!!,
                )
            )
        }
        .addOnFailureListener {
            onSignUpFailure(it.message ?: "Sign Up Error")
        }

}

private fun signIn(
    auth: FirebaseAuth,
    email: String,
    password: String,
    onSignInSuccess: (ProfileObject) -> Unit,
    onSignInFailure: (String) -> Unit
) {
    if (email.isBlank() || password.isBlank()) {
        onSignInFailure("Email and password cannot be empty")
        return
    }

    auth.signInWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) onSignInSuccess(
                ProfileObject(
                    task.result.user?.uid!!,
                    task.result.user?.email!!,
                )
            )
        }
        .addOnFailureListener {
            onSignInFailure(it.message ?: "Sign In Error")
        }

}
