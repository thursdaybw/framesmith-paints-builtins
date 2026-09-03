package com.framesmith.media.paint.builtins

import com.framesmith.media.paint.PaintId
import com.framesmith.media.paint.PaintOperation
import com.framesmith.media.paint.PaintPlugin
import com.framesmith.media.paint.PaintPluginResult
import com.framesmith.media.paint.PaintResolutionContext
import com.framesmith.media.paint.PaintSpec

internal class SolidPaintPlugin :
    PaintPlugin,
    FrameSmithPaintSpecInspector {

    override val paintId: PaintId = FrameSmithPaintIds.SOLID

    override fun inspect(paint: PaintSpec): FrameSmithPaintSpecInspection {

        return inspectSolidSpec(paint)

    }

    override fun resolve(
        paint: PaintSpec,
        context: PaintResolutionContext,
    ): PaintPluginResult {

        return when (val inspection = inspect(paint)) {
            is FrameSmithPaintSpecInspection.Solid -> {
                PaintPluginResult.Resolved(listOf(PaintOperation.SolidFill(inspection.color)))
            }

            is FrameSmithPaintSpecInspection.InvalidFrameSmithPaint -> {
                PaintPluginResult.InvalidParameters(inspection.reason)
            }

            else -> {
                PaintPluginResult.InvalidParameters("solid paint inspection did not return solid details")
            }
        }

    }

}
