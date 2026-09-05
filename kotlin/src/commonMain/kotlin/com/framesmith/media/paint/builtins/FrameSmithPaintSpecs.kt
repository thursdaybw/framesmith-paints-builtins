package com.framesmith.media.paint.builtins

import com.framesmith.media.paint.PaintList
import com.framesmith.media.paint.PaintNumber
import com.framesmith.media.paint.PaintObject
import com.framesmith.media.paint.PaintSpec
import com.framesmith.media.paint.PaintText

/** Authored FrameSmith paint specs. Resolution semantics remain in the matching plugins. */
object FrameSmithPaintSpecs {

    fun solid(color: String): PaintSpec {

        return PaintSpec(
            id = FrameSmithPaintIds.SOLID,
            parameters = PaintObject.of(COLOR to PaintText(color)),
        )

    }

    fun solidStroke(
        color: String,
        widthPercentOfHeight: Double,
    ): PaintSpec {

        return PaintSpec(
            id = FrameSmithPaintIds.SOLID_STROKE,
            parameters =
                PaintObject.of(
                    COLOR to PaintText(color),
                    WIDTH_PERCENT_OF_HEIGHT to PaintNumber(widthPercentOfHeight),
                ),
        )

    }

    fun linearGradient(
        stops: List<GradientStopSpec>,
        start: PercentPointSpec = PercentPointSpec.leftCenter(),
        end: PercentPointSpec = PercentPointSpec.rightCenter(),
    ): PaintSpec {

        return PaintSpec(
            id = FrameSmithPaintIds.LINEAR_GRADIENT,
            parameters =
                PaintObject.of(
                    STOPS to PaintList.from(stops.map(GradientStopSpec::toPaintValue)),
                    START to start.toPaintValue(),
                    END to end.toPaintValue(),
                ),
        )

    }

    fun radialGradient(
        stops: List<GradientStopSpec>,
        center: PercentPointSpec = PercentPointSpec.center(),
        radiusPercentOfMinimumDimension: Double = DEFAULT_RADIAL_RADIUS_PERCENT,
    ): PaintSpec {

        return PaintSpec(
            id = FrameSmithPaintIds.RADIAL_GRADIENT,
            parameters =
                PaintObject.of(
                    STOPS to PaintList.from(stops.map(GradientStopSpec::toPaintValue)),
                    CENTER to center.toPaintValue(),
                    RADIUS_PERCENT_OF_MINIMUM_DIMENSION to PaintNumber(radiusPercentOfMinimumDimension),
                ),
        )

    }

    data class GradientStopSpec(
        val offsetPercent: Double,
        val color: String,
    ) {

        internal fun toPaintValue(): PaintObject {

            return PaintObject.of(
                OFFSET_PERCENT to PaintNumber(offsetPercent),
                COLOR to PaintText(color),
            )

        }

    }

    data class PercentPointSpec(
        val xPercent: Double,
        val yPercent: Double,
    ) {

        internal fun toPaintValue(): PaintObject {

            return PaintObject.of(
                X_PERCENT to PaintNumber(xPercent),
                Y_PERCENT to PaintNumber(yPercent),
            )

        }

        companion object {

            fun center(): PercentPointSpec {

                return PercentPointSpec(CENTER_PERCENT, CENTER_PERCENT)

            }

            fun leftCenter(): PercentPointSpec {

                return PercentPointSpec(MINIMUM_PERCENT, CENTER_PERCENT)

            }

            fun rightCenter(): PercentPointSpec {

                return PercentPointSpec(FULL_PERCENT, CENTER_PERCENT)

            }

        }

    }

}

internal const val COLOR = "color"
internal const val WIDTH_PERCENT_OF_HEIGHT = "widthPercentOfHeight"
internal const val STOPS = "stops"
internal const val START = "start"
internal const val END = "end"
internal const val CENTER = "center"
internal const val RADIUS_PERCENT_OF_MINIMUM_DIMENSION = "radiusPercentOfMinimumDimension"
internal const val OFFSET_PERCENT = "offsetPercent"
internal const val X_PERCENT = "xPercent"
internal const val Y_PERCENT = "yPercent"
internal const val MINIMUM_PERCENT = 0.0
internal const val CENTER_PERCENT = 50.0
internal const val FULL_PERCENT = 100.0
internal const val DEFAULT_RADIAL_RADIUS_PERCENT = 50.0
