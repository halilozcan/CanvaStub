package com.halilozcan.canvastub

import android.graphics.Matrix
import kotlin.math.atan2
import kotlin.math.sqrt

private val values = FloatArray(9)

fun Matrix.getScale(): Float {
    getValues(values)
    val scaleX: Float = values[Matrix.MSCALE_X]
    val skewY: Float = values[Matrix.MSKEW_Y]
    return sqrt(scaleX * scaleX + skewY * skewY.toDouble()).toFloat()
}

fun Matrix.getRotation(): Float {
    getValues(values)
    val skewX = values[Matrix.MSKEW_X]
    val scaleX = values[Matrix.MSCALE_X]
    return -(atan2(skewX, scaleX) * (180 / Math.PI)).toFloat()
}