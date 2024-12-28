package com.example.taxonomy.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import com.example.taxonomy.R
import com.example.taxonomy.ui.data.ProfileObject

@Composable
fun AuthTextField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    textStyle: TextStyle = MaterialTheme.typography.bodyLarge,
    label: @Composable () -> Unit
) {
    TextField(
        modifier = modifier.fillMaxWidth(),
        value = value,
        onValueChange = onValueChange,
        textStyle = textStyle,
        label = label,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.secondary,
            unfocusedContainerColor = MaterialTheme.colorScheme.secondary,
            disabledContainerColor = MaterialTheme.colorScheme.secondary,
        )
    )
}

@Composable
fun ButtonWithoutIcon(onClick: () -> Unit, text: String) {
    Button(onClick = onClick) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.labelMedium,
            )
        }
    }
}

@Composable
fun TopAppBar(
    modifier: Modifier = Modifier,
    iconResId: Int?,
    iconSize: Dp = 20.dp,
    titleText: String,
    profileIcon: Int?,
    backgroundColor: Color = MaterialTheme.colorScheme.primary,
    contentColor: Color = MaterialTheme.colorScheme.onPrimary,
    navController: NavHostController?,
    navData: ProfileObject?
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .padding(start = 30.dp, top = 30.dp, end = 30.dp, bottom = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(iconResId)
                    .decoderFactory(SvgDecoder.Factory())
                    .build(),
                contentDescription = null,
                modifier = Modifier.size(iconSize),
                contentScale = ContentScale.Fit
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = titleText,
                color = contentColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.headlineLarge
            )
        }
        if (profileIcon != null) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(R.drawable.profile)
                    .decoderFactory(SvgDecoder.Factory())
                    .build(),
                contentDescription = "Sign Out",
                modifier = Modifier
                    .size(20.dp)
                    .clickable {
                        if (navData != null) {
                            navController?.navigate(ProfileObject(navData.uid))
                        }
                    },
                contentScale = ContentScale.Fit
            )
        }
    }
}

@Composable
fun ButtonWithIconRight(
    onClick: () -> Unit,
    text: String,
    icon: Int,
    contentDescription: String? = null
) {
    Button(onClick = onClick) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.labelMedium,
            )
            Spacer(modifier = Modifier.width(8.dp))
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(icon)
                    .decoderFactory(SvgDecoder.Factory())
                    .build(),
                contentDescription = contentDescription,
                modifier = Modifier.size(16.dp),
                contentScale = ContentScale.Fit
            )
        }
    }
}

@Composable
fun WordListCard(
    title: String,
    words: List<String>,
    onClick: () -> Unit,
    backgroundColor: Color = MaterialTheme.colorScheme.secondary,
    contentColor: Color = MaterialTheme.colorScheme.onSecondary,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(backgroundColor)
            .padding(24.dp)
            .clickable { onClick() },
    ) {
        Text(
            text = title,
            color = contentColor,
            style = MaterialTheme.typography.headlineMedium
                .copy(fontFeatureSettings = "c2sc, smcp"),
            textAlign = TextAlign.Start,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        if (words.isEmpty()) {
            Text(
                text = "Empty list",
                color = contentColor,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Start,
                modifier = Modifier.fillMaxWidth()
            )
        } else {
            words.forEach { word ->
                Text(
                    text = " • $word",
                    color = contentColor,
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Start,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun ProjectCard(
    title: String,
    backgroundColor: Color = MaterialTheme.colorScheme.secondary,
    contentColor: Color = MaterialTheme.colorScheme.onSecondary,
    onClick: () -> Unit,
    onDelete: (String) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(backgroundColor)
            .clickable(onClick = onClick)
            .padding(24.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = title,
            color = contentColor,
            style = MaterialTheme.typography.headlineMedium
                .copy(fontFeatureSettings = "c2sc, smcp"),
            textAlign = TextAlign.Start,
        )
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(R.drawable.trash)
                .decoderFactory(SvgDecoder.Factory())
                .build(),
            contentDescription = "Delete",
            modifier = Modifier
                .size(20.dp)
                .clickable {
                    onDelete(title)
                },
            contentScale = ContentScale.Fit
        )
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