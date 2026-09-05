package com.framesmith.media.paint.builtins

import com.framesmith.media.paint.PaintBounds
import com.framesmith.media.paint.PaintId
import com.framesmith.media.paint.PaintNumber
import com.framesmith.media.paint.PaintObject
import com.framesmith.media.paint.PaintResolutionContext
import com.framesmith.media.paint.PaintSpec
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
                listOf(FrameSmithPaintSpecs.solid("#123456")),
                context,
            )

        assertEquals(listOf(SolidFillPaintExecution("#123456")), executions)

    }

    @Test
    fun linearGradientResolvesPercentPointsInsideShapeBounds() {

        val spec =
            FrameSmithPaintSpecs.linearGradient(
                stops =
                    listOf(
                        FrameSmithPaintSpecs.GradientStopSpec(0.0, "#FF0000"),
                        FrameSmithPaintSpecs.GradientStopSpec(100.0, "#0000FF"),
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
            FrameSmithPaintSpecs.radialGradient(
                stops =
                    listOf(
                        FrameSmithPaintSpecs.GradientStopSpec(0.0, "#FFFFFF"),
                        FrameSmithPaintSpecs.GradientStopSpec(100.0, "#000000"),
                    ),
                center = FrameSmithPaintSpecs.PercentPointSpec(25.0, 75.0),
                radiusPercentOfMinimumDimension = 40.0,
            )

        val executions = FrameSmithPaintPlugins.resolver().resolve(listOf(spec), context)
        val operation = assertIs<RadialGradientFillPaintExecution>(executions.single())

        assertEquals(PaintExecutionPoint(175.0, 200.0), operation.center)
        assertEquals(80.0, operation.radiusPixels)

    }

    @Test
    fun malformedSuppliedSolidColorIsInvalidInsteadOfDefaulted() {

        val spec = PaintSpec(FrameSmithPaintIds.SOLID, PaintObject.empty())
        val failure =
            assertFailsWith<com.framesmith.media.paint.InvalidPaintParametersException> {

                FrameSmithPaintPlugins.resolver().resolve(listOf(spec), context)

            }

        assertEquals(FrameSmithPaintIds.SOLID, failure.paintId)

    }

    @Test
    fun malformedGradientPointIsInvalid() {

        val valid =
            FrameSmithPaintSpecs.linearGradient(
                listOf(
                    FrameSmithPaintSpecs.GradientStopSpec(0.0, "#FF0000"),
                    FrameSmithPaintSpecs.GradientStopSpec(100.0, "#0000FF"),
                ),
            )
        val malformed =
            valid.copy(
                parameters =
                    PaintObject.from(
                        valid.parameters.fields +
                            (START to PaintObject.of(X_PERCENT to PaintNumber(20.0))),
                    ),
            )

        val failure =
            assertFailsWith<com.framesmith.media.paint.InvalidPaintParametersException> {

                FrameSmithPaintPlugins.resolver().resolve(listOf(malformed), context)

            }

        assertEquals(FrameSmithPaintIds.LINEAR_GRADIENT, failure.paintId)

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

class FrameSmithPaintSpecInspectionTest {

    @Test
    fun `solid inspection exposes typed authored details`() {

        val inspection = FrameSmithPaintSpecs.inspect(FrameSmithPaintSpecs.solid("#123456"))

        assertEquals(
            FrameSmithPaintSpecInspection.Solid("#123456"),
            inspection,
        )

    }

    @Test
    fun `gradient inspection exposes typed authored details`() {

        val stops =
            listOf(
                FrameSmithPaintSpecs.GradientStopSpec(0.0, "#111111"),
                FrameSmithPaintSpecs.GradientStopSpec(100.0, "#EEEEEE"),
            )
        val start = FrameSmithPaintSpecs.PercentPointSpec(10.0, 20.0)
        val end = FrameSmithPaintSpecs.PercentPointSpec(90.0, 80.0)
        val inspection = FrameSmithPaintSpecs.inspect(FrameSmithPaintSpecs.linearGradient(stops, start, end))

        assertEquals(
            FrameSmithPaintSpecInspection.LinearGradient(stops, start, end),
            inspection,
        )

    }

    @Test
    fun `radial gradient inspection exposes typed authored details`() {

        val stops =
            listOf(
                FrameSmithPaintSpecs.GradientStopSpec(0.0, "#FFFFFF"),
                FrameSmithPaintSpecs.GradientStopSpec(100.0, "#000000"),
            )
        val center = FrameSmithPaintSpecs.PercentPointSpec(20.0, 30.0)
        val inspection =
            FrameSmithPaintSpecs.inspect(
                FrameSmithPaintSpecs.radialGradient(stops, center, 45.0),
            )

        assertEquals(
            FrameSmithPaintSpecInspection.RadialGradient(stops, center, 45.0),
            inspection,
        )

    }

    @Test
    fun `extension paint remains explicitly other`() {

        val inspection =
            FrameSmithPaintSpecs.inspect(
                PaintSpec(PaintId("example.paint.external")),
            )

        assertEquals(FrameSmithPaintSpecInspection.OtherPaint, inspection)

    }

    @Test
    fun `malformed first-party paint remains explicit`() {

        val inspection =
            FrameSmithPaintSpecs.inspect(
                PaintSpec(FrameSmithPaintIds.SOLID),
            )

        assertIs<FrameSmithPaintSpecInspection.InvalidFrameSmithPaint>(inspection)

    }

}
