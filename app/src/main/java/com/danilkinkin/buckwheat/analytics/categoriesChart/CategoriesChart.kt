package com.danilkinkin.buckwheat.analytics.categoriesChart

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.livedata.observeAsState
import androidx.hilt.navigation.compose.hiltViewModel
import com.danilkinkin.buckwheat.data.SpendsViewModel
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.danilkinkin.buckwheat.R
import com.danilkinkin.buckwheat.data.ExtendCurrency
import com.danilkinkin.buckwheat.data.entities.Transaction
import com.danilkinkin.buckwheat.data.entities.TransactionType
import com.danilkinkin.buckwheat.ui.BuckwheatTheme
import com.danilkinkin.buckwheat.ui.isNightMode
import com.danilkinkin.buckwheat.util.HarmonizedColorPalette
import com.danilkinkin.buckwheat.util.combineColors
import com.danilkinkin.buckwheat.util.harmonize
import com.danilkinkin.buckwheat.util.harmonizeWithColor
import com.danilkinkin.buckwheat.util.toPalette
import java.math.BigDecimal
import java.util.Date

data class TagUsage(
    val name: String,
    val amount: BigDecimal,
    var color: HarmonizedColorPalette? = null,
    var isSpecial: Boolean = false,
    val categoryId: String? = null,
)

var baseColors = listOf(
    Color(0xFFF86BAE),
    Color(0xFFF36FFF),
    Color(0xFFAB96FF),
    Color(0xFF5FC7E7),
    Color(0xFF75E584),
    Color(0xFFFFD386),
    Color(0xFFEF7564),
)

@OptIn(ExperimentalLayoutApi::class, ExperimentalStdlibApi::class)
@Composable
fun CategoriesChartCard(
    modifier: Modifier = Modifier,
    spends: List<Transaction>,
    currency: ExtendCurrency,
    spendsViewModel: SpendsViewModel = hiltViewModel(),
) {
    val isNightMode = isNightMode()
    val labelWithoutTag = stringResource(R.string.quick_uncategorized)
    val categories by spendsViewModel.categories.observeAsState(emptyList())
    val labelRest = stringResource(R.string.rest_tags)
    val maxDisplay = 7

    val colors = baseColors.map {
        toPalette(
            color = harmonizeWithColor(
                designColor = it,
                sourceColor = MaterialTheme.colorScheme.primary
            ),
        )
    }
    val restColor = toPalette(
        color = harmonize(
            designColor = Color(0xFF222222),
            sourceColor = MaterialTheme.colorScheme.primary
        ),
    ).copy(
        main = if (isNightMode) Color(0xFFF0F0F0) else Color(0xFF222222),
        onSurface = if (isNightMode) Color(0xFF1A1A1A) else Color(0xFFF4F4F4)
    )
    val appViewModel: com.danilkinkin.buckwheat.data.AppViewModel = hiltViewModel()
    val tags = remember(spends, categories, labelWithoutTag, colors, restColor) {
        spends.groupBy { it.categoryId }.map { (id, expenses) ->
            TagUsage(
                name = categories.find { it.id == id }?.name ?: labelWithoutTag,
                amount = expenses.fold(BigDecimal.ZERO) { sum, expense -> sum + expense.value },
                isSpecial = id == null,
                categoryId = id,
            )
        }.sortedByDescending { it.amount }.mapIndexed { index, tag ->
            tag.copy(color = if (tag.isSpecial) restColor else colors[index % colors.size])
        }
    }
    val chartTags = if (tags.size <= maxDisplay) tags else tags.take(maxDisplay - 1) + TagUsage(
        name = labelRest,
        amount = tags.drop(maxDisplay - 1).fold(BigDecimal.ZERO) { sum, tag -> sum + tag.amount },
        color = restColor,
        isSpecial = true,
    )

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = combineColors(
                MaterialTheme.colorScheme.surface,
                MaterialTheme.colorScheme.surfaceVariant,
                angle = 0.3f,
            ),
        )
    ) {
        if (chartTags.isNotEmpty() && chartTags.fold(BigDecimal.ZERO) { sum, tag -> sum + tag.amount } > BigDecimal.ZERO) {
            DonutChart(
                modifier = Modifier.padding(16.dp).size(64.dp),
                items = chartTags,
            )
        }
        Text(
            stringResource(R.string.quick_category_open_hint),
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            style = MaterialTheme.typography.bodySmall,
        )
        FlowRow(Modifier.padding(4.dp)) {
            tags.forEach { tag ->
                TagAmount(
                    modifier = Modifier.padding(4.dp),
                    value = tag.name,
                    amount = tag.amount,
                    palette = tag.color,
                    isSpecial = tag.isSpecial,
                    currency = currency,
                    onClick = {
                        appViewModel.openSheet(com.danilkinkin.buckwheat.data.PathState(
                            com.danilkinkin.buckwheat.analytics.CATEGORY_EXPENSES,
                            mapOf("categoryId" to tag.categoryId),
                        ))
                    },
                )
            }
        }
    }
}
