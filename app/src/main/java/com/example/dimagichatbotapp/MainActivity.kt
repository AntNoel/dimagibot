package com.example.dimagichatbotapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.dimagichatbotapp.ui.theme.DimagiChatbotAppTheme
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale



interface Command {
    val name: String
    fun run(): String
}

class TimeCommand : Command {
    override val name = "time"
    override fun run(): String {
        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val currentTime = formatter.format(Date())
        return "The current time is: ${currentTime}"
    }
}

class PingCommand : Command {
    override val name = "ping"
    override fun run(): String = "pong!"
}

class HelpCommand(private val allCommands: List<Command>) : Command {
    override val name = "help"
    override fun run(): String {
        return buildString {
            appendLine("Here is a list of all commands:")
            allCommands.forEach { appendLine("- ${it.name}") }
        }
    }
}


fun dispatchCommand(command: String): String {
    val allCommands: Map<String, Command> = listOf(
        PingCommand(),
        TimeCommand()
    ).associateBy { it.name }

    val helpCommand = HelpCommand(allCommands.values.toList())

    val commandName = command.lowercase()
    val cmd = allCommands[commandName]

    return cmd?.run() ?: helpCommand.run()
}


@Composable
fun TextBoxInput(inputText: String, onInputChange: (String) -> Unit, modifier: Modifier = Modifier) {
    TextField(
        value = inputText,
        onValueChange = onInputChange,
        placeholder = { Text("Enter a command") },
        modifier = modifier
    )
}

@Composable
fun SendButton(onSend: () -> Unit) {
    Button(onClick = onSend) {
        Text("Send")
    }
}

@Composable
fun MessageList(messages: List<String>, modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        content = {
            items(messages) { msg ->
                Text(msg)
            }
        }
    )
}




@Composable
fun ChatBot() {
    var inputText by remember { mutableStateOf("") }
    val messages = remember { mutableStateListOf<String>() }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        MessageList(
            messages = messages,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        )


        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            TextBoxInput(
                inputText = inputText,
                onInputChange = { inputText = it },
                Modifier.weight(1f)
            )

            SendButton(onSend = {
                if (inputText.isNotBlank()) {
                    messages.add("You: $inputText")
                    messages.add("Dimagi bot: ${dispatchCommand(inputText)}")
                    inputText = ""
                }
            })
        }
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DimagiChatbotAppTheme {
               ChatBot()
            }
        }
    }
}







