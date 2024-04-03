package com.example.stacker.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.stacker.R


//private val pixelify_regular = FontFamily(
//    Font(R.font.pixelify_sans_regular),
//)
//// Set of Material typography styles to start with
//val Typography = Typography(
//    bodyLarge = TextStyle(
//        fontFamily = FontFamily.Default,
//        fontWeight = FontWeight.Normal,
//        fontSize = 16.sp,
//        lineHeight = 24.sp,
//        letterSpacing = 0.5.sp
//    )
//    /* Other default text styles to override
//    titleLarge = TextStyle(
//        fontFamily = FontFamily.Default,
//        fontWeight = FontWeight.Normal,
//        fontSize = 22.sp,
//        lineHeight = 28.sp,
//        letterSpacing = 0.sp
//    ),
//    labelSmall = TextStyle(
//        fontFamily = FontFamily.Default,
//        fontWeight = FontWeight.Medium,
//        fontSize = 11.sp,
//        lineHeight = 16.sp,
//        letterSpacing = 0.5.sp
//    )
//    */
//)

// Declare the font families
object AppFont {
    val Pixelify = FontFamily(
        Font(R.font.pixelify_sans_regular),
        Font(R.font.pixelify_sans_medium, FontWeight.Medium),
        Font(R.font.pixelify_sans_bold, FontWeight.Bold),
        Font(R.font.pixelify_sans_semibold, FontWeight.SemiBold)
    )
}

private val defaultTypography = Typography()
val Typography = Typography(
    displayLarge = defaultTypography.displayLarge.copy(fontFamily = AppFont.Pixelify),
    displayMedium = defaultTypography.displayMedium.copy(fontFamily = AppFont.Pixelify),
    displaySmall = defaultTypography.displaySmall.copy(fontFamily = AppFont.Pixelify),

    headlineLarge = defaultTypography.headlineLarge.copy(fontFamily = AppFont.Pixelify),
    headlineMedium = defaultTypography.headlineMedium.copy(fontFamily = AppFont.Pixelify),
    headlineSmall = defaultTypography.headlineSmall.copy(fontFamily = AppFont.Pixelify),

    titleLarge = defaultTypography.titleLarge.copy(fontFamily = AppFont.Pixelify),
    titleMedium = defaultTypography.titleMedium.copy(fontFamily = AppFont.Pixelify),
    titleSmall = defaultTypography.titleSmall.copy(fontFamily = AppFont.Pixelify),

    bodyLarge = defaultTypography.bodyLarge.copy(fontFamily = AppFont.Pixelify),
    bodyMedium = defaultTypography.bodyMedium.copy(fontFamily = AppFont.Pixelify),
    bodySmall = defaultTypography.bodySmall.copy(fontFamily = AppFont.Pixelify),

    labelLarge = defaultTypography.labelLarge.copy(fontFamily = AppFont.Pixelify),
    labelMedium = defaultTypography.labelMedium.copy(fontFamily = AppFont.Pixelify),
    labelSmall = defaultTypography.labelSmall.copy(fontFamily = AppFont.Pixelify)
)