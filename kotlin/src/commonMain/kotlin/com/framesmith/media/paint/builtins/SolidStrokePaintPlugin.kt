package com.framesmith.media.paint.builtins

import com.framesmith.media.paint.PaintId
import com.framesmith.media.paint.PaintPlugin
import com.framesmith.media.paint.PaintPluginOutput
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
        output: PaintPluginOutput,
    ) {

        val color = paint.parameters.text(COLOR)
        val widthPercentOfHeight = paint.parameters.number(WIDTH_PERCENT_OF_HEIGHT)

        if (color.isNullOrBlank()) {
            output.invalid("solid stroke requires a non-blank '$COLOR'")
            return
        }

        if (widthPercentOfHeight == null || !widthPercentOfHeight.isFinite() || widthPercentOfHeight <= 0.0) {
            output.invalid("solid stroke requires positive '$WIDTH_PERCENT_OF_HEIGHT'")
            return
        }

        val widthPixels = context.bounds.heightPixels * widthPercentOfHeight / PERCENT_BASE

        if (!widthPixels.isFinite() || widthPixels <= 0.0) {
            output.invalid("solid stroke resolved width must be positive")
            return
        }

        output.add(
            SolidStrokePaintExecution(
                color = color,
                widthPixels = widthPixels,
            ),
        )

    }

    private companion object {

        const val PERCENT_BASE = 100.0

    }

}
