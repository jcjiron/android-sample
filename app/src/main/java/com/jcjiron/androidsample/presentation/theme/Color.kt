package com.jcjiron.androidsample.presentation.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

// Paleta base: portal verde + azul de la bata de Rick.
private val PortalGreen = Color(0xFF3F6A12)
private val PortalGreenLight = Color(0xFF97CE4C)
private val PortalGreenContainer = Color(0xFFC0F08A)
private val PortalGreenDarkContainer = Color(0xFF2C4F00)
private val LabBlue = Color(0xFF00658E)
private val LabBlueLight = Color(0xFF84CFFF)
private val LabBlueContainer = Color(0xFFC7E7FF)
private val LabBlueDarkContainer = Color(0xFF004C6C)
private val MortyYellow = Color(0xFF6B5E00)
private val MortyYellowLight = Color(0xFFDDC83A)
private val MortyYellowContainer = Color(0xFFF9E45A)
private val MortyYellowDarkContainer = Color(0xFF514700)
private val ErrorRed = Color(0xFFBA1A1A)
private val ErrorRedLight = Color(0xFFFFB4AB)
private val ErrorRedContainer = Color(0xFFFFDAD6)
private val ErrorRedDarkContainer = Color(0xFF93000A)

private val SurfaceLight = Color(0xFFF8FAF0)
private val SurfaceVariantLight = Color(0xFFE0E4D6)
private val SurfaceContainerLight = Color(0xFFEDEFE4)
private val OnSurfaceLight = Color(0xFF1A1C16)
private val OnSurfaceVariantLight = Color(0xFF44483D)
private val OutlineLight = Color(0xFF75796C)

private val SurfaceDark = Color(0xFF11140E)
private val SurfaceVariantDark = Color(0xFF44483D)
private val SurfaceContainerDark = Color(0xFF1E211A)
private val OnSurfaceDark = Color(0xFFE2E3D8)
private val OnSurfaceVariantDark = Color(0xFFC4C8BA)
private val OutlineDark = Color(0xFF8E9285)

val LightColors = lightColorScheme(
    primary = PortalGreen,
    onPrimary = Color.White,
    primaryContainer = PortalGreenContainer,
    onPrimaryContainer = PortalGreenDarkContainer,
    secondary = LabBlue,
    onSecondary = Color.White,
    secondaryContainer = LabBlueContainer,
    onSecondaryContainer = LabBlueDarkContainer,
    tertiary = MortyYellow,
    onTertiary = Color.White,
    tertiaryContainer = MortyYellowContainer,
    onTertiaryContainer = MortyYellowDarkContainer,
    error = ErrorRed,
    onError = Color.White,
    errorContainer = ErrorRedContainer,
    onErrorContainer = ErrorRedDarkContainer,
    background = SurfaceLight,
    onBackground = OnSurfaceLight,
    surface = SurfaceLight,
    onSurface = OnSurfaceLight,
    surfaceVariant = SurfaceVariantLight,
    onSurfaceVariant = OnSurfaceVariantLight,
    surfaceContainer = SurfaceContainerLight,
    outline = OutlineLight,
)

val DarkColors = darkColorScheme(
    primary = PortalGreenLight,
    onPrimary = PortalGreenDarkContainer,
    primaryContainer = PortalGreenDarkContainer,
    onPrimaryContainer = PortalGreenContainer,
    secondary = LabBlueLight,
    onSecondary = LabBlueDarkContainer,
    secondaryContainer = LabBlueDarkContainer,
    onSecondaryContainer = LabBlueContainer,
    tertiary = MortyYellowLight,
    onTertiary = MortyYellowDarkContainer,
    tertiaryContainer = MortyYellowDarkContainer,
    onTertiaryContainer = MortyYellowContainer,
    error = ErrorRedLight,
    onError = ErrorRedDarkContainer,
    errorContainer = ErrorRedDarkContainer,
    onErrorContainer = ErrorRedContainer,
    background = SurfaceDark,
    onBackground = OnSurfaceDark,
    surface = SurfaceDark,
    onSurface = OnSurfaceDark,
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = OnSurfaceVariantDark,
    surfaceContainer = SurfaceContainerDark,
    outline = OutlineDark,
)
