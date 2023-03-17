package cs346.whiteboard.client.ui

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cs346.whiteboard.client.constants.Colors
import cs346.whiteboard.client.constants.Shapes

@Composable
fun Dialog(modifier: Modifier,
           title: String,
           description: String,
           buttonText: String,
           onClick: () -> Unit) {
    Box(modifier = modifier.border(1.dp, Colors.secondaryVariant, Shapes.small)) {
        Column(
            modifier = modifier.padding(32.dp).align(Alignment.Center),
            horizontalAlignment = Alignment.Start) {
            SmallTitleText(title)
            Spacer(Modifier.height(8.dp))
            SecondaryBodyText(description)
            Spacer(Modifier.height(16.dp))
            Row {
                Spacer(modifier.weight(1.0f))
                PrimaryButton(
                    modifier = Modifier.size(100.dp, 40.dp),
                    text = buttonText,
                    enabled = true,
                    onClick = onClick)
            }
        }
    }

}