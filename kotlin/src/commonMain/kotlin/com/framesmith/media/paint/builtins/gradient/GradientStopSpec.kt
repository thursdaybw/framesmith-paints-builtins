package com.framesmith.media.paint.builtins

import com.framesmith.media.value.StructuredDecimal
import com.framesmith.media.value.StructuredObject
import com.framesmith.media.value.StructuredText

data class GradientStopSpec(
    val offsetPercent: Double,
    val color: String,
) {

    internal fun toPaintValue(): StructuredObject {

        return StructuredObject.of(
            GradientPaintParameters.OFFSET_PERCENT to StructuredDecimal(offsetPercent),
            GradientPaintParameters.COLOR to StructuredText(color),
        )

    }

}
