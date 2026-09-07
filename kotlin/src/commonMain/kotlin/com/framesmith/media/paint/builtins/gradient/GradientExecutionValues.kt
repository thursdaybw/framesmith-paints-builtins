package com.framesmith.media.paint.builtins

internal object GradientExecutionValues {

    fun stopFrom(spec: GradientStopSpec): PaintExecutionGradientStop {

        return PaintExecutionGradientStop(
            offsetPercent = spec.offsetPercent,
            color = spec.color,
        )

    }

}
