package com.librediet.app.ui.screens.foodsearch

import android.content.Context
import androidx.camera.view.PreviewView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import com.librediet.app.util.BarcodeScanState
import com.librediet.app.util.BarcodeScannerService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class BarcodeScannerViewModel @Inject constructor(
    private val barcodeScannerService: BarcodeScannerService
) : ViewModel() {

    val scanState: StateFlow<BarcodeScanState> = barcodeScannerService.state

    fun startScanning(
        context: Context,
        lifecycleOwner: LifecycleOwner,
        previewView: PreviewView
    ) {
        barcodeScannerService.startScanning(context, lifecycleOwner, previewView)
    }

    fun stopScanning() {
        barcodeScannerService.stopScanning()
    }

    fun resetState() {
        barcodeScannerService.reset()
    }

    override fun onCleared() {
        super.onCleared()
        stopScanning()
    }
}
