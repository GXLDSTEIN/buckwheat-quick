package com.danilkinkin.buckwheat.editor

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.danilkinkin.buckwheat.R
import com.danilkinkin.buckwheat.data.ExtendCurrency
import com.danilkinkin.buckwheat.data.SpendsViewModel
import com.danilkinkin.buckwheat.util.numberFormat
import com.danilkinkin.buckwheat.util.prettyDate
import java.math.BigDecimal

@Composable
fun BudgetBalances(spendsViewModel: SpendsViewModel = hiltViewModel()) {
    val budget by spendsViewModel.budget.observeAsState(BigDecimal.ZERO)
    val spent by spendsViewModel.spent.observeAsState(BigDecimal.ZERO)
    val daily by spendsViewModel.dailyBudget.observeAsState(BigDecimal.ZERO)
    val spentToday by spendsViewModel.spentFromDailyBudget.observeAsState(BigDecimal.ZERO)
    val finish by spendsViewModel.finishPeriodDate.observeAsState()
    val currency by spendsViewModel.currency.observeAsState(ExtendCurrency.none())
    val context = LocalContext.current
    if (finish == null) return

    Row(
        Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Balance(
            stringResource(R.string.quick_left_today),
            daily - spentToday,
            { numberFormat(context, it, currency = currency) },
            Modifier.weight(1f),
        )
        Balance(
            stringResource(R.string.quick_left_period, prettyDate(finish!!, "d MMM", simplifyIfToday = false)),
            budget - spent - spentToday,
            { numberFormat(context, it, currency = currency) },
            Modifier.weight(1f),
        )
    }
}

@Composable
private fun Balance(label: String, amount: BigDecimal, format: (BigDecimal) -> String, modifier: Modifier) {
    Column(modifier) {
        Text(label, style = MaterialTheme.typography.labelMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
        Text(
            format(amount),
            style = MaterialTheme.typography.titleLarge,
            color = if (amount.signum() < 0) MaterialTheme.colorScheme.error else LocalContentColor.current,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}
