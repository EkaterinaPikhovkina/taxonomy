package com.example.taxonomy

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import android.content.Intent
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream

@Composable
fun NERTagging(
    categories: List<String>,
    keywords: List<List<String>>,
    navigateBack: () -> Unit,
) {
    val context = LocalContext.current

    // Функция для создания текстового файла и сохранения в него результатов
    fun saveResultsToFile(categories: List<String>, keywords: List<List<String>>) {
        val fileName = "ner_tagging_results.txt"
        val fileContent = buildString {
            categories.forEachIndexed { index, category ->
                append("$category:\n")
                keywords[index].forEach { keyword ->
                    append("- $keyword\n")
                }
                append("\n")
            }
        }

        // Создаем файл во временном хранилище
        val tempFile = File(context.cacheDir, fileName)
        FileOutputStream(tempFile).use {
            it.write(fileContent.toByteArray())
        }

        // Получаем Uri файла через FileProvider
        val fileUri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            tempFile
        )

        // Создаем Intent для открытия файла
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(fileUri, "text/plain")
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK
        }

        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            // Обработка ошибки, например, показ Toast сообщения
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {

        TopAppBar(
            iconResId = R.drawable.tag,
            titleText = "NER Tagging"
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(start = 30.dp, top = 30.dp, end = 30.dp, bottom = 0.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            items(categories.size) { index ->
                WordListCard(
                    title = categories[index],
                    words = keywords[index],
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 36.dp, top = 36.dp, end = 30.dp, bottom = 30.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ButtonWithIconLeft(
                onClick = navigateBack,
                text = "Back",
                icon = R.drawable.arrow_left,
                contentDescription = null,
            )
            ButtonWithIconRight(
                onClick = { saveResultsToFile(categories, keywords) },
                text = "Download",
                icon = R.drawable.download,
                contentDescription = null,
            )
        }
    }
}

//@Preview
//@Composable
//fun PreviewNERTagging() {
//    TaxonomyTheme {
//        Surface {
//            NERTagging()
//        }
//    }
//}

