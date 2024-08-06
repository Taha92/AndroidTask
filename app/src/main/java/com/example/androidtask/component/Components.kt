package com.example.androidtask.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun TaskAppBar(
    title: String,
    icon: ImageVector? = null,
    navController: NavController,
    onBackArrowClicked: () -> Unit = {}
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        if (icon != null){
            Icon(
                imageVector = icon,
                contentDescription = "arrow back",
                modifier = Modifier.clickable { onBackArrowClicked.invoke() })
        }

        Spacer(modifier = Modifier.width(40.dp))

        Text(
            text = title,
            style = TextStyle(
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
        )
    }

}