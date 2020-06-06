package com.ubertob.purezenta

import javafx.application.Application
import javafx.application.Application.launch
import javafx.event.EventHandler
import javafx.scene.Scene
import javafx.scene.canvas.Canvas
import javafx.scene.canvas.GraphicsContext
import javafx.scene.input.MouseEvent
import javafx.scene.layout.StackPane
import javafx.scene.paint.Color
import javafx.stage.Stage


class JavaFX_DrawOnCanvas : Application() {
   override fun start(primaryStage: Stage) {
        val canvas = Canvas(400.0, 400.0)
        val graphicsContext: GraphicsContext = canvas.getGraphicsContext2D()
        initDraw(graphicsContext)
        canvas.addEventHandler(
            MouseEvent.MOUSE_PRESSED,
            { event ->
                graphicsContext.beginPath()
                graphicsContext.moveTo(event.getX(), event.getY())
                graphicsContext.stroke()
            }
        )
        canvas.addEventHandler(MouseEvent.MOUSE_DRAGGED,
            { event ->
                graphicsContext.lineTo(event.getX(), event.getY())
                graphicsContext.stroke()
            }
        )
        canvas.addEventHandler(MouseEvent.MOUSE_RELEASED,
            { event ->
            })
        val root = StackPane()
        root.children.add(canvas)
        val scene = Scene(root, 400.0, 400.0)
        primaryStage.setTitle("java-buddy.blogspot.com")
        primaryStage.setScene(scene)
        primaryStage.show()
    }

    private fun initDraw(gc: GraphicsContext) {
        val canvasWidth = gc.canvas.width
        val canvasHeight = gc.canvas.height
        gc.fill = Color.LIGHTGRAY
        gc.stroke = Color.BLACK
        gc.lineWidth = 5.0
        gc.fill()
        gc.strokeRect(
            0.0, 0.0,  //y of the upper left corner
            canvasWidth,  //width of the rectangle
            canvasHeight
        ) //height of the rectangle
        gc.fill = Color.RED
        gc.stroke = Color.BLUE
        gc.lineWidth = 1.0
    }


}

fun main(args: Array<String>) {
    Application.launch(JavaFX_DrawOnCanvas::class.java, *args)
}