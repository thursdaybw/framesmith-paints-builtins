package com.framesmith.media.paint.builtins

import com.framesmith.media.paint.PaintExecutionId
import com.framesmith.media.paint.PaintId
import com.framesmith.media.paint.PaintSpec
import com.framesmith.media.value.StructuredDecimal
import com.framesmith.media.value.StructuredList
import com.framesmith.media.value.StructuredObject

object RadialGradientPaint {

    val id = PaintId("framesmith.paint.radial-gradient")

    val executionId = PaintExecutionId("framesmith.paint.execution.radial-gradient-fill")

    internal val plugin = RadialGradientPaintPlugin()

    fun spec(
        stops: List<GradientStopSpec>,
        center: PercentPointSpec = PercentPointSpec.center(),
        radiusPercentOfMinimumDimension: Double = DEFAULT_RADIUS_PERCENT,
    ): PaintSpec {

        return PaintSpec(
            id = id,
            parameters =
                StructuredObject.of(
                    STOPS_PARAMETER to StructuredList.from(stops.map(GradientStopSpec::toPaintValue)),
                    CENTER_PARAMETER to center.toPaintValue(),
                    RADIUS_PARAMETER to StructuredDecimal(radiusPercentOfMinimumDimension),
                ),
        )

    }

    fun details(paint: PaintSpec): RadialGradientPaintDetails {

        requirePaintIdentity(paint)
        val stops = GradientPaintParameters.parseStops(paint.parameters)
        val center = GradientPaintParameters.parsePoint(paint.parameters.objectValue(CENTER_PARAMETER), CENTER_PARAMETER)
        val radiusPercent = paint.parameters.number(RADIUS_PARAMETER)

        if (radiusPercent == null || !radiusPercent.isFinite() || radiusPercent <= 0.0) {
            throw FrameSmithPaintParameterException("radial gradient requires positive '$RADIUS_PARAMETER'")
        }

        return RadialGradientPaintDetails(
            stops = stops,
            center = center,
            radiusPercentOfMinimumDimension = radiusPercent,
        )

    }

    private fun requirePaintIdentity(paint: PaintSpec) {

        if (paint.id != id) {
            throw IllegalArgumentException("Expected paint '${id.value}', got '${paint.id.value}'")
        }

    }

    private const val STOPS_PARAMETER = "stops"

    private const val CENTER_PARAMETER = "center"

    private const val RADIUS_PARAMETER = "radiusPercentOfMinimumDimension"

    private const val DEFAULT_RADIUS_PERCENT = 50.0

}
