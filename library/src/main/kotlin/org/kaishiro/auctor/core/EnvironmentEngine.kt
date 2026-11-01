package org.kaishiro.auctor.core

object EnvironmentEngine
{
	val hasDisplay
		get() = (!System.getenv("DISPLAY").isNullOrBlank() || !System.getenv("WAYLAND_DISPLAY").isNullOrBlank())
	val sessionType
		get() = System.getenv("XDG_SESSION_TYPE")?.lowercase()
	
	val hasTerminal = (System.console() != null || !System.getenv("TERM").isNullOrBlank() && System.getenv("TERM") !=
	"dumb" || sessionType == "tty")
	
	val type: Type
		get()
		{
			return if(hasDisplay || sessionType in setOf("x11", "wayland"))
				Type.Graphical
			else if(hasTerminal)
				Type.Terminal
			else if(sessionType == "console" || sessionType == "unspecified")
				Type.Headless
			else
				Type.Unknown
		}
	
	enum class Type
	{
		Graphical,
		Terminal,
		Headless,
		Unknown
	}
}
