package com.framesmith.media.paint.builtins

import com.framesmith.media.paint.PaintId
import com.framesmith.media.paint.PaintSpec
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class FrameSmithPaintDetailsTest {

    @Test
    fun `solid details expose authored color`() {

        val details = SolidPaint.details(SolidPaint.spec("#123456"))

        assertEquals(SolidPaintDetails("#123456"), details)

    }

    @Test
    fun `linear gradient details expose authored geometry`() {

        val stops =
            listOf(
                GradientStopSpec(0.0, "#111111"),
                GradientStopSpec(100.0, "#EEEEEE"),
            )
        val start = PercentPointSpec(10.0, 20.0)
        val end = PercentPointSpec(90.0, 80.0)
        val details = LinearGradientPaint.details(LinearGradientPaint.spec(stops, start, end))

        assertEquals(LinearGradientPaintDetails(stops, start, end), details)

    }

    @Test
    fun `radial gradient details expose authored geometry`() {

        val stops =
            listOf(
                GradientStopSpec(0.0, "#FFFFFF"),
                GradientStopSpec(100.0, "#000000"),
            )
        val center = PercentPointSpec(20.0, 30.0)
        val details =
            RadialGradientPaint.details(
                RadialGradientPaint.spec(stops, center, 45.0),
            )

        assertEquals(RadialGradientPaintDetails(stops, center, 45.0), details)

    }

    @Test
    fun `solid lookup ignores unrelated paint identities`() {

        val paints =
            listOf(
                PaintSpec(PaintId("example.paint.external")),
                SolidPaint.spec("#ABCDEF"),
            )

        assertEquals(SolidPaintDetails("#ABCDEF"), SolidPaint.detailsFrom(paints))

    }

    @Test
    fun `malformed first party paint throws paint parameter exception`() {

        val failure =
            try {
                SolidPaint.details(PaintSpec(SolidPaint.id))
                null
            } catch (failure: FrameSmithPaintParameterException) {
                failure
            }

        assertIs<FrameSmithPaintParameterException>(failure)

    }

}
