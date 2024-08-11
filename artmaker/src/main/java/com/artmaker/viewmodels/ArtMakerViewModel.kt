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
package com.artmaker.viewmodels

import android.app.Application
import android.content.Context
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.artmaker.actions.ArtMakerAction
import com.artmaker.actions.DrawEvent
import com.artmaker.models.ArtMakerDefaults
import com.artmaker.models.PointsData
import com.artmaker.sharedpreferences.ArtMakerSharedPreferences
import com.artmaker.sharedpreferences.PreferenceKeys
import com.artmaker.sharedpreferences.PreferenceKeys.SELECTED_STROKE_WIDTH
import com.artmaker.state.ArtMakerUIState
import com.artmaker.utils.saveToDisk
import com.artmaker.utils.shareBitmap
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Stack

internal class ArtMakerViewModel(
    private val preferences: ArtMakerSharedPreferences,
    private val applicationContext: Context,
    private val defaults: ArtMakerDefaults
) : ViewModel() {

    private var _artMakerUIState = MutableStateFlow(
        value = if (preferences.contains(PreferenceKeys.SELECTED_STROKE_COLOUR)){
            ArtMakerUIState(
                strokeColour = preferences.get(
                    PreferenceKeys.SELECTED_STROKE_COLOUR,
                    defaults.strokeColor.toArgb(),
                ),
                strokeWidth = preferences.get(
                    SELECTED_STROKE_WIDTH,
                    defaults.strokeWidth.toInt(),
                ),
            )
        } else {

            ArtMakerUIState(
                strokeColour = defaults.strokeColor.toArgb(),
                strokeWidth =0
            )
        }
    )
    val artMakerUIState = _artMakerUIState.asStateFlow()

    private val undoStack = Stack<PointsData>()

    private val _pathList = mutableStateListOf<PointsData>()
    val pathList: SnapshotStateList<PointsData> = _pathList

    private val _shouldTriggerArtExport = MutableStateFlow(false)
    val shouldTriggerArtExport: StateFlow<Boolean> = _shouldTriggerArtExport

    private val _imageBitmap: MutableState<ImageBitmap?> = mutableStateOf(null)
    val imageBitmap: State<ImageBitmap?> = _imageBitmap

    fun onAction(action: ArtMakerAction) {
        when (action) {
            ArtMakerAction.TriggerArtExport -> triggerArtExport()
            is ArtMakerAction.ExportArt -> exportArt(action.bitmap)
            ArtMakerAction.Redo -> redo()
            ArtMakerAction.Undo -> undo()
            ArtMakerAction.Clear -> clear()
            ArtMakerAction.UpdateBackground -> updateBackgroundColour()
            is ArtMakerAction.SelectStrokeColour -> updateStrokeColor(colour = action.color)
            is ArtMakerAction.SelectStrokeWidth -> selectStrokeWidth(strokeWidth = action.strokeWidth, defaults = defaults)
        }
    }

    fun onDrawEvent(event: DrawEvent) {
        when (event) {
            is DrawEvent.AddNewShape -> addNewShape(event.offset)
            DrawEvent.UndoLastShapePoint -> undoLastShapePoint()
            is DrawEvent.UpdateCurrentShape -> updateCurrentShape(event.offset)
        }
    }

    private fun addNewShape(offset: Offset) {
        val data = PointsData(
            points = mutableStateListOf(offset),
            strokeColor = Color(artMakerUIState.value.strokeColour),
            strokeWidth = artMakerUIState.value.strokeWidth.toFloat(),
        )
        _pathList.add(data)
    }

     fun getValue():Int  =
        if (preferences.contains(PreferenceKeys.SELECTED_STROKE_COLOUR)){
            _artMakerUIState.value.strokeWidth
        }else {
            defaults.strokeWidth.toInt()

    }

    private fun updateCurrentShape(offset: Offset) {
        val idx = _pathList.lastIndex
        _pathList[idx].points.add(offset)
    }

    private fun undoLastShapePoint() {
        val idx = _pathList.lastIndex
        _pathList[idx].points.removeLast()
    }

    private fun triggerArtExport() {
        _shouldTriggerArtExport.update { true }
    }

    private fun exportArt(bitmap: ImageBitmap) {
        viewModelScope.launch {
            val uri = bitmap.asAndroidBitmap().saveToDisk(applicationContext)
            _shouldTriggerArtExport.update { false }
            shareBitmap(applicationContext, uri)
        }
    }

    private fun redo() {
        if (undoStack.isNotEmpty()) {
            pathList.add(undoStack.pop())
        }
    }

    private fun undo() {
        if (_pathList.isNotEmpty()) {
            undoStack.push(_pathList.removeLast())
        }
    }

    private fun clear() {
        _pathList.clear()
        undoStack.clear()
    }

    private fun updateBackgroundColour() {}

    private fun updateStrokeColor(colour: Color) {
        preferences.set(
            key = PreferenceKeys.SELECTED_STROKE_COLOUR,
            value = colour.toArgb(),
        )
        _artMakerUIState.update {
            it.copy(strokeColour = preferences.get(PreferenceKeys.SELECTED_STROKE_COLOUR, 0))
        }
    }

    fun setImage(bitmap: ImageBitmap?) {
        _imageBitmap.value = bitmap
    }

    private fun selectStrokeWidth(strokeWidth: Int, defaults: ArtMakerDefaults) {

        if (preferences.contains(SELECTED_STROKE_WIDTH)){
            preferences.set(
                key = SELECTED_STROKE_WIDTH,
                value = strokeWidth,
            )
            _artMakerUIState.update {
                it.copy(
                    strokeWidth = preferences.get(
                        SELECTED_STROKE_WIDTH,
                        defaultValue = 5,
                    ),
                )
            }
        } else {
            _artMakerUIState.update {
                it.copy(
                    strokeWidth = defaults.strokeWidth.toInt()
                )
            }
        }
    }

    companion object {
        fun provideFactory(
            application: Application,
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(ArtMakerViewModel::class.java)) {
                    @Suppress("UNCHECKED_CAST")
                    return ArtMakerViewModel(
                        preferences = ArtMakerSharedPreferences(
                            context = application,
                        ),
                        applicationContext = application.applicationContext,
                        defaults = ArtMakerDefaults()
                    ) as T
                }
                throw IllegalArgumentException("Unknown ViewModel Class")
            }
        }
    }
}
