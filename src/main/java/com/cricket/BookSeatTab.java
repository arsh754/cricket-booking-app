package com.cricket;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.List;
import java.util.stream.Collectors;

public class BookSeatTab {

    private StadiumService service;

    public BookSeatTab(StadiumService service) {
        this.service = service;
    }

    public Tab createTab() {
        Tab tab = new Tab("Book Seat");
        tab.setClosable(false);

        // --- Seat selector combobox ---
        ComboBox<Seat> seatCombo = new ComboBox<>();
        seatCombo.setPromptText("Select an available seat...");
        seatCombo.setPrefWidth(300);

        // Custom display in the dropdown: show ID + type + stand + price
        seatCombo.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Seat seat, boolean empty) {
                super.updateItem(seat, empty);
                setText(empty || seat == null ? null :
                        seat.getSeatId() + "  |  " + seat.getType() + "  |  " +
                        seat.getStand() + " Stand  |  INR " + seat.getPrice());
            }
        });
        seatCombo.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Seat seat, boolean empty) {
                super.updateItem(seat, empty);
                setText(empty || seat == null ? "Select an available seat..." : seat.getSeatId() +
                        "  -  " + seat.getType() + ", " + seat.getStand() + " Stand");
            }
        });

        // --- Seat preview panel ---
        VBox previewBox = new VBox(6);
        previewBox.setPadding(new Insets(12));
        previewBox.setStyle("-fx-border-color: #cccccc; -fx-border-radius: 4; -fx-background-color: #f9f9f9; -fx-background-radius: 4;");
        previewBox.setVisible(false);
        previewBox.setManaged(false);

        Label previewTitle = new Label("Seat Details");
        previewTitle.setFont(Font.font("System", FontWeight.BOLD, 13));

        Label lSeatId    = new Label();
        Label lType      = new Label();
        Label lStand     = new Label();
        Label lRow       = new Label();
        Label lNumber    = new Label();
        Label lPrice     = new Label();
        lPrice.setFont(Font.font("System", FontWeight.BOLD, 13));

        previewBox.getChildren().addAll(previewTitle,
                new Separator(),
                lSeatId, lType, lStand, lRow, lNumber, lPrice);

        // Populate combo whenever tab is selected
        tab.setOnSelectionChanged(e -> {
            if (tab.isSelected()) refreshCombo(seatCombo);
        });

        seatCombo.valueProperty().addListener((obs, oldVal, seat) -> {
            if (seat == null) {
                previewBox.setVisible(false);
                previewBox.setManaged(false);
                return;
            }
            lSeatId.setText("Seat ID   :  " + seat.getSeatId());
            lType.setText("Type      :  " + seat.getType());
            lStand.setText("Stand     :  " + seat.getStand());
            lRow.setText("Row       :  " + seat.getRow());
            lNumber.setText("Seat No.  :  " + seat.getNumber());
            lPrice.setText("Price     :  INR " + seat.getPrice());
            previewBox.setVisible(true);
            previewBox.setManaged(true);
        });

        // --- Booking form ---
        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(10);
        form.setPadding(new Insets(10, 0, 10, 0));

        TextField customerNameField = new TextField();
        customerNameField.setPromptText("Full name");

        TextField phoneField = new TextField();
        phoneField.setPromptText("Phone number");

        TextField matchField = new TextField();
        matchField.setPromptText("e.g. IND vs AUS");

        form.add(new Label("Customer Name:"), 0, 0);
        form.add(customerNameField, 1, 0);
        form.add(new Label("Phone:"), 0, 1);
        form.add(phoneField, 1, 1);
        form.add(new Label("Match:"), 0, 2);
        form.add(matchField, 1, 2);

        Button bookBtn = new Button("Confirm Booking");

        bookBtn.setOnAction(e -> {
            Seat selected = seatCombo.getValue();
            String name  = customerNameField.getText().trim();
            String phone = phoneField.getText().trim();
            String match = matchField.getText().trim();

            if (selected == null || name.isEmpty() || phone.isEmpty() || match.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "All fields are required.");
                return;
            }

            Booking booking = service.bookSeat(selected.getSeatId(), name, phone, match);
            if (booking == null) {
                showAlert(Alert.AlertType.ERROR, "Seat could not be booked.");
                return;
            }

            String receipt = "Booking Confirmed!\n\n"
                    + "Booking ID : " + booking.getBookingId() + "\n"
                    + "Seat       : " + booking.getSeat().getSeatId() + "\n"
                    + "Type       : " + booking.getSeat().getType() + "\n"
                    + "Stand      : " + booking.getSeat().getStand() + "\n"
                    + "Customer   : " + booking.getCustomerName() + "\n"
                    + "Phone      : " + booking.getCustomerPhone() + "\n"
                    + "Match      : " + booking.getMatchName() + "\n"
                    + "Date/Time  : " + booking.getBookingTimeFormatted() + "\n"
                    + "Amount     : INR " + booking.getTotalAmount();

            showAlert(Alert.AlertType.INFORMATION, receipt);

            refreshCombo(seatCombo);
            seatCombo.setValue(null);
            customerNameField.clear();
            phoneField.clear();
            matchField.clear();
            previewBox.setVisible(false);
            previewBox.setManaged(false);
        });

        VBox content = new VBox(12);
        content.setPadding(new Insets(20));
        content.getChildren().addAll(
                new Label("Select Seat:"), seatCombo,
                previewBox,
                new Separator(),
                form,
                bookBtn
        );

        tab.setContent(content);
        return tab;
    }

    private void refreshCombo(ComboBox<Seat> combo) {
        List<Seat> available = service.getAllSeats().stream()
                .filter(Seat::isAvailable)
                .collect(Collectors.toList());
        Seat current = combo.getValue();
        combo.setItems(FXCollections.observableArrayList(available));
        if (current != null && available.contains(current)) combo.setValue(current);
    }

    private void showAlert(Alert.AlertType type, String msg) {
        Alert alert = new Alert(type, msg, ButtonType.OK);
        alert.setResizable(true);
        alert.showAndWait();
    }
}
