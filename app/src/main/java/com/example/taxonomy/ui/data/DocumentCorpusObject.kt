package com.example.taxonomy.ui.data

import kotlinx.serialization.Serializable

@Serializable
data class DocumentCorpusObject(
    val uid: String = "",
    val email: String = ""
)
