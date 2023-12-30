package com.example.power.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun GoodText(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
    placeholder: (@Composable () -> Unit)? = null,
    fontSize: TextUnit = MaterialTheme.typography.bodyLarge.fontSize
) {

    BasicTextField(modifier = modifier
        .background(
            MaterialTheme.colorScheme.surfaceVariant,
            RoundedCornerShape(30.dp),
        )
        .fillMaxWidth(),
        value = value,
        keyboardOptions = KeyboardOptions.Default.copy(
            imeAction = ImeAction.Search
        ),
        onValueChange = onValueChange,
        singleLine = true,
        cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
        textStyle = LocalTextStyle.current.copy(
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = fontSize
        ),
        decorationBox = { innerTextField ->
            Row(
                modifier,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (leadingIcon != null) leadingIcon()
                Box(
                    Modifier
                        .weight(1f)
                        .padding(horizontal = 15.dp)
                        .align(Alignment.CenterVertically)) {
                    if (value.isEmpty())
                        if (placeholder != null) {
                            placeholder()
                        }
                    innerTextField()
                }
                if (trailingIcon != null) trailingIcon()
            }
        }
    )
}

@Composable
fun SearchItem(
    searchVal: String,
    setVal: (String) -> Unit
) {
    GoodText(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
            .heightIn(20.dp),
        placeholder = { Text(
            text = "Search",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        ) },
        leadingIcon = {
            Icon(imageVector = Icons.Filled.Search,
                contentDescription = "Search Icon",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        trailingIcon = {
            if (searchVal != "")
                Icon(imageVector = Icons.Filled.Close,
                    contentDescription = "erase search text",
                    Modifier.clickable {
                        setVal("")
                    },
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
        },
        onValueChange = {
            setVal(it)
        },
        value = searchVal
    )
}

@Preview
@Composable
fun SearchPreview() {
    SearchItem(searchVal = "", setVal = { string -> })
}


@Composable
fun GoodTextField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
    placeholder: (@Composable () -> Unit)? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    fontSize: TextUnit = MaterialTheme.typography.bodyLarge.fontSize,
    backgroundColor: Color = MaterialTheme.colorScheme.surfaceVariant
) {

    BasicTextField(modifier = modifier
        .background(
            backgroundColor,
            RoundedCornerShape(5.dp),
        ),
        value = value,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        onValueChange = onValueChange,
        singleLine = true,
        cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
        textStyle = LocalTextStyle.current.copy(
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = fontSize
        ),
        decorationBox = {
            Column (
                modifier,
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ){
                Row(
                    Modifier.padding(5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    if (leadingIcon != null) leadingIcon()
                    Box{
                        if (value.isEmpty())
                            if (placeholder != null) {
                                placeholder()
                            }
                        it()
                    }
                    if (trailingIcon != null) trailingIcon()
                }
            }
        }
    )
}
@Preview(showBackground = true)
@Composable
fun PreviewGoodTextField() {
    GoodTextField(value = "3", onValueChange = {})
}
