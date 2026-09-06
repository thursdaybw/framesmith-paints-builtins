package com.framesmith.media.paint.builtins

data class RadialGradientPaintDetails(
    val stops: List<FrameSmithPaintSpecs.GradientStopSpec>,
    val center: FrameSmithPaintSpecs.PercentPointSpec,
    val radiusPercentOfMinimumDimension: Double,
)
