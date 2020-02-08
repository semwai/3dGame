package sample

import javafx.animation.ParallelTransition
import javafx.animation.RotateTransition
import javafx.animation.TranslateTransition
import javafx.application.Application
import javafx.geometry.BoundingBox
import javafx.scene.*
import javafx.scene.control.Button
import javafx.scene.effect.BlendMode
import javafx.scene.effect.DropShadow
import javafx.scene.image.Image
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.input.KeyEvent.KEY_PRESSED
import javafx.scene.input.MouseEvent
import javafx.scene.input.ScrollEvent
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.scene.paint.PhongMaterial
import javafx.scene.shape.CullFace
import javafx.scene.shape.Sphere
import javafx.scene.transform.Rotate
import javafx.scene.transform.Translate
import javafx.stage.Stage
import javafx.util.Duration
import java.io.IOException
import java.util.*
import kotlin.math.absoluteValue
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random
import kotlin.system.exitProcess


class Main : Application() {
    override fun start(primaryStage: Stage) {

        TextureLoader.load()
//        for (i in 0 until 10)
//            for (j in 0 until 10)
//                Container.box(i * 200, j * 200, if (Map.get(j, i) == 1) Container.BoxType.Wall else Container.BoxType.Floor)
        Container.floor()


        for (i in 0 until 2)
            for (j in 0 until 8)
                Container.unit(j,i)
        val light = PointLight()

        light.transforms.add(Translate(-3000.0, 3000.0, -20000.0))


        val group = Group()
        group.children.addAll(Container.data)

        val camera: Camera = PerspectiveCamera()
        camera.transforms.addAll(
                Rotate(45.0, Rotate.X_AXIS),
                Rotate(22.5, Rotate.Y_AXIS),
                Rotate(22.5, Rotate.Z_AXIS)
        )
        val scene = Scene(group, WIDTH.toDouble(), HEIGHT.toDouble(), true, SceneAntialiasing.BALANCED)


        scene.fill = Color.BLACK
        scene.camera = camera
        primaryStage.addEventHandler(KEY_PRESSED) { event: KeyEvent ->
            val angleProperty = camera.rotationAxis
            val speed = 100.0
            when (event.code) {
                KeyCode.W -> {
                    camera.transforms.add(
                            Translate(speed * sin(angleProperty.x), -speed * cos(angleProperty.x), speed * cos(angleProperty.x))
                    )
                }
                KeyCode.S -> {
                    camera.transforms.add(
                            Translate(-speed * sin(angleProperty.x), speed * cos(angleProperty.x), -speed * cos(angleProperty.x))
                    )
                }
                KeyCode.A -> {
                    camera.transforms.add(
                            Translate(-speed * cos(angleProperty.x), 0.0)
                    )
                }
                KeyCode.D -> {
                    camera.transforms.add(
                            Translate(speed * cos(angleProperty.x), 0.0)
                    )
                }
                KeyCode.C -> {
                    light.color = Color.SKYBLUE
                }
                KeyCode.F11 -> primaryStage.isFullScreen = !primaryStage.isFullScreen
                KeyCode.ESCAPE -> exitProcess(0)
            }
        }
        primaryStage.addEventHandler(ScrollEvent.SCROLL) { event: ScrollEvent ->
            val delta: Double = event.deltaY
            val angleProperty = camera.rotationAxis
            camera.transforms.addAll(
                    Translate(0.0, -delta * sin(angleProperty.x), delta * cos(angleProperty.x))
            )
        }
        primaryStage.title = "Genuine Coder"
        primaryStage.scene = scene
        primaryStage.show()


    }

    companion object {
        private const val WIDTH = 800
        private const val HEIGHT = 600
        @JvmStatic
        fun main(args: Array<String>) {


            launch(Main::class.java)
        }
    }
}

class Container {
    enum class BoxType(val value: Int) {
        Floor(0),
        Wall(1)
    }

    companion object {
        val data = mutableListOf<Node>()
        var r = Random(0)


        fun box(x: Int, y: Int, type: BoxType) {
            val model = javafx.scene.shape.Box(200.0, 200.0, 200 * type.value + 10.0)
            val material = javafx.scene.paint.PhongMaterial()
            material.diffuseMap = if (type.value == 1) TextureLoader.images[TEXTURE.Stone.value] else TextureLoader.images[TEXTURE.Grass.value]
            model.cullFace = CullFace.BACK
            model.material = material
            model.translateXProperty().set(x.toDouble())
            model.translateYProperty().set(y.toDouble())
            model.translateZProperty().set(700.0 - 100 * (type.value))
            model.setOnMouseClicked {

                ParallelTransition(model, RotateTransition(Duration.millis(1500.0)).apply {
                    byAngle = 90.0
                    cycleCount = 1
                    isAutoReverse = true
                }).play()

            }
            data.add(model)
        }


        fun floor(){
            val model = javafx.scene.shape.Box(200.0*8, 200.0*8, 1.0)
            model.translateXProperty().set(200.0*3 + 100.0)
            model.translateYProperty().set(200.0*3 + 100.0)
            val material = PhongMaterial()

            material.diffuseMap = TextureLoader.images[TEXTURE.Floor.value]

            model.material = material

            data.add(model)
        }

        fun unit(x: Int, y: Int){
            val model = javafx.scene.shape.Cylinder(50.0,30.0)
            model.translateXProperty().set(x*200.0)
            model.translateYProperty().set(y*200.0)
            model.transforms.addAll(
                    Rotate(90.0, Rotate.X_AXIS)
            )
            model.material = PhongMaterial().apply { diffuseColor = Color.RED }
            data.add(model)
        }
        fun player(){
            val model = Sphere(300.0, 36)
            val b = BoundingBox(-10000.0,-10000.0,1400.0,1000.0, 1000.0, 1000.0)

            model.setOnMouseClicked {
                val a = ParallelTransition(model, TranslateTransition(Duration.millis(100.0)).apply {
                    this.byZ = 100.0
                })
                if (!model.layoutBounds.intersects(b))
                    a.play()

            }
            data.add(model)
        }
    }
}

class TextureLoader {
    companion object {
        val images = mutableListOf<Image>()

        fun load() {
            val folder = "assets/images/"
            listOf("stone.jpg", "grass.jpg", "floor.jpg").forEach {
                images.add(Image(javaClass.getResourceAsStream(folder + it)))
            }

        }
    }

}

enum class TEXTURE(val value: Int) {
    Stone(0),
    Grass(1),
    Floor(2)
}