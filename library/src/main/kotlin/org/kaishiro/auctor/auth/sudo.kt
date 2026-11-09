package org.kaishiro.auctor.auth

import java.util.concurrent.TimeUnit

class SudoManager
{
	fun authenticate(
		password: ByteArray
	): Boolean
	{
		require(password.none {
			it == 0.toByte() || it == '\n'.code.toByte()
		})
		val process = ProcessBuilder(
			"sudo", "-S", "-p", "", "-v"
		)
			.apply {
				inheritIO()
				// redirectOutput(ProcessBuilder.Redirect.DISCARD)
				// redirectError(ProcessBuilder.Redirect.DISCARD)
			}
			.start()
		
		return try
		{
			process.outputStream.use {
				it.write(password)
				it.write('\n'.code)
			}
			if(process.waitFor(30, TimeUnit.SECONDS))
			{
				process.destroyForcibly()
				false
			}
			else
			{
				process.exitValue() == 0
			}
		}
		finally
		{
			password.fill(0)
		}
	}
	
	fun isAvailable(): Boolean
	{
		val process = ProcessBuilder(
			"sudo", "-n", "-v"
		)
			.apply {
				redirectOutput(ProcessBuilder.Redirect.DISCARD)
				redirectError(ProcessBuilder.Redirect.DISCARD)
			}
			.start()
		
		return try
		{
			process.waitFor(10, TimeUnit.SECONDS) && process.exitValue() == 0
		}
		finally
		{
			if(process.isAlive)
				process.destroyForcibly()
		}
	}
	
	fun execute(
		password: String,
		vararg command: String
	): Process
	{
		require(command.isNotEmpty())
		require(command.none {
			it.isBlank()
		})
		
		return ProcessBuilder(
			listOf("echo", "-n", "\"${password}\"", "|", "sudo", "-S", "-n", "---") + command
		)
			.start()
	}
}
