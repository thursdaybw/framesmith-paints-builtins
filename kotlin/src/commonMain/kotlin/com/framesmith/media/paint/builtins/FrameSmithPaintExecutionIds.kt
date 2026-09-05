package com.framesmith.media.paint.builtins

import com.framesmith.media.paint.PaintExecutionId

/** Stable execution identities emitted by FrameSmith first-party paint plugins. */
object FrameSmithPaintExecutionIds {

    val SOLID_FILL = PaintExecutionId("framesmith.paint.execution.solid-fill")

    val LINEAR_GRADIENT_FILL = PaintExecutionId("framesmith.paint.execution.linear-gradient-fill")

    val RADIAL_GRADIENT_FILL = PaintExecutionId("framesmith.paint.execution.radial-gradient-fill")

    val SOLID_STROKE = PaintExecutionId("framesmith.paint.execution.solid-stroke")

}
