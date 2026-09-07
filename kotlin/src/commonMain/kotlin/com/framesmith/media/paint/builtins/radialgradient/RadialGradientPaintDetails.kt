package com.framesmith.media.paint.builtins

data class RadialGradientPaintDetails(
    val stops: List<GradientStopSpec>,
    val center: PercentPointSpec,
    val radiusPercentOfMinimumDimension: Double,
)
