package org.kaishiro.auctor.auth

import com.jaredrummler.ktsh.Shell
import com.jaredrummler.ktsh.Shell.Command.Result
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
		command: String
	): Result
	{
		require(command.isNotEmpty())
		val process = ShellWrapper.instance.run("echo -n \"$password\" | sudo -S -p '' $command")
		if(process.isSuccess)
			println(process.stdout())
		else
			error(process.stderr())
		// TODO : optimisations
		return process
	}
}

object ShellWrapper
{
	val instance: Shell by lazy {
		sequenceOf(
			"/usr/bin/env zsh",
			"/usr/bin/env fish",
			"/user/bin/env bash"
		)
			.firstNotNullOfOrNull { path ->
				try
				{
					Shell(path)
				}
				catch(e: Exception)
				{
					null
				}
			}
			?: error("No supported shell found")
	}
}
