package com.danilkinkin.buckwheat.editor.tagging

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.danilkinkin.buckwheat.R
import com.danilkinkin.buckwheat.data.SpendsViewModel
import com.danilkinkin.buckwheat.editor.EditorViewModel
import com.danilkinkin.buckwheat.editor.FocusController

@Composable
fun TaggingToolbar(
    spendsViewModel: SpendsViewModel = hiltViewModel(),
    editorViewModel: EditorViewModel = hiltViewModel(),
    editorFocusController: FocusController,
) {
    val categories by spendsViewModel.categories.observeAsState(emptyList())
    val spends by spendsViewModel.spends.observeAsState(emptyList())
    val selected by editorViewModel.currentCategoryId.observeAsState()
    val comment by editorViewModel.currentComment.observeAsState("")
    val usage = remember(spends) { spends.groupingBy { it.categoryId }.eachCount() }
    val sorted = remember(categories, usage) { categories.sortedByDescending { usage[it.id] ?: 0 } }
    var showCategoryDialog by remember { mutableStateOf(false) }
    var showCommentDialog by remember { mutableStateOf(false) }
    var categoryName by remember { mutableStateOf("") }
    var commentDraft by remember { mutableStateOf("") }

    Column(Modifier.fillMaxWidth()) {
        Row(
            Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            FilterChip(
                selected = selected == null,
                onClick = { editorViewModel.currentCategoryId.value = null },
                label = { Text(stringResource(R.string.quick_uncategorized)) },
            )
            sorted.forEach { category ->
                FilterChip(
                    selected = selected == category.id,
                    onClick = { editorViewModel.currentCategoryId.value = category.id },
                    label = { Text(category.name) },
                )
            }
            AssistChip(
                onClick = { categoryName = ""; showCategoryDialog = true },
                label = { Text(stringResource(R.string.quick_add_category)) },
            )
        }
        TextButton(
            onClick = { commentDraft = comment; showCommentDialog = true },
            modifier = Modifier.padding(horizontal = 12.dp),
            contentPadding = PaddingValues(horizontal = 8.dp),
        ) {
            Text(
                text = comment.ifEmpty { stringResource(R.string.quick_add_comment) },
                maxLines = 1,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
            )
        }
    }

    if (showCategoryDialog) {
        val existing = categories.find { it.name.equals(categoryName.trim(), ignoreCase = true) }
        AlertDialog(
            onDismissRequest = { showCategoryDialog = false; editorFocusController.focus() },
            title = { Text(stringResource(R.string.quick_add_category)) },
            text = {
                OutlinedTextField(
                    value = categoryName,
                    onValueChange = { categoryName = it.take(40) },
                    label = { Text(stringResource(R.string.quick_category_name)) },
                    singleLine = true,
                )
            },
            confirmButton = {
                TextButton(
                    enabled = categoryName.isNotBlank(),
                    onClick = {
                        val category = existing ?: spendsViewModel.addCategory(categoryName)
                        editorViewModel.currentCategoryId.value = category.id
                        showCategoryDialog = false
                        editorFocusController.focus()
                    },
                ) { Text(stringResource(android.R.string.ok)) }
            },
            dismissButton = {
                TextButton(onClick = { showCategoryDialog = false; editorFocusController.focus() }) {
                    Text(stringResource(android.R.string.cancel))
                }
            },
        )
    }
    if (showCommentDialog) {
        AlertDialog(
            onDismissRequest = { showCommentDialog = false; editorFocusController.focus() },
            title = { Text(stringResource(R.string.quick_comment)) },
            text = {
                OutlinedTextField(
                    value = commentDraft,
                    onValueChange = { commentDraft = it },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    maxLines = 5,
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    editorViewModel.currentComment.value = commentDraft.trim()
                    showCommentDialog = false
                    editorFocusController.focus()
                }) { Text(stringResource(android.R.string.ok)) }
            },
            dismissButton = {
                TextButton(onClick = { showCommentDialog = false; editorFocusController.focus() }) {
                    Text(stringResource(android.R.string.cancel))
                }
            },
        )
    }
}
