package com.example.redactor

import javafx.animation.KeyFrame
import javafx.animation.Timeline
import javafx.application.Application
import javafx.embed.swing.SwingFXUtils.fromFXImage
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.SplitPane
import javafx.scene.image.ImageView
import javafx.scene.layout.AnchorPane
import javafx.stage.FileChooser
import javafx.stage.Stage
import javafx.util.Duration
import javax.imageio.ImageIO


class MainActivity : SplitPane() {

    @FXML
    private lateinit var brightnessButton: Button

    @FXML
    private lateinit var addImageButton: Button

    @FXML
    private lateinit var addTextButton: Button

    @FXML
    private lateinit var blurFilterButton: Button

    @FXML
    private lateinit var floatButton: Button

    @FXML
    private lateinit var globalOutputImageView: ImageView

    @FXML
    private lateinit var grayFilterButton: Button


    @FXML
    private lateinit var intButton: Button

    @FXML
    private lateinit var invertFilterButton: Button

    @FXML
    private lateinit var nodeContainer: AnchorPane

    @FXML
    private lateinit var sepiaFilterButton: Button

    @FXML
    private lateinit var stringButton: Button

    @FXML
    private lateinit var transformMoveButton: Button

    @FXML
    private lateinit var transformRotateButton: Button

    @FXML
    private lateinit var transformScaleButton: Button

    @FXML
    private lateinit var saveImageButton: Button

    @FXML
    private lateinit var saveSchemeButton: Button

    @FXML
    private lateinit var loadSchemeButton: Button

    @FXML
    fun initialize() {
        floatButton.onAction = EventHandler {
            nodeContainer.children.add(FloatClass())
        }
        intButton.onAction = EventHandler {
            nodeContainer.children.add(IntClass())
        }
        stringButton.onAction = EventHandler {
            nodeContainer.children.add(StringClass())
        }

        addTextButton.onAction = EventHandler {
            nodeContainer.children.add(AddText())
        }
        addImageButton.onAction = EventHandler {
            nodeContainer.children.add(AddImage())
        }
        brightnessButton.onAction = EventHandler {
            nodeContainer.children.add(BrightnessClass())
        }
        grayFilterButton.onAction = EventHandler {
            nodeContainer.children.add(GrayFilterClass())
        }
        sepiaFilterButton.onAction = EventHandler {
            nodeContainer.children.add(SepiaFilterClass())
        }
        invertFilterButton.onAction = EventHandler {
            nodeContainer.children.add(InvertFilterClass())
        }
        blurFilterButton.onAction = EventHandler {
            nodeContainer.children.add(BlurFilterClass())
        }
        transformMoveButton.onAction = EventHandler {
            nodeContainer.children.add(TransformMove())
        }
        transformRotateButton.onAction = EventHandler {
            nodeContainer.children.add(TransformRotate())
        }
        transformScaleButton.onAction = EventHandler {
            nodeContainer.children.add(TransformScale())
        }
        saveImageButton.onAction = EventHandler {
            val fileChooser = FileChooser()
            fileChooser.extensionFilters.add(FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg"))
            val file = fileChooser.showSaveDialog(Stage())
            ImageIO.write(fromFXImage(this.globalOutputImageView.image, null), "png", file)
        }


        saveSchemeButton.onAction = EventHandler {
            val fileChooser = FileChooser()
            fileChooser.extensionFilters.add(FileChooser.ExtensionFilter("Text Files", "*.txt"))
            val file = fileChooser.showSaveDialog(Stage())
            val nodes = mutableListOf<DraggableNodeController>()
            for (nnode in this.nodeContainer.children) {
                if (nnode.javaClass.superclass == DraggableNodeController().javaClass) {
                    nodes.add(nnode as DraggableNodeController)
                }
            }
            var text = ""
            text += "NODES\n"
            for (node in nodes) {
                text += "${node.getCallableClassName()} ${node.toSerial()}\n"
            }
            text += "LINKS\n"
            for (node in nodes) {
                for (llink in node.inputVBox.children) {
                    val link = llink as NodeLinkController
                    if (link.linked) {
                        text += "${nodes.indexOf(node)} " +
                                "${nodes.indexOf(link.sourceMainParent?.mainParent)} " +
                                "${node.inputVBox.children.indexOf(link)} " +
                                "${link.sourceMainParent!!.mainParent.outputVBox.children.indexOf(link.sourceMainParent)}\n"
                    }
                }
            }
            file.writeText(text)

        }
        loadSchemeButton.onAction = EventHandler {
            val fileChooser = FileChooser()

            fileChooser.extensionFilters.add(FileChooser.ExtensionFilter("Text Files", "*.txt"))
            val file = fileChooser.showOpenDialog(Stage())
            val lines = file.readText().split("\n")
            this.nodeContainer.children.clear()
            val nodes = mutableListOf<DraggableNodeController>()
            var i = 1
            if (lines[0] == "NODES") {

                while (lines[i] != "LINKS" && i <lines.size && lines[i] != "") {
                    val line = lines[i].split(" ")
                    val newNode = this.getNode(line[0])
                    newNode.fromSerial(line.subList(1, line.size))
                    nodes.add(newNode)
                    this.nodeContainer.children.add(newNode)
                    i += 1
                }
            }
            val timeline = Timeline(KeyFrame(Duration.millis(10.0), { _ ->
                if (lines[i] == "LINKS") {
                    i += 1
                    for (j in i until lines.size) {
                        if (lines[j] == "") break
                        val line = lines[j].split(" ")

                        (nodes[line[1].toInt()].outputVBox.children[line[3].toInt()] as NodeLinkController).mconnection(
                            (nodes[line[0].toInt()].inputVBox.children[line[2].toInt()] as NodeLinkController).circleItem
                        )
                    }
                }
            }))

            timeline.cycleCount = 1
            timeline.play()
        }



        val tmp = EndPointClass()

        tmp.imageV = this.globalOutputImageView
        nodeContainer.children.addAll(tmp, InputImage())
    }

    private fun getNode(node: String): DraggableNodeController {
        if (node == "FloatClass") return FloatClass()
        if (node == "IntClass") return IntClass()
        if (node == "StringClass") return StringClass()
        if (node == "InputImage") return InputImage()
        if (node == "AddText") return AddText()
        if (node == "AddImage") return AddImage()
        if (node == "GrayFilterClass") return GrayFilterClass()
        if (node == "BrightnessClass") return BrightnessClass()
        if (node == "SepiaFilterClass") return SepiaFilterClass()
        if (node == "InvertFilterClass") return InvertFilterClass()
        if (node == "BlurFilterClass") return BlurFilterClass()
        if (node == "EndPointClass") {

            val tmp = EndPointClass()
            tmp.imageV = this.globalOutputImageView
            return tmp
        }
        if (node == "TransformScale") return TransformScale()
        if (node == "TransformMove") return TransformMove()
        if (node == "TransformRotate") return TransformRotate()

        return DraggableNodeController()
    }

    init {
        val tmp = FXMLLoader(javaClass.getResource("main.fxml"))
       tmp.setRoot(this)
        tmp.setController(this)
        tmp.load<Any>()
    }

}


class Node : Application() {
    override fun start(stage: Stage) {
        val mainScene = Scene(MainActivity(), 1280.0, 500.0)
        stage.title = "Node"
        stage.scene = mainScene
        stage.show()
    }
}

fun main() {

    nu.pattern.OpenCV.loadLocally()
    Application.launch(Node::class.java)
}
