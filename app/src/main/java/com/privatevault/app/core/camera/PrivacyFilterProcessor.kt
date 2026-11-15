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
 * High-performance, 100% offline image processing engine.
 * Emulates the top 10 Snapchat aesthetic lenses using pure on-device Android graphics.
 * Zero network dependencies, zero telemetry, zero cloud exposure.
 */
object PrivacyFilterProcessor {

    /**
     * Applies the chosen filter at full camera resolution before AES-256-GCM vault encryption.
     */
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
     * Bilateral skin-smoothing approximation: low-frequency blur with high-frequency edge retention.
     */
    private fun applyCleanGlowFilter(source: Bitmap): Bitmap {
        val width = source.width
        val height = source.height

        // Downsample and upsample to generate low-frequency skin base
        val blurFactor = 6
        val small = Bitmap.createScaledBitmap(source, (width / blurFactor).coerceAtLeast(1), (height / blurFactor).coerceAtLeast(1), true)
        val smoothed = Bitmap.createScaledBitmap(small, width, height, true)
        small.recycle()

        // Blend: 55% smoothed + 45% original (retains sharp eyes & lips while clearing pores)
        val result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(result)
        canvas.drawBitmap(source, 0f, 0f, null)

        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            alpha = (0.55f * 255).toInt()
        }
        canvas.drawBitmap(smoothed, 0f, 0f, paint)
        smoothed.recycle()

        // Subtle warm glow color matrix
        val matrix = ColorMatrix(
            floatArrayOf(
                1.06f, 0.02f, 0.00f, 0f, 6f,
                0.00f, 1.04f, 0.00f, 0f, 4f,
                0.00f, 0.00f, 1.00f, 0f, 2f,
                0.00f, 0.00f, 0.00f, 1f, 0f
            )
        )
        return transformWithMatrix(result, matrix)
    }

    /**
     * #2 Golden Hour (Sun-Kissed):
     * Amber sunset tones + soft corner vignette.
     */
    private fun applyGoldenHourFilter(source: Bitmap): Bitmap {
        val matrix = ColorMatrix(
            floatArrayOf(
                1.25f, 0.08f, 0.00f, 0f, 22f,
                0.00f, 1.10f, 0.00f, 0f, 10f,
                0.00f, 0.00f, 0.85f, 0f, -14f,
                0.00f, 0.00f, 0.00f, 1f, 0f
            )
        )
        val colorGraded = transformWithMatrix(source, matrix)
        applyRadialVignette(colorGraded, intensity = 0.45f)
        return colorGraded
    }

    /**
     * #3 Glass Skin:
     * Reflective dewy highlights with crystal-clear skin evening.
     */
    private fun applyGlassSkinFilter(source: Bitmap): Bitmap {
        val matrix = ColorMatrix(
            floatArrayOf(
                1.08f, 0.00f, 0.00f, 0f, 12f,
                0.00f, 1.08f, 0.00f, 0f, 12f,
                0.00f, 0.00f, 1.12f, 0f, 16f,
                0.00f, 0.00f, 0.00f, 1f, 0f
            )
        )
        return transformWithMatrix(source, matrix)
    }

    /**
     * #4 90s Polaroid / Retro Film:
     * Lifted matte blacks + warm analog curve + border vignette.
     */
    private fun applyVintage90sFilter(source: Bitmap): Bitmap {
        val matrix = ColorMatrix(
            floatArrayOf(
                1.15f, 0.06f, 0.00f, 0f, 18f,
                0.00f, 1.06f, 0.02f, 0f, 16f,
                0.00f, 0.00f, 0.90f, 0f, 14f,
                0.00f, 0.00f, 0.00f, 1f, 0f
            )
        )
        val graded = transformWithMatrix(source, matrix)
        applyRadialVignette(graded, intensity = 0.55f)
        return graded
    }

    /**
     * #5 Vogue Noir:
     * High-contrast dramatic silver monochrome.
     */
    private fun applyVogueNoirFilter(source: Bitmap): Bitmap {
        val matrix = ColorMatrix()
        matrix.setSaturation(0f)

        val scale = 1.35f
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
        applyRadialVignette(noir, intensity = 0.40f)
        return noir
    }

    /**
     * #6 Blush Peach:
     * Soft rosy cheek warmth and radiant flattering portrait light.
     */
    private fun applyBlushPeachFilter(source: Bitmap): Bitmap {
        val matrix = ColorMatrix(
            floatArrayOf(
                1.22f, 0.04f, 0.00f, 0f, 20f,
                0.00f, 1.04f, 0.04f, 0f, 8f,
                0.00f, 0.04f, 0.96f, 0f, 4f,
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
                1.20f, 0.00f, 0.00f, 0f, 15f,  // Warm orange highlights
                0.00f, 1.05f, 0.05f, 0f, 4f,
                0.00f, 0.08f, 1.25f, 0f, 24f,  // Rich teal/blue shadows
                0.00f, 0.00f, 0.00f, 1f, 0f
            )
        )
        val graded = transformWithMatrix(source, matrix)
        applyRadialVignette(graded, intensity = 0.35f)
        return graded
    }

    /**
     * #8 Ethereal Dream (Highlight Bloom):
     * Diffused highlight bloom and soft angelic glow.
     */
    private fun applyEtherealDreamFilter(source: Bitmap): Bitmap {
        val width = source.width
        val height = source.height

        // Downsample for soft highlight bloom
        val factor = 8
        val small = Bitmap.createScaledBitmap(source, (width / factor).coerceAtLeast(1), (height / factor).coerceAtLeast(1), true)
        val bloom = Bitmap.createScaledBitmap(small, width, height, true)
        small.recycle()

        val result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(result)
        canvas.drawBitmap(source, 0f, 0f, null)

        val bloomPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            alpha = (0.40f * 255).toInt()
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
                1.15f, 0.00f, 0.00f, 0f, 25f,
                0.00f, 1.15f, 0.00f, 0f, 25f,
                0.00f, 0.00f, 1.15f, 0f, 30f,
                0.00f, 0.00f, 0.00f, 1f, 0f
            )
        )
        return transformWithMatrix(source, matrix)
    }

    /**
     * #10 Anonymize:
     * Full-frame mosaic shield for mutual discretion.
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
            Color.argb((intensity * 0.3f * 255).toInt(), 0, 0, 0),
            Color.argb((intensity * 255).toInt(), 0, 0, 0)
        )
        val stops = floatArrayOf(0.45f, 0.80f, 1.0f)

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
                    1.25f, 0.08f, 0.00f, 0f, 22f,
                    0.00f, 1.10f, 0.00f, 0f, 10f,
                    0.00f, 0.00f, 0.85f, 0f, -14f,
                    0f, 0f, 0f, 1f, 0f
                )
            )
            PrivacyFilter.GLASS_SKIN -> ComposeColorMatrix(
                floatArrayOf(
                    1.08f, 0.00f, 0.00f, 0f, 12f,
                    0.00f, 1.08f, 0.00f, 0f, 12f,
                    0.00f, 0.00f, 1.12f, 0f, 16f,
                    0f, 0f, 0f, 1f, 0f
                )
            )
            PrivacyFilter.VINTAGE_90S -> ComposeColorMatrix(
                floatArrayOf(
                    1.15f, 0.06f, 0.00f, 0f, 18f,
                    0.00f, 1.06f, 0.02f, 0f, 16f,
                    0.00f, 0.00f, 0.90f, 0f, 14f,
                    0f, 0f, 0f, 1f, 0f
                )
            )
            PrivacyFilter.VOGUE_NOIR -> ComposeColorMatrix(
                floatArrayOf(
                    0.38f, 0.70f, 0.14f, 0f, -30f,
                    0.38f, 0.70f, 0.14f, 0f, -30f,
                    0.38f, 0.70f, 0.14f, 0f, -30f,
                    0f, 0f, 0f, 1f, 0f
                )
            )
            PrivacyFilter.BLUSH_PEACH -> ComposeColorMatrix(
                floatArrayOf(
                    1.22f, 0.04f, 0.00f, 0f, 20f,
                    0.00f, 1.04f, 0.04f, 0f, 8f,
                    0.00f, 0.04f, 0.96f, 0f, 4f,
                    0f, 0f, 0f, 1f, 0f
                )
            )
            PrivacyFilter.CYBER_MOODY -> ComposeColorMatrix(
                floatArrayOf(
                    1.20f, 0.00f, 0.00f, 0f, 15f,
                    0.00f, 1.05f, 0.05f, 0f, 4f,
                    0.00f, 0.08f, 1.25f, 0f, 24f,
                    0f, 0f, 0f, 1f, 0f
                )
            )
            PrivacyFilter.ETHEREAL_DREAM -> ComposeColorMatrix(
                floatArrayOf(
                    1.12f, 0.05f, 0.05f, 0f, 18f,
                    0.05f, 1.12f, 0.05f, 0f, 18f,
                    0.05f, 0.05f, 1.12f, 0f, 18f,
                    0f, 0f, 0f, 1f, 0f
                )
            )
            PrivacyFilter.NIGHT_BOOST -> ComposeColorMatrix(
                floatArrayOf(
                    1.15f, 0.00f, 0.00f, 0f, 25f,
                    0.00f, 1.15f, 0.00f, 0f, 25f,
                    0.00f, 0.00f, 1.15f, 0f, 30f,
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
