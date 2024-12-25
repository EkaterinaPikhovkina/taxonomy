package com.example.taxonomy.ui.login.data

import kotlinx.serialization.Serializable

@Serializable
data class DocumentCorpusObject(
    val uid: String = "",
    val email: String = ""
)
