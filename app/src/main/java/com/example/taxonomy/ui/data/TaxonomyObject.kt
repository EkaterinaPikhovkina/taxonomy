package com.example.taxonomy.ui.data

import kotlinx.serialization.Serializable

@Serializable
data class TaxonomyObject (
    val uid: String = "",
    val name: String = "",
    val categories: String = "",
    val keywords: String = ""
)