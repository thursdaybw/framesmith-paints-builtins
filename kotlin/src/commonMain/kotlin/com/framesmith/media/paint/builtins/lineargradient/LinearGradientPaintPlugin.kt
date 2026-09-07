package com.framesmith.media.paint.builtins

import com.framesmith.media.paint.PaintPlugin
import com.framesmith.media.paint.PaintPluginOutput
import com.framesmith.media.paint.PaintResolutionContext
import com.framesmith.media.paint.PaintSpec

internal class LinearGradientPaintPlugin : PaintPlugin {

    override val paintId = LinearGradientPaint.id

    override fun resolve(
        paint: PaintSpec,
        context: PaintResolutionContext,
        output: PaintPluginOutput,
    ) {

        val parsed =
            try {
                LinearGradientPaint.details(paint)
            } catch (failure: FrameSmithPaintParameterException) {
                output.invalid(failure.message)
                return
            }
        val start = parsed.start.resolveIn(context)
        val end = parsed.end.resolveIn(context)

        if (start == end) {
            output.invalid("linear gradient start and end must differ")
            return
        }

        output.add(
            LinearGradientFillPaintExecution(
                stops = parsed.stops.map(GradientExecutionValues::stopFrom),
                start = start,
                end = end,
            ),
        )

    }

}
