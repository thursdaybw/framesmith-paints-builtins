package com.framesmith.media.paint.builtins

import com.framesmith.media.paint.PaintBounds
import com.framesmith.media.paint.PaintId
import com.framesmith.media.paint.PaintNumber
import com.framesmith.media.paint.PaintObject
import com.framesmith.media.paint.PaintOperation
import com.framesmith.media.paint.PaintResolution
import com.framesmith.media.paint.PaintResolutionContext
import com.framesmith.media.paint.PaintSpec
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class FrameSmithPaintPluginsTest {

    private val context = PaintResolutionContext(PaintBounds(100.0, 50.0, 300.0, 200.0))

    @Test
    fun solidSpecResolvesToPortableSolidFill() {

        val resolution =
            FrameSmithPaintPlugins.resolver().resolve(
                listOf(FrameSmithPaintSpecs.solid("#123456")),
                context,
            )

        assertEquals(
            listOf(PaintOperation.SolidFill("#123456")),
            assertIs<PaintResolution.Resolved>(resolution).operations,
        )

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

        val resolution = FrameSmithPaintPlugins.resolver().resolve(listOf(spec), context)
        val operation =
            assertIs<PaintOperation.LinearGradientFill>(
                assertIs<PaintResolution.Resolved>(resolution).operations.single(),
            )

        assertEquals(PaintOperation.Point(100.0, 150.0), operation.start)
        assertEquals(PaintOperation.Point(400.0, 150.0), operation.end)

    }

    @Test
    fun malformedSuppliedSolidColorIsInvalidInsteadOfDefaulted() {

        val spec = PaintSpec(FrameSmithPaintIds.SOLID, PaintObject.empty())
        val resolution = FrameSmithPaintPlugins.resolver().resolve(listOf(spec), context)

        assertEquals(
            FrameSmithPaintIds.SOLID,
            assertIs<PaintResolution.InvalidParameters>(resolution).paintId,
        )

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

        val resolution = FrameSmithPaintPlugins.resolver().resolve(listOf(malformed), context)

        assertEquals(
            FrameSmithPaintIds.LINEAR_GRADIENT,
            assertIs<PaintResolution.InvalidParameters>(resolution).paintId,
        )

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
                    ): com.framesmith.media.paint.PaintPluginResult {

                        return com.framesmith.media.paint.PaintPluginResult.Resolved(
                            listOf(PaintOperation.SolidFill("#ABCDEF")),
                        )

                    }

                },
            )
        val resolution = FrameSmithPaintPlugins.resolver(extension).resolve(listOf(PaintSpec(extensionId)), context)

        assertEquals(
            listOf(PaintOperation.SolidFill("#ABCDEF")),
            assertIs<PaintResolution.Resolved>(resolution).operations,
        )

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
