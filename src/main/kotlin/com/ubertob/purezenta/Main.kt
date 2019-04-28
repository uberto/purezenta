package com.ubertob.purezenta

import javafx.application.Application
import javafx.embed.swing.SwingFXUtils
import javafx.scene.Scene
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.stage.Screen
import javafx.stage.Stage
import javafx.scene.layout.StackPane
import javafx.scene.text.Text
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.rendering.ImageType
import org.apache.pdfbox.rendering.PDFRenderer
import java.awt.Graphics2D
import java.awt.Toolkit
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO


class Main : Application() {

    override fun start(primaryStage: Stage?) {
        if (primaryStage != null) {
            primaryStage.title = "Purezenta"
            primaryStage.setMaximized(true)
            primaryStage.setFullScreen(true)




            println("Number of screens found ${Screen.getScreens().size}")

            for ( screen in Screen.getScreens() ) {

                println("bounds ${screen.bounds}")
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