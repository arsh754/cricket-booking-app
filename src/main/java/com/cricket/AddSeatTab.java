package com.cricket;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class AddSeatTab {

    private StadiumService service;

    public AddSeatTab(StadiumService service) {
        this.service = service;
    }

    public Tab createTab() {
        Tab tab = new Tab("Add Seating");
        tab.setClosable(false);

        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(10);
        form.setPadding(new Insets(20));

        TextField seatIdField = new TextField();
        seatIdField.setPromptText("e.g. N-A-01");

        ComboBox<String> typeBox = new ComboBox<>();
        typeBox.getItems().addAll("GENERAL", "PREMIUM", "VIP");
        typeBox.setValue("GENERAL");

        ComboBox<String> standBox = new ComboBox<>();
        standBox.getItems().addAll("NORTH", "SOUTH", "EAST", "WEST");
        standBox.setValue("NORTH");

        TextField rowField = new TextField();
        rowField.setPromptText("Row number");

        TextField numberField = new TextField();
        numberField.setPromptText("Seat number");

        TextField priceField = new TextField();
        priceField.setPromptText("Price in INR");

        form.add(new Label("Seat ID:"), 0, 0);
        form.add(seatIdField, 1, 0);
        form.add(new Label("Type:"), 0, 1);
        form.add(typeBox, 1, 1);
        form.add(new Label("Stand:"), 0, 2);
        form.add(standBox, 1, 2);
        form.add(new Label("Row:"), 0, 3);
        form.add(rowField, 1, 3);
        form.add(new Label("Seat Number:"), 0, 4);
        form.add(numberField, 1, 4);
        form.add(new Label("Price (INR):"), 0, 5);
        form.add(priceField, 1, 5);

        Button addBtn = new Button("Add Seat");
        Button clearBtn = new Button("Clear");

        HBox buttons = new HBox(10, addBtn, clearBtn);
        form.add(buttons, 1, 6);

        addBtn.setOnAction(e -> {
            String id = seatIdField.getText().trim();
            String rowText = rowField.getText().trim();
            String numText = numberField.getText().trim();
            String priceText = priceField.getText().trim();

            if (id.isEmpty() || rowText.isEmpty() || numText.isEmpty() || priceText.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "All fields are required.");
                return;
            }
            if (service.seatIdExists(id)) {
                showAlert(Alert.AlertType.ERROR, "Seat ID already exists.");
                return;
            }

            int row = Integer.parseInt(rowText);
            int num = Integer.parseInt(numText);
            double price = Double.parseDouble(priceText);

            Seat seat = new Seat(id,
                    Seat.SeatType.valueOf(typeBox.getValue()),
                    Seat.Stand.valueOf(standBox.getValue()),
                    row, num, price);
            service.addSeat(seat);
            showAlert(Alert.AlertType.INFORMATION, "Seat added successfully: " + id);

            seatIdField.clear();
            rowField.clear();
            numberField.clear();
            priceField.clear();
        });

        clearBtn.setOnAction(e -> {
            seatIdField.clear();
            rowField.clear();
            numberField.clear();
            priceField.clear();
            typeBox.setValue("GENERAL");
            standBox.setValue("NORTH");
        });

        VBox content = new VBox(form);
        content.setPadding(new Insets(10));
        tab.setContent(content);
        return tab;
    }

    private void showAlert(Alert.AlertType type, String msg) {
        Alert alert = new Alert(type, msg, ButtonType.OK);
        alert.showAndWait();
    }
}
