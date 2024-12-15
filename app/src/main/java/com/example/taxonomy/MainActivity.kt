package com.example.taxonomy

import android.app.Instrumentation.ActivityResult
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

import com.chaquo.python.PyObject
import com.chaquo.python.Python

class MainActivity : ComponentActivity() {
    val launcher = registerForActivityResult(ActivityResultContracts.OpenDocument()){

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()

            NavHost(navController = navController, startDestination = "document_corpus") {
                composable("document_corpus") {
                    DocumentCorpus{ navController.popBackStack() }
                }
//                composable("term_extraction") {
//                    TermExtraction{ navController.popBackStack() }
//                }
//                composable("term_categorization") {
//                    TermCategorization{ navController.popBackStack() }
//                }
//                composable("taxonomy_visualisation") {
//                    TaxonomyVisualization{ navController.popBackStack() }
//                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DocumentCorpus(
    navigateBack: () -> Unit,
    ) {
        val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())

        val python = Python.getInstance()
        val pythonModule = python.getModule("test")
        val result : PyObject = pythonModule.callAttr("fun")
        val message : String = result.toString()

        Scaffold(
            modifier = Modifier
                .nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                MediumTopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.primary,
                    ),
                    title = {
                        Text(
                            "Document Corpus $message",
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = navigateBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Localized description"
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = { /* do something */ }) {
                            Icon(
                                imageVector = Icons.Filled.Menu,
                                contentDescription = "Localized description"
                            )
                        }
                    },
                    scrollBehavior = scrollBehavior
                )
            },
            bottomBar = {
                BottomAppBar(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.primary,
                ) {
                    Button(onClick = { /* do something */ }) {
                        Text("Next")
                    }
                }
            },
        ) { innerPadding ->
            ScrollContent(innerPadding)
        }
    }

@Composable
fun ScrollContent(innerPadding: PaddingValues) {
    val loadedFiles = remember { mutableStateOf<List<String>>(emptyList()) }

//    val launcher = rememberLauncherForActivityResult(contract = ActivityResultContracts.OpenDocument(),
//        onResult = { println(it) })

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
        onResult = { uri ->
            if (uri != null) {
                // Add the file name to the list (for example, for display)
                val fileName = uri.lastPathSegment ?: "Unknown file"
                loadedFiles.value += fileName
            }
        }
    )

    Column(
        modifier = Modifier
            .padding(innerPadding),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        OutlinedButton(onClick = { launcher.launch(arrayOf("text/plain")) }) {
            Text("Load files")
        }
//        val conversationSample = listOf(
//            "s11042-019-07880-y.pdf",
//            "s11042-019-07880-y.pdf"
//        )
//        FileList(conversationSample)
        FileList(loadedFiles.value)
    }
}

@Composable
fun FileRow(file: String) {
    Text(text = file)
}

@Composable
fun FileList(files: List<String>) {
    LazyColumn (
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ){
        items(files) { file ->
            FileRow(file)
        }
    }
}

@Preview
@Composable
fun PreviewDocumentCorpus() {
    DocumentCorpus(navigateBack = {})
}