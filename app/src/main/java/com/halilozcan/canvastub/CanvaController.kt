package com.halilozcan.canvastub

import android.graphics.Matrix
import android.graphics.PointF
import android.graphics.RectF
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

@Composable
fun rememberCanvasController(): CanvaController {
    return remember { CanvaController() }
}

class CanvaController {
    val imageRectF = RectF()
    val viewRectF = RectF()
    val destinationRectF = RectF()

    var imageMatrix: MutableState<Matrix> = mutableStateOf(Matrix())


    var currentUserTouchType: MutableState<UserTouchType> = mutableStateOf(UserTouchType.NONE)

    var lastDistance: MutableState<Float> = mutableStateOf(0f)

    val userTouchMidPoint: MutableState<PointF> = mutableStateOf(PointF())
    val userLastTouchPointF: MutableState<PointF> = mutableStateOf(PointF(0f, 0f))
}

enum class UserTouchType {
    NONE,
    TRANSLATE,
    SCALE
}