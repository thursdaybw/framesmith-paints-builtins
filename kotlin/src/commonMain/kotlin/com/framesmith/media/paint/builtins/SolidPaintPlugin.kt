package com.framesmith.media.paint.builtins

import com.framesmith.media.paint.PaintId
import com.framesmith.media.paint.PaintPlugin
import com.framesmith.media.paint.PaintPluginOutput
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
        output: PaintPluginOutput,
    ) {

        val color = paint.parameters.text(COLOR)

        if (color.isNullOrBlank()) {
            output.invalid("solid paint requires a non-blank '$COLOR'")
            return
        }

        output.add(SolidFillPaintExecution(color))

    }

}
