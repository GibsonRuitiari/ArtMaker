package com.artmaker.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Redo
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

/**
 * We can add the controller as a constructor to [ArtMakerControlMenu]  composable and remove the function types.
 * As an alternative we could add the logic to the ArtMaker and leave the [ArtMakerControlMenu]
 * without any functionality.
 */
@Composable
internal fun ArtMakerControlMenu(
    onExportArt: () -> Unit,
    onUnDoActionClicked: () -> Unit,
    onRedoActionClicked: () -> Unit,
    onClearActionClicked: () -> Unit,
    onUpdateBackgroundActionClicked: () -> Unit,
    onColorSelected: () -> Unit
) {
    Row(
        modifier = Modifier.padding(12.dp),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        MenuItem(
            imageVector =Icons.Filled.Upload,
            onItemClicked = onExportArt,
            colorTint = MaterialTheme.colorScheme.primary
        )
        MenuItem(
            imageVector = Icons.Filled.Brush,
            onItemClicked = onColorSelected,
            colorTint = MaterialTheme.colorScheme.primary
        )
        MenuItem(
            imageVector = Icons.AutoMirrored.Filled.Undo,
            onItemClicked = onUnDoActionClicked,
            colorTint = MaterialTheme.colorScheme.primary
        )
        MenuItem(
            imageVector = Icons.AutoMirrored.Filled.Redo,
            onItemClicked = onRedoActionClicked,
            colorTint = MaterialTheme.colorScheme.primary
        )
        MenuItem(
            imageVector = Icons.Filled.Refresh,
            onItemClicked = onClearActionClicked,
            colorTint = MaterialTheme.colorScheme.primary
        )
        MenuItem(
            imageVector = Icons.Filled.UploadFile,
            onItemClicked = onUpdateBackgroundActionClicked,
            colorTint = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
private fun RowScope.MenuItem(
    imageVector: ImageVector,
    onItemClicked: () -> Unit,
    colorTint: Color,
) {
    val modifier = Modifier.size(28.dp)
    IconButton(
        onClick = onItemClicked, modifier = Modifier.weight(1f, true)
    ) {
        Icon(
            imageVector = imageVector,
            contentDescription = null,
            tint = colorTint,
            modifier = modifier
        )
    }
}