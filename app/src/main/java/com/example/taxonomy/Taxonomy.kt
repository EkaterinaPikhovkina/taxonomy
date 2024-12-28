package com.example.taxonomy

import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.taxonomy.ui.data.ProfileObject
import com.example.taxonomy.ui.data.ProjectData
import com.example.taxonomy.ui.data.ProjectsObject
import com.example.taxonomy.ui.data.TaxonomyObject
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream

@Composable
fun Taxonomy(
    navController: NavHostController,
    navData: TaxonomyObject,
) {
    val categories = navData.categories.split(",").filter { it.isNotBlank() }
    val keywordsMap = remember(navData.keywords) {
        navData.keywords.split("|")
            .mapIndexed { index, keywordGroup ->
                val category = categories.getOrNull(index) ?: "Category $index"
                category to keywordGroup.split(",").filter { it.isNotBlank() }
            }.associate { it }
    }
    val firestore = remember { Firebase.firestore }
    val projectName = remember { mutableStateOf(navData.name) }
    val projectsList = remember { mutableStateOf(emptyList<ProjectData>()) }
    val errorState = remember { mutableStateOf("") }
    val context = LocalContext.current

    LaunchedEffect(navData.uid) {
        val db = Firebase.firestore
        getUserProjects(db, navData.uid) { projects ->
            projectsList.value = projects
        }
    }

    fun createTemporaryFile(categories: List<String>, keywords: Map<String, List<String>>) {
        val fileName = projectName.value.ifEmpty {
            "ner_tagging_results.txt"
        } + ".txt"
        val fileContent = buildString {
            categories.forEachIndexed { index, category ->
                append("$category:\n")
                keywords[category]?.forEach { keyword ->
                    append("- $keyword\n")
                }
                append("\n")
            }
        }

        val tempFile = File(context.cacheDir, fileName)
        FileOutputStream(tempFile).use {
            it.write(fileContent.toByteArray())
        }

        val fileUri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            tempFile
        )

        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(fileUri, "text/plain")
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK
        }
        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            errorState.value = e.message.toString()
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {

        TopAppBar(
            iconResId = R.drawable.tag,
            titleText = "Taxonomy Visualisation",
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
            item {
                AuthTextField(
                    value = projectName.value,
                    onValueChange = {
                        projectName.value = it
                        errorState.value = ""
                    },
                    label = { Text("Project name") }
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

            items(keywordsMap.keys.toList()) { category ->
                WordListCard(
                    title = category,
                    words = keywordsMap[category] ?: emptyList(),
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 36.dp, top = 36.dp, end = 30.dp, bottom = 30.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ButtonWithIconRight(
                onClick = {
                    if (projectName.value.isBlank()) {
                        errorState.value = "Project name cannot be empty"
                    } else {
                        createTemporaryFile(categories, keywordsMap)
                    }
                },
                text = "View",
                icon = R.drawable.eye,
                contentDescription = null,
            )
            ButtonWithIconRight(
                onClick = {
                    val projectExists = projectsList.value.any { it.name == projectName.value }

                    if (projectName.value.isBlank()) {
                        errorState.value = "Project name cannot be empty"
                    } else {
                        if (projectExists) {
                            errorState.value = "Project with this name already exists"
                        } else {
                            saveProject(
                                firestore,
                                uid = navData.uid,
                                ProjectData(
                                    name = projectName.value,
                                    categories = categories,
                                    keywords = keywordsMap
                                )
                            )
                            navController.navigate(ProjectsObject(navData.uid))
                        }
                    }
                },
                text = "Save",
                icon = R.drawable.download,
                contentDescription = null,
            )
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

private fun saveProject(
    db: FirebaseFirestore,
    uid: String,
    project: ProjectData,
) {
    db.collection("users")
        .document(uid)
        .collection("projects")
        .add(project)
}

