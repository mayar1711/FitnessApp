package com.example.fitnessapp.ui.home.view.composabled

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*

@Composable
fun DataCard(
    label: String,
    value: String,
    unit: String,
    icon: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .padding(8.dp)
            .size(150.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 5.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(vertical = 16.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = label,
                    color = Color.Black,
                    fontSize = 16.sp
                )
                Icon(
                    painter = painterResource(id = icon),
                    contentDescription = null,
                    tint = Color.Blue,
                    modifier = Modifier.size(24.dp)
                )

            }

            Text(
                text = value,
                color = Color.Black,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = unit,
                fontSize = 14.sp,
                color = Color.Gray
            )
        }
    }
}
