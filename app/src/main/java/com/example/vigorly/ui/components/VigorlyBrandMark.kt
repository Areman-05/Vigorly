package com.example.vigorly.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vigorly.R
import com.example.vigorly.ui.theme.OnSurface
import com.example.vigorly.ui.theme.PrimaryAccent
import com.example.vigorly.ui.theme.VigorlyDisplayFamily

/**
 * Wordmark fitness: Bebas Neue, V en acento, resto blanco.
 */
@Composable
fun VigorlyBrandMark(
    modifier: Modifier = Modifier,
    compact: Boolean = false,
    @Suppress("UNUSED_PARAMETER") size: androidx.compose.ui.unit.Dp = 168.dp,
    @Suppress("UNUSED_PARAMETER") animate: Boolean = true,
    @Suppress("UNUSED_PARAMETER") progress: Float = 1f
) {
    val brand = stringResource(R.string.brand_name).uppercase()
    val mark = buildAnnotatedString {
        withStyle(
            SpanStyle(
                fontFamily = VigorlyDisplayFamily,
                fontSize = if (compact) 52.sp else 84.sp,
                letterSpacing = if (compact) 1.5.sp else 2.sp,
                color = PrimaryAccent
            )
        ) {
            append(brand.firstOrNull() ?: 'V')
        }
        if (brand.length > 1) {
            withStyle(
                SpanStyle(
                    fontFamily = VigorlyDisplayFamily,
                    fontSize = if (compact) 44.sp else 72.sp,
                    letterSpacing = if (compact) 3.sp else 5.sp,
                    color = OnSurface
                )
            ) {
                append(brand.drop(1))
            }
        }
    }

    Column(
        modifier = modifier.semantics { contentDescription = brand },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = mark)
    }
}
