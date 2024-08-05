package com.example.androidtask

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.androidtask.model.TaskEntity
import com.example.androidtask.screen.MainViewModel
import com.example.androidtask.ui.theme.AndroidTaskTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AndroidTaskTheme {
                MyApp()
            }
        }
    }
}

@Preview
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyApp(viewModel: MainViewModel = androidx.lifecycle.viewmodel.compose.viewModel()) {
    //val tasks by viewModel.task.collectAsState()
    val taskList = viewModel.taskList.collectAsState().value
    val context = LocalContext.current

    Scaffold(topBar = {
        TopAppBar(
            title = { Text("Tasks") },
            actions = {
                IconButton(onClick = {
                    //val intent = Intent(context, QRCodeScannerActivity::class.java)
                    //(context as Activity).startActivityForResult(intent, REQUEST_CODE_QR_SCAN)
                }) {
                    //Icon(Icons.Default.QrCodeScanner, contentDescription = "QR Code Scanner")
                }
            }
        )
    }) {
        Surface(modifier = Modifier
            .fillMaxSize()
            .padding(top = it.calculateTopPadding(), start = 3.dp, end = 3.dp, bottom = 3.dp)
        ) {
            Content(taskList)
        }
    }
}

@Composable
fun Content(taskList: List<TaskEntity>) {
    LazyColumn {
        items(taskList) { task ->
            TaskItem(task)

            Divider(modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 4.dp))
        }
    }
}

@Composable
fun TaskItem(task: TaskEntity) {
    val colorCode = if (task.colorCode.isNotEmpty()) task.colorCode else "#1df70e"
    val color = Color(android.graphics.Color.parseColor(colorCode))

    Card(modifier = Modifier
        .clip(RoundedCornerShape(5.dp))
        .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = color)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Text(
                text = task.task,
                style = MaterialTheme.typography.bodySmall,
                )
            Text(
                text = task.title,
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = task.description,
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
                )
        }
    }
}