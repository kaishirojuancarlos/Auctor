package org.kaishiro.auctor.core

import org.kaishiro.auctor.auth.PasswordVerifier

fun launch(
	arguments: Array<String>
)
{
	val verifier = PasswordVerifier()
	val valid = verifier.verify("kaishiro", "hellium".toByteArray(), "login")
	println(":: $valid")
}
