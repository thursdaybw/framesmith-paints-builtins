package com.framesmith.media.paint.builtins

import com.framesmith.media.paint.PaintBounds
import com.framesmith.media.paint.PaintOperation
import com.framesmith.media.paint.PaintResolution
import com.framesmith.media.paint.PaintResolutionContext
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class SolidStrokePaintPluginTest {

    @Test
    fun solidStroke_resolvesWidthFromTargetHeight() {

        val resolution =
            FrameSmithPaintPlugins.resolver().resolve(
                paints = listOf(FrameSmithPaintSpecs.solidStroke("#112233", 2.5)),
                context = PaintResolutionContext(PaintBounds(0.0, 0.0, 200.0, 80.0)),
            )

        val resolved = assertIs<PaintResolution.Resolved>(resolution)
        assertEquals(listOf(PaintOperation.SolidStroke("#112233", 2.0)), resolved.operations)

    }

    @Test
    fun solidStroke_rejectsNonPositiveWidth() {

        val resolution =
            FrameSmithPaintPlugins.resolver().resolve(
                paints = listOf(FrameSmithPaintSpecs.solidStroke("#112233", 0.0)),
                context = PaintResolutionContext(PaintBounds(0.0, 0.0, 200.0, 80.0)),
            )

        assertIs<PaintResolution.InvalidParameters>(resolution)

    }

}
