package com.framesmith.media.paint.builtins

import com.framesmith.media.paint.PaintPlugin
import com.framesmith.media.paint.PaintPluginComposition
import com.framesmith.media.paint.PaintPluginContribution
import com.framesmith.media.paint.ResolvePaint

/** First-party paints composed through the same seam as external contributions. */
object FrameSmithPaintPlugins {

    private val firstPartyPlugins: List<PaintPlugin> =
        listOf(
            SolidPaintPlugin(),
            LinearGradientPaintPlugin(),
            RadialGradientPaintPlugin(),
            SolidStrokePaintPlugin(),
        )

    val contribution: PaintPluginContribution =
        PaintPluginContribution.of(*firstPartyPlugins.toTypedArray())

    fun resolver(vararg extensions: PaintPluginContribution): ResolvePaint {

        return PaintPluginComposition.compose(contribution, *extensions)

    }

}
