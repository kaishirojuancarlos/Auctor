package org.kaishiro.auctor.core

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEachIndexed
import androidx.compose.ui.window.DialogWindow
import androidx.compose.ui.window.application
import com.github.ajalt.clikt.completion.completionOption
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.help
import com.github.ajalt.clikt.parameters.arguments.multiple
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.help
import com.github.ajalt.clikt.parameters.options.option
import org.jetbrains.compose.resources.painterResource
import org.kaishiro.library.generated.resources.Res
import org.kaishiro.library.generated.resources.auctor_icon

fun CommandEngine.processCommand(
	title: String,
	description: String,
	command: List<String>
)
{
	val commandActual = command.getOrNull(0)
	commandActual?.let { commandActual ->
		val environmentType = EnvironmentEngine.type
		if(environmentType == EnvironmentEngine.Type.Graphical)
		{
			application(
				exitProcessOnExit = false
			)
			{
				val displayWindow = rememberSaveable { mutableStateOf(true) }
				if(displayWindow.value)
					DialogWindow(
						onCloseRequest = {},
						title = title,
						undecorated = true,
						transparent = true,
						icon = painterResource(Res.drawable.auctor_icon)
					)
					{
						AuctorTheme {
							Column(
								modifier = Modifier
									.background(
										MaterialTheme.colorScheme.background,
										RoundedCornerShape(15.dp)
									)
							)
							{
								Scaffold(
									modifier = Modifier
										.padding(10.dp),
									bottomBar = {
										Column(
											verticalArrangement = Arrangement.spacedBy(10.dp),
											horizontalAlignment = Alignment.CenterHorizontally,
											modifier = Modifier
												.fillMaxWidth()
										)
										{
											HorizontalDivider(
												modifier = Modifier
													.fillMaxWidth(0.9f),
												thickness = 1.dp,
												color = MaterialTheme.colorScheme.onBackground
											)
											Row(
												verticalAlignment = Alignment.CenterVertically,
												horizontalArrangement = Arrangement.End,
												modifier = Modifier
													.fillMaxWidth()
											)
											{
												OutlinedButton(
													onClick = {
														displayWindow.value = false
													}
												)
												{
													Text("Cancel")
												}
												Spacer(
													modifier = Modifier
														.size(10.dp)
												)
												Button(
													onClick = {
														TODO()
													}
												)
												{
													Text("Submit")
												}
											}
										}
									},
									topBar = {
										Column(
											verticalArrangement = Arrangement.spacedBy(10.dp),
											horizontalAlignment = Alignment.CenterHorizontally,
											modifier = Modifier
												.fillMaxWidth()
										)
										{
											Image(
												painterResource(Res.drawable.auctor_icon),
												contentDescription = null,
												modifier = Modifier
													.size(50.dp)
											)
											Text(
												title,
												style = MaterialTheme.typography.titleMedium,
												color = MaterialTheme.colorScheme.primary
											)
											HorizontalDivider(
												modifier = Modifier
													.fillMaxWidth(0.9f),
												thickness = 2.dp,
												color = MaterialTheme.colorScheme.onBackground
											)
										}
									}
								)
								{
									Column(
										modifier = Modifier
											.padding(it)
									)
									{
										LazyColumn(
											verticalArrangement = Arrangement.spacedBy(10.dp),
											horizontalAlignment = Alignment.Start,
											modifier = Modifier
												.fillMaxSize()
												.padding(10.dp)
										)
										{
											item {
												Text(
													"Please authenticate the command to run with root access:",
													color = MaterialTheme.colorScheme.onBackground
												)
											}
											item {
												Surface(
													shape = RoundedCornerShape(15.dp),
													color = MaterialTheme.colorScheme.surface,
													contentColor = MaterialTheme.colorScheme.onSurface,
													modifier = Modifier
														.fillMaxWidth()
												)
												{
													Text(
														buildString {
															command.fastForEachIndexed { index, value ->
																if(index > 0)
																	append(" ")
																append(value)
															}
														},
														style = MaterialTheme.typography.labelMedium,
														modifier = Modifier
															.padding(10.dp)
													)
												}
											}
											item {
												Text(
													description,
													color = MaterialTheme.colorScheme.onBackground
												)
											}
											item {
												val enteredPassword = rememberSaveable { mutableStateOf("") }
												val visiblePassword = rememberSaveable { mutableStateOf(false) }
												val isError = rememberSaveable { mutableStateOf(false) }
												OutlinedTextField(
													value = enteredPassword.value,
													onValueChange = {
														enteredPassword.value = it
													},
													label = {
														Text("Password")
													},
													keyboardOptions = KeyboardOptions(
														keyboardType = KeyboardType.Password,
														imeAction = ImeAction.Done
													),
													keyboardActions = KeyboardActions(
														onDone = {
															TODO()
														}
													),
													isError = isError.value,
													trailingIcon = {
														Column(
															modifier = Modifier
																.clickable {
																	visiblePassword.value = !visiblePassword.value
																}
														)
														{
															Text(
																if(!visiblePassword.value)
																	"SHOW PASSWORD"
																else
																	"HIDE PASSWORD",
																modifier = Modifier
																	.padding(10.dp)
															)
														}
													},
													visualTransformation = if(!visiblePassword.value)
														PasswordVisualTransformation()
													else
														VisualTransformation.None,
													singleLine = true,
													colors = OutlinedTextFieldDefaults.colors(
														focusedTextColor = MaterialTheme.colorScheme.primary,
														unfocusedTextColor = MaterialTheme.colorScheme.secondary,
														focusedBorderColor = MaterialTheme.colorScheme.primary,
														unfocusedBorderColor = MaterialTheme.colorScheme.secondary,
														focusedLabelColor = MaterialTheme.colorScheme.primary,
														unfocusedLabelColor = MaterialTheme.colorScheme.secondary,
														focusedTrailingIconColor = MaterialTheme.colorScheme.primary,
														unfocusedTrailingIconColor = MaterialTheme.colorScheme
															.secondary,
														errorTextColor = MaterialTheme.colorScheme.error,
														errorBorderColor = MaterialTheme.colorScheme.error,
														errorLabelColor = MaterialTheme.colorScheme.error,
														errorTrailingIconColor = MaterialTheme.colorScheme.error
													)
													,
													modifier = Modifier
														.fillMaxWidth()
												)
											}
										}
									}
								}
							}
						}
					}
			}
		}
		else if(environmentType == EnvironmentEngine.Type.Terminal)
		{
			echo("TODO: TEXTUAL PROMPT OUTPUT! : $commandActual")
		}
	}
}

fun CommandEngine.displayInformation()
{
	val environmentType = EnvironmentEngine.type
	if(environmentType == EnvironmentEngine.Type.Graphical)
		application(
			exitProcessOnExit = false
		)
		{
			val displayWindow = rememberSaveable { mutableStateOf(true) }
			if(displayWindow.value)
				DialogWindow(
					onCloseRequest = {
						displayWindow.value = false
					},
					title = "Auctor",
					icon = painterResource(Res.drawable.auctor_icon)
				)
				{
					AuctorTheme {
						Text("TODO!")
					}
				}
		}
	else if(environmentType == EnvironmentEngine.Type.Terminal)
	{
		echo("TODO: TEXTUAL INFO OUTPUT!")
	}
}

class CommandEngine: CliktCommand()
{
	init
	{
		completionOption()
	}
	
	val displayInformationFlag by option("-i", "--info", "--information")
		.flag(
			default = false
		)
		.help("Displays standard information about Auctor")
	
	val title by option("-t", "--title")
	val description by option("-d", "--description")
	
	val command by argument()
		.multiple()
		.help("Command to pass through Auctor, with its arguments included")
	
	override fun run()
	{
		if(displayInformationFlag)
			displayInformation()
		val title = title
			?: "Auctor Authentication"
		val description = description
			?: "The process has requested root access."
		
		if(command.isNotEmpty())
			processCommand(
				title = title,
				description = description,
				command = command
			)
	}
}
