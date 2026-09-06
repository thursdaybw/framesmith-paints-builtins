package com.framesmith.media.paint.builtins

data class LinearGradientPaintDetails(
    val stops: List<FrameSmithPaintSpecs.GradientStopSpec>,
    val start: FrameSmithPaintSpecs.PercentPointSpec,
    val end: FrameSmithPaintSpecs.PercentPointSpec,
)
