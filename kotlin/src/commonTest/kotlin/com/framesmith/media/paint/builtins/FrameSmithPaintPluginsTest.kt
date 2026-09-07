package com.framesmith.media.paint.builtins

import com.framesmith.media.paint.PaintBounds
import com.framesmith.media.paint.PaintId
import com.framesmith.media.paint.PaintResolutionContext
import com.framesmith.media.paint.PaintSpec
import com.framesmith.media.value.StructuredObject
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs

class FrameSmithPaintPluginsTest {

    private val context = PaintResolutionContext(PaintBounds(100.0, 50.0, 300.0, 200.0))

    @Test
    fun solidSpecResolvesToPortableSolidFill() {

        val executions =
            FrameSmithPaintPlugins.resolver().resolve(
                listOf(SolidPaint.spec("#123456")),
                context,
            )

        assertEquals(listOf(SolidFillPaintExecution("#123456")), executions)

    }

    @Test
    fun linearGradientResolvesPercentPointsInsideShapeBounds() {

        val spec =
            LinearGradientPaint.spec(
                stops =
                    listOf(
                        GradientStopSpec(0.0, "#FF0000"),
                        GradientStopSpec(100.0, "#0000FF"),
                    ),
            )

        val executions = FrameSmithPaintPlugins.resolver().resolve(listOf(spec), context)
        val operation = assertIs<LinearGradientFillPaintExecution>(executions.single())

        assertEquals(PaintExecutionPoint(100.0, 150.0), operation.start)
        assertEquals(PaintExecutionPoint(400.0, 150.0), operation.end)

    }

    @Test
    fun radialGradientResolvesCenterAndRadiusInsideTargetBounds() {

        val spec =
            RadialGradientPaint.spec(
                stops =
                    listOf(
                        GradientStopSpec(0.0, "#FFFFFF"),
                        GradientStopSpec(100.0, "#000000"),
                    ),
                center = PercentPointSpec(25.0, 75.0),
                radiusPercentOfMinimumDimension = 40.0,
            )

        val executions = FrameSmithPaintPlugins.resolver().resolve(listOf(spec), context)
        val operation = assertIs<RadialGradientFillPaintExecution>(executions.single())

        assertEquals(PaintExecutionPoint(175.0, 200.0), operation.center)
        assertEquals(80.0, operation.radiusPixels)

    }

    @Test
    fun malformedSuppliedSolidColorIsInvalidInsteadOfDefaulted() {

        val spec = PaintSpec(SolidPaint.id, StructuredObject.empty())
        val failure =
            assertFailsWith<com.framesmith.media.paint.InvalidPaintParametersException> {

                FrameSmithPaintPlugins.resolver().resolve(listOf(spec), context)

            }

        assertEquals(SolidPaint.id, failure.paintId)

    }

    @Test
    fun malformedGradientPointIsInvalid() {

        val malformed = PaintSpec(LinearGradientPaint.id, StructuredObject.empty())

        val failure =
            assertFailsWith<com.framesmith.media.paint.InvalidPaintParametersException> {

                FrameSmithPaintPlugins.resolver().resolve(listOf(malformed), context)

            }

        assertEquals(LinearGradientPaint.id, failure.paintId)

    }

    @Test
    fun extensionContributionUsesSameCompositionPath() {

        val extensionId = PaintId("example.paint.custom")
        val extension =
            com.framesmith.media.paint.PaintPluginContribution.of(
                object : com.framesmith.media.paint.PaintPlugin {

                    override val paintId: PaintId = extensionId

                    override fun resolve(
                        paint: PaintSpec,
                        context: PaintResolutionContext,
                        output: com.framesmith.media.paint.PaintPluginOutput,
                    ) {

                        output.add(SolidFillPaintExecution("#ABCDEF"))

                    }

                },
            )
        val executions = FrameSmithPaintPlugins.resolver(extension).resolve(listOf(PaintSpec(extensionId)), context)

        assertEquals(listOf(SolidFillPaintExecution("#ABCDEF")), executions)

    }

}

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
