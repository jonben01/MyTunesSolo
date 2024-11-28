module com.example.spotify_clone {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires com.microsoft.sqlserver.jdbc;


    opens dk.easv.Jonas_MyTunesSolo to javafx.fxml;
    exports dk.easv.Jonas_MyTunesSolo;
    exports dk.easv.Jonas_MyTunesSolo.GUI;
    opens dk.easv.Jonas_MyTunesSolo.GUI to javafx.fxml;
    exports dk.easv.Jonas_MyTunesSolo.GUI.Controller;
    opens dk.easv.Jonas_MyTunesSolo.GUI.Controller to javafx.fxml;
}