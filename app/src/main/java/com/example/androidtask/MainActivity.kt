package com.example.androidtask

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
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
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MyApp()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyApp(viewModel: MainViewModel = androidx.lifecycle.viewmodel.compose.viewModel()) {
    //val tasks by viewModel.task.collectAsState()
    val taskList = viewModel.taskList.collectAsState().value
    val context = LocalContext.current

    Scaffold(
        topBar = {
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
        }
    ) {
        LazyColumn {
            items(taskList) { task ->
                TaskItem(task)
            }
        }
        /*SwipeRefresh(
            state = rememberSwipeRefreshState(isRefreshing = false),
            onRefresh = { viewModel.fetchTasks() }
        ) {
            LazyColumn {
                items(tasks) { task ->
                    TaskItem(task)
                }
            }
        }*/
    }
}

@Composable
fun TaskItem(task: TaskEntity) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(Color(task.colorCode.toInt()))
    ) {
        Text(text = task.task, style = MaterialTheme.typography.bodySmall)
        Text(text = task.title, style = MaterialTheme.typography.bodyMedium)
        Text(text = task.description, style = MaterialTheme.typography.bodyLarge)
    }
}