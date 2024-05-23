package com.example.gisapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import com.arcgismaps.ApiKey
import com.arcgismaps.ArcGISEnvironment
import com.example.gisapp.screens.MainScreen
import com.example.gisapp.ui.theme.GISAppTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // authentication with an API key or named user is
        // required to access basemaps and other location services
        ArcGISEnvironment.apiKey = ApiKey.create("AAPK50e045d996d6438c8c833eaac0c7e7e2hDYmJa6rYhBgBSZvdCIBhSre9fxcUDZw1dHevpkxmQbuM28dOqnXTmTtz-pZDgst")

        setContent {
            GISAppTheme {
                MapViewGeometryEditorApp()
            }

        }
    }
}


@Composable
fun MapViewGeometryEditorApp() {
    MainScreen()
}


