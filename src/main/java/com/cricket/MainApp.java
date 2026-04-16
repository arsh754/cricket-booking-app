package com.cricket;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.TabPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        StadiumService service = new StadiumService();

        TabPane tabPane = new TabPane();
        tabPane.setTabMinWidth(120);

        tabPane.getTabs().addAll(
                new AddSeatTab(service).createTab(),
                new BookSeatTab(service).createTab(),
                new ViewAllSeatsTab(service).createTab(),
                new SearchFilterTab(service).createTab()
        );

        VBox root = new VBox(tabPane);
        Scene scene = new Scene(root, 900, 600);

        primaryStage.setTitle("Cricket Stadium Seat Booking");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
