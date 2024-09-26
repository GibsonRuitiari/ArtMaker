/*
 * Copyright 2024 ArtMaker
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.artmaker.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.fbada006.shared.actions.ArtMakerAction
import io.artmaker.models.ArtMakerConfiguration
import io.artmaker.utils.isStylusConnected
import io.fbada006.artmaker.R

@Composable
fun StrokeSettings(
    strokeWidth: Int,
    shouldUseStylusOnly: Boolean,
    shouldDetectPressure: Boolean,
    onAction: (com.fbada006.shared.actions.ArtMakerAction) -> Unit,
    configuration: ArtMakerConfiguration,
    modifier: Modifier = Modifier,
) {
    var sliderPosition by remember { mutableIntStateOf(strokeWidth) }
    var stylusOnly by remember { mutableStateOf(shouldUseStylusOnly) }
    var detectPressure by remember { mutableStateOf(shouldDetectPressure) }

    Column(modifier = modifier, verticalArrangement = Arrangement.SpaceEvenly) {
        Slider(
            sliderPosition = sliderPosition.toFloat(),
            onValueChange = {
                sliderPosition = it.toInt()
                onAction(com.fbada006.shared.actions.ArtMakerAction.SetStrokeWidth(strokeWidth = sliderPosition))
            },
            configuration = configuration,
            modifier = Modifier.fillMaxWidth(),
        )
        // Only show these setting if there is a stylus connected
        if (isStylusConnected(LocalContext.current)) {
            HorizontalDivider()
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(R.string.use_stylus_only),
                    style = MaterialTheme.typography.bodyLarge,
                )
                Switch(
                    checked = stylusOnly,
                    onCheckedChange = {
                        stylusOnly = !stylusOnly
                        onAction(com.fbada006.shared.actions.ArtMakerAction.UpdateSetStylusOnly(shouldUseStylusOnly = stylusOnly))
                    },
                )
            }
            HorizontalDivider()
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(R.string.enable_pressure_detection),
                    style = MaterialTheme.typography.bodyLarge,
                )
                Switch(
                    checked = detectPressure,
                    onCheckedChange = {
                        detectPressure = !detectPressure
                        onAction(com.fbada006.shared.actions.ArtMakerAction.UpdateSetPressureDetection(shouldDetectPressure = detectPressure))
                    },
                )
            }
        }
    }
}
