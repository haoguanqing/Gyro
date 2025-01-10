package com.ghao.apps.gyro

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.ghao.apps.gyro.ui.components.VUMeter

@Composable
fun MainContent(modifier: Modifier) {
    val context = LocalContext.current
    val presenter = remember { Presenter(context) }
    val state = presenter.uiState.collectAsState()

    Column(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            text = "X: ${state.value.x}\n" +
                    "Y: ${state.value.y}\n" +
                    "Z: ${state.value.z}\n" +
                    "Current: ${state.value.magnitude}\n" +
                    "Rolling avg: ${state.value.avg}\n" +
                    "Min: ${state.value.min}\n" +
                    "Max: ${state.value.max}\n" +
                    "Freq: ${state.value.freq}Hz"
        )

        VUMeter(
            current = state.value.magnitude,
            max = state.value.max,
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
        )


        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                modifier = Modifier.width(64.dp),
                text = if (state.value.isRecording) "STOP" else "START",
                color = if (state.value.isRecording) Color.Red else Color.Green,
            )

            Switch(
                checked = state.value.isRecording,
                onCheckedChange = {
                    if (it) {
                        presenter.startRecording()
                    } else {
                        presenter.stopRecording()
                    }
                },
            )
        }

    }

    DisposableEffect(presenter) {
        onDispose {
            presenter.dispose()
        }
    }
}
