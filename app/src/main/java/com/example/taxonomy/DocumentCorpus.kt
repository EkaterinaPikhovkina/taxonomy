package com.example.taxonomy

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import com.chaquo.python.PyObject
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform
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
    val loadedFiles = remember { mutableStateOf<List<String>>(emptyList()) }
    val textContent = remember { mutableStateOf("") }
    val context = LocalContext.current

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            iconResId = R.drawable.doc,
            titleText = "Document Corpus",
            navController = navController,
            profileIcon = 1,
            navData = ProfileObject(navData.uid, navData.email)
        )

        fun processAllFiles(uris: List<Uri>) {
            uris.forEach { uri ->
                val contentResolver = context.contentResolver
                val inputStream = contentResolver.openInputStream(uri)
                if (inputStream != null) {
                    try {
                        val reader = BufferedReader(InputStreamReader(inputStream))
                        val text = reader.readText()
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

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(start = 30.dp, top = 32.dp, end = 30.dp, bottom = 0.dp),
            verticalArrangement = Arrangement.spacedBy(28.dp)
        ) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    OutlinedButton(
                        onClick = {
                            launcher.launch(
                                arrayOf(
                                    "text/plain",
                                    "application/pdf",
                                    "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
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
            }

            items(loadedFiles.value) { file ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = file,
                        style = MaterialTheme.typography.labelMedium,
                    )
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(R.drawable.close)
                            .decoderFactory(SvgDecoder.Factory())
                            .build(),
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        contentScale = ContentScale.Fit
                    )
                }
            }

            item {
                Text(
                    text = textContent.value,
                    style = MaterialTheme.typography.bodyLarge,
                )
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
                    if (textContent.value.isNotEmpty()) {
                        if (!Python.isStarted()) {
                            Python.start(AndroidPlatform(context))
                        }
                        val python = Python.getInstance()
                        val pythonModule = python.getModule("term_extraction")

                        val result: PyObject = pythonModule.callAttr("fun", textContent.value)

                        val resultTuple = result.asList()
                        val categoriesPyList = resultTuple[0].asList()
                        val keywordsPyList = resultTuple[1].asList()

                        val categories = categoriesPyList.map { it.toString() }
                        val keywords = keywordsPyList.map { keywordList ->
                            keywordList.asList().map { it.toString() }
                        }

                        val categoriesString = categories.joinToString(",")
                        val keywordsString = keywords.joinToString("|") { it.joinToString(",") }

                        navController.navigate(TaxonomyObject(
                            uid = navData.uid,
                            email = navData.email,
                            categories = categoriesString,
                            keywords = keywordsString
                        ))
                    }
                },
                text = "Next",
                icon = R.drawable.arrow_right,
                contentDescription = null,
            )
        }
    }
}
