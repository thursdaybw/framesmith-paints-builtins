package com.framesmith.media.paint.builtins

import com.framesmith.media.paint.PaintId
import com.framesmith.media.paint.PaintPlugin
import com.framesmith.media.paint.PaintPluginOutput
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
        output: PaintPluginOutput,
    ) {

        val parsed =
            try {
                parseLinearGradientParameters(paint.parameters)
            } catch (failure: FrameSmithPaintParameterException) {
                output.invalid(failure.message)
                return
            }
        val start = parsed.start.resolveIn(context)
        val end = parsed.end.resolveIn(context)

        if (start == end) {
            output.invalid("linear gradient start and end must differ")
            return
        }

        output.add(
            LinearGradientFillPaintExecution(
                stops = parsed.stops.map(FrameSmithPaintSpecs.GradientStopSpec::toExecutionStop),
                start = start,
                end = end,
            ),
        )

    }

}

private fun FrameSmithPaintSpecs.GradientStopSpec.toExecutionStop(): PaintExecutionGradientStop {

    return PaintExecutionGradientStop(offsetPercent, color)

}

private fun FrameSmithPaintSpecs.PercentPointSpec.resolveIn(context: PaintResolutionContext): PaintExecutionPoint {

    val bounds = context.bounds
    return PaintExecutionPoint(
        x = bounds.x + (bounds.widthPixels * xPercent / FULL_PERCENT),
        y = bounds.y + (bounds.heightPixels * yPercent / FULL_PERCENT),
    )

}
