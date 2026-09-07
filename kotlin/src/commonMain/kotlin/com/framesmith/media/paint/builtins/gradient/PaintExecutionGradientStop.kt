package com.framesmith.media.paint.builtins

/** Resolved portable gradient stop used by FrameSmith gradient execution primitives. */
data class PaintExecutionGradientStop(
    val offsetPercent: Double,
    val color: String,
) {

    init {

        require(offsetPercent in MINIMUM_PERCENT..FULL_PERCENT) {

            "PaintExecutionGradientStop offsetPercent must be from 0 to 100"

        }
        require(color.isNotBlank()) { "PaintExecutionGradientStop color must not be blank" }

    }

    private companion object {

        const val MINIMUM_PERCENT = 0.0

        const val FULL_PERCENT = 100.0

    }

}
