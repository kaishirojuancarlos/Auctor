package org.kaishiro.auctor.backend

import com.github.ajalt.mordant.rendering.TextColors
import com.github.ajalt.mordant.rendering.TextStyles
import com.github.ajalt.mordant.terminal.Terminal

internal fun displayTuiPrompt(
	title: String = Default.TITLE
)
{
	val terminal = Terminal()
	terminal.println(TextStyles.bold(TextStyles.underline(TextColors.blue(title))))
}
