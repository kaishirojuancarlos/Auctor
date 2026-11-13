package org.kaishiro.auctor.cmdline

import com.github.ajalt.clikt.completion.completionOption
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.multiple
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.int
import org.kaishiro.auctor.backend.Default
import org.kaishiro.auctor.backend.displayGuiPrompt
import org.kaishiro.auctor.backend.displayTuiPrompt

class CommandEngine: CliktCommand(
	name = "auctor"
)
{
	init {
		completionOption()
	}
	
	val tui by option(
		"--tui",
		help = "Explicitly uses the TUI backend."
	)
		.flag()
	val gui by option(
		"--gui",
		help = "Explicitly uses the GUI backend."
	)
		.flag()
	
	val title by option(
		"--title", "-t",
		help = "Modifies the title of the prompt."
	)
	val description by option(
		"--description", "-d",
		help = "Adds an optional prompt description."
	)
	val retries by option(
		"--retries", "-r",
		help = "Modifies the number of retries allowed before the prompt is closed."
	)
		.int()
	
	val command by argument(
		help = "The command to authenticate, with its arguments."
	)
		.multiple()
	
	override fun run()
	{
		var backend = Backend.Auto
		if(tui)
			backend = Backend.Tui
		if(gui)
			backend = Backend.Gui
		
		val title = title
			?: Default.TITLE
		val retries = retries
			?: Default.retires
		
		val command = command.joinToString(" ")
		when(backend)
		{
			Backend.Gui ->
			displayByBackend(
				Backend.Gui,
				title = title,
				process = command,
				description = description,
				retries = retries
			)
			Backend.Tui ->
			displayByBackend(
				Backend.Tui,
				title = title,
				process = command,
				description = description,
				retries = retries
			)
			Backend.Auto ->
			{
				val bc: String = System.getenv("XDG_SESSION_TYPE")
				bc.lowercase().trim().let {
					if(it == "wayland" || it == "x11")
						displayByBackend(
							Backend.Gui,
							title = title,
							process = command,
							description = description,
							retries = retries
						)
					else
						displayByBackend(
							Backend.Tui,
							title = title,
							process = command,
							description = description,
							retries = retries
						)
				}
			}
		}
	}
	
	fun displayByBackend(
		backend: Backend,
		title: String = Default.TITLE,
		process: String,
		description: String? = null,
		retries: Int = Default.retires
	)
	{
		if(backend == Backend.Gui)
			displayGuiPrompt(
				title = title,
				process = process,
				description = description,
				retries = retries
			)
		else
			displayTuiPrompt(
				title = title,
				process = process,
				description = description,
				retries = retries
			)
	}
	
	enum class Backend
	{
		Auto,
		Tui,
		Gui
	}
}
