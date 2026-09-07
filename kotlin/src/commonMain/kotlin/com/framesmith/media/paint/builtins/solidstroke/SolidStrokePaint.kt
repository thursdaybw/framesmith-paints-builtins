package com.framesmith.media.paint.builtins

import com.framesmith.media.paint.PaintExecutionId
import com.framesmith.media.paint.PaintId
import com.framesmith.media.paint.PaintSpec
import com.framesmith.media.value.StructuredDecimal
import com.framesmith.media.value.StructuredObject
import com.framesmith.media.value.StructuredText

object SolidStrokePaint {

    val id = PaintId("framesmith.paint.solid-stroke")

    val executionId = PaintExecutionId("framesmith.paint.execution.solid-stroke")

    internal val plugin = SolidStrokePaintPlugin()

    fun spec(
        color: String,
        widthPercentOfHeight: Double,
    ): PaintSpec {

        return PaintSpec(
            id = id,
            parameters =
                StructuredObject.of(
                    COLOR_PARAMETER to StructuredText(color),
                    WIDTH_PERCENT_OF_HEIGHT_PARAMETER to StructuredDecimal(widthPercentOfHeight),
                ),
        )

    }

    fun details(paint: PaintSpec): SolidStrokePaintDetails {

        requirePaintIdentity(paint)
        val color = paint.parameters.text(COLOR_PARAMETER)
        val widthPercentOfHeight = paint.parameters.number(WIDTH_PERCENT_OF_HEIGHT_PARAMETER)

        if (color.isNullOrBlank()) {
            throw FrameSmithPaintParameterException("solid stroke requires a non-blank '$COLOR_PARAMETER'")
        }

        if (widthPercentOfHeight == null || !widthPercentOfHeight.isFinite() || widthPercentOfHeight <= 0.0) {
            throw FrameSmithPaintParameterException(
                "solid stroke requires positive '$WIDTH_PERCENT_OF_HEIGHT_PARAMETER'",
            )
        }

        return SolidStrokePaintDetails(color, widthPercentOfHeight)

    }

    fun detailsFrom(paints: List<PaintSpec>): SolidStrokePaintDetails? {

        for (paint in paints) {
            if (paint.id == id) {
                return details(paint)
            }
        }

        return null

    }

    private fun requirePaintIdentity(paint: PaintSpec) {

        if (paint.id != id) {
            throw IllegalArgumentException("Expected paint '${id.value}', got '${paint.id.value}'")
        }

    }

    private const val COLOR_PARAMETER = "color"

    private const val WIDTH_PERCENT_OF_HEIGHT_PARAMETER = "widthPercentOfHeight"

}
