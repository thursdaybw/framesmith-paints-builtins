package com.framesmith.media.paint.builtins

import com.framesmith.media.paint.PaintSpec
import com.framesmith.media.value.StructuredDecimal
import com.framesmith.media.value.StructuredList
import com.framesmith.media.value.StructuredObject
import com.framesmith.media.value.StructuredText

/** Authored FrameSmith paint specs. Resolution semantics remain in the matching plugins. */
object FrameSmithPaintSpecs {

    fun solid(color: String): PaintSpec {

        return PaintSpec(
            id = FrameSmithPaintIds.SOLID,
            parameters = StructuredObject.of(COLOR to StructuredText(color)),
        )

    }

    fun solidStroke(
        color: String,
        widthPercentOfHeight: Double,
    ): PaintSpec {

        return PaintSpec(
            id = FrameSmithPaintIds.SOLID_STROKE,
            parameters =
                StructuredObject.of(
                    COLOR to StructuredText(color),
                    WIDTH_PERCENT_OF_HEIGHT to StructuredDecimal(widthPercentOfHeight),
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
                StructuredObject.of(
                    STOPS to StructuredList.from(stops.map(GradientStopSpec::toPaintValue)),
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
                StructuredObject.of(
                    STOPS to StructuredList.from(stops.map(GradientStopSpec::toPaintValue)),
                    CENTER to center.toPaintValue(),
                    RADIUS_PERCENT_OF_MINIMUM_DIMENSION to StructuredDecimal(radiusPercentOfMinimumDimension),
                ),
        )

    }

    data class GradientStopSpec(
        val offsetPercent: Double,
        val color: String,
    ) {

        internal fun toPaintValue(): StructuredObject {

            return StructuredObject.of(
                OFFSET_PERCENT to StructuredDecimal(offsetPercent),
                COLOR to StructuredText(color),
            )

        }

    }

    data class PercentPointSpec(
        val xPercent: Double,
        val yPercent: Double,
    ) {

        internal fun toPaintValue(): StructuredObject {

            return StructuredObject.of(
                X_PERCENT to StructuredDecimal(xPercent),
                Y_PERCENT to StructuredDecimal(yPercent),
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
