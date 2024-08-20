package com.lilstiffy.mockgps.extensions

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

fun Modifier.roundedShadow(radius: Dp): Modifier {
    return this
        .shadow(
            elevation = 4.dp,
            shape = RoundedCornerShape(radius),
            clip = true
        )
}