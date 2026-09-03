package com.framesmith.media.paint.builtins

import com.framesmith.media.paint.PaintId
import com.framesmith.media.paint.PaintList
import com.framesmith.media.paint.PaintObject
import com.framesmith.media.paint.PaintOperation
import com.framesmith.media.paint.PaintPlugin
import com.framesmith.media.paint.PaintPluginResult
import com.framesmith.media.paint.PaintResolutionContext
import com.framesmith.media.paint.PaintSpec

internal class LinearGradientPaintPlugin : PaintPlugin {

    override val paintId: PaintId = FrameSmithPaintIds.LINEAR_GRADIENT

    override fun resolve(
        paint: PaintSpec,
        context: PaintResolutionContext,
    ): PaintPluginResult {

        val parsed = parseGradient(paint.parameters)

        if (parsed is GradientParameters.Invalid) {
            return PaintPluginResult.InvalidParameters(parsed.reason)
        }

        parsed as GradientParameters.Valid
        val start = parsed.start.resolveIn(context)
        val end = parsed.end.resolveIn(context)

        if (start == end) {
            return PaintPluginResult.InvalidParameters("linear gradient start and end must differ")
        }

        return PaintPluginResult.Resolved(
            listOf(
                PaintOperation.LinearGradientFill(
                    stops = parsed.stops,
                    start = start,
                    end = end,
                ),
            ),
        )

    }

}

private sealed interface GradientParameters {

    data class Valid(
        val stops: List<PaintOperation.GradientStop>,
        val start: PercentPoint,
        val end: PercentPoint,
    ) : GradientParameters

    data class Invalid(
        val reason: String,
    ) : GradientParameters

}

private data class PercentPoint(
    val xPercent: Double,
    val yPercent: Double,
) {

    fun resolveIn(context: PaintResolutionContext): PaintOperation.Point {

        val bounds = context.bounds
        return PaintOperation.Point(
            x = bounds.x + (bounds.widthPixels * xPercent / FULL_PERCENT),
            y = bounds.y + (bounds.heightPixels * yPercent / FULL_PERCENT),
        )

    }

}

private fun parseGradient(parameters: PaintObject): GradientParameters {

    val stops = parseStops(parameters.list(STOPS))

    if (stops is ParsedStops.Invalid) {
        return GradientParameters.Invalid(stops.reason)
    }

    val start = parsePoint(parameters.objectValue(START), START)

    if (start is ParsedPoint.Invalid) {
        return GradientParameters.Invalid(start.reason)
    }

    val end = parsePoint(parameters.objectValue(END), END)

    if (end is ParsedPoint.Invalid) {
        return GradientParameters.Invalid(end.reason)
    }

    return GradientParameters.Valid(
        stops = (stops as ParsedStops.Valid).stops,
        start = (start as ParsedPoint.Valid).point,
        end = (end as ParsedPoint.Valid).point,
    )

}

private sealed interface ParsedStops {

    data class Valid(
        val stops: List<PaintOperation.GradientStop>,
    ) : ParsedStops

    data class Invalid(
        val reason: String,
    ) : ParsedStops

}

private fun parseStops(values: PaintList?): ParsedStops {

    if (values == null || values.values.size < MINIMUM_GRADIENT_STOP_COUNT) {
        return ParsedStops.Invalid("linear gradient requires at least two '$STOPS'")
    }

    val stops = mutableListOf<PaintOperation.GradientStop>()

    for (value in values.values) {
        val stop = value as? PaintObject

        if (stop == null) {
            return ParsedStops.Invalid("each linear gradient stop must be an object")
        }

        val offsetPercent = stop.number(OFFSET_PERCENT)
        val color = stop.text(COLOR)

        if (offsetPercent == null || offsetPercent !in MINIMUM_PERCENT..FULL_PERCENT || color.isNullOrBlank()) {
            return ParsedStops.Invalid("each linear gradient stop requires valid '$OFFSET_PERCENT' and '$COLOR'")
        }

        stops += PaintOperation.GradientStop(offsetPercent, color)
    }

    if (!stops.zipWithNext().all { (first, second) -> first.offsetPercent <= second.offsetPercent }) {
        return ParsedStops.Invalid("linear gradient stops must be ordered by '$OFFSET_PERCENT'")
    }

    return ParsedStops.Valid(stops.toList())

}

private sealed interface ParsedPoint {

    data class Valid(
        val point: PercentPoint,
    ) : ParsedPoint

    data class Invalid(
        val reason: String,
    ) : ParsedPoint

}

private fun parsePoint(
    value: PaintObject?,
    parameterName: String,
): ParsedPoint {

    if (value == null) {
        return ParsedPoint.Invalid("linear gradient requires '$parameterName'")
    }

    val xPercent = value.number(X_PERCENT)
    val yPercent = value.number(Y_PERCENT)

    if (xPercent == null || yPercent == null) {
        return ParsedPoint.Invalid("linear gradient '$parameterName' requires '$X_PERCENT' and '$Y_PERCENT'")
    }

    if (xPercent !in MINIMUM_PERCENT..FULL_PERCENT || yPercent !in MINIMUM_PERCENT..FULL_PERCENT) {
        return ParsedPoint.Invalid("linear gradient '$parameterName' percentages must be from 0 to 100")
    }

    return ParsedPoint.Valid(PercentPoint(xPercent, yPercent))

}

private const val MINIMUM_GRADIENT_STOP_COUNT = 2
