package com.KeyCampus;

import com.KeyCampus.dao.RetiradaDao;
import com.KeyCampus.database.DatabaseInitializer;
import com.KeyCampus.service.RetiradaService;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        DatabaseInitializer.init();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/MainLayout.fxml"));
        Scene scene = new Scene(loader.load());
        scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
        stage.setTitle("KeyCampus");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

}