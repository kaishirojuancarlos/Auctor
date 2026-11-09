package org.kaishiro.auctor.core

import org.kaishiro.auctor.backend.displayTuiPrompt

fun launch(
	arguments: Array<String>
)
{
	displayTuiPrompt(
		process = "pwd",
		description = "TEST!"
	)
}
