package org.kaishiro.auctor.backend

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Keyboard
import androidx.compose.material.icons.outlined.KeyboardHide
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.application
import androidx.constraintlayout.compose.ConstraintLayout
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.painterResource
import org.kaishiro.auctor.auth.PasswordVerifier
import org.kaishiro.auctor.auth.SudoManager
import org.kaishiro.auctor.auth.currentUsername
import org.kaishiro.auctor.backend.gui.AuctorGuiTheme
import org.kaishiro.auctor.backend.gui.AuctorGuiWindow
import org.kaishiro.library.generated.resources.Res
import org.kaishiro.library.generated.resources.auctor_icon
import kotlin.time.Duration.Companion.milliseconds

internal enum class PassReturn
{
	Invalid,
	Success
}

internal fun displayGuiPrompt(
	title: String = Default.TITLE,
	process: String,
	description: String? = null,
	retries: Int = Default.retires
)
{
	application(
		exitProcessOnExit = false
	)
	{
		AuctorGuiTheme {
			var count = 0
			val passwordVerifier = PasswordVerifier()
			val sudoManager = SudoManager()
			var textFieldSelectionFunction: () -> Unit = {}
			fun verifyAndPass(
				password: String
			): PassReturn
			{
				textFieldSelectionFunction.invoke()
				val password = password.toByteArray()
				val verified = passwordVerifier.verifyCurrentUser(password)
				if(!verified)
				{
					count++
					return PassReturn.Invalid
				}
				val process = sudoManager.execute(process)
				process.waitFor()
				return if(process.exitValue() != 0)
				{
					count++
					PassReturn.Invalid
				}
				else
					PassReturn.Success
			}
			val showDialog = rememberSaveable { mutableStateOf(true) }
			if(showDialog.value && count < retries)
				AuctorGuiWindow(
					onCloseRequest = {
						showDialog.value = false
					},
					title = title
				)
				{
					Card(
						shape = RoundedCornerShape(15.dp),
						colors = CardDefaults.cardColors(
							containerColor = MaterialTheme.colorScheme.background,
							contentColor = MaterialTheme.colorScheme.onBackground
						),
						elevation = CardDefaults.cardElevation(0.dp)
					)
					{
						ConstraintLayout(
							modifier = Modifier
								.padding(10.dp)
						)
						{
							val (icon, titleRef, note, processText, descriptionRef, passwordPrompt, actionBlock) = createRefs()
							
							Image(
								painterResource(Res.drawable.auctor_icon),
								contentDescription = null,
								modifier = Modifier
									.size(100.dp)
									.constrainAs(icon)
									{
										centerHorizontallyTo(parent)
									}
							)
							Text(
								title,
								fontWeight = FontWeight.Bold,
								color = MaterialTheme.colorScheme.primary,
								fontSize = 20.sp,
								modifier = Modifier
									.padding(
										top = 5.dp
									)
									.constrainAs(titleRef)
									{
										centerHorizontallyTo(parent)
										top.linkTo(icon.bottom)
									}
							)
							Text(
								"The process needs authentication to proceed with root access.",
								fontSize = 12.sp,
								modifier = Modifier
									.padding(
										top = 5.dp
									)
									.constrainAs(note)
									{
										centerHorizontallyTo(parent)
										top.linkTo(titleRef.bottom)
									}
							)
							Text(
								buildAnnotatedString {
									withStyle(SpanStyle(
										fontWeight = FontWeight.Bold,
										fontStyle = FontStyle.Italic
									))
									{
										append("Process: ")
									}
									withStyle(SpanStyle(
										color = MaterialTheme.colorScheme.secondary
									))
									{
										append(process)
									}
								},
								fontSize = 10.sp,
								modifier = Modifier
									.padding(
										top = 5.dp
									)
									.constrainAs(processText)
									{
										centerHorizontallyTo(parent)
										top.linkTo(note.bottom)
									}
							)
							val modifier = Modifier
							if(description != null)
								modifier
									.padding(
										top = 5.dp
									)
							Text(
								description
									?: "",
								fontSize = 10.sp,
								fontStyle = FontStyle.Italic,
								modifier = modifier
									.constrainAs(descriptionRef)
									{
										centerHorizontallyTo(parent)
										top.linkTo(processText.bottom)
									}
							)
							val showPassword = rememberSaveable { mutableStateOf(false) }
							val enteredPassword = rememberSaveable { mutableStateOf(TextFieldValue("")) }
							val isError = rememberSaveable { mutableStateOf(false) }
							textFieldSelectionFunction = {
								enteredPassword.value = enteredPassword.value.copy(
									selection = TextRange(0, enteredPassword.value.text.length)
								)
							}
							LaunchedEffect(isError.value)
							{
								if(isError.value)
								{
									delay(5000.milliseconds)
									isError.value = false
								}
							}
							OutlinedTextField(
								value = enteredPassword.value,
								onValueChange = {
									enteredPassword.value = it
									if(isError.value)
										isError.value = false
								},
								singleLine = true,
								isError = isError.value,
								colors = OutlinedTextFieldDefaults.colors(
									focusedTextColor = MaterialTheme.colorScheme.primary,
									unfocusedTextColor = MaterialTheme.colorScheme.secondary,
									errorTextColor = MaterialTheme.colorScheme.error,
									cursorColor = MaterialTheme.colorScheme.primary,
									errorCursorColor = MaterialTheme.colorScheme.error,
									focusedBorderColor = MaterialTheme.colorScheme.primary,
									unfocusedBorderColor = MaterialTheme.colorScheme.secondary,
									errorBorderColor = MaterialTheme.colorScheme.error,
									focusedTrailingIconColor = MaterialTheme.colorScheme.primary,
									unfocusedTrailingIconColor = MaterialTheme.colorScheme.secondary,
									errorTrailingIconColor = MaterialTheme.colorScheme.error,
									focusedLabelColor = MaterialTheme.colorScheme.primary,
									unfocusedLabelColor = MaterialTheme.colorScheme.secondary,
									errorLabelColor = MaterialTheme.colorScheme.error,
									focusedSupportingTextColor = MaterialTheme.colorScheme.primary,
									unfocusedSupportingTextColor = MaterialTheme.colorScheme.secondary,
									errorSupportingTextColor = MaterialTheme.colorScheme.error
								),
								supportingText = {
									if(isError.value)
										Text("The entered password is invalid!")
								},
								label = {
									Text(buildAnnotatedString {
										append("Password for [ ")
										withStyle(SpanStyle(
											fontWeight = FontWeight.Bold
										))
										{
											append(currentUsername())
										}
										append(" ]")
									})
								},
								keyboardActions = KeyboardActions(
									onDone = {
										if(verifyAndPass(enteredPassword.value.text) == PassReturn.Invalid)
											isError.value = true
										else
										{
											isError.value = false
											showDialog.value = false
										}
									}
								),
								keyboardOptions = KeyboardOptions(
									keyboardType = KeyboardType.Password,
									imeAction = ImeAction.Done
								),
								trailingIcon = {
									Box(
										modifier = Modifier
											.clickable {
												showPassword.value = !showPassword.value
											}
									)
									{
										Icon(
											if(!showPassword.value)
												Icons.Outlined.Keyboard
											else
												Icons.Outlined.KeyboardHide,
											contentDescription = null,
											modifier = Modifier
												.padding(10.dp)
										)
									}
								},
								visualTransformation = if(!showPassword.value)
									PasswordVisualTransformation()
								else
									VisualTransformation.None,
								modifier = Modifier
									.constrainAs(passwordPrompt)
									{
										centerHorizontallyTo(parent)
										top.linkTo(descriptionRef.bottom)
									}
									.padding(
										top = 5.dp
									)
							)
							Row(
								verticalAlignment = Alignment.CenterVertically,
								horizontalArrangement = Arrangement.spacedBy(10.dp),
								modifier = Modifier
									.constrainAs(actionBlock)
									{
										end.linkTo(parent.end)
										top.linkTo(passwordPrompt.bottom)
									}
									.padding(
										top = 5.dp
									)
							)
							{
								OutlinedButton(
									colors = ButtonDefaults.outlinedButtonColors(
										contentColor = MaterialTheme.colorScheme.error
									),
									border = BorderStroke(
										width = 3.dp,
										color = MaterialTheme.colorScheme.error
									),
									onClick = {
										showDialog.value = false
									}
								)
								{
									Text("Cancel")
								}
								Button(
									colors = ButtonDefaults.buttonColors(
										containerColor = MaterialTheme.colorScheme.primary,
										contentColor = MaterialTheme.colorScheme.onPrimary
									),
									onClick = {
										if(verifyAndPass(enteredPassword.value.text) == PassReturn.Invalid)
											isError.value = true
										else
										{
											isError.value = false
											showDialog.value = false
										}
									}
								)
								{
									Text("Submit")
								}
							}
						}
					}
				}
		}
	}
}
