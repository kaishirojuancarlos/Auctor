package org.kaishiro.auctor.auth

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
