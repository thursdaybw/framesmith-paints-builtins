package com.framesmith.media.paint.builtins

import com.framesmith.media.value.StructuredObject

internal object GradientPaintParameters {

    const val COLOR = "color"

    const val STOPS = "stops"

    const val OFFSET_PERCENT = "offsetPercent"

    const val X_PERCENT = "xPercent"

    const val Y_PERCENT = "yPercent"

    const val MINIMUM_PERCENT = 0.0

    const val CENTER_PERCENT = 50.0

    const val FULL_PERCENT = 100.0

    fun parseStops(parameters: StructuredObject): List<GradientStopSpec> {

        val values = parameters.list(STOPS)

        if (values == null || values.values.size < MINIMUM_GRADIENT_STOP_COUNT) {
            throw FrameSmithPaintParameterException("gradient requires at least two '$STOPS'")
        }

        val stops = mutableListOf<GradientStopSpec>()

        for (value in values.values) {
            val stop = value as? StructuredObject

            if (stop == null) {
                throw FrameSmithPaintParameterException("each gradient stop must be an object")
            }

            stops += parseStop(stop)
        }

        requireOrderedStops(stops)
        return stops.toList()

    }

    fun parsePoint(
        value: StructuredObject?,
        parameterName: String,
    ): PercentPointSpec {

        if (value == null) {
            throw FrameSmithPaintParameterException("gradient requires '$parameterName'")
        }

        val xPercent = value.number(X_PERCENT)
        val yPercent = value.number(Y_PERCENT)

        if (xPercent == null || yPercent == null) {
            throw FrameSmithPaintParameterException(
                "gradient '$parameterName' requires '$X_PERCENT' and '$Y_PERCENT'",
            )
        }

        if (xPercent !in MINIMUM_PERCENT..FULL_PERCENT || yPercent !in MINIMUM_PERCENT..FULL_PERCENT) {
            throw FrameSmithPaintParameterException("gradient '$parameterName' percentages must be from 0 to 100")
        }

        return PercentPointSpec(xPercent, yPercent)

    }

    private fun parseStop(stop: StructuredObject): GradientStopSpec {

        val offsetPercent = stop.number(OFFSET_PERCENT)
        val color = stop.text(COLOR)

        if (offsetPercent == null || offsetPercent !in MINIMUM_PERCENT..FULL_PERCENT || color.isNullOrBlank()) {
            throw FrameSmithPaintParameterException("each gradient stop requires valid '$OFFSET_PERCENT' and '$COLOR'")
        }

        return GradientStopSpec(offsetPercent, color)

    }

    private fun requireOrderedStops(stops: List<GradientStopSpec>) {

        var previousOffset = MINIMUM_PERCENT

        for (stop in stops) {
            if (stop.offsetPercent < previousOffset) {
                throw FrameSmithPaintParameterException("gradient stops must be ordered by '$OFFSET_PERCENT'")
            }

            previousOffset = stop.offsetPercent
        }

    }

    private const val MINIMUM_GRADIENT_STOP_COUNT = 2

}
