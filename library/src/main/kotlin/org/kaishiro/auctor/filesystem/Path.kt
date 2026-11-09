package org.kaishiro.auctor.filesystem

import io.github.sanyarnd.standardpaths.StandardPaths

object Path
{
	val homeDirectory = StandardPaths.home().get().toFile()
}
