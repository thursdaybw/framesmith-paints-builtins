package com.framesmith.media.paint.builtins

/** Resolved portable point used by FrameSmith paint execution primitives. */
data class PaintExecutionPoint(
    val x: Double,
    val y: Double,
) {

    init {

        require(x.isFinite()) { "PaintExecutionPoint x must be finite" }
        require(y.isFinite()) { "PaintExecutionPoint y must be finite" }

    }

}
