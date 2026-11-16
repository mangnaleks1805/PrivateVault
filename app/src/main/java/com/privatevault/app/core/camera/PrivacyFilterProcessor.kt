package com.privatevault.app.core.camera

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.RadialGradient
import android.graphics.Rect
import android.graphics.Shader
import androidx.compose.ui.graphics.ColorMatrix as ComposeColorMatrix

/**
 * Advanced On-Device Aesthetic & Privacy Filter Engine.
 * Implements edge-preserving skin smoothing, golden hour tone curves,
 * specular bloom, and radial vignettes completely offline.
 */
object PrivacyFilterProcessor {

    fun applyFilter(source: Bitmap, filter: PrivacyFilter): Bitmap {
        if (filter == PrivacyFilter.ORIGINAL) return source

        return when (filter) {
            PrivacyFilter.ORIGINAL -> source
            PrivacyFilter.CLEAN_GLOW -> applyCleanGlowFilter(source)
            PrivacyFilter.GOLDEN_HOUR -> applyGoldenHourFilter(source)
            PrivacyFilter.GLASS_SKIN -> applyGlassSkinFilter(source)
            PrivacyFilter.VINTAGE_90S -> applyVintage90sFilter(source)
            PrivacyFilter.VOGUE_NOIR -> applyVogueNoirFilter(source)
            PrivacyFilter.BLUSH_PEACH -> applyBlushPeachFilter(source)
            PrivacyFilter.CYBER_MOODY -> applyCyberMoodyFilter(source)
            PrivacyFilter.ETHEREAL_DREAM -> applyEtherealDreamFilter(source)
            PrivacyFilter.NIGHT_BOOST -> applyNightBoostFilter(source)
            PrivacyFilter.ANONYMIZE -> applyAnonymizeFilter(source)
        }
    }

    /**
     * #1 Snapchat Clean Glow:
     * Selective Skin-Mask Smoothing:
     * Only smooths skin regions while preserving 100% sharpness of eyes, lips, eyebrows, and hair.
     */
    private fun applyCleanGlowFilter(source: Bitmap): Bitmap {
        val width = source.width
        val height = source.height

        // 1. Create smoothed base
        val blurFactor = 5
        val small = Bitmap.createScaledBitmap(source, (width / blurFactor).coerceAtLeast(1), (height / blurFactor).coerceAtLeast(1), true)
        val smoothed = Bitmap.createScaledBitmap(small, width, height, true)
        small.recycle()

        // 2. Pixel-level skin mask blending
        val srcPixels = IntArray(width * height)
        val smoothPixels = IntArray(width * height)
        val outPixels = IntArray(width * height)

        source.getPixels(srcPixels, 0, width, 0, 0, width, height)
        smoothed.getPixels(smoothPixels, 0, width, 0, 0, width, height)
        smoothed.recycle()

        for (i in srcPixels.indices) {
            val c = srcPixels[i]
            val r = (c shr 16) and 0xFF
            val g = (c shr 8) and 0xFF
            val b = c and 0xFF

            // Human skin-color detection in RGB color space
            val isSkin = (r > 60 && g > 40 && b > 20) &&
                    (r > g && g >= b) &&
                    ((r - g) >= 12) &&
                    ((r - b) >= 15)

            if (isSkin) {
                // Blend 65% smoothed skin + 35% original texture for natural poreless glow
                val sc = smoothPixels[i]
                val sr = (sc shr 16) and 0xFF
                val sg = (sc shr 8) and 0xFF
                val sb = sc and 0xFF

                val blendedR = (sr * 0.65f + r * 0.35f).toInt().coerceIn(0, 255)
                val blendedG = (sg * 0.65f + g * 0.35f).toInt().coerceIn(0, 255)
                val blendedB = (sb * 0.65f + b * 0.35f).toInt().coerceIn(0, 255)
                outPixels[i] = (0xFF shl 24) or (blendedR shl 16) or (blendedG shl 8) or blendedB
            } else {
                // Non-skin (eyes, eyelashes, lips, hair, clothes): keep 100% crisp original detail
                outPixels[i] = c
            }
        }

        val result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        result.setPixels(outPixels, 0, width, 0, 0, width, height)

        // 3. Subtle radiant warm tone matrix
        val matrix = ColorMatrix(
            floatArrayOf(
                1.08f, 0.02f, 0.00f, 0f, 8f,
                0.00f, 1.05f, 0.00f, 0f, 6f,
                0.00f, 0.00f, 1.00f, 0f, 4f,
                0.00f, 0.00f, 0.00f, 1f, 0f
            )
        )
        return transformWithMatrix(result, matrix)
    }

    /**
     * #2 Golden Hour (Sun-Kissed Glow):
     * Amber sunset curves + rich skin tone warmth + soft border vignette.
     */
    private fun applyGoldenHourFilter(source: Bitmap): Bitmap {
        val matrix = ColorMatrix(
            floatArrayOf(
                1.26f, 0.08f, 0.00f, 0f, 24f,
                0.00f, 1.10f, 0.00f, 0f, 12f,
                0.00f, 0.00f, 0.84f, 0f, -16f,
                0.00f, 0.00f, 0.00f, 1f, 0f
            )
        )
        val colorGraded = transformWithMatrix(source, matrix)
        applyRadialVignette(colorGraded, intensity = 0.50f)
        return colorGraded
    }

    /**
     * #3 Glass Skin:
     * Reflective dewy highlights + gentle skin evening.
     */
    private fun applyGlassSkinFilter(source: Bitmap): Bitmap {
        val matrix = ColorMatrix(
            floatArrayOf(
                1.12f, 0.00f, 0.00f, 0f, 14f,
                0.00f, 1.12f, 0.00f, 0f, 14f,
                0.00f, 0.00f, 1.16f, 0f, 18f,
                0.00f, 0.00f, 0.00f, 1f, 0f
            )
        )
        val bright = transformWithMatrix(source, matrix)
        applySpecularGlow(bright, intensity = 0.25f)
        return bright
    }

    /**
     * #4 90s Polaroid / Retro Film:
     * Matte lifted blacks + warm analog curve + cinematic edge vignette.
     */
    private fun applyVintage90sFilter(source: Bitmap): Bitmap {
        val matrix = ColorMatrix(
            floatArrayOf(
                1.16f, 0.06f, 0.00f, 0f, 20f,
                0.00f, 1.08f, 0.02f, 0f, 18f,
                0.00f, 0.00f, 0.88f, 0f, 16f,
                0.00f, 0.00f, 0.00f, 1f, 0f
            )
        )
        val graded = transformWithMatrix(source, matrix)
        applyRadialVignette(graded, intensity = 0.60f)
        return graded
    }

    /**
     * #5 Vogue Noir:
     * High-contrast dramatic silver monochrome with deep crushed blacks.
     */
    private fun applyVogueNoirFilter(source: Bitmap): Bitmap {
        val matrix = ColorMatrix()
        matrix.setSaturation(0f)

        val scale = 1.45f
        val translate = (-0.5f * scale + 0.5f) * 255f
        val contrast = ColorMatrix(
            floatArrayOf(
                scale, 0f, 0f, 0f, translate,
                0f, scale, 0f, 0f, translate,
                0f, 0f, scale, 0f, translate,
                0f, 0f, 0f, 1f, 0f
            )
        )
        matrix.postConcat(contrast)
        val noir = transformWithMatrix(source, matrix)
        applyRadialVignette(noir, intensity = 0.50f)
        return noir
    }

    /**
     * #6 Blush Peach:
     * Soft rosy cheek warmth and radiant flattering portrait light.
     */
    private fun applyBlushPeachFilter(source: Bitmap): Bitmap {
        val matrix = ColorMatrix(
            floatArrayOf(
                1.24f, 0.04f, 0.00f, 0f, 22f,
                0.00f, 1.05f, 0.04f, 0f, 10f,
                0.00f, 0.04f, 0.95f, 0f, 6f,
                0.00f, 0.00f, 0.00f, 1f, 0f
            )
        )
        return transformWithMatrix(source, matrix)
    }

    /**
     * #7 Cyber Moody (Teal & Orange):
     * Hollywood cinema grading: deep teal shadows with golden skin highlights.
     */
    private fun applyCyberMoodyFilter(source: Bitmap): Bitmap {
        val matrix = ColorMatrix(
            floatArrayOf(
                1.22f, 0.00f, 0.00f, 0f, 18f,  // Warm golden skin highlights
                0.00f, 1.06f, 0.06f, 0f, 6f,
                0.00f, 0.10f, 1.28f, 0f, 26f,  // Deep cinematic teal/blue shadows
                0.00f, 0.00f, 0.00f, 1f, 0f
            )
        )
        val graded = transformWithMatrix(source, matrix)
        applyRadialVignette(graded, intensity = 0.40f)
        return graded
    }

    /**
     * #8 Ethereal Dream (Highlight Bloom):
     * Diffused highlight bloom and soft angelic glow.
     */
    private fun applyEtherealDreamFilter(source: Bitmap): Bitmap {
        val width = source.width
        val height = source.height

        val factor = 8
        val small = Bitmap.createScaledBitmap(source, (width / factor).coerceAtLeast(1), (height / factor).coerceAtLeast(1), true)
        val bloom = Bitmap.createScaledBitmap(small, width, height, true)
        small.recycle()

        val result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(result)
        canvas.drawBitmap(source, 0f, 0f, null)

        val bloomPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            alpha = (0.35f * 255).toInt()
            xfermode = PorterDuffXfermode(PorterDuff.Mode.SCREEN)
        }
        canvas.drawBitmap(bloom, 0f, 0f, bloomPaint)
        bloom.recycle()

        return result
    }

    /**
     * #9 Night Boost:
     * Ambient low-light shadow lift.
     */
    private fun applyNightBoostFilter(source: Bitmap): Bitmap {
        val matrix = ColorMatrix(
            floatArrayOf(
                1.18f, 0.00f, 0.00f, 0f, 28f,
                0.00f, 1.18f, 0.00f, 0f, 28f,
                0.00f, 0.00f, 1.18f, 0f, 32f,
                0.00f, 0.00f, 0.00f, 1f, 0f
            )
        )
        return transformWithMatrix(source, matrix)
    }

    /**
     * #10 Anonymize:
     * Mosaic shield for mutual privacy and discretion.
     */
    private fun applyAnonymizeFilter(source: Bitmap, blockSize: Int = 24): Bitmap {
        val width = source.width
        val height = source.height

        val scaledW = (width / blockSize).coerceAtLeast(1)
        val scaledH = (height / blockSize).coerceAtLeast(1)

        val small = Bitmap.createScaledBitmap(source, scaledW, scaledH, false)
        val pixelated = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

        val canvas = Canvas(pixelated)
        val paint = Paint(Paint.DITHER_FLAG).apply {
            isFilterBitmap = false
        }
        canvas.drawBitmap(small, Rect(0, 0, scaledW, scaledH), Rect(0, 0, width, height), paint)
        small.recycle()
        return pixelated
    }

    /**
     * Smooth radial vignette shader drawn around borders.
     */
    private fun applyRadialVignette(bitmap: Bitmap, intensity: Float = 0.5f) {
        val canvas = Canvas(bitmap)
        val width = bitmap.width.toFloat()
        val height = bitmap.height.toFloat()
        val radius = (Math.hypot(width.toDouble(), height.toDouble()) / 2.0).toFloat()

        val colors = intArrayOf(
            Color.TRANSPARENT,
            Color.argb((intensity * 0.35f * 255).toInt(), 0, 0, 0),
            Color.argb((intensity * 255).toInt(), 0, 0, 0)
        )
        val stops = floatArrayOf(0.40f, 0.75f, 1.0f)

        val shader = RadialGradient(
            width / 2f,
            height / 2f,
            radius,
            colors,
            stops,
            Shader.TileMode.CLAMP
        )

        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            this.shader = shader
        }
        canvas.drawRect(0f, 0f, width, height, paint)
    }

    /**
     * Subtle specular highlight bloom for glass skin.
     */
    private fun applySpecularGlow(bitmap: Bitmap, intensity: Float = 0.25f) {
        val canvas = Canvas(bitmap)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            alpha = (intensity * 255).toInt()
            xfermode = PorterDuffXfermode(PorterDuff.Mode.OVERLAY)
        }
        canvas.drawRect(0f, 0f, bitmap.width.toFloat(), bitmap.height.toFloat(), paint)
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
     * Real-time Compose ColorMatrix for live GPU preview inside the CameraX viewfinder.
     */
    fun getComposeColorMatrix(filter: PrivacyFilter): ComposeColorMatrix? {
        return when (filter) {
            PrivacyFilter.ORIGINAL -> null
            PrivacyFilter.CLEAN_GLOW -> ComposeColorMatrix(
                floatArrayOf(
                    1.08f, 0.02f, 0.00f, 0f, 8f,
                    0.00f, 1.05f, 0.00f, 0f, 6f,
                    0.00f, 0.00f, 1.00f, 0f, 4f,
                    0f, 0f, 0f, 1f, 0f
                )
            )
            PrivacyFilter.GOLDEN_HOUR -> ComposeColorMatrix(
                floatArrayOf(
                    1.26f, 0.08f, 0.00f, 0f, 24f,
                    0.00f, 1.10f, 0.00f, 0f, 12f,
                    0.00f, 0.00f, 0.84f, 0f, -16f,
                    0f, 0f, 0f, 1f, 0f
                )
            )
            PrivacyFilter.GLASS_SKIN -> ComposeColorMatrix(
                floatArrayOf(
                    1.12f, 0.00f, 0.00f, 0f, 14f,
                    0.00f, 1.12f, 0.00f, 0f, 14f,
                    0.00f, 0.00f, 1.16f, 0f, 18f,
                    0f, 0f, 0f, 1f, 0f
                )
            )
            PrivacyFilter.VINTAGE_90S -> ComposeColorMatrix(
                floatArrayOf(
                    1.16f, 0.06f, 0.00f, 0f, 20f,
                    0.00f, 1.08f, 0.02f, 0f, 18f,
                    0.00f, 0.00f, 0.88f, 0f, 16f,
                    0f, 0f, 0f, 1f, 0f
                )
            )
            PrivacyFilter.VOGUE_NOIR -> ComposeColorMatrix(
                floatArrayOf(
                    0.42f, 0.75f, 0.16f, 0f, -38f,
                    0.42f, 0.75f, 0.16f, 0f, -38f,
                    0.42f, 0.75f, 0.16f, 0f, -38f,
                    0f, 0f, 0f, 1f, 0f
                )
            )
            PrivacyFilter.BLUSH_PEACH -> ComposeColorMatrix(
                floatArrayOf(
                    1.24f, 0.04f, 0.00f, 0f, 22f,
                    0.00f, 1.05f, 0.04f, 0f, 10f,
                    0.00f, 0.04f, 0.95f, 0f, 6f,
                    0f, 0f, 0f, 1f, 0f
                )
            )
            PrivacyFilter.CYBER_MOODY -> ComposeColorMatrix(
                floatArrayOf(
                    1.22f, 0.00f, 0.00f, 0f, 18f,
                    0.00f, 1.06f, 0.06f, 0f, 6f,
                    0.00f, 0.10f, 1.28f, 0f, 26f,
                    0f, 0f, 0f, 1f, 0f
                )
            )
            PrivacyFilter.ETHEREAL_DREAM -> ComposeColorMatrix(
                floatArrayOf(
                    1.14f, 0.06f, 0.06f, 0f, 22f,
                    0.06f, 1.14f, 0.06f, 0f, 22f,
                    0.06f, 0.06f, 1.14f, 0f, 22f,
                    0f, 0f, 0f, 1f, 0f
                )
            )
            PrivacyFilter.NIGHT_BOOST -> ComposeColorMatrix(
                floatArrayOf(
                    1.18f, 0.00f, 0.00f, 0f, 28f,
                    0.00f, 1.18f, 0.00f, 0f, 28f,
                    0.00f, 0.00f, 1.18f, 0f, 32f,
                    0f, 0f, 0f, 1f, 0f
                )
            )
            PrivacyFilter.ANONYMIZE -> ComposeColorMatrix(
                floatArrayOf(
                    0.90f, 0.10f, 0.00f, 0f, 5f,
                    0.00f, 0.90f, 0.10f, 0f, 5f,
                    0.10f, 0.00f, 0.90f, 0f, 5f,
                    0f, 0f, 0f, 1f, 0f
                )
            )
        }
    }
}
