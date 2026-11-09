package org.kaishiro.auctor.core

import org.kaishiro.auctor.auth.PasswordVerifier
import org.kaishiro.auctor.auth.SudoManager
import org.kaishiro.auctor.auth.currentUsername
import org.kaishiro.auctor.backend.displayTuiPrompt

fun launch(
	arguments: Array<String>
)
{
	displayTuiPrompt()
}
