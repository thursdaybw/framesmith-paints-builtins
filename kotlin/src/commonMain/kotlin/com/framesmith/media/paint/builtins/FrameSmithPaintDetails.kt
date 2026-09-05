package com.framesmith.media.paint.builtins

import com.framesmith.media.paint.PaintObject
import com.framesmith.media.paint.PaintSpec

data class SolidPaintDetails(
    val color: String,
)

data class SolidStrokePaintDetails(
    val color: String,
    val widthPercentOfHeight: Double,
)

data class LinearGradientPaintDetails(
    val stops: List<FrameSmithPaintSpecs.GradientStopSpec>,
    val start: FrameSmithPaintSpecs.PercentPointSpec,
    val end: FrameSmithPaintSpecs.PercentPointSpec,
)

data class RadialGradientPaintDetails(
    val stops: List<FrameSmithPaintSpecs.GradientStopSpec>,
    val center: FrameSmithPaintSpecs.PercentPointSpec,
    val radiusPercentOfMinimumDimension: Double,
)

object FrameSmithPaintDetails {

    fun solid(paint: PaintSpec): SolidPaintDetails {

        requirePaintId(paint, FrameSmithPaintIds.SOLID)
        val color = paint.parameters.text(COLOR)

        if (color.isNullOrBlank()) {
            throw FrameSmithPaintParameterException("solid paint requires a non-blank '$COLOR'")
        }

        return SolidPaintDetails(color)

    }

    fun solidStroke(paint: PaintSpec): SolidStrokePaintDetails {

        requirePaintId(paint, FrameSmithPaintIds.SOLID_STROKE)
        val color = paint.parameters.text(COLOR)
        val widthPercentOfHeight = paint.parameters.number(WIDTH_PERCENT_OF_HEIGHT)

        if (color.isNullOrBlank()) {
            throw FrameSmithPaintParameterException("solid stroke requires a non-blank '$COLOR'")
        }

        if (widthPercentOfHeight == null || !widthPercentOfHeight.isFinite() || widthPercentOfHeight <= 0.0) {
            throw FrameSmithPaintParameterException("solid stroke requires positive '$WIDTH_PERCENT_OF_HEIGHT'")
        }

        return SolidStrokePaintDetails(color, widthPercentOfHeight)

    }

    fun linearGradient(paint: PaintSpec): LinearGradientPaintDetails {

        requirePaintId(paint, FrameSmithPaintIds.LINEAR_GRADIENT)
        return parseLinearGradientParameters(paint.parameters)

    }

    fun radialGradient(paint: PaintSpec): RadialGradientPaintDetails {

        requirePaintId(paint, FrameSmithPaintIds.RADIAL_GRADIENT)
        return parseRadialGradientParameters(paint.parameters)

    }

    fun solidFrom(paints: List<PaintSpec>): SolidPaintDetails? {

        for (paint in paints) {
            if (paint.id == FrameSmithPaintIds.SOLID) {
                return solid(paint)
            }
        }

        return null

    }

    fun solidStrokeFrom(paints: List<PaintSpec>): SolidStrokePaintDetails? {

        for (paint in paints) {
            if (paint.id == FrameSmithPaintIds.SOLID_STROKE) {
                return solidStroke(paint)
            }
        }

        return null

    }

    fun isSolid(paint: PaintSpec): Boolean {

        return paint.id == FrameSmithPaintIds.SOLID

    }

}

class FrameSmithPaintParameterException(
    override val message: String,
) : IllegalArgumentException(message)

internal fun parseRadialGradientParameters(parameters: PaintObject): RadialGradientPaintDetails {

    val stops = parseGradientStops(parameters)
    val center = parsePercentPoint(parameters.objectValue(CENTER), CENTER)
    val radiusPercent = parameters.number(RADIUS_PERCENT_OF_MINIMUM_DIMENSION)

    if (radiusPercent == null || !radiusPercent.isFinite() || radiusPercent <= 0.0) {
        throw FrameSmithPaintParameterException(
            "radial gradient requires positive '$RADIUS_PERCENT_OF_MINIMUM_DIMENSION'",
        )
    }

    return RadialGradientPaintDetails(
        stops = stops,
        center = center,
        radiusPercentOfMinimumDimension = radiusPercent,
    )

}

internal fun parseLinearGradientParameters(parameters: PaintObject): LinearGradientPaintDetails {

    val stops = parseGradientStops(parameters)
    val start = parsePercentPoint(parameters.objectValue(START), START)
    val end = parsePercentPoint(parameters.objectValue(END), END)

    return LinearGradientPaintDetails(
        stops = stops,
        start = start,
        end = end,
    )

}

private fun requirePaintId(
    paint: PaintSpec,
    expectedId: com.framesmith.media.paint.PaintId,
) {

    if (paint.id != expectedId) {
        throw IllegalArgumentException("Expected paint '${expectedId.value}', got '${paint.id.value}'")
    }

}

private fun parseGradientStops(parameters: PaintObject): List<FrameSmithPaintSpecs.GradientStopSpec> {

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

    var previousOffset = MINIMUM_PERCENT
    for (stop in stops) {
        if (stop.offsetPercent < previousOffset) {
            throw FrameSmithPaintParameterException("gradient stops must be ordered by '$OFFSET_PERCENT'")
        }

        previousOffset = stop.offsetPercent
    }

    return stops.toList()

}

private fun parsePercentPoint(
    value: PaintObject?,
    parameterName: String,
): FrameSmithPaintSpecs.PercentPointSpec {

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

    return FrameSmithPaintSpecs.PercentPointSpec(xPercent, yPercent)

}

private const val MINIMUM_GRADIENT_STOP_COUNT = 2
