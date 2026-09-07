package com.framesmith.media.paint.builtins

import com.framesmith.media.paint.PaintPlugin
import com.framesmith.media.paint.PaintPluginOutput
import com.framesmith.media.paint.PaintResolutionContext
import com.framesmith.media.paint.PaintSpec

internal class SolidPaintPlugin : PaintPlugin {

    override val paintId = SolidPaint.id

    override fun resolve(
        paint: PaintSpec,
        context: PaintResolutionContext,
        output: PaintPluginOutput,
    ) {

        val details =
            try {
                SolidPaint.details(paint)
            } catch (failure: FrameSmithPaintParameterException) {
                output.invalid(failure.message)
                return
            }

        output.add(SolidFillPaintExecution(details.color))

    }

}
