package com.framesmith.media.paint.builtins

import com.framesmith.media.paint.PaintId
import com.framesmith.media.paint.PaintOperation
import com.framesmith.media.paint.PaintPlugin
import com.framesmith.media.paint.PaintPluginResult
import com.framesmith.media.paint.PaintResolutionContext
import com.framesmith.media.paint.PaintSpec

internal class SolidPaintPlugin : PaintPlugin {

    override val paintId: PaintId = FrameSmithPaintIds.SOLID

    override fun resolve(
        paint: PaintSpec,
        context: PaintResolutionContext,
    ): PaintPluginResult {

        val color = paint.parameters.text(COLOR)

        if (color.isNullOrBlank()) {
            return PaintPluginResult.InvalidParameters("solid paint requires a non-blank '$COLOR'")
        }

        return PaintPluginResult.Resolved(
            listOf(PaintOperation.SolidFill(color)),
        )

    }

}
