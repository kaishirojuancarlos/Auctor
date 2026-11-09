package org.kaishiro.auctor.auth

import com.sun.jna.Callback
import com.sun.jna.Library
import com.sun.jna.Native
import com.sun.jna.NativeLong
import com.sun.jna.Pointer
import com.sun.jna.Structure
import com.sun.jna.ptr.PointerByReference
import io.micronaut.core.annotation.ReflectiveAccess

@ReflectiveAccess
internal interface PamNative: Library
{
	fun pam_start(
		service: String,
		user: String,
		conversation: PamConv,
		handle: PointerByReference
	): Int
	
	fun pam_authenticate(
		handle: Pointer,
		flags: Int
	): Int
	fun pam_acct_mgmt(
		handle: Pointer,
		flags: Int
	): Int
	fun pam_end(
		handle: Pointer,
		status: Int
	)
	
	companion object
	{
		val INSTANCE = Native.load("pam", PamNative::class.java)
		
		const val SUCCESS = 0
		const val CONV_ERR = 19
		
		const val PROMPT_ECHO_OFF = 1
		const val PROMPT_ECHO_ON = 2
		const val ERROR_MSG = 3
		const val TEXT_INFO = 4
	}
}

@Structure.FieldOrder("msg_style", "msg")
internal class PamMessage(): Structure()
{
	@JvmField var msg_style: Int = 0
	@JvmField var msg: String? = null
	
	constructor(
		pointer: Pointer
	): this()
	{
		useMemory(pointer)
		read()
	}
}

@Structure.FieldOrder("resp", "resp_retcode")
internal class PamResponse(): Structure()
{
	@JvmField var resp: Pointer? = null
	@JvmField var resp_retcode: Int = 0
	
	constructor(
		pointer: Pointer
	): this()
	{
		useMemory(pointer)
		read()
	}
}

internal interface PamConversation: Callback
{
	fun invoke(
		count: Int,
		messages: Pointer,
		responses: PointerByReference,
		data: Pointer?
	): Int
}

@Structure.FieldOrder("conv", "appdata_ptr")
internal class PamConv: Structure()
{
	@JvmField var conv: PamConversation? = null
	@JvmField var appdata_ptr: Pointer? = null
}

class PasswordVerifier
{
	fun verifyCurrentUser(
		password: ByteArray
	): Boolean = verify(password = password, username = currentUsername())
	
	fun verify(
		username: String,
		password: ByteArray,
		service: String = "login"
	): Boolean
	{
		return verifyStatus(username, password, service) == PamNative.SUCCESS
	}
	
	fun verifyStatus(
		username: String,
		password: ByteArray,
		service: String = "login"
	): Int
	{
		require(username.isNotBlank())
		require(service.isNotBlank())
		require(password.none {
			it == 0.toByte()
		})
		
		val secret = password.copyOf()
		val libC = LibC.INSTANCE
		val pam = PamNative.INSTANCE
		
		val callback = object: PamConversation {
			override fun invoke(count: Int, messages: Pointer, responses: PointerByReference, data: Pointer?): Int
			{
				return try
				{
					if(count <= 0 || count > 32)
						return PamNative.CONV_ERR
					val responseSize = PamResponse().size()
					val arraySize = responseSize * count
					val array = libC.malloc(NativeLong(arraySize.toLong()))
						?: return PamNative.CONV_ERR
					array.clear(arraySize.toLong())
					
					try
					{
						for(i in 0 until count)
						{
							val messagePointer = messages.getPointer(i.toLong() * Native.POINTER_SIZE)
								?: throw IllegalStateException()
							val message = PamMessage(messagePointer)
							val response = PamResponse(array.share(i.toLong() * responseSize))
							
							when(message.msg_style)
							{
								PamNative.PROMPT_ECHO_OFF ->
								{
									val buffer = libC.malloc(NativeLong(secret.size.toLong() + 1))
										?: throw OutOfMemoryError()
									buffer.write(0, secret, 0, secret.size)
									buffer.setByte(secret.size.toLong(), 0)
									response.resp = buffer
								}
								
								PamNative.ERROR_MSG ->
									PamNative.ERROR_MSG
								
								PamNative.TEXT_INFO ->
									response.resp = null
								else ->
									throw IllegalStateException("Unsupported PAM prompt")
							}
							response.resp_retcode = 0
							response.write()
						}
						responses.value = array
						PamNative.SUCCESS
					}
					catch(e: Throwable)
					{
						for(i in 0 until count)
						{
							val response = PamResponse(array.share(i.toLong() * responseSize))
							response.read()
							response.resp?.let(libC::free)
						}
						libC.free(array)
						PamNative.CONV_ERR
					}
				}
				catch(e: Throwable)
				{
					PamNative.CONV_ERR
				}
			}
		}
		val conversation = PamConv().apply {
			conv = callback
			write()
		}
		val handle = PointerByReference()
		var status = PamNative.CONV_ERR
		var started = false
		
		try
		{
			status = pam.pam_start(service, username, conversation, handle)
			if(status != PamNative.SUCCESS)
				return status
			started = true
			status = pam.pam_authenticate(handle.value, 0)
			if(status == PamNative.SUCCESS)
				status = pam.pam_acct_mgmt(handle.value, 0)
			return status
		}
		finally
		{
			if(started)
				pam.pam_end(handle.value, status)
			secret.fill(0)
		}
	}
}

@ReflectiveAccess
internal interface LibC: Library
{
	fun malloc(
		size: NativeLong
	): Pointer?
	fun free(
		pointer: Pointer
	)
	
	fun geteuid(): Int
	fun getpwuid(
		uid: Int
	): Pointer?
	
	companion object
	{
		val INSTANCE = Native.load("c", LibC::class.java)
	}
}

internal fun currentUsername(): String
{/*
	val libC = LibC.INSTANCE
	val password = libC.getpwuid(libC.geteuid())
		?: error("Unable to resolve current user")
	return password.getString(0)*/
	return System.getProperty("user.name")
}
