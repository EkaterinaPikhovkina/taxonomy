package com.example.taxonomy

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
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
fun Taxonomy(
    navController: NavHostController,
    navigateBack: () -> Unit,
    navData: TaxonomyObject,
) {
    val categories = navData.categories.split(",").filter { it.isNotBlank() }
    val keywordsMap = remember(navData.keywords) { // Используем remember для Map
        navData.keywords.split("|")
            .mapIndexed { index, keywordGroup ->
                val category = categories.getOrNull(index) ?: "Category $index" // Обезопасим доступ к categories
                category to keywordGroup.split(",").filter { it.isNotBlank() }
            }.associate { it }
    }
    val firestore = remember { Firebase.firestore }
    val projectName = remember { mutableStateOf(navData.name) }

    Column(modifier = Modifier.fillMaxSize()) {

        TopAppBar(
            iconResId = R.drawable.tag,
            titleText = "Taxonomy Visualisation",
            navController = navController,
            profileIcon = 1,
            navData = ProfileObject(navData.uid, navData.email)
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
                    onValueChange = { projectName.value = it },
                    label = { Text("Project name") }
                )
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
            ButtonWithIconLeft(
                onClick = navigateBack,
                text = "Back",
                icon = R.drawable.arrow_left,
                contentDescription = null,
            )
            ButtonWithIconRight(
                onClick = {
                    saveProject(
                        firestore,
                        uid = navData.uid,
                        ProjectData(
                            name = projectName.value,
                            categories = categories,
                            keywords = keywordsMap
                        )
                    )
                    navController.navigate(ProjectsObject(navData.uid, navData.email))
                },
                text = "Save",
                icon = R.drawable.download,
                contentDescription = null,
            )
        }

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
