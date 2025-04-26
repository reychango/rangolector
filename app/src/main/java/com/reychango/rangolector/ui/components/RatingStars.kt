package com.reychango.rangolector.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarHalf
import androidx.compose.material.icons.filled.StarOutline
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import kotlin.math.ceil
import kotlin.math.floor

@Composable
fun RatingStars(
    rating: Float,
    maxStars: Int = 5,
    starColor: Color = Color(0xFFFFD700),
    modifier: Modifier = Modifier
) {
    val fullStars = floor(rating).toInt()
    val hasHalfStar = (rating - fullStars) >= 0.5f
    val emptyStars = maxStars - fullStars - (if (hasHalfStar) 1 else 0)

    Row(modifier = modifier) {
        // Estrellas llenas
        repeat(fullStars) {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = null,
                tint = starColor
            )
        }

        // Media estrella si es necesario
        if (hasHalfStar) {
            Icon(
                imageVector = Icons.Default.StarHalf,
                contentDescription = null,
                tint = starColor
            )
        }

        // Estrellas vacías
        repeat(emptyStars) {
            Icon(
                imageVector = Icons.Default.StarOutline,
                contentDescription = null,
                tint = starColor
            )
        }
    }
} 