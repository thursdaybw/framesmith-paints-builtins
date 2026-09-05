package com.framesmith.media.paint.builtins

import com.framesmith.media.paint.PaintExecutionSpec

/** Host-neutral execution request for a radial-gradient fill. */
data class RadialGradientFillPaintExecution(
    val stops: List<PaintExecutionGradientStop>,
    val center: PaintExecutionPoint,
    val radiusPixels: Double,
) : PaintExecutionSpec {

    override val id = FrameSmithPaintExecutionIds.RADIAL_GRADIENT_FILL

    init {

        require(stops.size >= MINIMUM_GRADIENT_STOP_COUNT) {

            "RadialGradientFillPaintExecution must contain at least two stops"

        }
        require(stops.zipWithNext().all(::isOrderedGradientStopPair)) {

            "RadialGradientFillPaintExecution stops must be ordered by offsetPercent"

        }
        require(radiusPixels.isFinite() && radiusPixels > 0.0) {

            "RadialGradientFillPaintExecution radiusPixels must be finite and positive"

        }

    }

    private companion object {

        const val MINIMUM_GRADIENT_STOP_COUNT = 2

        fun isOrderedGradientStopPair(pair: Pair<PaintExecutionGradientStop, PaintExecutionGradientStop>): Boolean {

            return pair.first.offsetPercent <= pair.second.offsetPercent

        }

    }

}
