package com.framesmith.media.paint.builtins

import com.framesmith.media.paint.PaintPlugin
import com.framesmith.media.paint.PaintPluginOutput
import com.framesmith.media.paint.PaintResolutionContext
import com.framesmith.media.paint.PaintSpec
import kotlin.math.min

internal class RadialGradientPaintPlugin : PaintPlugin {

    override val paintId = RadialGradientPaint.id

    override fun resolve(
        paint: PaintSpec,
        context: PaintResolutionContext,
        output: PaintPluginOutput,
    ) {

        val parsed =
            try {
                RadialGradientPaint.details(paint)
            } catch (failure: FrameSmithPaintParameterException) {
                output.invalid(failure.message)
                return
            }
        val center = parsed.center.resolveIn(context)
        val minimumDimension = min(context.bounds.widthPixels, context.bounds.heightPixels)
        val radiusPixels = minimumDimension * parsed.radiusPercentOfMinimumDimension / GradientPaintParameters.FULL_PERCENT

        if (!radiusPixels.isFinite() || radiusPixels <= 0.0) {
            output.invalid("radial gradient resolved radius must be positive")
            return
        }

        output.add(
            RadialGradientFillPaintExecution(
                stops = parsed.stops.map(GradientExecutionValues::stopFrom),
                center = center,
                radiusPixels = radiusPixels,
            ),
        )

    }

}
