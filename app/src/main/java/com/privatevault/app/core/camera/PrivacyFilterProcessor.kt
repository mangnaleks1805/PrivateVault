package com.privatevault.app.core.camera

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import androidx.compose.ui.graphics.ColorMatrix as ComposeColorMatrix

object PrivacyFilterProcessor {

    /**
     * Applies the selected privacy/discretion filter to a captured Bitmap.
     * 100% offline, zero-network, hardware-accelerated where available.
     */
    fun applyFilter(source: Bitmap, filter: PrivacyFilter): Bitmap {
        if (filter == PrivacyFilter.NONE) return source

        return when (filter) {
            PrivacyFilter.NONE -> source
            PrivacyFilter.NOIR -> applyNoirFilter(source)
            PrivacyFilter.VINTAGE -> applyVintageFilter(source)
            PrivacyFilter.COOL_NIGHT -> applyCoolNightFilter(source)
            PrivacyFilter.PIXELATE -> applyPixelateFilter(source, blockSize = 24)
            PrivacyFilter.PRIVACY_BLUR -> applyPrivacyBlurFilter(source)
        }
    }

    private fun applyNoirFilter(source: Bitmap): Bitmap {
        val matrix = ColorMatrix()
        matrix.setSaturation(0f)

        // Enhance contrast
        val scale = 1.2f
        val translate = (-0.5f * scale + 0.5f) * 255f
        val contrastMatrix = ColorMatrix(
            floatArrayOf(
                scale, 0f, 0f, 0f, translate,
                0f, scale, 0f, 0f, translate,
                0f, 0f, scale, 0f, translate,
                0f, 0f, 0f, 1f, 0f
            )
        )
        matrix.postConcat(contrastMatrix)

        return transformWithMatrix(source, matrix)
    }

    private fun applyVintageFilter(source: Bitmap): Bitmap {
        val matrix = ColorMatrix(
            floatArrayOf(
                1.18f, 0.08f, 0.00f, 0f, 18f,
                0.00f, 1.05f, 0.00f, 0f, 8f,
                0.00f, 0.00f, 0.88f, 0f, -12f,
                0.00f, 0.00f, 0.00f, 1f, 0f
            )
        )
        return transformWithMatrix(source, matrix)
    }

    private fun applyCoolNightFilter(source: Bitmap): Bitmap {
        val matrix = ColorMatrix(
            floatArrayOf(
                0.88f, 0.00f, 0.00f, 0f, -8f,
                0.00f, 0.98f, 0.05f, 0f, 6f,
                0.00f, 0.05f, 1.22f, 0f, 20f,
                0.00f, 0.00f, 0.00f, 1f, 0f
            )
        )
        return transformWithMatrix(source, matrix)
    }

    private fun applyPixelateFilter(source: Bitmap, blockSize: Int = 24): Bitmap {
        val width = source.width
        val height = source.height

        val scaledWidth = (width / blockSize).coerceAtLeast(1)
        val scaledHeight = (height / blockSize).coerceAtLeast(1)

        val smallBitmap = Bitmap.createScaledBitmap(source, scaledWidth, scaledHeight, false)
        val pixelated = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

        val canvas = Canvas(pixelated)
        val paint = Paint(Paint.DITHER_FLAG).apply {
            isFilterBitmap = false // Nearest neighbor creates crisp privacy blocks
        }
        val srcRect = android.graphics.Rect(0, 0, scaledWidth, scaledHeight)
        val dstRect = android.graphics.Rect(0, 0, width, height)
        canvas.drawBitmap(smallBitmap, srcRect, dstRect, paint)

        smallBitmap.recycle()
        return pixelated
    }

    private fun applyPrivacyBlurFilter(source: Bitmap): Bitmap {
        val width = source.width
        val height = source.height

        val factor = 8
        val smallWidth = (width / factor).coerceAtLeast(1)
        val smallHeight = (height / factor).coerceAtLeast(1)

        val small = Bitmap.createScaledBitmap(source, smallWidth, smallHeight, true)
        val blurred = Bitmap.createScaledBitmap(small, width, height, true)

        small.recycle()
        return blurred
    }

    private fun transformWithMatrix(source: Bitmap, matrix: ColorMatrix): Bitmap {
        val result = Bitmap.createBitmap(source.width, source.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(result)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            colorFilter = ColorMatrixColorFilter(matrix)
        }
        canvas.drawBitmap(source, 0f, 0f, paint)
        return result
    }

    /**
     * Returns a Compose-compatible ColorMatrix for real-time live preview in viewfinder.
     */
    fun getComposeColorMatrix(filter: PrivacyFilter): ComposeColorMatrix? {
        return when (filter) {
            PrivacyFilter.NONE -> null
            PrivacyFilter.NOIR -> ComposeColorMatrix(
                floatArrayOf(
                    0.33f * 1.2f, 0.59f * 1.2f, 0.11f * 1.2f, 0f, -25f,
                    0.33f * 1.2f, 0.59f * 1.2f, 0.11f * 1.2f, 0f, -25f,
                    0.33f * 1.2f, 0.59f * 1.2f, 0.11f * 1.2f, 0f, -25f,
                    0f, 0f, 0f, 1f, 0f
                )
            )
            PrivacyFilter.VINTAGE -> ComposeColorMatrix(
                floatArrayOf(
                    1.18f, 0.08f, 0.00f, 0f, 18f,
                    0.00f, 1.05f, 0.00f, 0f, 8f,
                    0.00f, 0.00f, 0.88f, 0f, -12f,
                    0.00f, 0.00f, 0.00f, 1f, 0f
                )
            )
            PrivacyFilter.COOL_NIGHT -> ComposeColorMatrix(
                floatArrayOf(
                    0.88f, 0.00f, 0.00f, 0f, -8f,
                    0.00f, 0.98f, 0.05f, 0f, 6f,
                    0.00f, 0.05f, 1.22f, 0f, 20f,
                    0.00f, 0.00f, 0.00f, 1f, 0f
                )
            )
            PrivacyFilter.PRIVACY_BLUR -> ComposeColorMatrix(
                floatArrayOf(
                    0.95f, 0.00f, 0.00f, 0f, 10f,
                    0.00f, 0.95f, 0.00f, 0f, 10f,
                    0.00f, 0.00f, 0.95f, 0f, 10f,
                    0.00f, 0.00f, 0.00f, 1f, 0f
                )
            )
            PrivacyFilter.PIXELATE -> ComposeColorMatrix(
                floatArrayOf(
                    0.90f, 0.10f, 0.00f, 0f, 5f,
                    0.00f, 0.90f, 0.10f, 0f, 5f,
                    0.10f, 0.00f, 0.90f, 0f, 5f,
                    0.00f, 0.00f, 0.00f, 1f, 0f
                )
            )
        }
    }
}
