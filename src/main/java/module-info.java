module test {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.mail;


    opens test to javafx.fxml;
    exports test;
}

