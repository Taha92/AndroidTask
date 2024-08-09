package com.example.androidtask.screen

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.example.androidtask.R
import com.google.zxing.ResultPoint
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import com.journeyapps.barcodescanner.DecoratedBarcodeView
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(navController: NavController, viewModel: SearchViewModel) {
    var showMenu by remember { mutableStateOf(false) }
    var showScanner by remember { mutableStateOf(false) }
    var query by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()

    Scaffold(topBar = {
        TopAppBar(
            title = { Text("Tasks") },
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back icon"
                    )
                }
            },
            actions = {
                IconButton(onClick = { showMenu = !showMenu }) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "More icon"
                    )
                }
                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text(text = "Scan") },
                        leadingIcon = {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_qr_code),
                                contentDescription = "QR code icon"
                            )
                        },
                        onClick = { showScanner = true; showMenu = false },
                    )

                }
            }
        )
    }) {
        Surface(modifier = Modifier
            .fillMaxSize()
            .padding(top = it.calculateTopPadding(), start = 3.dp, end = 3.dp, bottom = 3.dp)
        ) {
            Column {
                SearchForm(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .alpha(if (showScanner) 0f else 1f),
                    searchQuery = query,
                ) { searchQuery ->
                    showScanner = false
                    query = searchQuery
                    viewModel.searchTasks(query = query)
                }
                TaskList(viewModel)
            }

            if (showScanner) {
                viewModel.clearTasks()
                Log.d("SearchScreen", "Starting scanner")
                StartScanning { scannedQuery ->
                    query = scannedQuery
                    showScanner = false
                    coroutineScope.launch {
                        viewModel.searchTasks(query = query)
                    }
                }
            }
        }
    }
}

@Composable
fun StartScanning(onResult: (String) -> Unit) {
    val context = LocalContext.current
    var hasCameraPermission by remember { mutableStateOf(false) }
    val decoratedBarcodeView = remember { DecoratedBarcodeView(context) }
    var isScanning by remember { mutableStateOf(true) }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted: Boolean ->
            hasCameraPermission = isGranted
            if (isGranted) {
                Log.d("QRCodeScannerScreen", "Camera permission granted")
                decoratedBarcodeView.resume() // Start the camera preview
            } else {
                //cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                Log.d("QRCodeScannerScreen", "Camera permission denied")
            }
        }
    )

    LaunchedEffect(Unit) {
        when (PackageManager.PERMISSION_GRANTED) {
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) -> {
                hasCameraPermission = true
                Log.d("QRCodeScannerScreen", "Camera permission already granted")
                decoratedBarcodeView.resume() // Start the camera preview
            }
            else -> {
                cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            Log.d("QRCodeScannerScreen", "Disposing and pausing camera")
            decoratedBarcodeView.pause()
        }
    }

    Column(
        modifier = Modifier
            .padding(12.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (hasCameraPermission) {
            if (isScanning) {
                AndroidView(
                    factory = { decoratedBarcodeView },
                    modifier = Modifier.fillMaxSize(),
                ) { view ->
                    view.decodeContinuous(object : BarcodeCallback {
                        override fun barcodeResult(result: BarcodeResult?) {
                            result?.let {
                                Log.d("QRCodeScannerScreen", "Barcode result: ${it.text}")
                                onResult(it.text)
                                isScanning = false
                            }
                        }

                        override fun possibleResultPoints(resultPoints: MutableList<ResultPoint>?) {}
                    })
                    view.resume() // Ensure the camera preview is resumed
                }
            }

        } else {
            Button(onClick = {
                Log.d("TAG", "Button: Clicked")
                // Redirect user to app settings to manually grant permission if denied
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri = Uri.fromParts("package", context.packageName, null)
                intent.data = uri
                context.startActivity(intent)
            }) {
                Text("Grant Camera Permission")
            }
        }
    }

}

@Composable
fun TaskList(viewModel: SearchViewModel) {
    val taskList = viewModel.taskList.collectAsState().value

    if(taskList.isEmpty()) {
        Column(modifier = Modifier
            .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "No task...")
        }
    } else {
        LazyColumn {
            items(taskList) { task ->
                TaskItem(task)

                Divider(modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 4.dp))
            }
        }
    }
}

@Composable
fun SearchForm(
    modifier: Modifier = Modifier,
    hint: String = "Search",
    searchQuery: String,
    onSearch: (String) -> Unit = {},
) {
    Column(modifier) {
        val searchQueryState = rememberSaveable { mutableStateOf("") }
        val keyboardController = LocalSoftwareKeyboardController.current
        val valid = remember(searchQueryState.value) { searchQueryState.value.trim().isNotEmpty() }

        InputField(
            valueState = searchQueryState,
            labelId = if (searchQuery.isNotEmpty()) searchQuery else hint,
            enabled = true,
            onAction = KeyboardActions {
                if (!valid) {
                    return@KeyboardActions
                } else {
                    onSearch(searchQueryState.value.trim())
                    searchQueryState.value = ""
                    keyboardController?.hide()
                }
            }
        )
    }
}

@Composable
fun InputField(
    modifier: Modifier = Modifier,
    valueState: MutableState<String>,
    labelId: String,
    enabled: Boolean,
    isSingleLine: Boolean = true,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Next,
    onAction: KeyboardActions = KeyboardActions.Default
) {
    OutlinedTextField(
        value = valueState.value,
        onValueChange = {
            valueState.value = it
        },
        label = { Text(text = labelId) },
        singleLine = isSingleLine,
        textStyle = TextStyle(
            fontSize = 18.sp,
            color = MaterialTheme.colorScheme.onBackground
        ),
        modifier = modifier
            .padding(bottom = 10.dp, start = 10.dp, end = 10.dp)
            .fillMaxWidth(),
        enabled = enabled,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType, imeAction = imeAction),
        keyboardActions = onAction
    )
}