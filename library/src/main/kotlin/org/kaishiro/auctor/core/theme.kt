package org.kaishiro.auctor.core

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.sp
import com.materialkolor.dynamicColorScheme
import org.jetbrains.skiko.currentSystemTheme

object ColourScheme
{
	val light = lightColorScheme()
	val dark = darkColorScheme()
}

val typography = Typography(
	bodyMedium = TextStyle(
		fontSize = 12.sp
	),
	bodyLarge = TextStyle(
		fontSize = 14.sp
	),
	bodySmall = TextStyle(
		fontSize = 10.sp
	),
	
	titleMedium = TextStyle(
		fontSize = 14.sp,
		fontWeight = FontWeight.Bold
	),
	titleLarge = TextStyle(
		fontSize = 16.sp,
		fontWeight = FontWeight.Bold
	),
	titleSmall = TextStyle(
		fontSize = 12.sp,
		fontWeight = FontWeight.Bold
	),
	
	labelMedium = TextStyle(
		fontSize = 10.sp,
		fontWeight = FontWeight.Thin
	),
	labelLarge = TextStyle(
		fontSize = 12.sp,
		fontWeight = FontWeight.Thin
	),
	labelSmall = TextStyle(
		fontSize = 8.sp,
		fontWeight = FontWeight.Thin
	)
)

@Composable
fun AuctorTheme(
	useDarkTheme: Boolean = isSystemInDarkTheme(),
	content: @Composable () -> Unit
)
{
	val colourScheme = if(!useDarkTheme)
		ColourScheme.light
	else
		ColourScheme.dark
	
	MaterialTheme(
		colorScheme = colourScheme,
		typography = typography,
		content = {
			CompositionLocalProvider(
				LocalTextStyle provides typography.bodyMedium,
				LocalDensity provides Density(0.75f)
			)
			{
				content()
			}
		}
	)
}
