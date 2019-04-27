package com.ubertob.purezenta

import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.stage.Screen
import javafx.stage.Stage
import javafx.scene.layout.StackPane
import javafx.scene.text.Text


class Main : Application() {

    override fun start(primaryStage: Stage?) {
        if (primaryStage != null) {
            primaryStage.title = "Purezenta"
            primaryStage.setMaximized(true)
            primaryStage.setFullScreen(true)

            for ( screen in Screen.getScreens() ) {

                println("bounds ${screen.bounds}")
            }

            val image = Image("file:examples/marina.jpg")

            // simple displays ImageView the image as is
            val iv1 = ImageView()
            iv1.setImage(image)


            val root = StackPane()
            root.children.add(iv1)
            with(primaryStage) {
                scene = Scene(root)
                show()
            }
        }
    }

}


fun main(args: Array<String>) {
    Application.launch(Main::class.java, *args)
}