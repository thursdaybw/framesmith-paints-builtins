package com.framesmith.media.paint.builtins

import com.framesmith.media.paint.PaintExecutionSpec

/** Host-neutral execution request for a linear-gradient fill. */
data class LinearGradientFillPaintExecution(
    val stops: List<PaintExecutionGradientStop>,
    val start: PaintExecutionPoint,
    val end: PaintExecutionPoint,
) : PaintExecutionSpec {

    override val id = FrameSmithPaintExecutionIds.LINEAR_GRADIENT_FILL

    init {

        require(stops.size >= MINIMUM_GRADIENT_STOP_COUNT) {

            "LinearGradientFillPaintExecution must contain at least two stops"

        }
        require(stops.zipWithNext().all(::isOrderedGradientStopPair)) {

            "LinearGradientFillPaintExecution stops must be ordered by offsetPercent"

        }
        require(start != end) { "LinearGradientFillPaintExecution start and end must differ" }

    }

    private companion object {

        const val MINIMUM_GRADIENT_STOP_COUNT = 2

        fun isOrderedGradientStopPair(pair: Pair<PaintExecutionGradientStop, PaintExecutionGradientStop>): Boolean {

            return pair.first.offsetPercent <= pair.second.offsetPercent

        }

    }

}
