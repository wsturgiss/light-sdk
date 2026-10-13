package com.thelightphone.uidemo

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import com.thelightphone.sdk.LightPhotoCapture
import com.thelightphone.sdk.LightScreen
import com.thelightphone.sdk.LightViewModel
import com.thelightphone.sdk.SealedLightActivity
import com.thelightphone.sdk.ui.LightBarButton
import com.thelightphone.sdk.ui.LightCapturedPhoto
import com.thelightphone.sdk.ui.LightIcons
import com.thelightphone.sdk.ui.LightText
import com.thelightphone.sdk.ui.LightTextVariant
import com.thelightphone.sdk.ui.LightTheme
import com.thelightphone.sdk.ui.LightThemeController
import com.thelightphone.sdk.ui.LightThemeTokens
import com.thelightphone.sdk.ui.LightTopBar
import com.thelightphone.sdk.ui.LightTopBarCenter
import com.thelightphone.sdk.ui.gridUnitsAsDp

class UiDemoPhotoCaptureViewModel : LightViewModel<Unit>()

class UiDemoPhotoCaptureScreen(sealedActivity: SealedLightActivity) :
    LightScreen<Unit, UiDemoPhotoCaptureViewModel>(sealedActivity) {

    override val viewModelClass: Class<UiDemoPhotoCaptureViewModel>
        get() = UiDemoPhotoCaptureViewModel::class.java

    override fun createViewModel() = UiDemoPhotoCaptureViewModel()

    @Composable
    override fun Content() {
        val themeColors by LightThemeController.colors.collectAsState()
        var photo by remember { mutableStateOf<LightCapturedPhoto?>(null) }

        LightTheme(colors = themeColors) {
            val captured = photo
            if (captured == null) {
                LightPhotoCapture(
                    onCaptured = { photo = it },
                    onBack = { goBack() },
                )
            } else {
                PhotoResult(photo = captured, onBack = { photo = null })
            }
        }
    }
}

@Composable
private fun PhotoResult(photo: LightCapturedPhoto, onBack: () -> Unit) {
    val bitmap = remember(photo) { BitmapFactory.decodeByteArray(photo.jpeg, 0, photo.jpeg.size) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LightThemeTokens.colors.background),
    ) {
        LightTopBar(
            leftButton = LightBarButton.LightIcon(icon = LightIcons.BACK, onClick = onBack),
            center = LightTopBarCenter.Text("Photo"),
        )
        Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
        )
        LightText(
            text = listOfNotNull(
                "${photo.width} x ${photo.height}, ${photo.jpeg.size / 1024} KB",
                photo.exposureTimeSeconds?.let { "Exposure ${"%.4f".format(it)} s" },
                photo.iso?.let { "ISO $it" },
                photo.aperture?.let { "f/$it" },
            ).joinToString("\n"),
            variant = LightTextVariant.Detail,
            modifier = Modifier.padding(1f.gridUnitsAsDp()),
        )
    }
}
