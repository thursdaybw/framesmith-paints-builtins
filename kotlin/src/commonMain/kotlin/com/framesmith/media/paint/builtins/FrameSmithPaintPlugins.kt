package com.framesmith.media.paint.builtins

import com.framesmith.media.paint.PaintId
import com.framesmith.media.paint.PaintPlugin
import com.framesmith.media.paint.PaintPluginComposition
import com.framesmith.media.paint.PaintPluginContribution
import com.framesmith.media.paint.PaintSpec
import com.framesmith.media.paint.ResolvePaint

/** First-party paints composed through the same seam as external contributions. */
object FrameSmithPaintPlugins {

    private val firstPartyPlugins: List<PaintPlugin> =
        listOf(
            SolidPaintPlugin(),
            LinearGradientPaintPlugin(),
            SolidStrokePaintPlugin(),
        )

    private val inspectorsById: Map<PaintId, FrameSmithPaintSpecInspector> =
        firstPartyPlugins
            .filterIsInstance<FrameSmithPaintSpecInspector>()
            .associateBy(FrameSmithPaintSpecInspector::paintId)

    val contribution: PaintPluginContribution =
        PaintPluginContribution.of(*firstPartyPlugins.toTypedArray())

    fun resolver(vararg extensions: PaintPluginContribution): ResolvePaint {

        return PaintPluginComposition.compose(contribution, *extensions)

    }

    internal fun inspect(paint: PaintSpec): FrameSmithPaintSpecInspection {

        val inspector = inspectorsById[paint.id]

        if (inspector == null) {
            return FrameSmithPaintSpecInspection.OtherPaint
        }

        return inspector.inspect(paint)

    }

}
