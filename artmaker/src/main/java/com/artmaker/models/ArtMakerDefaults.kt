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
package com.artmaker.models

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb

data class ArtMakerConfiguration(
    val sliderThumbColor: Color = Color.Unspecified,
    val sliderActiveTrackColor: Color = Color.Unspecified,
    val sliderInactiveTickColor: Color = Color.Unspecified,
    val sliderTextColor: Color = Color.Unspecified,
    val colors: List<Color> = emptyList(),
    val backgroundColor: Int = Color.White.toArgb(),
    val controllerBackground: Color = Color.Unspecified,
    val sliderBackground: Color = Color.Unspecified,
)
