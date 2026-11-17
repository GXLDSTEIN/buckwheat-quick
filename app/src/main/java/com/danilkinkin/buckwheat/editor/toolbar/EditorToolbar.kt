package com.danilkinkin.buckwheat.editor.toolbar

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.danilkinkin.buckwheat.R
import com.danilkinkin.buckwheat.base.BigIconButton
import com.danilkinkin.buckwheat.data.AppViewModel
import com.danilkinkin.buckwheat.data.PathState
import com.danilkinkin.buckwheat.data.SpendsViewModel
import com.danilkinkin.buckwheat.editor.EditMode
import com.danilkinkin.buckwheat.editor.EditStage
import com.danilkinkin.buckwheat.editor.EditorViewModel
import com.danilkinkin.buckwheat.editor.toolbar.restBudgetPill.RestBudgetPill
import com.danilkinkin.buckwheat.settings.SETTINGS_SHEET
import com.danilkinkin.buckwheat.util.observeLiveData
import kotlinx.coroutines.launch

@Composable
fun EditorToolbar(
    onOpenHistory: (() -> Unit)? = null,
    spendsViewModel: SpendsViewModel = hiltViewModel(),
    appViewModel: AppViewModel = hiltViewModel(),
    editorViewModel: EditorViewModel = hiltViewModel(),
) {
    val coroutineScope = rememberCoroutineScope()
    val isDebug = appViewModel.isDebug.observeAsState(false)
    val mode by editorViewModel.mode.observeAsState(EditMode.ADD)

    val spendsCountScale = remember { Animatable(1f) }

    observeLiveData(editorViewModel.stage) {
        if (it === EditStage.COMMITTING_SPENT) {
            coroutineScope.launch {
                spendsCountScale.animateTo(
                    1.05f,
                    animationSpec = tween(
                        durationMillis = 20,
                        easing = LinearEasing
                    )
                )
                spendsCountScale.animateTo(
                    1f,
                    animationSpec = tween(
                        durationMillis = 120,
                        easing = LinearEasing,
                    )
                )
            }
        }
    }

    Row(
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = if (isDebug.value) 6.dp else 20.dp, end = 6.dp, top = 6.dp)
            .statusBarsPadding(),
    ) {
        if (isDebug.value) {
            BigIconButton(
                icon = painterResource(R.drawable.ic_developer_mode),
                contentDescription = null,
                onClick = { appViewModel.openSheet(PathState(DEBUG_MENU_SHEET)) },
            )
        }
        Spacer(modifier = Modifier.width(4.dp))
        if (mode == EditMode.EDIT) {
            CancelEditSpent()
        } else {
            androidx.compose.material3.Text(
                text = androidx.compose.ui.res.stringResource(R.string.app_name),
                style = androidx.compose.material3.MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f),
                maxLines = 1,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
            )
            BigIconButton(
                icon = painterResource(R.drawable.ic_analytics),
                contentDescription = androidx.compose.ui.res.stringResource(R.string.view_analytics),
                onClick = { appViewModel.openSheet(PathState(com.danilkinkin.buckwheat.analytics.ANALYTICS_SHEET)) },
            )
            if (onOpenHistory != null) {
                BigIconButton(
                    icon = painterResource(R.drawable.ic_clock),
                    contentDescription = androidx.compose.ui.res.stringResource(R.string.history_title),
                    onClick = onOpenHistory,
                )
            }
            BigIconButton(
                icon = painterResource(R.drawable.ic_balance_wallet),
                contentDescription = androidx.compose.ui.res.stringResource(R.string.quick_open_budget),
                onClick = { appViewModel.openSheet(PathState(com.danilkinkin.buckwheat.wallet.WALLET_SHEET)) },
            )
        }
        Spacer(modifier = Modifier.width(4.dp))
        BigIconButton(
            icon = painterResource(R.drawable.ic_settings),
            contentDescription = null,
            onClick = {
                appViewModel.openSheet(PathState(SETTINGS_SHEET))
            },
        )
    }
}
