package com.example.vigorly.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * Jerarquía tipográfica premium / clean:
 * - Display (Bebas) para números hero
 * - Sans para UI: tracking cerrado en títulos, body respirable, labels caps discretos
 *
 * letterSpacing siempre en sp: Material3 interpola bodyLarge/bodySmall en OutlinedTextField
 * y crashea si mezcla em y sp.
 */
val DisplayHero = TextStyle(
    fontFamily = VigorlyDisplayFamily,
    fontWeight = FontWeight.Normal,
    fontSize = 72.sp,
    lineHeight = 72.sp,
    letterSpacing = 1.4.sp
)

val DisplayStat = TextStyle(
    fontFamily = VigorlyDisplayFamily,
    fontWeight = FontWeight.Normal,
    fontSize = 44.sp,
    lineHeight = 44.sp,
    letterSpacing = 0.4.sp
)

val HeadlineLg = TextStyle(
    fontFamily = VigorlyFontFamily,
    fontWeight = FontWeight.Bold,
    fontSize = 32.sp,
    lineHeight = 38.sp,
    letterSpacing = (-1).sp
)

val HeadlineLgMobile = TextStyle(
    fontFamily = VigorlyFontFamily,
    fontWeight = FontWeight.Bold,
    fontSize = 30.sp,
    lineHeight = 36.sp,
    letterSpacing = (-1).sp
)

val HeadlineMd = TextStyle(
    fontFamily = VigorlyFontFamily,
    fontWeight = FontWeight.SemiBold,
    fontSize = 22.sp,
    lineHeight = 28.sp,
    letterSpacing = (-0.5).sp
)

val BodyLg = TextStyle(
    fontFamily = VigorlyFontFamily,
    fontWeight = FontWeight.Normal,
    fontSize = 17.sp,
    lineHeight = 26.sp,
    letterSpacing = (-0.2).sp
)

val BodyMd = TextStyle(
    fontFamily = VigorlyFontFamily,
    fontWeight = FontWeight.Normal,
    fontSize = 15.sp,
    lineHeight = 22.sp,
    letterSpacing = (-0.1).sp
)

val LabelCaps = TextStyle(
    fontFamily = VigorlyFontFamily,
    fontWeight = FontWeight.SemiBold,
    fontSize = 11.sp,
    lineHeight = 14.sp,
    letterSpacing = 1.5.sp
)

val ButtonText = TextStyle(
    fontFamily = VigorlyFontFamily,
    fontWeight = FontWeight.SemiBold,
    fontSize = 16.sp,
    lineHeight = 20.sp,
    letterSpacing = (-0.2).sp
)

val Typography = Typography(
    displayLarge = DisplayHero,
    displayMedium = DisplayStat,
    headlineLarge = HeadlineLg,
    headlineMedium = HeadlineMd,
    bodyLarge = BodyLg,
    bodyMedium = BodyMd,
    bodySmall = BodyMd.copy(
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.4.sp
    ),
    labelSmall = LabelCaps,
    labelMedium = LabelCaps,
    labelLarge = ButtonText
)
