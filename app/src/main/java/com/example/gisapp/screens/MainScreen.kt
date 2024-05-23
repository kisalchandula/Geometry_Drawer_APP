@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.gisapp.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.arcgismaps.geometry.GeometryType
import com.arcgismaps.mapping.ArcGISMap
import com.arcgismaps.mapping.BasemapStyle
import com.arcgismaps.mapping.Viewpoint
import com.arcgismaps.mapping.symbology.SimpleFillSymbol
import com.arcgismaps.mapping.symbology.SimpleFillSymbolStyle
import com.arcgismaps.mapping.symbology.SimpleLineSymbol
import com.arcgismaps.mapping.symbology.SimpleLineSymbolStyle
import com.arcgismaps.mapping.symbology.SimpleMarkerSymbol
import com.arcgismaps.mapping.symbology.SimpleMarkerSymbolStyle
import com.arcgismaps.mapping.view.Graphic
import com.arcgismaps.mapping.view.GraphicsOverlay
import com.arcgismaps.mapping.view.geometryeditor.GeometryEditor
import com.arcgismaps.mapping.view.geometryeditor.VertexTool
import com.arcgismaps.toolkit.geoviewcompose.MapView

// Example symbols for all geometry types
private val pointSymbol: SimpleMarkerSymbol by lazy {
    SimpleMarkerSymbol(
        style = SimpleMarkerSymbolStyle.Circle,
        color = com.arcgismaps.Color.fromRgba(155, 12, 232),
        size = 15f
    )
}

private val lineSymbol: SimpleLineSymbol by lazy {
    SimpleLineSymbol(
        style = SimpleLineSymbolStyle.Dash,
        color = com.arcgismaps.Color.fromRgba(155, 12, 232),
        width = 2f
    )
}

private val fillSymbol: SimpleFillSymbol by lazy {
    SimpleFillSymbol(
        style = SimpleFillSymbolStyle.Cross,
        color = com.arcgismaps.Color.black,
        outline = lineSymbol
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val arcGISMap = remember {
        ArcGISMap(BasemapStyle.ArcGISNavigation).apply {
            initialViewpoint = Viewpoint(
                latitude = 6.927079,
                longitude = 79.861244,
                scale = 72000.0
            )
        }
    }

    var graphicsOverlays by remember { mutableStateOf(emptyList<GraphicsOverlay>()) }
    val geometryEditor = remember { GeometryEditor() }

    // Track the currently selected geometry tool
    var selectedTool by remember { mutableStateOf<GeometryType?>(null) }

    // Current GraphicsOverlay to store sketched graphics
    val currentGraphicsOverlay = remember { GraphicsOverlay() }

    // Add the current graphics overlay to the list
    if (!graphicsOverlays.contains(currentGraphicsOverlay)) {
        graphicsOverlays = graphicsOverlays + currentGraphicsOverlay
    }

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = { Text("Geometry Drawer App") },
            )
        },
        content = { innerPadding ->
            Box(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
            ) {
                MapView(
                    arcGISMap,
                    modifier = Modifier.fillMaxSize(),
                    geometryEditor = geometryEditor,
                    graphicsOverlays = graphicsOverlays,
                )
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp)
                ){
                    // Buttons for selecting geometry tools
                    GeometryToolButtons(
                        selectedTool = selectedTool,
                        onToolSelected = { tool ->
                            selectedTool = tool
                            startGeometryEditor(geometryEditor, tool)
                        },
                        geometryEditor = geometryEditor,
                        currentGraphicsOverlay = currentGraphicsOverlay
                    )
                }

            }
        }
    )
}

@Composable
fun GeometryToolButtons(
    selectedTool: GeometryType?,
    onToolSelected: (GeometryType) -> Unit,
    geometryEditor: GeometryEditor,
    currentGraphicsOverlay: GraphicsOverlay
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ){
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Button(
                onClick = { refreshGeometryEditor(geometryEditor, currentGraphicsOverlay) },
                modifier = Modifier
                    .align(Alignment.TopEnd) // Align the button to the top end of the Box
                    .padding(15.dp), // Add padding to create space around the button
                colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.secondary)
            ) {
                Text(text = "Refresh", color = MaterialTheme.colorScheme.inversePrimary)
            }
        }
        Box(
            modifier = Modifier.padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.padding(vertical = 16.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    GeometryToolButton(
                        isSelected = selectedTool == GeometryType.Multipoint,
                        onClick = { onToolSelected(GeometryType.Multipoint) },
                        text = "Point"
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    GeometryToolButton(
                        isSelected = selectedTool == GeometryType.Polyline,
                        onClick = { onToolSelected(GeometryType.Polyline) },
                        text = "Line"
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    GeometryToolButton(
                        isSelected = selectedTool == GeometryType.Polygon,
                        onClick = { onToolSelected(GeometryType.Polygon) },
                        text = "Polygon"
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                }
                Row(
                    modifier = Modifier.padding(vertical = 16.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ){
                    Button(
                        onClick = { clearGeometryEditor(geometryEditor) },
                        colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.secondary)
                    ) {
                        Text(text = "Clear", color = MaterialTheme.colorScheme.inversePrimary)
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Button(
                        onClick = { addSketchedGraphic(geometryEditor, currentGraphicsOverlay) },
                        colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.secondary)
                    ) {
                        Text(text = "Save", color = MaterialTheme.colorScheme.inversePrimary)
                    }
                }
            }
        }
    }

}

@Composable
fun GeometryToolButton(
    isSelected: Boolean,
    onClick: () -> Unit,
    text: String
) {
    val buttonColor = if (isSelected) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface

    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = buttonColor
        )
    ) {
        Text(text = text, color = MaterialTheme.colorScheme.inverseOnSurface)
    }
}

fun startGeometryEditor(geometryEditor: GeometryEditor, tool: GeometryType) {

    geometryEditor.tool = when (tool) {
        GeometryType.Multipoint -> VertexTool()
        GeometryType.Polyline -> VertexTool()
        GeometryType.Polygon -> VertexTool()
        else -> throw IllegalArgumentException("Unsupported geometry type")
    }
    geometryEditor.start(tool)
}

/**
 * Applies the sketched Geometry to the [currentGraphicsOverlay] using the [geometryEditor]
 */
fun addSketchedGraphic(geometryEditor: GeometryEditor, currentGraphicsOverlay: GraphicsOverlay) {
    val sketchGeometry = geometryEditor.geometry.value

    if (sketchGeometry == null) {
        println("Sketch geometry is null")
        return
    }

    val symbol = when (sketchGeometry) {
        is com.arcgismaps.geometry.Multipoint -> pointSymbol
        is com.arcgismaps.geometry.Polyline -> lineSymbol
        is com.arcgismaps.geometry.Polygon -> fillSymbol
        else -> {
            println("Unsupported geometry type: ${sketchGeometry::class.java.simpleName}")
            null
        }
    }

    if (symbol == null) {
        println("Symbol is null for geometry type: ${sketchGeometry::class.java.simpleName}")
        return
    }

    val graphic = Graphic(sketchGeometry).apply {
        this.symbol = symbol
    }

    currentGraphicsOverlay.graphics.add(graphic)
    geometryEditor.stop()
    println("Graphic added with geometry type: ${sketchGeometry::class.java.simpleName}")
}


/**
 * Clears the current editing session of the [geometryEditor]
 */
fun clearGeometryEditor(geometryEditor: GeometryEditor) {
    geometryEditor.clearGeometry()
    geometryEditor.clearSelection()
    geometryEditor.stop()


}

/**
 * Clears the current editing session of the [geometryEditor]
 */
fun refreshGeometryEditor(geometryEditor: GeometryEditor, currentGraphicsOverlay: GraphicsOverlay) {
    geometryEditor.clearGeometry()
    geometryEditor.clearSelection()
    geometryEditor.stop()

    // Remove all graphics from the current overlay
    currentGraphicsOverlay.graphics.clear()
}

