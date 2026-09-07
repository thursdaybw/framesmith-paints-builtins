package com.framesmith.media.paint.builtins

data class LinearGradientPaintDetails(
    val stops: List<GradientStopSpec>,
    val start: PercentPointSpec,
    val end: PercentPointSpec,
)
