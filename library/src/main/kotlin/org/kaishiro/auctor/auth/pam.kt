package org.kaishiro.auctor.auth

import com.sun.jna.Callback
import com.sun.jna.Library
import com.sun.jna.Native
import com.sun.jna.Pointer
import com.sun.jna.Structure
import com.sun.jna.ptr.PointerByReference

internal interface PamNative: Library
{
	fun pam_start(
		service: String,
		user: String,
		conversation: PamConversation,
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
		const val PROMPT_ECHO_OFF = 1
		const val PROMPT_ECHO_ON = 2
		const val ERROR_MSG = 3
		const val TEXT_INFO = 4
	}
}

@Structure.FieldOrder("msg_style", "msg")
internal class PamMessage: Structure()
{
	@JvmField var msg_style: Int = 0
	@JvmField var msg: String? = null
}

@Structure.FieldOrder("resp", "resp_retcode")
internal class PamResponse: Structure()
{
	@JvmField var resp: Pointer? = null
	@JvmField var resp_retcode: Int = 0
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
