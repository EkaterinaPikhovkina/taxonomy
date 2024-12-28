package com.example.taxonomy

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.taxonomy.ui.data.ProfileObject
import com.example.taxonomy.ui.data.ProjectData
import com.example.taxonomy.ui.data.ProjectsObject
import com.example.taxonomy.ui.data.TaxonomyObject
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

@Composable
fun Projects(
    navController: NavHostController,
    navData: ProjectsObject
) {
    val projectsList = remember { mutableStateOf(emptyList<ProjectData>()) }
    val errorState = remember { mutableStateOf("") }

    LaunchedEffect(navData.uid) {
        val db = Firebase.firestore
        getUserProjects(db, navData.uid) { projects ->
            projectsList.value = projects
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {

        TopAppBar(
            iconResId = R.drawable.folder,
            titleText = "My Projects",
            navController = navController,
            profileIcon = 1,
            navData = ProfileObject(navData.uid)
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(start = 30.dp, top = 30.dp, end = 30.dp, bottom = 0.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {

            items(projectsList.value) { project ->

                ProjectCard(
                    title = project.name,
                    onClick = {
                        navController.navigate(
                            TaxonomyObject(
                                uid = navData.uid,
                                name = project.name,
                                categories = project.categories.joinToString(","),
                                keywords = project.keywords.entries.joinToString("|") { (category, keywords) ->
                                    "$category," + keywords.joinToString(",")
                                }
                            )
                        )
                    },
                    onDelete = { projectName ->
                        deleteProject(
                            db = FirebaseFirestore.getInstance(),
                            uid = navData.uid,
                            projectName = projectName
                        ) { success ->
                            if (success) {
                                getUserProjects(
                                    db = FirebaseFirestore.getInstance(),
                                    uid = navData.uid
                                ) { projects ->
                                    projectsList.value = projects
                                }
                            } else {
                                errorState.value = "Error"
                            }
                        }
                    }
                )
            }
            if (errorState.value.isNotEmpty()) {
                item {
                    Text(
                        text = errorState.value,
                        style = MaterialTheme.typography.bodyLarge,
                    )
                }
            }
        }
    }
}

private fun getUserProjects(
    db: FirebaseFirestore,
    uid: String,
    onProjects: (List<ProjectData>) -> Unit
) {
    db.collection("users")
        .document(uid)
        .collection("projects")
        .get()
        .addOnSuccessListener { querySnapshot ->
            val projects = querySnapshot.toObjects(ProjectData::class.java)
            onProjects(projects)
        }
        .addOnFailureListener { e ->
            println("Error getting projects: $e")
            onProjects(emptyList())
        }
}

private fun deleteProject(db: FirebaseFirestore, uid: String, projectName: String, onResult: (Boolean) -> Unit) {
    db.collection("users")
        .document(uid)
        .collection("projects")
        .whereEqualTo("name", projectName)
        .get()
        .addOnSuccessListener { querySnapshot ->
            for (document in querySnapshot) {
                document.reference.delete()
                    .addOnSuccessListener {
                        onResult(true)
                    }
                    .addOnFailureListener { e ->
                        println("Error deleting project: $e")
                        onResult(false)
                    }
                return@addOnSuccessListener
            }
            onResult(false)
        }
        .addOnFailureListener { e ->
            println("Error finding project: $e")
            onResult(false)
        }
}


