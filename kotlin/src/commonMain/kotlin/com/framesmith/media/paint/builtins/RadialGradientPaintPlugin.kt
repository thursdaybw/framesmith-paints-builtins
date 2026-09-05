package com.framesmith.media.paint.builtins

import com.framesmith.media.paint.PaintId
import com.framesmith.media.paint.PaintOperation
import com.framesmith.media.paint.PaintPlugin
import com.framesmith.media.paint.PaintPluginResult
import com.framesmith.media.paint.PaintResolutionContext
import com.framesmith.media.paint.PaintSpec
import kotlin.math.min

internal class RadialGradientPaintPlugin :
    PaintPlugin,
    FrameSmithPaintSpecInspector {

    override val paintId: PaintId = FrameSmithPaintIds.RADIAL_GRADIENT

    override fun inspect(paint: PaintSpec): FrameSmithPaintSpecInspection {

        return inspectRadialGradientSpec(paint)

    }

    override fun resolve(
        paint: PaintSpec,
        context: PaintResolutionContext,
    ): PaintPluginResult {

        val parsed = parseRadialGradientSpec(paint.parameters)

        if (parsed is ParsedRadialGradientSpec.Invalid) {
            return PaintPluginResult.InvalidParameters(parsed.reason)
        }

        parsed as ParsedRadialGradientSpec.Valid
        val center = parsed.center.resolveIn(context)
        val minimumDimension = min(context.bounds.widthPixels, context.bounds.heightPixels)
        val radiusPixels = minimumDimension * parsed.radiusPercentOfMinimumDimension / FULL_PERCENT

        if (!radiusPixels.isFinite() || radiusPixels <= 0.0) {
            return PaintPluginResult.InvalidParameters("radial gradient resolved radius must be positive")
        }

        return PaintPluginResult.Resolved(
            listOf(
                PaintOperation.RadialGradientFill(
                    stops = parsed.stops.map(FrameSmithPaintSpecs.GradientStopSpec::toRadialOperationStop),
                    center = center,
                    radiusPixels = radiusPixels,
                ),
            ),
        )

    }

}

private fun FrameSmithPaintSpecs.GradientStopSpec.toRadialOperationStop(): PaintOperation.GradientStop {

    return PaintOperation.GradientStop(offsetPercent, color)

}

private fun FrameSmithPaintSpecs.PercentPointSpec.resolveIn(context: PaintResolutionContext): PaintOperation.Point {

    val bounds = context.bounds
    return PaintOperation.Point(
        x = bounds.x + (bounds.widthPixels * xPercent / FULL_PERCENT),
        y = bounds.y + (bounds.heightPixels * yPercent / FULL_PERCENT),
    )

}
