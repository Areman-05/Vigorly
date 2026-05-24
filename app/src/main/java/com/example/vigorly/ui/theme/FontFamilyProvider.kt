package com.example.vigorly.ui.theme

import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import com.example.vigorly.R

private val provider = androidx.compose.ui.text.googlefonts.GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs
)

private val robotoFlex = GoogleFont("Roboto Flex")

val VigorlyFontFamily: FontFamily = FontFamily(
    Font(googleFont = robotoFlex, fontProvider = provider, weight = FontWeight.Normal),
    Font(googleFont = robotoFlex, fontProvider = provider, weight = FontWeight.SemiBold),
    Font(googleFont = robotoFlex, fontProvider = provider, weight = FontWeight.Bold),
    Font(googleFont = robotoFlex, fontProvider = provider, weight = FontWeight.ExtraBold),
    Font(googleFont = robotoFlex, fontProvider = provider, weight = FontWeight.Black)
)
