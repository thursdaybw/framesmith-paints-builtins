package com.framesmith.media.paint.builtins

import com.framesmith.media.paint.PaintId
import com.framesmith.media.paint.PaintPlugin
import com.framesmith.media.paint.PaintPluginOutput
import com.framesmith.media.paint.PaintResolutionContext
import com.framesmith.media.paint.PaintSpec

internal class SolidStrokePaintPlugin : PaintPlugin {

    override val paintId: PaintId = FrameSmithPaintIds.SOLID_STROKE

    override fun resolve(
        paint: PaintSpec,
        context: PaintResolutionContext,
        output: PaintPluginOutput,
    ) {

        val details =
            try {
                FrameSmithPaintDetails.solidStroke(paint)
            } catch (failure: FrameSmithPaintParameterException) {
                output.invalid(failure.message)
                return
            }

        val widthPixels = context.bounds.heightPixels * details.widthPercentOfHeight / PERCENT_BASE

        if (!widthPixels.isFinite() || widthPixels <= 0.0) {
            output.invalid("solid stroke resolved width must be positive")
            return
        }

        output.add(
            SolidStrokePaintExecution(
                color = details.color,
                widthPixels = widthPixels,
            ),
        )

    }

    private companion object {

        const val PERCENT_BASE = 100.0

    }

}
