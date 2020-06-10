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

    var pdfPages: List<WritableImage> = emptyList()

    var mode: MarkMode = MarkMode.SpotLight


    override fun start(primaryStage: Stage?) {
        if (primaryStage != null) {
            val pars = getParameters().unnamed

            if (pars.size < 1)
                throw IllegalStateException("You need to pass the pdf file as first parameter!")

            pdfPages = loadPdf(File(pars.first()))

            primaryStage.title = "Purezenta"
            primaryStage.setMaximized(true)
            primaryStage.setFullScreen(true)

            val imageView = createImageView()

            val canvas = Canvas(imageView.fitWidth, imageView.fitHeight)
            val graphicsContext: GraphicsContext = canvas.getGraphicsContext2D()

            addMouseEvents(graphicsContext, canvas)

            val root = StackPane()

            root.children.add(imageView)
            root.children.add(canvas)

            showPdfPage(imageView, currPage)

            with(primaryStage) {
                scene = Scene(root)
                scene.addEventFilter(KeyEvent.KEY_PRESSED, keyboardEvents(imageView, graphicsContext))
                show()
            }
        }
    }

    private fun showPdfPage(imageView: ImageView, page: Int) {
        imageView.setImage(pdfPages[page])
    }

    private fun createImageView(): ImageView {
        val screens = Screen.getScreens()
        screens.forEachIndexed { i, screen ->
            println("bounds for screen number $i  ${screen.bounds}") //screen give coordinate for each monitor but they are sharing same area
        }
        val imageView = ImageView()
        imageView.fitHeight = screens.first().bounds.height
        imageView.fitWidth = screens.first().bounds.width
        return imageView
    }

    enum class MarkMode { DrawingLines, SpotLight, Highlight, Zoom }

    private fun addMouseEvents(graphicsContext: GraphicsContext, canvas: Canvas) {
        val strokeColor = Color.RED
        val strokeWidth = 5.0
        val spotLightBg = Color(0.0, 0.0, 0.0, 0.8)

        canvas.addEventHandler(MouseEvent.MOUSE_PRESSED)
        { event ->
            when (mode) {
                MarkMode.SpotLight -> {
                    graphicsContext.clearRect(0.0, 0.0, canvas.width, canvas.height)
                    graphicsContext.fill = spotLightBg
                    graphicsContext.fillRect(0.0, 0.0, canvas.width, canvas.height)
                    graphicsContext.clearRect(event.getX() - 100, event.getY() - 100, 200.0, 200.0)
                }
                MarkMode.DrawingLines -> {
                    graphicsContext.stroke = strokeColor
                    graphicsContext.lineWidth = strokeWidth
                    graphicsContext.beginPath()
                    graphicsContext.moveTo(event.getX(), event.getY())
                    graphicsContext.stroke()
                }
                else -> event.consume()
            }


        }

        canvas.addEventHandler(MouseEvent.MOUSE_DRAGGED)
        { event ->
            when (mode) {
                MarkMode.SpotLight -> {
                    graphicsContext.clearRect(0.0, 0.0, canvas.width, canvas.height)
                    graphicsContext.fill = spotLightBg
                    graphicsContext.fillRect(0.0, 0.0, canvas.width, canvas.height)
                    graphicsContext.clearRect(event.getX() - 100, event.getY() - 100, 200.0, 200.0)
                }
                MarkMode.DrawingLines -> {
                    graphicsContext.stroke = strokeColor
                    graphicsContext.lineWidth = strokeWidth
                    graphicsContext.lineTo(event.getX(), event.getY())
                    graphicsContext.stroke()
                }
                else -> event.consume()
            }
        }

        canvas.addEventHandler(MouseEvent.MOUSE_RELEASED)
        { event ->
            when (mode) {
                MarkMode.SpotLight -> graphicsContext.clearRect(0.0, 0.0, canvas.width, canvas.height)
                else -> event.consume() //nothing
            }
        }


    }


    private fun keyboardEvents(imageView: ImageView, graphicsContext: GraphicsContext) = EventHandler<KeyEvent> { ke ->

        when (ke.code) {
            KeyCode.RIGHT -> {
                currPage += 1
                showPdfPage(imageView, currPage)
                graphicsContext.clearRect(0.0, 0.0, imageView.fitWidth, imageView.fitHeight)
            }
            KeyCode.LEFT -> {
                currPage -= 1
                showPdfPage(imageView, currPage)
                graphicsContext.clearRect(0.0, 0.0, imageView.fitWidth, imageView.fitHeight)
            }
            KeyCode.L -> mode = MarkMode.DrawingLines
            KeyCode.S -> mode = MarkMode.SpotLight
            KeyCode.Q -> {
                System.exit(0)
            }
            else -> ke.consume()
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


}


fun main(args: Array<String>) {
    Application.launch(Main::class.java, *args)
}
