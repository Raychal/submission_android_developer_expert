package com.raychal.core.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.progressSemantics
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp

private const val DefaultAnimationDuration = 600
private const val DefaultAnimationDelay = 400
private const val DefaultStartDelay = 0
private const val DefaultLineCount = 5

private val DefaultMaxLineHeight = 32.dp
private val DefaultMinLineHeight = 16.dp
private val DefaultLineWidth = 3.dp
private val DefaultLineSpacing = 4.dp
private val DefaultLineCornerRadius = 3.dp

/**
 * A composable function that displays a line scale style indeterminate progress indicator.
 *
 * The indicator consists of multiple vertical lines that scale up and down in a staggered,
 * wave-like animation pattern. Each line animates sequentially with a delay, creating a
 * continuous wave effect that cycles to indicate an ongoing operation.
 *
 * @param modifier The modifier to be applied to the progress indicator's container.
 * @param color The color of the animated lines. Defaults to [Primary75].
 * @param animationDuration The duration in milliseconds for one complete animation cycle of each line
 *                          (from minimum height to maximum height and back to minimum).
 *                          A longer duration results in a slower animation. Defaults to 600ms.
 * @param animationDelay The delay in milliseconds between the start of each line's animation.
 *                       This creates the staggered wave effect. Defaults to 400ms.
 * @param startDelay The initial delay in milliseconds before the first line starts animating.
 *                   Allows for a delayed start of the entire animation sequence. Defaults to 0ms.
 * @param lineCount The number of vertical lines in the indicator. Must be a positive integer.
 *                  Defaults to 5.
 * @param maxLineHeight The maximum height the lines will scale to during their animation cycle.
 *                      Defaults to 32.dp.
 * @param minLineHeight The minimum height the lines will scale to during their animation cycle.
 *                      Defaults to 16.dp.
 * @param lineWidth The fixed width of each vertical line. Defaults to 3.dp.
 * @param lineSpacing The horizontal space between adjacent lines. Defaults to 4.dp.
 * @param lineCornerRadius The corner radius applied to the ends of each line (creates rounded rectangles).
 *                        Defaults to 3.dp.
 */
@Composable
fun LineScaleProgressIndicator(
    modifier: Modifier = Modifier,
    color: Color = Color.Gray,
    animationDuration: Int = DefaultAnimationDuration,
    animationDelay: Int = DefaultAnimationDelay,
    startDelay: Int = DefaultStartDelay,
    lineCount: Int = DefaultLineCount,
    maxLineHeight: Dp = DefaultMaxLineHeight,
    minLineHeight: Dp = DefaultMinLineHeight,
    lineWidth: Dp = DefaultLineWidth,
    lineSpacing: Dp = DefaultLineSpacing,
    lineCornerRadius: Dp = DefaultLineCornerRadius
) {
    val transition = rememberInfiniteTransition()

    val duration = startDelay + animationDuration + animationDelay

    val height = arrayListOf<Float>().apply {
        for (i in 0 until lineCount) {
            val delay = startDelay + animationDelay / (lineCount - 1) * i
            val height by transition.animateFloat(
                initialValue = 0f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = keyframes {
                        durationMillis = duration
                        0f at delay using LinearEasing
                        1f at delay + (animationDuration / 2) using LinearEasing
                        0f at delay + animationDuration
                        0f at duration
                    }
                )
            )
            add(height)
        }
    }

    val width = (lineWidth + lineSpacing) * lineCount - lineSpacing

    ProgressIndicator(modifier, width, maxLineHeight) {
        drawIndeterminateLineScaleIndicator(
            maxHeight = maxLineHeight.toPx(),
            height = height.map { lerp(minLineHeight, maxLineHeight, it).toPx() },
            width = lineWidth.toPx(),
            cornerRadius = lineCornerRadius.toPx(),
            spacing = lineSpacing.toPx(),
            color = color
        )
    }
}

/**
 * Draws the visual representation of the line scale progress indicator.
 *
 * This private function handles the low-level drawing operations to render
 * the animated lines with the specified properties.
 *
 * @param maxHeight The maximum possible height of a line in pixels (i.e., [maxLineHeight]).
 * @param height A list containing the current heights for each line based on their animation state.
 * @param width The fixed width of each line in pixels (i.e., [lineWidth]).
 * @param cornerRadius The corner radius in pixels applied to the ends of each line.
 * @param spacing The horizontal space between adjacent lines in pixels.
 * @param color The color to draw the lines.
 */
private fun DrawScope.drawIndeterminateLineScaleIndicator(
    maxHeight: Float,
    height: List<Float>,
    width: Float,
    cornerRadius: Float,
    spacing: Float,
    color: Color
) {

    for (i in height.indices) {
        val x = i * (width + spacing)
        val y = (maxHeight - height[i]) / 2
        drawRoundRect(
            color = color,
            topLeft = Offset(x, y),
            size = Size(width, height[i]),
            cornerRadius = CornerRadius(cornerRadius)
        )
    }
}

/**
 * Internal composable function that provides a standardized canvas for drawing rectangular progress indicators.
 *
 * This function creates a rectangular canvas with common accessibility semantics and focusability
 * for progress indicator components. It abstracts away the common setup required
 * for drawing custom progress indicators using the Canvas API.
 *
 * @param modifier The modifier to be applied to the canvas.
 * @param width The width of the rectangular canvas.
 * @param height The height of the rectangular canvas.
 * @param onDraw The drawing operation to perform on the canvas using [DrawScope].
 */
@Composable
internal fun ProgressIndicator(
    modifier: Modifier,
    width: Dp,
    height: Dp,
    onDraw: DrawScope.() -> Unit
) {
    Canvas(
        modifier = modifier
            .progressSemantics()
            .size(width, height)
            .focusable(),
        onDraw = onDraw
    )
}