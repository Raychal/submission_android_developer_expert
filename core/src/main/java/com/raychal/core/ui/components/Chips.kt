package com.raychal.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.raychal.core.ui.theme.Background
import com.raychal.core.ui.theme.SubmissionAndroidDeveloperExpertTheme

@Composable
fun ChipText(
    modifier: Modifier = Modifier,
    text: String,
    color: Color = Color.Black
) {
    Text(
        modifier = modifier,
        text = text,
        color = color
    )
}


@Composable
fun Chips(
    modifier: Modifier = Modifier,
    isActive: Boolean = false,
    value: String,
    label: String,
    onStateChanged: (String) -> Unit,
    activeBorderColor: Color = Color.White,
    activeBackgroundColor: Color = Color.Black,
    inactiveBackgroundColor: Color = Color.Black,
    textColor: Color = Color.White.copy(alpha = 0.8f),
    activeTextColor: Color = Color.White,
) {
    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(if (isActive) activeBackgroundColor else inactiveBackgroundColor)
            .border(
                2.dp,
                if (isActive) activeBorderColor else Color.Transparent,
                RoundedCornerShape(100.dp)
            )
            .clickable {
                onStateChanged(value)
            },
        contentAlignment = Alignment.Center
    ) {
        ChipText(
            modifier = Modifier.padding(horizontal = 17.dp, vertical = 6.5.dp),
            text = label,
            color = if (isActive) activeTextColor else textColor
        )
    }
}

@Composable
fun FilterChips(
    modifier: Modifier = Modifier,
    dataList: List<Pair<String, String>>,
    activeState: String,
    isMultiSelect: Boolean = true,
    onStateChanged: (String) -> Unit
) {
    val activeList = if (activeState.isEmpty()) emptyList() else activeState.split(",")

    Row(
        modifier = modifier
            .clip(CircleShape)
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        dataList.forEach { (label, value) ->
            val isActive = activeList.contains(value)

            Chips(
                modifier = Modifier.fillMaxWidth(),
                isActive = isActive,
                value = value,
                label = label,
                onStateChanged = { clickedValue ->
                    val newState = if (isMultiSelect) {
                        if (activeList.contains(clickedValue)) {
                            activeList.filter { it != clickedValue }.joinToString(",")
                        } else {
                            (activeList + clickedValue).joinToString(",")
                        }
                    } else {
                        if (isActive) "" else clickedValue
                    }
                    onStateChanged(newState)
                }
            )
        }
    }
}

@Preview
@Composable
private fun ChipPreview() {
    val dataList = listOf(
        "Received" to "Received Data",
        "Outstanding" to "Outstanding Data",
        "Info" to "Info Data",
        "All" to "All Data"
    )

    SubmissionAndroidDeveloperExpertTheme {
        Column {
            Column(
                modifier = Modifier
                    .padding(10.dp)
                    .background(Background),
                verticalArrangement = Arrangement.spacedBy(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Chips(
                    modifier = Modifier.fillMaxWidth(),
                    isActive = true,
                    value = "value",
                    label = "label",
                    onStateChanged = {}
                )
                Chips(
                    modifier = Modifier.fillMaxWidth(),
                    isActive = false,
                    value = "value",
                    label = "label",
                    onStateChanged = {}
                )
            }

            Column(
                modifier = Modifier
                    .padding(10.dp)
                    .background(Background),
                verticalArrangement = Arrangement.spacedBy(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                FilterChips(
                    modifier = Modifier.fillMaxWidth(),
                    dataList = dataList,
                    activeState = "",
                    onStateChanged = {}
                )
            }
        }
    }
}