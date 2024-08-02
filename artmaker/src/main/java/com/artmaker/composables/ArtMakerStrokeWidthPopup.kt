package com.artmaker.composables

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties

@Composable
internal fun ArtMakerStrokeWidthPopup() {
    Dialog(onDismissRequest = { /*TODO*/ }, properties = DialogProperties(
        dismissOnClickOutside = true,
        dismissOnBackPress = true
    )) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(210.dp)
                .padding(14.dp),
            shape = RoundedCornerShape(21.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
        ) {
            ArtMakerStrokeWidthSlider()
        }
    }
}