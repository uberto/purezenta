package com.ubertob.purezenta

import javafx.application.Application
import javafx.embed.swing.SwingFXUtils
import javafx.event.EventHandler
import javafx.scene.Scene
import javafx.scene.image.ImageView
import javafx.scene.input.MouseEvent
import javafx.scene.layout.Pane
import javafx.scene.layout.StackPane
import javafx.scene.paint.Color
import javafx.scene.shape.Line
import javafx.stage.Screen
import javafx.stage.Stage
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.rendering.PDFRenderer
import java.io.File


class Main : Application() {

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
            val iv1 = ImageView()

//            val image = Image("file:examples/marina.jpg")
//            // simple displays ImageView the image as is
//            iv1.setImage(image)

            attachImage(iv1)

            val root = StackPane()
            root.children.add(iv1)
            with(primaryStage) {
                scene = Scene(root)

//Registering the event filter
                scene.addEventFilter(MouseEvent.MOUSE_CLICKED, drawLineEvent(root));


                show()
            }
        }
    }

    private fun attachImage(imageView: ImageView) {

        val file = File("examples/4rulesfunctional.pdf")

        val document = PDDocument.load(file)
        val pdfRenderer = PDFRenderer(document)

        val bim = pdfRenderer.renderImage(0, 2f)

        val image = SwingFXUtils.toFXImage(bim, null)

        imageView.setImage(image)
        imageView.fitHeight = 1080.0
        imageView.fitWidth = 1920.0


        document.close()
    }

}


fun main(args: Array<String>) {
    Application.launch(Main::class.java, *args)
}

var last = Point(0, 0)
fun drawLineEvent(pane: Pane): EventHandler<MouseEvent> =
    EventHandler { me ->
        println(me)
//        if (me == MOUSE_CLICKED) {
        println("draw line pane ${last.xx}  ${last.yy} ${me.sceneX} ${me.sceneY})")

        val l = Line()
        pane.children.add(l)


        l.startX = last.xx
        l.endX = me.sceneX
        l.startY = last.yy
        l.endY = me.sceneY
        l.stroke = Color.RED
        l.strokeWidth = 10.0

        l.translateX = l.startX
        l.translateY = l.startY

        last = Point(me.x, me.y)
//        }
    }


data class Point(val x: Int, val y: Int) {

    constructor(ax: Double, ay: Double) : this(ax.toInt(), ay.toInt())


    val xx: Double = x.toDouble()
    val yy: Double = y.toDouble()
}