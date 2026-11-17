package com.danilkinkin.buckwheat.analytics

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import com.danilkinkin.buckwheat.R
import com.danilkinkin.buckwheat.data.ExtendCurrency
import com.danilkinkin.buckwheat.data.SpendsViewModel
import com.danilkinkin.buckwheat.data.entities.Transaction
import com.danilkinkin.buckwheat.data.entities.TransactionType
import com.danilkinkin.buckwheat.util.numberFormat
import com.danilkinkin.buckwheat.util.prettyDate
import java.math.BigDecimal

const val CATEGORY_EXPENSES = "categoryExpenses"

fun expensesForCategory(spends: List<Transaction>, categoryId: String?): List<Transaction> =
    spends.filter { it.type == TransactionType.SPENT && it.categoryId == categoryId }.sortedByDescending { it.date }

@Composable
fun CategoryExpensesDialog(
    categoryId: String?,
    onDismiss: () -> Unit,
    spendsViewModel: SpendsViewModel = hiltViewModel(),
) {
    val categories by spendsViewModel.categories.observeAsState(emptyList())
    val spends by spendsViewModel.spends.observeAsState(emptyList())
    val currency by spendsViewModel.currency.observeAsState(ExtendCurrency.none())
    val title = categories.find { it.id == categoryId }?.name ?: stringResource(R.string.quick_uncategorized)
    val filtered = remember(spends, categoryId) { expensesForCategory(spends, categoryId) }
    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        CategoryExpensesContent(title, filtered, currency, onDismiss)
    }
}

@Composable
fun CategoryExpensesContent(
    title: String,
    transactions: List<Transaction>,
    currency: ExtendCurrency,
    onDismiss: () -> Unit,
) {
    val context = LocalContext.current
    val total = transactions.fold(BigDecimal.ZERO) { sum, expense -> sum + expense.value }
    Surface(Modifier.fillMaxSize()) {
        Column(Modifier.safeDrawingPadding()) {
            Row(Modifier.fillMaxWidth().padding(8.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(title, style = MaterialTheme.typography.headlineSmall, maxLines = 2,
                    modifier = Modifier.weight(1f).padding(12.dp))
                IconButton(onClick = onDismiss) {
                    Icon(painterResource(R.drawable.ic_close), stringResource(android.R.string.cancel))
                }
            }
            Text(stringResource(R.string.quick_category_period), modifier = Modifier.padding(horizontal = 20.dp),
                style = MaterialTheme.typography.labelMedium)
            Text(numberFormat(context, total, currency = currency), modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
                style = MaterialTheme.typography.headlineMedium)
            Text(stringResource(R.string.quick_expense_count, transactions.size), modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp))
            if (transactions.isEmpty()) {
                Text(stringResource(R.string.quick_category_empty), modifier = Modifier.padding(20.dp))
            }
            LazyColumn(Modifier.weight(1f).fillMaxWidth(), contentPadding = PaddingValues(bottom = 24.dp)) {
                items(transactions, key = { it.uid }) { transaction ->
                    Column(Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 16.dp)) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            Text(numberFormat(context, transaction.value, currency = currency),
                                modifier = Modifier.weight(1f), style = MaterialTheme.typography.titleLarge,
                                maxLines = 1, overflow = TextOverflow.Ellipsis)
                            Text(prettyDate(transaction.date, shortMonth = true), style = MaterialTheme.typography.labelMedium)
                        }
                        if (transaction.comment.isNotBlank()) {
                            Text(transaction.comment, modifier = Modifier.padding(top = 8.dp), style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                    HorizontalDivider()
                }
            }
        }
    }
}
