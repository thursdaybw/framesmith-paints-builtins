package com.framesmith.media.paint.builtins

import com.framesmith.media.paint.PaintId
import com.framesmith.media.paint.PaintPlugin
import com.framesmith.media.paint.PaintPluginOutput
import com.framesmith.media.paint.PaintResolutionContext
import com.framesmith.media.paint.PaintSpec
import kotlin.math.min

internal class RadialGradientPaintPlugin : PaintPlugin {

    override val paintId: PaintId = FrameSmithPaintIds.RADIAL_GRADIENT

    override fun resolve(
        paint: PaintSpec,
        context: PaintResolutionContext,
        output: PaintPluginOutput,
    ) {

        val parsed =
            try {
                FrameSmithPaintDetails.radialGradient(paint)
            } catch (failure: FrameSmithPaintParameterException) {
                output.invalid(failure.message)
                return
            }
        val center = parsed.center.resolveIn(context)
        val minimumDimension = min(context.bounds.widthPixels, context.bounds.heightPixels)
        val radiusPixels = minimumDimension * parsed.radiusPercentOfMinimumDimension / FULL_PERCENT

        if (!radiusPixels.isFinite() || radiusPixels <= 0.0) {
            output.invalid("radial gradient resolved radius must be positive")
            return
        }

        output.add(
            RadialGradientFillPaintExecution(
                stops = parsed.stops.map(FrameSmithPaintSpecs.GradientStopSpec::toExecutionStop),
                center = center,
                radiusPixels = radiusPixels,
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
