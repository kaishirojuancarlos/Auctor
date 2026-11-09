package org.kaishiro.auctor.backend.gui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowState
import org.jetbrains.compose.resources.painterResource
import org.kaishiro.library.generated.resources.Res
import org.kaishiro.library.generated.resources.auctor_icon

@Composable
fun AuctorGuiTheme(
	content: @Composable () -> Unit
)
{
	MaterialTheme(
		colorScheme = darkColorScheme()
	)
	{
		content()
	}
}

@Composable
fun AuctorGuiWindow(
	onCloseRequest: () -> Unit,
	title: String = "",
	content: @Composable () -> Unit
)
{
	Window(
		onCloseRequest = onCloseRequest,
		title = title,
		state = WindowState(
			placement = WindowPlacement.Maximized
		),
		undecorated = true,
		transparent = true,
		icon = painterResource(Res.drawable.auctor_icon)
	)
	{
		Box(
			modifier = Modifier
				.fillMaxSize()
				.background(
					color = Color.Black.copy(
						alpha = 0.75f
					)
				)
				.alpha(0.75f),
			contentAlignment = Alignment.Center
		)
		{
			// intentional redundancy due to certain behaviour in Compose Desktop
			AuctorGuiTheme {
				content()
			}
		}
	}
}
