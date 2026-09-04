package com.framesmith.media.paint.builtins

import com.framesmith.media.paint.PaintId
import com.framesmith.media.paint.PaintOperation
import com.framesmith.media.paint.PaintPlugin
import com.framesmith.media.paint.PaintPluginResult
import com.framesmith.media.paint.PaintResolutionContext
import com.framesmith.media.paint.PaintSpec

internal class SolidStrokePaintPlugin :
    PaintPlugin,
    FrameSmithPaintSpecInspector {

    override val paintId: PaintId = FrameSmithPaintIds.SOLID_STROKE

    override fun inspect(paint: PaintSpec): FrameSmithPaintSpecInspection {

        return inspectSolidStrokeSpec(paint)

    }

    override fun resolve(
        paint: PaintSpec,
        context: PaintResolutionContext,
    ): PaintPluginResult {

        return when (val inspection = inspect(paint)) {
            is FrameSmithPaintSpecInspection.SolidStroke -> {
                val widthPixels = context.bounds.heightPixels * inspection.widthPercentOfHeight / PERCENT_BASE

                if (!widthPixels.isFinite() || widthPixels <= 0.0) {
                    return PaintPluginResult.InvalidParameters("solid stroke resolved width must be positive")
                }

                PaintPluginResult.Resolved(
                    listOf(
                        PaintOperation.SolidStroke(
                            color = inspection.color,
                            widthPixels = widthPixels,
                        ),
                    ),
                )
            }

            is FrameSmithPaintSpecInspection.InvalidFrameSmithPaint -> {
                PaintPluginResult.InvalidParameters(inspection.reason)
            }

            else -> {
                PaintPluginResult.InvalidParameters("solid stroke inspection did not return stroke details")
            }
        }

    }

    private companion object {

        const val PERCENT_BASE = 100.0

    }

}
