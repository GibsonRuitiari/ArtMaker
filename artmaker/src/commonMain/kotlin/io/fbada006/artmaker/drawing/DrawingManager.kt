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
package io.fbada006.artmaker.drawing

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import io.fbada006.artmaker.actions.DrawEvent
import io.fbada006.artmaker.actions.UndoRedoEventType
import io.fbada006.artmaker.composables.LineStyle
import io.fbada006.artmaker.models.PointF
import io.fbada006.artmaker.models.PointsData
import io.fbada006.artmaker.utils.erasePointData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlin.properties.Delegates

/**
 * Handles all the logic related to drawing including clearing, undo, and redo
 */
internal class DrawingManager {
    private val undoStack = ArrayDeque<UndoRedoEventType>()
    private val redoStack = ArrayDeque<UndoRedoEventType>()

    private val _pathList = mutableStateListOf<PointsData>()
    val pathList: SnapshotStateList<PointsData> = _pathList

    private val _undoRedoState = MutableStateFlow(UndoRedoState())
    val undoRedoState: StateFlow<UndoRedoState> = _undoRedoState

    private var strokeWidth by Delegates.notNull<Int>()

    fun onDrawEvent(event: DrawEvent, strokeColor: Int, strokeWidth: Int, lineStyle: LineStyle) {
        when (event) {
            is DrawEvent.AddNewShape -> addNewShape(event.offset, strokeColor, strokeWidth, event.pressure, lineStyle)
            DrawEvent.UndoLastShapePoint -> undoLastShapePoint()
            is DrawEvent.UpdateCurrentShape -> updateCurrentShape(event.offset, event.pressure)
            DrawEvent.Clear -> clear()
            DrawEvent.Redo -> redo()
            DrawEvent.Undo -> undo()
            is DrawEvent.Erase -> erase(event.offset)
        }
    }

    private fun addNewShape(offset: Offset, strokeColor: Int, strokeWidth: Int, pressure: Float, lineStyle: LineStyle) {
        this.strokeWidth = strokeWidth
        val data = PointsData(
            points = mutableStateListOf(offset),
            strokeColor = Color(strokeColor),
            strokeWidth = strokeWidth.toFloat(),
            alphas = mutableStateListOf(pressure),
            lineStyle = lineStyle,
        )
        undoStack.addLast(UndoRedoEventType.BeforeErase(data))
        _pathList.add(data)
        redoStack.clear()
        _undoRedoState.update { computeUndoRedoState() }
    }

    private fun updateCurrentShape(offset: Offset, pressure: Float) {
        if (pathList.isNotEmpty()) {
            val idx = _pathList.lastIndex
            _pathList[idx].points.add(offset)
            _pathList[idx].alphas.add(pressure)
        }
    }

    private fun undoLastShapePoint() {
        val idx = _pathList.lastIndex
        _pathList[idx].points.removeLast()
    }

    private fun redo() {
        if (redoStack.isEmpty()) return
        when (val action = redoStack.removeLast()) {
            is UndoRedoEventType.BeforeErase -> {
                undoStack.addLast(UndoRedoEventType.BeforeErase(action.pathData))
                _pathList.add(action.pathData)
            }
            is UndoRedoEventType.AfterErase -> {
                undoStack.addLast(UndoRedoEventType.AfterErase(_pathList.toList(), action.newState))
                _pathList.clear()
                _pathList.addAll(action.newState)
            }
        }
        _undoRedoState.update { computeUndoRedoState() }
    }

    private fun undo() {
        if (undoStack.isEmpty()) return
        when (val action = undoStack.removeLast()) {
            is UndoRedoEventType.BeforeErase -> {
                redoStack.addLast(UndoRedoEventType.BeforeErase(_pathList.removeLast()))
            }
            is UndoRedoEventType.AfterErase -> {
                redoStack.addLast(UndoRedoEventType.AfterErase(_pathList.toList(), action.newState))
                _pathList.clear()
                _pathList.addAll(action.oldState)
            }
        }
        _undoRedoState.update { computeUndoRedoState() }
    }

    private fun clear() {
        _pathList.clear()
        redoStack.clear()
        undoStack.clear()
        _undoRedoState.update { computeUndoRedoState() }
    }

    private fun computeUndoRedoState(): UndoRedoState = UndoRedoState(
        canUndo = undoStack.isNotEmpty(),
        canRedo = redoStack.isNotEmpty(),
        canClear = _pathList.isNotEmpty() || undoStack.isNotEmpty() || redoStack.isNotEmpty(),
        canErase = _pathList.isNotEmpty(),
    )

    private fun erase(offset: Offset) {
        val erasedPoint = PointF(offset.x, offset.y)
        // Store the old state before erasing
        val oldPath = _pathList.toList()
        val newPath = erasePointData(
            pointsData = _pathList,
            erasedPoints = arrayOf(erasedPoint),
            eraseRadius = strokeWidth.toFloat(),
        )
        // Only push to undoStack if there's an actual change
        if (oldPath != newPath) {
            undoStack.addLast(UndoRedoEventType.AfterErase(oldPath, newPath))
            // Clear the existing points and add the new points
            _pathList.clear()
            _pathList.addAll(newPath)

            redoStack.clear()
            _undoRedoState.update { computeUndoRedoState() }
        }
    }
}

internal data class UndoRedoState(
    val canUndo: Boolean = false,
    val canRedo: Boolean = false,
    val canClear: Boolean = false,
    val canErase: Boolean = false,
)
