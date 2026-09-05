package com.framesmith.media.paint.builtins

import com.framesmith.media.paint.PaintExecutionSpec

/** Host-neutral execution request for a solid stroke. */
data class SolidStrokePaintExecution(
    val color: String,
    val widthPixels: Double,
) : PaintExecutionSpec {

    override val id = FrameSmithPaintExecutionIds.SOLID_STROKE

    init {

        require(color.isNotBlank()) { "SolidStrokePaintExecution color must not be blank" }
        require(widthPixels.isFinite() && widthPixels > 0.0) {

            "SolidStrokePaintExecution widthPixels must be finite and positive"

        }

    }

}
