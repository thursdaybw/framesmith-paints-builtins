package com.framesmith.media.paint.builtins

import com.framesmith.media.value.StructuredDecimal
import com.framesmith.media.value.StructuredObject

data class PercentPointSpec(
    val xPercent: Double,
    val yPercent: Double,
) {

    internal fun toPaintValue(): StructuredObject {

        return StructuredObject.of(
            GradientPaintParameters.X_PERCENT to StructuredDecimal(xPercent),
            GradientPaintParameters.Y_PERCENT to StructuredDecimal(yPercent),
        )

    }

    internal fun resolveIn(context: com.framesmith.media.paint.PaintResolutionContext): PaintExecutionPoint {

        val bounds = context.bounds
        return PaintExecutionPoint(
            x = bounds.x + (bounds.widthPixels * xPercent / GradientPaintParameters.FULL_PERCENT),
            y = bounds.y + (bounds.heightPixels * yPercent / GradientPaintParameters.FULL_PERCENT),
        )

    }

    companion object {

        fun center(): PercentPointSpec {

            return PercentPointSpec(GradientPaintParameters.CENTER_PERCENT, GradientPaintParameters.CENTER_PERCENT)

        }

        fun leftCenter(): PercentPointSpec {

            return PercentPointSpec(GradientPaintParameters.MINIMUM_PERCENT, GradientPaintParameters.CENTER_PERCENT)

        }

        fun rightCenter(): PercentPointSpec {

            return PercentPointSpec(GradientPaintParameters.FULL_PERCENT, GradientPaintParameters.CENTER_PERCENT)

        }

    }

}
