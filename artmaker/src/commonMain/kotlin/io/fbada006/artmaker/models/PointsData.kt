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
package io.fbada006.artmaker.models

import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import io.fbada006.artmaker.composables.LineStyle

/**
 * This class will hold each shape drawn on screen be it a single dot or multiple shapes drawn
 * on screen.
 */
internal data class PointsData(
    var points: SnapshotStateList<Offset>,
    val strokeWidth: Float = 15f,
    val strokeColor: Color,
    val alphas: MutableList<Float>,
    val lineStyle: LineStyle,

)

/**
 * Return the Alpha value of the shape drawn depending on whether Pressure Detection has been activated or not.
 * The alpha will always be 1 during no pressure detection.
 *
 * @param detectPressure Represents the Pressure Detection state (On or Off).
 *
 * @return the alpha
 */
internal fun PointsData.alpha(detectPressure: Boolean): Float = if (detectPressure) {
    this.alphas.average().coerceAtLeast(0.0).coerceAtMost(1.0).toFloat()
} else {
    1.0f
}
