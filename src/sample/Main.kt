package sample

import com.sun.javafx.event.EventDispatchTreeImpl
import javafx.animation.ParallelTransition
import javafx.animation.RotateTransition
import javafx.animation.TranslateTransition
import javafx.application.Application
import javafx.event.EventDispatchChain
import javafx.scene.*
import javafx.scene.image.Image
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.input.KeyEvent.KEY_PRESSED
import javafx.scene.input.ScrollEvent
import javafx.scene.paint.Color
import javafx.scene.paint.PhongMaterial
import javafx.scene.shape.Box
import javafx.scene.shape.CullFace
import javafx.scene.shape.Sphere
import javafx.scene.transform.Rotate
import javafx.scene.transform.Translate
import javafx.stage.Stage
import javafx.util.Duration
import kotlin.math.PI
import kotlin.math.absoluteValue
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random
import kotlin.system.exitProcess


class Main : Application() {
    override fun start(primaryStage: Stage) {
        TextureLoader.load()
        //Container.floor()
        for (i in 0 until Map.map1.size)
            for (j in 0 until Map.map1[0].size)
                Container.box(j * 200, i * 200, if (Map.get(j, i) == 0) Container.BoxType.Floor else Container.BoxType.Wall)
        Container.unit(1, 1)
        Container.point(5, 5)
        val light = PointLight()
        light.transforms.add(Translate(-3000.0, 3000.0, -2000.0))
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
            group.children.forEach {
                it.eventDispatcher.dispatchEvent(event, EventDispatchTreeImpl())
            }
            val angleProperty = camera.rotationAxis
            val speed = 100.0
            when (event.code) {
                KeyCode.UP -> {
                    camera.transforms.add(
                            Translate(speed * sin(angleProperty.x), -speed * cos(angleProperty.x), speed * cos(angleProperty.x))
                    )
                }
                KeyCode.DOWN -> {
                    camera.transforms.add(
                            Translate(-speed * sin(angleProperty.x), speed * cos(angleProperty.x), -speed * cos(angleProperty.x))
                    )
                }
                KeyCode.LEFT -> {
                    camera.transforms.add(
                            Translate(-speed * cos(angleProperty.x), 0.0)
                    )
                }
                KeyCode.RIGHT -> {
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

        primaryStage.title = "☺"
        primaryStage.scene = scene
        primaryStage.isFullScreen = true
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
            val model = Box(200.0, 200.0, 200 * type.value + 10.0)
            val material = PhongMaterial()
            material.diffuseMap = TextureLoader.get(if (type.value == 1) TEXTURE.Stone else TEXTURE.Floor)
            model.cullFace = CullFace.BACK
            model.material = material
            model.translateXProperty().set(x.toDouble())
            model.translateYProperty().set(y.toDouble())
            model.translateZProperty().set(-100.0 * (type.value))
            model.setOnMouseClicked {

                ParallelTransition(model, RotateTransition(Duration.millis(1500.0)).apply {
                    byAngle = 90.0
                    cycleCount = 1
                    isAutoReverse = true
                }).play()

            }
            data.add(model)
        }


        fun floor() {
            val model = Box(200.0 * 8, 200.0 * 8, 1.0)
            model.translateXProperty().set(200.0 * 3 + 100.0)
            model.translateYProperty().set(200.0 * 3 + 100.0)
            val material = PhongMaterial()
            material.diffuseMap = TextureLoader.get(TEXTURE.Floor)
            model.material = material
            data.add(model)
        }

        fun unit(x: Int, y: Int) {
            val model = javafx.scene.shape.Cylinder(80.0, 30.0)
            val group = Group()
            var angle = PI / 2
            var cx: Double = x.toDouble()
            var cy: Double = y.toDouble()
            group.translateXProperty().set(x * 200.0)
            group.translateYProperty().set(y * 200.0 + 20)
            model.transforms.addAll(
                    Translate(0.0, 0.0, -140.0),
                    Rotate(-22.0, Rotate.X_AXIS)
            )
            model.material = PhongMaterial().apply {
                this.diffuseMap = TextureLoader.get(TEXTURE.Boy)
            }
            group.setOnKeyPressed {
                //println(it.toString())
                when (it.code) {
                    KeyCode.SPACE -> {
                        ParallelTransition(group, TranslateTransition(Duration.millis(100.0)).apply {
                            if (Map.get((cx + cos(angle)).toInt(), (cy + sin(angle)).toInt()) == 0) {
                                this.byY = 200.0 * sin(angle)
                                this.byX = 200.0 * cos(angle)
                                cy += sin(angle) / 2
                                cx += cos(angle) / 2
                                println("$cx $cy")
                            }

                        }).play()
                    }
                    KeyCode.Q -> {
                        ParallelTransition(group, RotateTransition(Duration.millis(10.0)).apply {
                            this.byAngle = -90.0 / 10
                            angle -= PI / 2 / 10
                        }).play()
                    }
                    KeyCode.E -> {
                        ParallelTransition(group, RotateTransition(Duration.millis(10.0)).apply {
                            this.byAngle = 90.0 / 10
                            angle += PI / 2 / 10
                        }).play()
                    }
                }
            }
            group.children.addAll(model,
                    Sphere(90.0).apply {
                        translateZ = -30.0
                        material = PhongMaterial().apply {
                            diffuseMap = TextureLoader.get(TEXTURE.Jacket)
                        }
                    })
            data.add(group)
        }

        fun point(x: Int, y: Int) {
            val model = javafx.scene.shape.Cylinder(110.0, 300.0)
            val group = Group()
            model.transforms.addAll(
                    Translate(x * 200.0, y * 200.0, -140.0),
                    Rotate(90.0, Rotate.X_AXIS)
            )
            model.material = PhongMaterial().apply {
                this.diffuseMap = TextureLoader.get(TEXTURE.Floor)
            }
            group.children.addAll(model)
            data.add(group)
        }

        fun player() {
            val model = Sphere(300.0, 36)

            model.setOnMouseClicked {
                val a = ParallelTransition(model, TranslateTransition(Duration.millis(100.0)).apply {
                    this.byX = 100.0
                })
                a.play()

            }
            data.add(model)
        }
    }
}

class TextureLoader {
    companion object {
        private val images = mutableListOf<Image>()
        fun load() {
            val folder = "assets/images/"
            listOf("stone.jpg", "grass.jpg", "floor.jpg", "boy.jpg", "jaket.jpg").forEach {
                images.add(Image(javaClass.getResourceAsStream(folder + it)))
            }
        }

        fun get(texture: TEXTURE): Image {
            return images[texture.value]
        }
    }

}

enum class TEXTURE(val value: Int) {
    Stone(0),
    Grass(1),
    Floor(2),
    Boy(3),
    Jacket(4)
}