package com.example.taxonomy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import androidx.navigation.toRoute
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform
import com.example.taxonomy.ui.data.DocumentCorpusObject
import com.example.taxonomy.ui.data.LoginScreenObject
import com.example.taxonomy.ui.data.ProfileObject
import com.example.taxonomy.ui.data.ProjectsObject
import com.example.taxonomy.ui.data.TaxonomyObject
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

                NavHost(
                    navController = navController,
                    startDestination = LoginScreenObject
                ) {

                    composable<LoginScreenObject> {
                        LoginScreen { navData ->
                            navController.navigate(navData,
                                navOptions {
                                    popUpTo<LoginScreenObject> {
                                        inclusive = true
                                    }
                                }
                            )
                        }
                    }

                    composable<ProfileObject> { navEntry ->
                        Profile(
                            navController = navController,
                            navEntry.toRoute<ProfileObject>()
                        )
                    }

                    composable<DocumentCorpusObject> { navEntry ->
                        DocumentCorpus(
                            navController = navController,
                            navEntry.toRoute<DocumentCorpusObject>()
                        )
                    }

                    composable<TaxonomyObject> { navEntry ->
                        Taxonomy(
                            navController = navController,
                            navigateBack = {
                                navController.popBackStack()
                            },
                            navEntry.toRoute<TaxonomyObject>(),
                        )
                    }

                    composable<ProjectsObject> { navEntry ->
                        val receivedData = navEntry.toRoute<ProjectsObject>()
                        Projects(
                            navController = navController,
                            receivedData
                        )
                    }
                }
            }
        }
    }
}

