package com.example.taxonomy.ui.data

import kotlinx.serialization.Serializable

@Serializable
data class ProfileObject(
    val uid: String = "",
    val email: String = ""
)