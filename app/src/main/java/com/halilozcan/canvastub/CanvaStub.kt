package com.halilozcan.canvastub

import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.graphics.PointF
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

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun CanvaStub(controller: CanvaController = rememberCanvasController()) {
    val bitmap = BitmapFactory.decodeResource(LocalContext.current.resources, R.drawable.sample)

    val canvaInvalidator = remember { mutableStateOf(0) }
    val currentTouchPointF = PointF(0f, 0f)

    Canvas(modifier = Modifier
        .fillMaxSize()
        .onSizeChanged {
            controller.imageRectF.set(0f, 0f, bitmap.width.toFloat(), bitmap.height.toFloat())
            controller.viewRectF.set(0f, 0f, it.width.toFloat(), it.height.toFloat())
            val heightScale = controller.viewRectF.height() / controller.imageRectF.height()
            val widthScale = controller.viewRectF.width() / controller.imageRectF.width()
            val scale = minOf(heightScale, widthScale)
            val translationX =
                (controller.viewRectF.width() - controller.imageRectF.width() * scale) / 2f
            val translationY =
                (controller.viewRectF.height() - controller.imageRectF.height() * scale) / 2f
            val matrix = Matrix().apply {
                setScale(scale, scale)
                postTranslate(translationX, translationY)
            }
            controller.imageMatrix.value = matrix
            canvaInvalidator.value++
        }
        .pointerInteropFilter { event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    currentTouchPointF.x = event.x
                    currentTouchPointF.y = event.y
                }

                MotionEvent.ACTION_MOVE -> {
                    val differenceX = event.x - currentTouchPointF.x
                    val differenceY = event.y - currentTouchPointF.y

                    currentTouchPointF.x = event.x
                    currentTouchPointF.y = event.y

                    controller.imageMatrix.value = controller.imageMatrix.value.apply {
                        postTranslate(differenceX, differenceY)
                    }
                }
                MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> {

                }
                else -> false
            }
            canvaInvalidator.value++
            true
        }, onDraw = {
        drawIntoCanvas {
            if (canvaInvalidator.value != 0) {
                controller.imageMatrix.value.mapRect(
                    controller.destinationRectF,
                    controller.imageRectF
                )
                it.drawImageRect(
                    bitmap.asImageBitmap(),
                    srcOffset = IntOffset(
                        controller.imageRectF.left.toInt(),
                        controller.imageRectF.top.toInt()
                    ),
                    dstOffset = IntOffset(
                        controller.destinationRectF.left.toInt(),
                        controller.destinationRectF.top.toInt()
                    ),
                    dstSize = IntSize(
                        controller.destinationRectF.width().toInt(),
                        controller.destinationRectF.height().toInt()
                    ),
                    paint = Paint()
                )
            }
        }
    })
}

@Preview
@Composable
fun ComposeCanvasPreview() {
    CanvaStubTheme {
        CanvaStub()
    }
}