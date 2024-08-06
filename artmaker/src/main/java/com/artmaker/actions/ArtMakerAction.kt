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
package com.artmaker.actions

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap

/**
 * Define all of the user's actions
 */
sealed interface ArtMakerAction {
    data object TriggerArtExport : ArtMakerAction
    data class ExportArt(val bitmap: ImageBitmap) : ArtMakerAction
    data object Undo : ArtMakerAction
    data object Redo : ArtMakerAction
    data object Clear : ArtMakerAction
    data object UpdateBackground : ArtMakerAction
    data class SelectStrokeColour(val color: Color) : ArtMakerAction

    data class SelectStrokeWidth(val strokeWidth: Int) : ArtMakerAction
}