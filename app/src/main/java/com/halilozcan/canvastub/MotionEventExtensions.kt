package com.halilozcan.canvastub

import android.graphics.PointF
import android.view.MotionEvent
import kotlin.math.atan2
import kotlin.math.sqrt


fun MotionEvent.getHypoDistance(): Float {
    val firstPointer = PointF(getX(0), getY(0))
    val secondPointer = PointF(getX(1), getY(1))
    val x = firstPointer.x - secondPointer.x
    val y = firstPointer.y - secondPointer.y
    return sqrt(x * x + y * y)
}

fun MotionEvent.getMidPoint(): PointF {
    val firstPointer = PointF(getX(0), getY(0))
    val secondPointer = PointF(getX(1), getY(1))
    val midX = (firstPointer.x + secondPointer.x) / 2f
    val midY = (firstPointer.y + secondPointer.y) / 2f
    return PointF(midX, midY)
}

fun MotionEvent.getRotation(): Float {
    val firstPointer = PointF(getX(0), getY(0))
    val secondPointer = PointF(getX(1), getY(1))
    val deltaX = firstPointer.x - secondPointer.x
    val deltaY = firstPointer.y - secondPointer.y
    val degreeInRadians = atan2(deltaY, deltaX).toDouble()
    return Math.toDegrees(degreeInRadians).toFloat()
}