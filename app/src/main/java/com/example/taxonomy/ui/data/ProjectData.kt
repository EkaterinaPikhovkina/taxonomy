package com.example.taxonomy.ui.data

data class ProjectData(
    val name: String = "",
    val categories: List<String> = emptyList(),
    val keywords: Map<String, List<String>> = emptyMap()
)
