package com.halilozcan.canvastub

import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.view.MotionEvent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import com.halilozcan.canvastub.ui.theme.CanvaStubTheme

private const val MINIMUM_DISTANCE_THRESHOLD = 10f
private const val MAXIMUM_SCALE = 5f
private const val MINIMUM_SCALE = 0.6f

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun CanvaStub(controller: CanvaController = rememberCanvasController()) {

    val bitmap = BitmapFactory.decodeResource(LocalContext.current.resources, R.drawable.sample)

    val canvaInvalidator = remember { mutableStateOf(0) }

    Canvas(modifier = Modifier
        .fillMaxSize()
        .onSizeChanged {
            controller.run {
                imageRectF.set(0f, 0f, bitmap.width.toFloat(), bitmap.height.toFloat())
                controller.viewRectF.set(0f, 0f, it.width.toFloat(), it.height.toFloat())
                val heightScale = viewRectF.height() / imageRectF.height()
                val widthScale = viewRectF.width() / imageRectF.width()
                val scale = minOf(heightScale, widthScale)
                val translationX = (viewRectF.width() - imageRectF.width() * scale) / 2f
                val translationY = (viewRectF.height() - imageRectF.height() * scale) / 2f
                imageMatrix.value.setScale(scale, scale)
                imageMatrix.value.postTranslate(translationX, translationY)
                canvaInvalidator.value++
            }
        }
        .pointerInteropFilter { event ->
            controller.apply {
                when (event.actionMasked) {
                    MotionEvent.ACTION_DOWN -> {
                        currentUserTouchType.value = UserTouchType.TRANSLATE
                        userLastTouchPointF.value.set(event.x, event.y)
                    }

                    MotionEvent.ACTION_POINTER_DOWN -> {
                        lastDistance.value = event.getHypoDistance()
                        if (lastDistance.value > MINIMUM_DISTANCE_THRESHOLD) {
                            userTouchMidPoint.value.set(event.getMidPoint())
                            currentUserTouchType.value = UserTouchType.SCALE
                        }
                    }

                    MotionEvent.ACTION_MOVE -> {
                        processUserMoveTouchOperation(event, this)
                        userLastTouchPointF.value.set(event.x, event.y)
                    }

                    MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_POINTER_UP -> {
                        currentUserTouchType.value = UserTouchType.NONE
                    }
                    else -> Unit
                }
            }

            canvaInvalidator.value++
            true
        }, onDraw = {
        drawIntoCanvas {
            controller.run {
                if (canvaInvalidator.value != 0) {
                    imageMatrix.value.mapRect(destinationRectF, imageRectF)
                    it.drawImageRect(
                        image = bitmap.asImageBitmap(),
                        srcOffset = IntOffset(imageRectF.left.toInt(), imageRectF.top.toInt()),
                        dstOffset = IntOffset(
                            destinationRectF.left.toInt(),
                            destinationRectF.top.toInt()
                        ),
                        dstSize = IntSize(
                            controller.destinationRectF.width().toInt(),
                            controller.destinationRectF.height().toInt()
                        ),
                        paint = Paint()
                    )
                }
            }
        }
    })
}

fun processUserMoveTouchOperation(event: MotionEvent, controller: CanvaController) {
    when (controller.currentUserTouchType.value) {
        UserTouchType.NONE -> Unit
        UserTouchType.TRANSLATE -> {
            translateImageWithMotion(event, controller)
        }
        UserTouchType.SCALE -> {
            scaleImageWithMotion(event, controller)
        }
    }
}

fun translateImageWithMotion(event: MotionEvent, controller: CanvaController) {
    val distanceX = event.x - controller.userLastTouchPointF.value.x
    val distanceY = event.y - controller.userLastTouchPointF.value.y
    controller.imageMatrix.value = controller.imageMatrix.value.apply {
        postTranslate(distanceX, distanceY)
    }
}

fun scaleImageWithMotion(event: MotionEvent, controller: CanvaController) {
    controller.apply {
        val distance = event.getHypoDistance()
        val matrix = Matrix(imageMatrix.value)
        val scale = distance / lastDistance.value
        if (distance > MINIMUM_DISTANCE_THRESHOLD) {
            lastDistance.value = distance
            matrix.postScale(scale, scale, userTouchMidPoint.value.x, userTouchMidPoint.value.y)
        }

        val currentMatrixScale = matrix.getScale()

        when {
            currentMatrixScale > MAXIMUM_SCALE -> Unit
            currentMatrixScale < MINIMUM_SCALE -> Unit
            else -> {
                imageMatrix.value.postScale(
                    scale,
                    scale,
                    userTouchMidPoint.value.x,
                    userTouchMidPoint.value.y
                )
            }
        }
    }
}

@Preview
@Composable
fun ComposeCanvasPreview() {
    CanvaStubTheme {
        CanvaStub()
    }
}