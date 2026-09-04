package com.framesmith.media.paint.builtins

import com.framesmith.media.paint.PaintId
import com.framesmith.media.paint.PaintObject
import com.framesmith.media.paint.PaintSpec

/** Product-facing inspection of authored specs owned by FrameSmith's first-party paint package. */
sealed interface FrameSmithPaintSpecInspection {

    data class Solid(
        val color: String,
    ) : FrameSmithPaintSpecInspection

    data class SolidStroke(
        val color: String,
        val widthPercentOfHeight: Double,
    ) : FrameSmithPaintSpecInspection

    data class LinearGradient(
        val stops: List<FrameSmithPaintSpecs.GradientStopSpec>,
        val start: FrameSmithPaintSpecs.PercentPointSpec,
        val end: FrameSmithPaintSpecs.PercentPointSpec,
    ) : FrameSmithPaintSpecInspection

    data class InvalidFrameSmithPaint(
        val paintId: PaintId,
        val reason: String,
    ) : FrameSmithPaintSpecInspection

    data object OtherPaint : FrameSmithPaintSpecInspection

}

internal interface FrameSmithPaintSpecInspector {

    val paintId: PaintId

    fun inspect(paint: PaintSpec): FrameSmithPaintSpecInspection

}

fun FrameSmithPaintSpecs.inspect(paint: PaintSpec): FrameSmithPaintSpecInspection {

    return FrameSmithPaintPlugins.inspect(paint)

}

internal fun inspectSolidSpec(paint: PaintSpec): FrameSmithPaintSpecInspection {

    val color = paint.parameters.text(COLOR)

    if (color.isNullOrBlank()) {
        return FrameSmithPaintSpecInspection.InvalidFrameSmithPaint(
            paint.id,
            "solid paint requires a non-blank '$COLOR'",
        )
    }

    return FrameSmithPaintSpecInspection.Solid(color)

}

internal fun inspectSolidStrokeSpec(paint: PaintSpec): FrameSmithPaintSpecInspection {

    val color = paint.parameters.text(COLOR)
    val widthPercentOfHeight = paint.parameters.number(WIDTH_PERCENT_OF_HEIGHT)

    if (color.isNullOrBlank()) {
        return FrameSmithPaintSpecInspection.InvalidFrameSmithPaint(
            paint.id,
            "solid stroke requires a non-blank '$COLOR'",
        )
    }

    if (widthPercentOfHeight == null || !widthPercentOfHeight.isFinite() || widthPercentOfHeight <= 0.0) {
        return FrameSmithPaintSpecInspection.InvalidFrameSmithPaint(
            paint.id,
            "solid stroke requires positive '$WIDTH_PERCENT_OF_HEIGHT'",
        )
    }

    return FrameSmithPaintSpecInspection.SolidStroke(color, widthPercentOfHeight)

}

internal fun inspectLinearGradientSpec(paint: PaintSpec): FrameSmithPaintSpecInspection {

    return when (val parsed = parseGradientSpec(paint.parameters)) {
        is ParsedGradientSpec.Valid -> {
            FrameSmithPaintSpecInspection.LinearGradient(
                stops = parsed.stops,
                start = parsed.start,
                end = parsed.end,
            )
        }

        is ParsedGradientSpec.Invalid -> {
            FrameSmithPaintSpecInspection.InvalidFrameSmithPaint(paint.id, parsed.reason)
        }
    }

}

internal sealed interface ParsedGradientSpec {

    data class Valid(
        val stops: List<FrameSmithPaintSpecs.GradientStopSpec>,
        val start: FrameSmithPaintSpecs.PercentPointSpec,
        val end: FrameSmithPaintSpecs.PercentPointSpec,
    ) : ParsedGradientSpec

    data class Invalid(
        val reason: String,
    ) : ParsedGradientSpec

}

internal fun parseGradientSpec(parameters: PaintObject): ParsedGradientSpec {

    val stops = parseGradientStops(parameters)

    if (stops is ParsedGradientStops.Invalid) {
        return ParsedGradientSpec.Invalid(stops.reason)
    }

    val start = parsePercentPoint(parameters.objectValue(START), START)

    if (start is ParsedPercentPoint.Invalid) {
        return ParsedGradientSpec.Invalid(start.reason)
    }

    val end = parsePercentPoint(parameters.objectValue(END), END)

    if (end is ParsedPercentPoint.Invalid) {
        return ParsedGradientSpec.Invalid(end.reason)
    }

    return ParsedGradientSpec.Valid(
        stops = (stops as ParsedGradientStops.Valid).stops,
        start = (start as ParsedPercentPoint.Valid).point,
        end = (end as ParsedPercentPoint.Valid).point,
    )

}

private sealed interface ParsedGradientStops {

    data class Valid(
        val stops: List<FrameSmithPaintSpecs.GradientStopSpec>,
    ) : ParsedGradientStops

    data class Invalid(
        val reason: String,
    ) : ParsedGradientStops

}

private fun parseGradientStops(parameters: PaintObject): ParsedGradientStops {

    val values = parameters.list(STOPS)

    if (values == null || values.values.size < MINIMUM_GRADIENT_STOP_COUNT) {
        return ParsedGradientStops.Invalid("linear gradient requires at least two '$STOPS'")
    }

    val stops = mutableListOf<FrameSmithPaintSpecs.GradientStopSpec>()

    for (value in values.values) {
        val stop = value as? PaintObject

        if (stop == null) {
            return ParsedGradientStops.Invalid("each linear gradient stop must be an object")
        }

        val offsetPercent = stop.number(OFFSET_PERCENT)
        val color = stop.text(COLOR)

        if (offsetPercent == null || offsetPercent !in MINIMUM_PERCENT..FULL_PERCENT || color.isNullOrBlank()) {
            return ParsedGradientStops.Invalid("each linear gradient stop requires valid '$OFFSET_PERCENT' and '$COLOR'")
        }

        stops += FrameSmithPaintSpecs.GradientStopSpec(offsetPercent, color)
    }

    if (!stops.zipWithNext().all(::isOrderedGradientStopPair)) {
        return ParsedGradientStops.Invalid("linear gradient stops must be ordered by '$OFFSET_PERCENT'")
    }

    return ParsedGradientStops.Valid(stops.toList())

}

private fun isOrderedGradientStopPair(pair: Pair<FrameSmithPaintSpecs.GradientStopSpec, FrameSmithPaintSpecs.GradientStopSpec>): Boolean {

    return pair.first.offsetPercent <= pair.second.offsetPercent

}

private sealed interface ParsedPercentPoint {

    data class Valid(
        val point: FrameSmithPaintSpecs.PercentPointSpec,
    ) : ParsedPercentPoint

    data class Invalid(
        val reason: String,
    ) : ParsedPercentPoint

}

private fun parsePercentPoint(
    value: PaintObject?,
    parameterName: String,
): ParsedPercentPoint {

    if (value == null) {
        return ParsedPercentPoint.Invalid("linear gradient requires '$parameterName'")
    }

    val xPercent = value.number(X_PERCENT)
    val yPercent = value.number(Y_PERCENT)

    if (xPercent == null || yPercent == null) {
        return ParsedPercentPoint.Invalid("linear gradient '$parameterName' requires '$X_PERCENT' and '$Y_PERCENT'")
    }

    if (xPercent !in MINIMUM_PERCENT..FULL_PERCENT || yPercent !in MINIMUM_PERCENT..FULL_PERCENT) {
        return ParsedPercentPoint.Invalid("linear gradient '$parameterName' percentages must be from 0 to 100")
    }

    return ParsedPercentPoint.Valid(FrameSmithPaintSpecs.PercentPointSpec(xPercent, yPercent))

}

private const val MINIMUM_GRADIENT_STOP_COUNT = 2
