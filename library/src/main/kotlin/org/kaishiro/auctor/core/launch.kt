package org.kaishiro.auctor.core

import com.github.ajalt.clikt.core.main
import org.kaishiro.auctor.cmdline.CommandEngine

fun launch(
	arguments: Array<String>
)
{
	CommandEngine().main(argv = arguments)
}
