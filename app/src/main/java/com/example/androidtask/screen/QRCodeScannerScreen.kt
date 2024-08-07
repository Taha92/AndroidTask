package com.example.androidtask.screen

import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import com.google.zxing.ResultPoint
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import com.journeyapps.barcodescanner.DecoratedBarcodeView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QRCodeScannerScreen(
    navController: NavHostController,
    onResult: (String) -> Unit
) {
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

    Scaffold(topBar = {
        TopAppBar(
            title = { Text("Scan") },
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back icon"
                    )
                }
            }
        )
    }) {
        Surface(modifier = Modifier
            .fillMaxSize()
            .padding(top = it.calculateTopPadding(), start = 3.dp, end = 3.dp, bottom = 3.dp)
        ) {
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
                                        //view.pause()
                                    }
                                }

                                override fun possibleResultPoints(resultPoints: MutableList<ResultPoint>?) {}
                            })
                            view.resume() // Ensure the camera preview is resumed
                        }
                    }

                } else {
                    Button(onClick = { cameraPermissionLauncher.launch(Manifest.permission.CAMERA) }) {
                        Text("Grant Camera Permission")
                    }
                }
            }
        }

    }


}
