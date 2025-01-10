package com.ghao.apps.gyro.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.unit.dp

@Composable
fun VUMeter(
    current: Float,
    max: Float,
    modifier: Modifier = Modifier,
    orientation: Orientation = Orientation.Horizontal
) {
    // Ensure current does not exceed max
    val clampedCurrent = current.coerceIn(0f, max)

    // Calculate the fill percentage
    val fillFraction = if (max > 0) clampedCurrent / max else 0f

    // Define colors for the VU meter
    val backgroundColor = Color.Gray
    val fillColor = when {
        fillFraction < 0.6f -> Color.Green
        fillFraction < 0.9f -> lerp(Color.Green, Color.Yellow, (fillFraction - 0.6f) / 0.2f)
        else -> lerp(Color.Yellow, Color.Red, (fillFraction - 0.9f) / 0.1f)
    }

    // VU Meter UI
    Box(
        modifier
            .background(backgroundColor)
            .border(2.dp, Color.Black)
            .clip(RoundedCornerShape(4.dp))
            .padding(4.dp)
    ) {
        Box(
            Modifier
                .fillMaxWidth(if (orientation == Orientation.Horizontal) fillFraction else 1f)
                .fillMaxHeight(if (orientation == Orientation.Vertical) fillFraction else 1f)
                .background(fillColor)
        )
    }
}
