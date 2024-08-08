package com.vanillavideoplayer.hd.videoplayer.pro.core.ui.composviews

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.debugInspectorInfo
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import com.vanillavideoplayer.hd.videoplayer.pro.core.ui.R
import com.vanillavideoplayer.hd.videoplayer.pro.core.ui.designsystem.VanillaIcons
import com.vanillavideoplayer.hd.videoplayer.pro.core.ui.preview.LightDarkPrev


@Composable
fun VanillaPrefSwitchWithDividerView(
    s: String = "", desc: String? = null, imageVector: ImageVector? = null, enabled: Boolean = true, isChecked: Boolean = true, click: (() -> Unit) = {}, checked: () -> Unit = {}
) {
    VanillaPrefItem(header = s, desc = desc, vector = imageVector, modifier = Modifier.clickable(
        enabled = enabled, onClick = click
    ), boolEnabled = enabled, func = {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            HorizontalDivider(
                modifier = Modifier
                    .height(32.dp)
                    .padding(horizontal = 8.dp)
                    .width(1.dp), color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
            )
            VanillaSwitchView(
                checked = isChecked, onCheckedChange = { checked() }, modifier = Modifier.padding(start = 12.dp), enabled = enabled
            )
        }
    })
}

@Composable
fun RadioStringBtnView(
    s: String, selected: Boolean, function: () -> Unit, modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .selectable(
                selected = selected, onClick = function, role = Role.RadioButton
            )
            .padding(12.dp), verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selected, onClick = null
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = s)
    }
}

@Composable
fun VanillaPrefSwitchWithDividerPrev() {
    VanillaPrefSwitchWithDividerView(s = "Header", desc = "Description of the pref item will take place here.", imageVector = VanillaIcons.DoubleTap, click = {}, checked = {})
}

@Composable
fun VanillaPrefSwitchView(
    title: String, description: String? = null, icon: ImageVector? = null, enabled: Boolean = true, isChecked: Boolean = true, onClick: (() -> Unit) = {}
) {
    VanillaPrefItem(header = title, desc = description, vector = icon, modifier = Modifier.toggleable(value = isChecked, enabled = enabled, onValueChange = { onClick() }), boolEnabled = enabled, func = {
        VanillaSwitchView(
            checked = isChecked, onCheckedChange = null, modifier = Modifier.padding(start = 20.dp), enabled = enabled
        )
    })
}


@Composable
fun VanillaPrefItem(
    header: String, modifier: Modifier = Modifier, desc: String? = null, vector: ImageVector? = null, boolEnabled: Boolean, func: @Composable () -> Unit = {}
) {
    ListItem(leadingContent = {
        vector?.let {
            Column(
                modifier = Modifier.fillMaxHeight(), verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = vector, contentDescription = null, modifier = Modifier
                        .padding(horizontal = 12.dp)
                        .size(24.dp), tint = MaterialTheme.colorScheme.secondary.applyAlpha(boolEnabled)
                )
            }
        }
    }, headlineContent = {
        Text(
            text = header, maxLines = 1, style = MaterialTheme.typography.titleLarge.copy(fontSize = 20.sp), color = LocalContentColor.current.applyAlpha(boolEnabled)
        )
    }, supportingContent = {
        desc?.let {
            Text(
                text = it, maxLines = 2, overflow = TextOverflow.Ellipsis, style = MaterialTheme.typography.bodyMedium, color = LocalContentColor.current.applyAlpha(boolEnabled), modifier = Modifier.padding(top = 2.dp)
            )
        }
    }, trailingContent = func, modifier = modifier.padding(vertical = 10.dp)
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun VanillaChoosablePrefView(
    title: String, modifier: Modifier = Modifier, description: String? = null, selected: Boolean = false, onClick: () -> Unit = {}, onLongClick: () -> Unit = {}
) {
    ListItem(headlineContent = {
        Text(
            text = title, maxLines = 1, style = MaterialTheme.typography.titleMedium.copy(
                textDecoration = if (selected) TextDecoration.LineThrough else TextDecoration.None
            )
        )
    }, supportingContent = {
        description?.let {
            Text(
                text = it, maxLines = 2, overflow = TextOverflow.Ellipsis, style = MaterialTheme.typography.bodyMedium.copy(
                    textDecoration = if (selected) TextDecoration.LineThrough else TextDecoration.None
                )
            )
        }
    }, trailingContent = {
        Checkbox(
            modifier = Modifier.semantics { contentDescription = title }, checked = selected, onCheckedChange = null
        )
    }, modifier = modifier
        .combinedClickable(
            onClick = onClick, onLongClick = onLongClick
        )
        .padding(start = 10.dp)
        .padding(vertical = 2.dp)
    )
}

@Composable
fun VanillaPrefItemPrev() {
    VanillaPrefItem(
        header = "header", desc = "Description of the pref item take place here.", vector = VanillaIcons.DoubleTap, boolEnabled = true
    )
}

@Composable
fun VanillaSwitchView(
    checked: Boolean, onCheckedChange: ((Boolean) -> Unit)?, modifier: Modifier = Modifier, enabled: Boolean = true, checkedIcon: ImageVector = VanillaIcons.Check
) {
    val thumbContent: (@Composable () -> Unit)? = if (checked) {
        {
            Icon(
                imageVector = checkedIcon, contentDescription = null, modifier = Modifier.size(SwitchDefaults.IconSize)
            )
        }
    } else {
        null
    }

    Switch(
        checked = checked, onCheckedChange = onCheckedChange, modifier = modifier, enabled = enabled, thumbContent = thumbContent
    )
}


@Composable
fun VanillaChoosablePrefPrev() {
    VanillaChoosablePrefView(
        title = "header", description = "Description of the pref item take place here."
    )
}

internal fun Color.applyAlpha(enabled: Boolean): Color {
    return if (enabled) this else this.copy(alpha = 0.6f)
}

@Composable
fun VanillaPlayerDialogWithPositiveAndNagative(
    title: String, onDoneClick: () -> Unit, onDismissClick: () -> Unit, content: @Composable () -> Unit
) {
    VanillaPlayerDialog(title = { Text(text = title) }, confirmButton = { VanillaDoneButton(onClick = onDoneClick) }, dismissButton = { VanillaCancelButton(onClick = onDismissClick) }, onDismissRequest = onDismissClick, content = content
    )
}


@Composable
fun VanillaPlayerDialog(
    onDismissRequest: () -> Unit, title: @Composable () -> Unit, content: @Composable () -> Unit, confirmButton: @Composable () -> Unit, dismissButton: @Composable () -> Unit, modifier: Modifier = Modifier, dialogProperties: DialogProperties = VanillaPlayerDialogDefaults.dialogProperties
) {
    val configuration = LocalConfiguration.current

    AlertDialog(
        title = title, text = { Column { content() } }, modifier = modifier.widthIn(max = configuration.screenWidthDp.dp - VanillaPlayerDialogDefaults.dialogMargin * 2), onDismissRequest = onDismissRequest, confirmButton = confirmButton, dismissButton = dismissButton, properties = dialogProperties
    )
}

object VanillaPlayerDialogDefaults {
    val dialogProperties: DialogProperties = DialogProperties(
        usePlatformDefaultWidth = false, dismissOnBackPress = true, dismissOnClickOutside = true, decorFitsSystemWindows = true
    )
    val dialogMargin: Dp = 16.dp
}


fun Modifier.horizontalFadingEdge(
    scrollState: ScrollState,
    length: Dp,
    edgeColor: Color? = null,
) = composed(debugInspectorInfo {
    name = "length"
    value = length
}) {
    val color = edgeColor ?: MaterialTheme.colorScheme.surface

    drawWithContent {
        val lengthValue = length.toPx()
        val scrollFromStart = scrollState.value
        val scrollFromEnd = scrollState.maxValue - scrollState.value

        val startFadingEdgeStrength = lengthValue * (scrollFromStart / lengthValue).coerceAtMost(1f)

        val endFadingEdgeStrength = lengthValue * (scrollFromEnd / lengthValue).coerceAtMost(1f)

        drawContent()

        drawRect(
            brush = Brush.horizontalGradient(
                colors = listOf(
                    color,
                    Color.Transparent,
                ),
                startX = 0f,
                endX = startFadingEdgeStrength,
            ),
            size = Size(
                startFadingEdgeStrength,
                this.size.height,
            ),
        )

        drawRect(
            brush = Brush.horizontalGradient(
                colors = listOf(
                    Color.Transparent,
                    color,
                ),
                startX = size.width - endFadingEdgeStrength,
                endX = size.width,
            ),
            topLeft = Offset(x = size.width - endFadingEdgeStrength, y = 0f),
        )
    }
}


@Composable
fun VanillaDoneButton(
    onClick: () -> Unit, modifier: Modifier = Modifier, enabled: Boolean = true
) {
    TextButton(
        enabled = enabled, onClick = onClick, modifier = modifier
    ) {
        Text(text = stringResource(R.string.done))
    }
}

@Composable
fun VanillaCancelButton(
    onClick: () -> Unit, modifier: Modifier = Modifier, enabled: Boolean = true
) {
    TextButton(
        enabled = enabled, onClick = onClick, modifier = modifier
    ) {
        Text(text = stringResource(R.string.cancel))
    }
}

fun Modifier.verticalFadingEdge(
    scrollState: ScrollState,
    length: Dp,
    edgeColor: Color? = null,
) = composed(debugInspectorInfo {
    name = "length"
    value = length
})
{
    val color = edgeColor ?: MaterialTheme.colorScheme.surface

    drawWithContent {
        val lengthValue = length.toPx()
        val scrollFromTop = scrollState.value
        val scrollFromBottom = scrollState.maxValue - scrollState.value

        val topFadingEdgeStrength = lengthValue * (scrollFromTop / lengthValue).coerceAtMost(1f)

        val bottomFadingEdgeStrength = lengthValue * (scrollFromBottom / lengthValue).coerceAtMost(1f)

        drawContent()

        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    color,
                    Color.Transparent,
                ),
                startY = 0f,
                endY = topFadingEdgeStrength,
            ),
            size = Size(
                this.size.width, topFadingEdgeStrength
            ),
        )

        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color.Transparent,
                    color,
                ),
                startY = size.height - bottomFadingEdgeStrength,
                endY = size.height,
            ),
            topLeft = Offset(x = 0f, y = size.height - bottomFadingEdgeStrength),
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun VanillaClickablePrefItem(
    title: String, modifier: Modifier = Modifier, description: String? = null, enabled: Boolean = true, icon: ImageVector? = null, onClick: () -> Unit = {}, onLongClick: () -> Unit = {}
) {
    VanillaPrefItem(
        header = title, desc = description, vector = icon, boolEnabled = enabled, modifier = modifier.combinedClickable(
            onClick = onClick, enabled = enabled, onLongClick = onLongClick
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VanillaPlayerCenterAlignedTopBar(
    s: String,
    modifier: Modifier = Modifier,
    icon: @Composable () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {},
    barColors: TopAppBarColors = TopAppBarDefaults.centerAlignedTopAppBarColors(
        containerColor = Color.Transparent, // Set the background color to transparent
        scrolledContainerColor = Color.Transparent // Ensure it stays transparent when scrolled
    ),
    topAppBarScrollBehavior: TopAppBarScrollBehavior? = null
) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = s, fontWeight = FontWeight.Bold
            )
        },
        navigationIcon = icon,
        actions = actions,
        colors = barColors,
        modifier = modifier,
        scrollBehavior = topAppBarScrollBehavior
    )
}

@OptIn(ExperimentalMaterial3Api::class)

@Composable
private fun VanillaPlayerMainTopBarPrev() {
    VanillaPlayerCenterAlignedTopBar(s = "Vanilla Player Pro", actions = {
        IconButton(onClick = {}) {
            Icon(
                imageVector = Icons.Rounded.Settings, contentDescription = "Settings"
            )
        }
    })
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VanillaPlayerTopBar(
    s: String,
    modifier: Modifier = Modifier,
    icon: @Composable () -> Unit = {},
    function: @Composable RowScope.() -> Unit = {},
    topAppBarColors: TopAppBarColors = TopAppBarDefaults.topAppBarColors(),
    behavior: TopAppBarScrollBehavior? = null
) {
    TopAppBar(
        title = {
            Text(
                text = s, maxLines = 1, overflow = TextOverflow.Ellipsis
            )
        },
        navigationIcon = icon,
        actions = function,
        colors = topAppBarColors,
        modifier = modifier,
        scrollBehavior = behavior
    )
}

@Composable
private fun VanillaClickablePrefItemPrev() {
    VanillaClickablePrefItem(
        title = "header", description = "Description of the pref item take place here.", icon = VanillaIcons.DoubleTap, onClick = {}, enabled = false
    )
}


@Composable
fun VanillaPrefSwitchPrev() {
    VanillaPrefSwitchView(title = "header", description = "Description of the pref item take place here.", icon = VanillaIcons.DoubleTap, onClick = {})
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun VanillaPrefCheckbox(
    s: String, desc: String? = null, vector: ImageVector? = null, boolEnabled: Boolean = true, boolChecked: Boolean = true, click: (() -> Unit) = {}, longClick: (() -> Unit) = {}
) {
    VanillaPrefItem(header = s, desc = desc, vector = vector, modifier = Modifier
        .toggleable(value = boolChecked, enabled = boolEnabled, onValueChange = { click() })
        .combinedClickable(
            onClick = click, onLongClick = longClick
        ), boolEnabled = boolEnabled, func = {
        Checkbox(
            checked = boolChecked, onCheckedChange = null
        )
    })
}