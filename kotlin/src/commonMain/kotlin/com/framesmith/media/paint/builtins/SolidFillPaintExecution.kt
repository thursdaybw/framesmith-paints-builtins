package com.framesmith.media.paint.builtins

import com.framesmith.media.paint.PaintExecutionSpec

/** Host-neutral execution request for a solid fill. */
data class SolidFillPaintExecution(
    val color: String,
) : PaintExecutionSpec {

    override val id = FrameSmithPaintExecutionIds.SOLID_FILL

    init {

        require(color.isNotBlank()) { "SolidFillPaintExecution color must not be blank" }

    }

}
