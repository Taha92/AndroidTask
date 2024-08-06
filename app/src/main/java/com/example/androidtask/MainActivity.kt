package com.example.androidtask

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import com.example.androidtask.model.TaskEntity
import com.example.androidtask.screen.MainViewModel
import com.example.androidtask.ui.theme.AndroidTaskTheme
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshIndicator
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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
    val loading = viewModel.loading.collectAsState().value
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
            /*if(loading) {
                Column(modifier = Modifier
                    .fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator()
                    Text(text = "Loading...")
                }
            } else {
                Content(taskList, viewModel)
            }*/
            if(loading){
                Column (modifier = Modifier){

                    Card(modifier = Modifier
                        .fillMaxWidth(1f)
                        .height(60.dp)
                        .shimmerEffect()
                        .clip(RoundedCornerShape(5.dp)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    ){}

                }
            }
            else{
                Content(taskList, viewModel)
            }
        }
    }
}

@Composable
fun Content(taskList: List<TaskEntity>, viewModel: MainViewModel) {

    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = false)

    SwipeRefresh(state = swipeRefreshState,
        onRefresh = {
            viewModel.viewModelScope.launch(Dispatchers.IO) {
                viewModel.fetchTask()
            }
        },
        indicator = {
            state, refresh ->
        SwipeRefreshIndicator(state = state,
            refreshTriggerDistance = refresh, backgroundColor = Color(0xFF63358B),
            contentColor = Color.White)
    }) {
        LazyColumn {
            items(taskList) { task ->
                TaskItem(task)

                Divider(modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 4.dp))
            }
        }
    }

    /*SwipeRefresh(
        state = rememberSwipeRefreshState(isRefreshing = false),
        onRefresh = {
            viewModel.viewModelScope.launch(Dispatchers.IO) {
                viewModel.fetchTask()
            }
        }
    ) {
        LazyColumn {
            items(taskList) { task ->
                TaskItem(task)

                Divider(modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 4.dp))
            }
        }
    }*/
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

fun Modifier.shimmerEffect(): Modifier = composed {
    var size by remember {
        mutableStateOf(IntSize.Zero)
    }

    val transition = rememberInfiniteTransition()
    val startOffsetX by transition.animateFloat(initialValue = -2 * size.width.toFloat(), targetValue = 2 * size.width.toFloat(), animationSpec = infiniteRepeatable(
        tween(durationMillis = 1000)
    ))

    background(
        brush = Brush.linearGradient(colors = listOf(Color(0xFF77717A), Color(0xFF5C4F66), Color(0xFF766D7E)), start = Offset(startOffsetX, 0f), end = Offset(startOffsetX + size.width.toFloat()
            , size.height.toFloat()))
    ).onGloballyPositioned {
        size = it.size
    }
}