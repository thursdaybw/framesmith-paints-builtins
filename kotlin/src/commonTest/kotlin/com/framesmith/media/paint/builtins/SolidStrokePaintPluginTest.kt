package com.framesmith.media.paint.builtins

import com.framesmith.media.paint.PaintBounds
import com.framesmith.media.paint.PaintResolutionContext
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class SolidStrokePaintPluginTest {

    @Test
    fun solidStroke_resolvesWidthFromTargetHeight() {

        val executions =
            FrameSmithPaintPlugins.resolver().resolve(
                paints = listOf(FrameSmithPaintSpecs.solidStroke("#112233", 2.5)),
                context = PaintResolutionContext(PaintBounds(0.0, 0.0, 200.0, 80.0)),
            )

        assertEquals(listOf(SolidStrokePaintExecution("#112233", 2.0)), executions)

    }

    @Test
    fun solidStroke_rejectsNonPositiveWidth() {

        assertFailsWith<com.framesmith.media.paint.InvalidPaintParametersException> {

            FrameSmithPaintPlugins.resolver().resolve(
                paints = listOf(FrameSmithPaintSpecs.solidStroke("#112233", 0.0)),
                context = PaintResolutionContext(PaintBounds(0.0, 0.0, 200.0, 80.0)),
            )

        }

    }

}
