package com.example.redactor

import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.geometry.Point2D
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.image.ImageView
import javafx.scene.input.ClipboardContent
import javafx.scene.input.TransferMode
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.Pane
import javafx.scene.layout.VBox
import java.util.*

open class DraggableNodeController : AnchorPane() {

    @FXML
    lateinit var deleteNode: Button

    @FXML
    lateinit var outputImageView: ImageView

    @FXML
    lateinit var nodeName: Label

    @FXML
    lateinit var inputVBox: VBox

    @FXML
    private lateinit var mainLayout: AnchorPane

    @FXML
    lateinit var nodeContentVBox: VBox

    @FXML
    lateinit var outputVBox: VBox

    @FXML
    fun initialize() {
        id = UUID.randomUUID().toString()
        nodeContentVBox.onDragDetected = EventHandler { mouseEvent ->
            val offset = Point2D(
                mouseEvent.sceneX - mainLayout.layoutX,
                mouseEvent.sceneY - mainLayout.layoutY
            )
            mainLayout.parent.onDragOver = EventHandler { dragEvent ->
                mainLayout.layoutX = dragEvent.sceneX - offset.x
                mainLayout.layoutY = dragEvent.sceneY - offset.y
                dragEvent.acceptTransferModes(*TransferMode.ANY)
                dragEvent.consume()
            }
            mainLayout.parent.onDragDropped = EventHandler { dragEvent ->

                mainLayout.parent.onDragOver = null
                mainLayout.parent.onDragDropped = null
                dragEvent.isDropCompleted = true
                dragEvent.consume()
            }
            val content = ClipboardContent()
            content.putString("node")
            mainLayout.startDragAndDrop(*TransferMode.ANY).setContent(content)
            mouseEvent.consume()
        }

        nodeContentVBox.onDragDone = EventHandler { dragEvent ->
            mainLayout.parent.onDragOver = null
            mainLayout.parent.onDragDropped = null
            dragEvent.consume()
        }
        deleteNode.onAction = EventHandler { event ->
            inputVBox.children.forEach { n ->
                (n as NodeLinkController).deleteAllNodes()
            }
            outputVBox.children.forEach { n ->
                (n as NodeLinkController).deleteAllNodes()
            }
            (this.parent as Pane).children.remove(this)
        }

    }

    init {
        val fxmlLoader = FXMLLoader(javaClass.getResource("DraggableNode.fxml"))
        fxmlLoader.setRoot(this)
        fxmlLoader.setController(this)
        fxmlLoader.load<DraggableNodeController>()
    }
}
