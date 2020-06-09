package com.ubertob.purezenta

import javafx.application.Application
import javafx.embed.swing.SwingFXUtils
import javafx.event.EventHandler
import javafx.scene.Scene
import javafx.scene.canvas.Canvas
import javafx.scene.canvas.GraphicsContext
import javafx.scene.image.ImageView
import javafx.scene.image.WritableImage
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.input.MouseEvent
import javafx.scene.layout.StackPane
import javafx.scene.paint.Color
import javafx.stage.Screen
import javafx.stage.Stage
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.rendering.PDFRenderer
import java.io.File


class Main : Application() {

    var currPage = 0

    val pdfPages = loadPdf(File("examples/4rulesfunctional.pdf"))

    //    val mode: MarkMode = MarkMode.DrawingLines
    val mode: MarkMode = MarkMode.SpotLight


    override fun start(primaryStage: Stage?) {
        if (primaryStage != null) {
            primaryStage.title = "Purezenta"
            primaryStage.setMaximized(true)
            primaryStage.setFullScreen(true)
            //it goes fullscreen on one monitor only


            println("Number of screens found ${Screen.getScreens().size}")

            for (screen in Screen.getScreens()) {

                println("bounds ${screen.bounds}") //screen give coordinate for each monitor but they are sharing same area
            }
            val imageView = ImageView()
            imageView.fitHeight = 1080.0
            imageView.fitWidth = 1920.0

            showPdfPage(imageView, currPage)


            val canvas = Canvas(imageView.fitWidth, imageView.fitHeight)
            val graphicsContext: GraphicsContext = canvas.getGraphicsContext2D()

            when (mode) {
                MarkMode.DrawingLines -> doLineDrawing(graphicsContext, canvas)
                MarkMode.SpotLight -> doSpotLight(graphicsContext, canvas)

                else -> TODO()
            }


            val root = StackPane()



            root.children.add(imageView)
            root.children.add(canvas)

            with(primaryStage) {
                scene = Scene(root)
                scene.addEventFilter(KeyEvent.KEY_PRESSED, turnPageEvent(imageView, graphicsContext))
                show()
            }
        }
    }

    enum class MarkMode { DrawingLines, SpotLight, Highlight, Zoom }

    private fun doLineDrawing(graphicsContext: GraphicsContext, canvas: Canvas) {
        graphicsContext.stroke = Color.RED
        graphicsContext.lineWidth = 5.0
        canvas.addEventHandler(
            MouseEvent.MOUSE_PRESSED
        )
        { event ->
            graphicsContext.beginPath()
            graphicsContext.moveTo(event.getX(), event.getY())
            graphicsContext.stroke()
        }

        canvas.addEventHandler(MouseEvent.MOUSE_DRAGGED)
        { event ->
            graphicsContext.lineTo(event.getX(), event.getY())
            graphicsContext.stroke()
        }
    }

    private fun doSpotLight(
        graphicsContext: GraphicsContext,
        canvas: Canvas
    ) {

        val spotLightBg = Color(0.0, 0.0, 0.0, 0.8)

        graphicsContext.fill = spotLightBg
        canvas.addEventHandler(
            MouseEvent.MOUSE_PRESSED
        )
        { event ->

            graphicsContext.clearRect(0.0, 0.0, canvas.width, canvas.height)
            graphicsContext.fill = spotLightBg
            graphicsContext.fillRect(0.0, 0.0, canvas.width, canvas.height)
            graphicsContext.clearRect(event.getX() - 100, event.getY() - 100, 200.0, 200.0)

        }

        canvas.addEventHandler(MouseEvent.MOUSE_DRAGGED)
        { event ->

            graphicsContext.clearRect(0.0, 0.0, canvas.width, canvas.height)
            graphicsContext.fill = spotLightBg
            graphicsContext.fillRect(0.0, 0.0, canvas.width, canvas.height)
            graphicsContext.clearRect(event.getX() - 100, event.getY() - 100, 200.0, 200.0)
        }

        canvas.addEventHandler(MouseEvent.MOUSE_RELEASED)
        { event ->
            graphicsContext.clearRect(0.0, 0.0, canvas.width, canvas.height)
        }
    }


    private fun turnPageEvent(imageView: ImageView, graphicsContext: GraphicsContext) = EventHandler<KeyEvent> { ke ->

        if (ke.getCode() == KeyCode.RIGHT) {
            println("right")
            currPage += 1
            showPdfPage(imageView, currPage)
            graphicsContext.clearRect(0.0, 0.0, imageView.fitWidth, imageView.fitHeight)
        } else if (ke.getCode() == KeyCode.LEFT) {
            println("left")
            currPage -= 1
            showPdfPage(imageView, currPage)
            graphicsContext.clearRect(0.0, 0.0, imageView.fitWidth, imageView.fitHeight)
        }
    }

    private fun loadPdf(file: File): List<WritableImage> {

        println("Opening pdf ${file.canonicalFile}")
        val document = PDDocument.load(file)
        try {
            val pdfRenderer = PDFRenderer(document)

            val pages = 0..document.pages.count - 1

            return pages.map {
                println("Rendering page $it")
                val bim = pdfRenderer.renderImage(it, 4f)
                SwingFXUtils.toFXImage(bim, null)
            }
        } finally {
            document.close()
        }
    }

    private fun showPdfPage(imageView: ImageView, page: Int) {

        imageView.setImage(pdfPages[page])

    }

}


fun main(args: Array<String>) {
    Application.launch(Main::class.java, *args)
}
