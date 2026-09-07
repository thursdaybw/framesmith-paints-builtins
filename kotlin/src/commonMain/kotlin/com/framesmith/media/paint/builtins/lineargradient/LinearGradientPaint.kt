package com.framesmith.media.paint.builtins

import com.framesmith.media.paint.PaintExecutionId
import com.framesmith.media.paint.PaintId
import com.framesmith.media.paint.PaintSpec
import com.framesmith.media.value.StructuredList
import com.framesmith.media.value.StructuredObject

object LinearGradientPaint {

    val id = PaintId("framesmith.paint.linear-gradient")

    val executionId = PaintExecutionId("framesmith.paint.execution.linear-gradient-fill")

    internal val plugin = LinearGradientPaintPlugin()

    fun spec(
        stops: List<GradientStopSpec>,
        start: PercentPointSpec = PercentPointSpec.leftCenter(),
        end: PercentPointSpec = PercentPointSpec.rightCenter(),
    ): PaintSpec {

        return PaintSpec(
            id = id,
            parameters =
                StructuredObject.of(
                    STOPS_PARAMETER to StructuredList.from(stops.map(GradientStopSpec::toPaintValue)),
                    START_PARAMETER to start.toPaintValue(),
                    END_PARAMETER to end.toPaintValue(),
                ),
        )

    }

    fun details(paint: PaintSpec): LinearGradientPaintDetails {

        requirePaintIdentity(paint)
        val stops = GradientPaintParameters.parseStops(paint.parameters)
        val start = GradientPaintParameters.parsePoint(paint.parameters.objectValue(START_PARAMETER), START_PARAMETER)
        val end = GradientPaintParameters.parsePoint(paint.parameters.objectValue(END_PARAMETER), END_PARAMETER)

        return LinearGradientPaintDetails(
            stops = stops,
            start = start,
            end = end,
        )

    }

    private fun requirePaintIdentity(paint: PaintSpec) {

        if (paint.id != id) {
            throw IllegalArgumentException("Expected paint '${id.value}', got '${paint.id.value}'")
        }

    }

    private const val STOPS_PARAMETER = "stops"

    private const val START_PARAMETER = "start"

    private const val END_PARAMETER = "end"

}
