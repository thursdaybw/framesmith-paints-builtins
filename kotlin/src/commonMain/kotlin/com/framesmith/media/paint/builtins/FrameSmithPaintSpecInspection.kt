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

    data class RadialGradient(
        val stops: List<FrameSmithPaintSpecs.GradientStopSpec>,
        val center: FrameSmithPaintSpecs.PercentPointSpec,
        val radiusPercentOfMinimumDimension: Double,
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

    return try {
        val parsed = parseLinearGradientParameters(paint.parameters)
        FrameSmithPaintSpecInspection.LinearGradient(
            stops = parsed.stops,
            start = parsed.start,
            end = parsed.end,
        )
    } catch (failure: FrameSmithPaintParameterException) {
        FrameSmithPaintSpecInspection.InvalidFrameSmithPaint(paint.id, failure.message)
    }

}

internal fun inspectRadialGradientSpec(paint: PaintSpec): FrameSmithPaintSpecInspection {

    return try {
        val parsed = parseRadialGradientParameters(paint.parameters)
        FrameSmithPaintSpecInspection.RadialGradient(
            stops = parsed.stops,
            center = parsed.center,
            radiusPercentOfMinimumDimension = parsed.radiusPercentOfMinimumDimension,
        )
    } catch (failure: FrameSmithPaintParameterException) {
        FrameSmithPaintSpecInspection.InvalidFrameSmithPaint(paint.id, failure.message)
    }

}

internal data class LinearGradientPaintParameters(
    val stops: List<FrameSmithPaintSpecs.GradientStopSpec>,
    val start: FrameSmithPaintSpecs.PercentPointSpec,
    val end: FrameSmithPaintSpecs.PercentPointSpec,
)

internal data class RadialGradientPaintParameters(
    val stops: List<FrameSmithPaintSpecs.GradientStopSpec>,
    val center: FrameSmithPaintSpecs.PercentPointSpec,
    val radiusPercentOfMinimumDimension: Double,
)

internal class FrameSmithPaintParameterException(
    override val message: String,
) : IllegalArgumentException(message)

internal fun parseRadialGradientParameters(parameters: PaintObject): RadialGradientPaintParameters {

    val stops = parseGradientStops(parameters)
    val center = parsePercentPoint(parameters.objectValue(CENTER), CENTER)
    val radiusPercent = parameters.number(RADIUS_PERCENT_OF_MINIMUM_DIMENSION)

    if (radiusPercent == null || !radiusPercent.isFinite() || radiusPercent <= 0.0) {
        throw FrameSmithPaintParameterException(
            "radial gradient requires positive '$RADIUS_PERCENT_OF_MINIMUM_DIMENSION'",
        )
    }

    return RadialGradientPaintParameters(
        stops = stops,
        center = center,
        radiusPercentOfMinimumDimension = radiusPercent,
    )

}

internal fun parseLinearGradientParameters(parameters: PaintObject): LinearGradientPaintParameters {

    val stops = parseGradientStops(parameters)
    val start = parsePercentPoint(parameters.objectValue(START), START)
    val end = parsePercentPoint(parameters.objectValue(END), END)

    return LinearGradientPaintParameters(
        stops = stops,
        start = start,
        end = end,
    )

}

internal fun parseGradientStops(parameters: PaintObject): List<FrameSmithPaintSpecs.GradientStopSpec> {

    val values = parameters.list(STOPS)

    if (values == null || values.values.size < MINIMUM_GRADIENT_STOP_COUNT) {
        throw FrameSmithPaintParameterException("gradient requires at least two '$STOPS'")
    }

    val stops = mutableListOf<FrameSmithPaintSpecs.GradientStopSpec>()

    for (value in values.values) {
        val stop = value as? PaintObject

        if (stop == null) {
            throw FrameSmithPaintParameterException("each gradient stop must be an object")
        }

        val offsetPercent = stop.number(OFFSET_PERCENT)
        val color = stop.text(COLOR)

        if (offsetPercent == null || offsetPercent !in MINIMUM_PERCENT..FULL_PERCENT || color.isNullOrBlank()) {
            throw FrameSmithPaintParameterException("each gradient stop requires valid '$OFFSET_PERCENT' and '$COLOR'")
        }

        stops += FrameSmithPaintSpecs.GradientStopSpec(offsetPercent, color)
    }

    if (!stops.zipWithNext().all(::isOrderedGradientStopPair)) {
        throw FrameSmithPaintParameterException("gradient stops must be ordered by '$OFFSET_PERCENT'")
    }

    return stops.toList()

}

private fun isOrderedGradientStopPair(pair: Pair<FrameSmithPaintSpecs.GradientStopSpec, FrameSmithPaintSpecs.GradientStopSpec>): Boolean {

    return pair.first.offsetPercent <= pair.second.offsetPercent

}

private fun parsePercentPoint(
    value: PaintObject?,
    parameterName: String,
): FrameSmithPaintSpecs.PercentPointSpec {

    if (value == null) {
        throw FrameSmithPaintParameterException("linear gradient requires '$parameterName'")
    }

    val xPercent = value.number(X_PERCENT)
    val yPercent = value.number(Y_PERCENT)

    if (xPercent == null || yPercent == null) {
        throw FrameSmithPaintParameterException(
            "linear gradient '$parameterName' requires '$X_PERCENT' and '$Y_PERCENT'",
        )
    }

    if (xPercent !in MINIMUM_PERCENT..FULL_PERCENT || yPercent !in MINIMUM_PERCENT..FULL_PERCENT) {
        throw FrameSmithPaintParameterException("linear gradient '$parameterName' percentages must be from 0 to 100")
    }

    return FrameSmithPaintSpecs.PercentPointSpec(xPercent, yPercent)

}

private const val MINIMUM_GRADIENT_STOP_COUNT = 2
