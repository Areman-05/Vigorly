package com.example.vigorly.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp

val DisplayHero = TextStyle(
    fontFamily = VigorlyFontFamily,
    fontWeight = FontWeight.Black,
    fontSize = 80.sp,
    lineHeight = 80.sp,
    letterSpacing = (-0.04).em
)

val DisplayStat = TextStyle(
    fontFamily = VigorlyFontFamily,
    fontWeight = FontWeight.ExtraBold,
    fontSize = 48.sp,
    lineHeight = 48.sp,
    letterSpacing = (-0.02).em
)

val HeadlineLg = TextStyle(
    fontFamily = VigorlyFontFamily,
    fontWeight = FontWeight.Bold,
    fontSize = 32.sp,
    lineHeight = 40.sp
)

val HeadlineLgMobile = TextStyle(
    fontFamily = VigorlyFontFamily,
    fontWeight = FontWeight.Bold,
    fontSize = 28.sp,
    lineHeight = 34.sp
)

val HeadlineMd = TextStyle(
    fontFamily = VigorlyFontFamily,
    fontWeight = FontWeight.SemiBold,
    fontSize = 24.sp,
    lineHeight = 30.sp
)

val BodyLg = TextStyle(
    fontFamily = VigorlyFontFamily,
    fontWeight = FontWeight.Normal,
    fontSize = 19.sp,
    lineHeight = 28.sp
)

val BodyMd = TextStyle(
    fontFamily = VigorlyFontFamily,
    fontWeight = FontWeight.Normal,
    fontSize = 17.sp,
    lineHeight = 25.sp
)

val LabelCaps = TextStyle(
    fontFamily = VigorlyFontFamily,
    fontWeight = FontWeight.Bold,
    fontSize = 13.sp,
    lineHeight = 17.sp,
    letterSpacing = 0.1.em
)

val ButtonText = TextStyle(
    fontFamily = VigorlyFontFamily,
    fontWeight = FontWeight.SemiBold,
    fontSize = 17.sp,
    lineHeight = 21.sp
)

val Typography = Typography(
    displayLarge = DisplayHero,
    displayMedium = DisplayStat,
    headlineLarge = HeadlineLg,
    headlineMedium = HeadlineMd,
    bodyLarge = BodyLg,
    bodyMedium = BodyMd,
    labelSmall = LabelCaps,
    labelLarge = ButtonText
)
