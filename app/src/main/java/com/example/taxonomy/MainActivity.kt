package com.example.taxonomy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform
import com.example.taxonomy.ui.theme.TaxonomyTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TaxonomyTheme {
                val navController = rememberNavController()

                if (!Python.isStarted()) {
                    Python.start(AndroidPlatform(this))
                }

                NavHost(navController = navController, startDestination = "document_corpus") {
                    composable("document_corpus") {
                        DocumentCorpus (navController = navController)
                    }
                    composable(
                        "ner_tagging/{categories}/{keywords}",
                        arguments = listOf(
                            navArgument("categories") { type = NavType.StringType },
                            navArgument("keywords") { type = NavType.StringType }
                        )
                    ) { backStackEntry ->
                        val categoriesString = backStackEntry.arguments?.getString("categories") ?: ""
                        val keywordsString = backStackEntry.arguments?.getString("keywords") ?: ""

                        val categories = categoriesString.split(",").filter { it.isNotBlank() }
                        val keywords = keywordsString.split("|").map { it ->
                            it.split(",").filter { it.isNotBlank() }
                        }

                        NERTagging(categories = categories, keywords = keywords) {
                            navController.popBackStack()
                        }
                    }
                }
            }
        }
    }
}