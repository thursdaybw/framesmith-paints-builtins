package com.framesmith.media.paint.builtins

import com.framesmith.media.paint.PaintId
import com.framesmith.media.paint.PaintOperation
import com.framesmith.media.paint.PaintPlugin
import com.framesmith.media.paint.PaintPluginResult
import com.framesmith.media.paint.PaintResolutionContext
import com.framesmith.media.paint.PaintSpec

internal class LinearGradientPaintPlugin :
    PaintPlugin,
    FrameSmithPaintSpecInspector {

    override val paintId: PaintId = FrameSmithPaintIds.LINEAR_GRADIENT

    override fun inspect(paint: PaintSpec): FrameSmithPaintSpecInspection {

        return inspectLinearGradientSpec(paint)

    }

    override fun resolve(
        paint: PaintSpec,
        context: PaintResolutionContext,
    ): PaintPluginResult {

        val parsed = parseGradientSpec(paint.parameters)

        if (parsed is ParsedGradientSpec.Invalid) {
            return PaintPluginResult.InvalidParameters(parsed.reason)
        }

        parsed as ParsedGradientSpec.Valid
        val start = parsed.start.resolveIn(context)
        val end = parsed.end.resolveIn(context)

        if (start == end) {
            return PaintPluginResult.InvalidParameters("linear gradient start and end must differ")
        }

        return PaintPluginResult.Resolved(
            listOf(
                PaintOperation.LinearGradientFill(
                    stops = parsed.stops.map(FrameSmithPaintSpecs.GradientStopSpec::toOperationStop),
                    start = start,
                    end = end,
                ),
            ),
        )

    }

}

private fun FrameSmithPaintSpecs.GradientStopSpec.toOperationStop(): PaintOperation.GradientStop {

    return PaintOperation.GradientStop(offsetPercent, color)

}

private fun FrameSmithPaintSpecs.PercentPointSpec.resolveIn(context: PaintResolutionContext): PaintOperation.Point {

    val bounds = context.bounds
    return PaintOperation.Point(
        x = bounds.x + (bounds.widthPixels * xPercent / FULL_PERCENT),
        y = bounds.y + (bounds.heightPixels * yPercent / FULL_PERCENT),
    )

}
