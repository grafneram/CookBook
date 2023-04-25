module ProjectFolder.assignment12 {
    requires javafx.controls;
    requires javafx.fxml;

    opens ProjectFolder to javafx.fxml;
    exports ProjectFolder;
}