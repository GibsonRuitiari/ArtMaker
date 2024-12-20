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
package io.fbada006.artmaker.actions

import androidx.compose.ui.geometry.Offset

/**
 * Events that happen during drawing
 */
sealed interface DrawEvent {
    data class AddNewShape(val offset: Offset, val pressure: Float) : DrawEvent
    data class UpdateCurrentShape(val offset: Offset, val pressure: Float) : DrawEvent
    data object UndoLastShapePoint : DrawEvent
    data object Undo : DrawEvent
    data object Redo : DrawEvent
    data object Clear : DrawEvent
    data class Erase(val offset: Offset) : DrawEvent
}
