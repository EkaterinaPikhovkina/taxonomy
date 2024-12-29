package com.example.taxonomy.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.chaquo.python.PyObject
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform
import com.example.taxonomy.R
import com.example.taxonomy.ui.data.DocumentCorpusObject
import com.example.taxonomy.ui.data.ProfileObject
import com.example.taxonomy.ui.data.TaxonomyObject
import java.io.BufferedReader
import java.io.InputStreamReader

@Composable
fun DocumentCorpus(
    navController: NavHostController,
    navData: DocumentCorpusObject
) {
    val textContent = remember { mutableStateOf("") }
    val context = LocalContext.current
    val errorState = remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        TopAppBar(
            iconResId = R.drawable.doc,
            titleText = "Document Corpus",
            navController = navController,
            profileIcon = 1,
            navData = ProfileObject(navData.uid)
        )

        fun processAllFiles(uris: List<Uri>) {
            uris.forEach { uri ->
                val contentResolver = context.contentResolver
                val fileName = try {
                    val cursor = contentResolver.query(uri, null, null, null, null)
                    cursor?.use {
                        if (it.moveToFirst()) {
                            val displayNameIndex = it.getColumnIndex("_display_name")
                            if (displayNameIndex != -1) it.getString(displayNameIndex) else null
                        } else null
                    }
                } catch (e: Exception) {
                    errorState.value = "Error getting file name: ${e.message}"
                    return
                }

                if (fileName == null) {
                    errorState.value = "Could not get file name"
                    return
                }


                if (!fileName.endsWith(".txt", ignoreCase = true)) {
                    errorState.value = "Unsupported file type. Only .txt files are allowed."
                    return
                }

                val inputStream = contentResolver.openInputStream(uri)
                if (inputStream != null) {
                    try {
                        val reader = BufferedReader(InputStreamReader(inputStream))
                        val text = reader.readText()
                        errorState.value = ""
                        textContent.value += text
                    } catch (e: Exception) {
                        textContent.value += "Error reading file: ${e.message}\n"
                    } finally {
                        inputStream.close()
                    }
                }
            }
        }

        val launcher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.OpenMultipleDocuments(),
            onResult = { uris ->
                processAllFiles(uris)
            }
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(start = 30.dp, top = 30.dp, end = 30.dp, bottom = 0.dp),
        ) {
            Text(
                text = "This program supports processing files with .txt extension and in English",
                style = MaterialTheme.typography.bodyLarge,
            )
            Spacer(modifier = Modifier.height(20.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                OutlinedButton(
                    onClick = {
                        launcher.launch(
                            arrayOf(
                                "text/plain"
                            )
                        )
                    },
                ) {
                    Text(
                        text = "Load Data",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSecondary
                    )
                }
            }
            if (errorState.value.isNotEmpty()) {
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = errorState.value,
                    style = MaterialTheme.typography.bodyLarge,
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
            ) {
                item {
                    Text(
                        text = textContent.value,
                        style = MaterialTheme.typography.bodyLarge,
                    )
                }
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 36.dp, top = 36.dp, end = 30.dp, bottom = 30.dp),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ButtonWithIconRight(
                onClick = {
                    if (textContent.value.isEmpty()) {
                        errorState.value = "Please load data first"
                    } else {
                        if (!Python.isStarted()) {
                            Python.start(AndroidPlatform(context))
                        }
                        val python = Python.getInstance()
                        val pythonModule = python.getModule("term_extraction")

                        val result: PyObject =
                            pythonModule.callAttr("extract_entities", textContent.value)

                        val resultTuple = result.asList()
                        val categoriesPyList = resultTuple[0].asList()
                        val keywordsPyList = resultTuple[1].asList()

                        val categories = categoriesPyList.map { it.toString() }
                        val keywords = keywordsPyList.map { keywordList ->
                            keywordList.asList().map { it.toString() }
                        }

                        val categoriesString = categories.joinToString(",")
                        val keywordsString = keywords.joinToString("|") { it.joinToString(",") }

                        navController.navigate(
                            TaxonomyObject(
                                uid = navData.uid,
                                categories = categoriesString,
                                keywords = keywordsString
                            )
                        )
                    }
                },
                text = "Next",
                icon = R.drawable.arrow_right,
                contentDescription = null,
            )
        }
    }
}
