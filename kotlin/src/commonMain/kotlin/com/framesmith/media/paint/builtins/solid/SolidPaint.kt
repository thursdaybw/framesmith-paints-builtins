package com.framesmith.media.paint.builtins

import com.framesmith.media.paint.PaintExecutionId
import com.framesmith.media.paint.PaintId
import com.framesmith.media.paint.PaintSpec
import com.framesmith.media.value.StructuredObject
import com.framesmith.media.value.StructuredText

object SolidPaint {

    val id = PaintId("framesmith.paint.solid")

    val executionId = PaintExecutionId("framesmith.paint.execution.solid-fill")

    internal val plugin = SolidPaintPlugin()

    fun spec(color: String): PaintSpec {

        return PaintSpec(
            id = id,
            parameters = StructuredObject.of(COLOR_PARAMETER to StructuredText(color)),
        )

    }

    fun details(paint: PaintSpec): SolidPaintDetails {

        requirePaintIdentity(paint)
        val color = paint.parameters.text(COLOR_PARAMETER)

        if (color.isNullOrBlank()) {
            throw FrameSmithPaintParameterException("solid paint requires a non-blank '$COLOR_PARAMETER'")
        }

        return SolidPaintDetails(color)

    }

    fun detailsFrom(paints: List<PaintSpec>): SolidPaintDetails? {

        for (paint in paints) {
            if (paint.id == id) {
                return details(paint)
            }
        }

        return null

    }

    fun matches(paint: PaintSpec): Boolean {

        return paint.id == id

    }

    private fun requirePaintIdentity(paint: PaintSpec) {

        if (!matches(paint)) {
            throw IllegalArgumentException("Expected paint '${id.value}', got '${paint.id.value}'")
        }

    }

    private const val COLOR_PARAMETER = "color"

}
