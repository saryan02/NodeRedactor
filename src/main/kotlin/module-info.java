module ru.twistru.imageconstruct {
    requires javafx.swing;
    requires javafx.fxml;
    requires javafx.controls;
    requires kotlin.stdlib;
    requires opencv;

    opens com.example.redactor to javafx.fxml;
    exports com.example.redactor;
}