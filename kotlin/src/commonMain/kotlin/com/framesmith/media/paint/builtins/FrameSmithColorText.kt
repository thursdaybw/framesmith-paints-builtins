package com.framesmith.media.paint.builtins

internal object FrameSmithColorText {

    fun isValid(value: String): Boolean {

        return COLOR_PATTERN.matches(value)

    }

    fun requireValid(
        value: String?,
        description: String,
    ): String {

        if (value == null || !isValid(value)) {
            throw FrameSmithPaintParameterException(
                "$description requires a #RRGGBB or #AARRGGBB color",
            )
        }

        return value

    }

    private val COLOR_PATTERN = Regex("^#[0-9A-Fa-f]{6}([0-9A-Fa-f]{2})?$")

}
