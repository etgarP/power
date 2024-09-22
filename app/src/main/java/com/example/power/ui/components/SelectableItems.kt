package com.example.power.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

/**
 * a selectable item with a picture and text
 * also has a selected or not selected icon
 */
@Composable
fun ItemWithPicture(
    modifier: Modifier = Modifier,
    selected: Boolean = true,
    enableImage: Boolean = true,
    text: String = "",
    @DrawableRes imageId: Int,
    onClick: () -> Unit = {}
) {
    val color by animateColorAsState( // animates the color of the button
        if (selected) MaterialTheme.colorScheme.surfaceVariant
        else MaterialTheme.colorScheme.surface, label = ""
    )
    Button(
        modifier = modifier
            .fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(containerColor = color),
        contentPadding = PaddingValues(0.dp),
        onClick = { onClick() },
        shape = RoundedCornerShape(15.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.background(color)
        ) {
            if (enableImage)
                Image( // the image of the button
                    modifier = Modifier
                        .height(70.dp)
                        .width(90.dp),
                    painter = painterResource(id = imageId),
                    contentDescription = "",
                    contentScale = ContentScale.Crop,
                )
            Text( // the text of the button
                modifier = Modifier
                    .padding(vertical = 20.dp, horizontal = 15.dp)
                    .weight(1f),
                text = text,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyLarge
            )
            if (selected)
                Icon( // checked circle
                    imageVector = Icons.Filled.CheckCircle,
                    contentDescription = "",
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(16.dp)
                )
            else
                Icon( // not checked circle
                    imageVector = Icons.Outlined.CheckCircle,
                    contentDescription = "",
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(16.dp)
                )
        }
    }
}

/**
 * small selectable item with text
 */
@Composable
fun smallSelectableItem(
    modifier: Modifier = Modifier,
    selected: Boolean = true,
    text: String = "",
    onClick: () -> Unit = {}
) {
    val color by animateColorAsState( // selected and unselected icon
        if (selected) MaterialTheme.colorScheme.surfaceVariant
        else MaterialTheme.colorScheme.surface, label = ""
    )
    Row(modifier, horizontalArrangement = Arrangement.Center) {
        Button(
            modifier = Modifier.width(100.dp),
            colors = ButtonDefaults.buttonColors(containerColor = color),
            contentPadding = PaddingValues(0.dp),
            onClick = { onClick() },
            shape = RoundedCornerShape(15.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.background(color) // setting the color
            ) {
                Text( // the button text
                    modifier = Modifier
                        .padding(vertical = 20.dp, horizontal = 15.dp)
                        .weight(1f),
                    text = text,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }

}