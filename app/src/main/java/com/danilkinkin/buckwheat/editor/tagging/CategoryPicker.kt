package com.danilkinkin.buckwheat.editor.tagging

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.danilkinkin.buckwheat.R
import com.danilkinkin.buckwheat.data.entities.Category

@Composable
fun CategoryPicker(
    categories: List<Category>,
    selectedId: String?,
    onSelect: (String?) -> Unit,
    onCreate: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    var query by rememberSaveable { mutableStateOf("") }
    val matches = remember(categories, query) {
        categories.filter { it.name.contains(query.trim(), ignoreCase = true) }
    }
    val cellWidth = 104.dp * LocalDensity.current.fontScale.coerceAtLeast(1f)
    val uncategorized = stringResource(R.string.quick_uncategorized)
    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Surface(Modifier.fillMaxSize()) {
            Column(Modifier.safeDrawingPadding().imePadding()) {
                Row(Modifier.fillMaxWidth().padding(8.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(stringResource(R.string.quick_categories), style = MaterialTheme.typography.headlineSmall, modifier = Modifier.padding(12.dp))
                    IconButton(onClick = onDismiss) {
                        Icon(painterResource(R.drawable.ic_close), stringResource(android.R.string.cancel))
                    }
                }
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    label = { Text(stringResource(R.string.quick_search_categories)) },
                    leadingIcon = { Icon(painterResource(R.drawable.ic_search), null) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                )
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(cellWidth),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                ) {
                    if (query.isBlank() || uncategorized.contains(query.trim(), ignoreCase = true)) {
                        item(key = "uncategorized") {
                            CategoryChoice(uncategorized, selectedId == null) { onSelect(null) }
                        }
                    }
                    items(matches, key = { it.id }) { category ->
                        CategoryChoice(category.name, selectedId == category.id) { onSelect(category.id) }
                    }
                    if (matches.isEmpty() && query.isNotBlank() && !uncategorized.contains(query.trim(), ignoreCase = true)) {
                        item(span = { GridItemSpan(maxLineSpan) }) {
                            Text(stringResource(R.string.quick_no_categories), modifier = Modifier.padding(vertical = 16.dp))
                        }
                    }
                }
                TextButton(onClick = { onCreate(query.trim()) }, modifier = Modifier.fillMaxWidth().padding(8.dp)) {
                    Text(stringResource(R.string.quick_add_category))
                }
            }
        }
    }
}

@Composable
private fun CategoryChoice(name: String, selected: Boolean, onClick: () -> Unit) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(name, maxLines = 4, overflow = TextOverflow.Ellipsis) },
        modifier = Modifier.fillMaxWidth().heightIn(min = 64.dp),
    )
}
