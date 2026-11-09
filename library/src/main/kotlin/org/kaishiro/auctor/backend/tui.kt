package org.kaishiro.auctor.backend

import com.github.ajalt.mordant.rendering.TextColors
import com.github.ajalt.mordant.rendering.TextStyles
import com.github.ajalt.mordant.terminal.Terminal
import com.github.ajalt.mordant.terminal.prompt
import org.kaishiro.auctor.auth.PasswordVerifier
import org.kaishiro.auctor.auth.SudoManager
import org.kaishiro.auctor.auth.currentUsername

internal fun displayTuiPrompt(
	title: String = Default.TITLE,
	process: String,
	description: String? = null,
	retries: Int = Default.retires
)
{
	val retries = if(retries > 1)
		retries
	else
		1
	val passwordVerifier = PasswordVerifier()
	val sudoManager = SudoManager()
	loop@for(count in 1..retries)
	{
		val terminal = Terminal()
		terminal.println(TextStyles.bold(TextStyles.underline(TextColors.blue(title))))
		terminal.println("The process needs authentication to proceed with root access.")
		terminal.println(TextStyles.bold(TextStyles.italic("Process: ${TextColors.gray(process)}")))
		description?.let {
			terminal.println(
				TextStyles.italic(it))
		}
		val password = terminal.prompt(TextStyles.bold(TextColors.blue("Enter password for [ ${currentUsername()} ]: ${TextColors.green
			("")}")))
		password?.let {
			val verified = passwordVerifier.verifyCurrentUser(it.toByteArray())
			if(verified)
			{
				val process = sudoManager.execute(it, process)
				process.waitFor()
				break@loop
			}
		}
		terminal.println(TextStyles.bold(TextStyles.italic(TextColors.red("The entered password is invalid!"))))
	}
}
