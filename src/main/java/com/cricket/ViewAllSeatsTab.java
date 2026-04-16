package com.cricket;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.List;

public class ViewAllSeatsTab {

    private StadiumService service;
    private TableView<SeatRow> table;

    public ViewAllSeatsTab(StadiumService service) {
        this.service = service;
    }

    public Tab createTab() {
        Tab tab = new Tab("View All Seats");
        tab.setClosable(false);

        table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<SeatRow, String> idCol = new TableColumn<>("Seat ID");
        idCol.setCellValueFactory(d -> d.getValue().seatId);

        TableColumn<SeatRow, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(d -> d.getValue().type);

        TableColumn<SeatRow, String> standCol = new TableColumn<>("Stand");
        standCol.setCellValueFactory(d -> d.getValue().stand);

        TableColumn<SeatRow, Integer> rowCol = new TableColumn<>("Row");
        rowCol.setCellValueFactory(d -> d.getValue().row.asObject());

        TableColumn<SeatRow, Integer> numCol = new TableColumn<>("Number");
        numCol.setCellValueFactory(d -> d.getValue().number.asObject());

        TableColumn<SeatRow, Double> priceCol = new TableColumn<>("Price (INR)");
        priceCol.setCellValueFactory(d -> d.getValue().price.asObject());

        TableColumn<SeatRow, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(d -> d.getValue().status);

        table.getColumns().addAll(idCol, typeCol, standCol, rowCol, numCol, priceCol, statusCol);

        Button refreshBtn = new Button("Refresh");
        refreshBtn.setOnAction(e -> loadData());

        Label bookingsTitle = new Label("All Bookings");
        bookingsTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 13px;");

        TableView<BookingRow> bookingTable = new TableView<>();
        bookingTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<BookingRow, String> bIdCol = new TableColumn<>("Booking ID");
        bIdCol.setCellValueFactory(d -> d.getValue().bookingId);

        TableColumn<BookingRow, String> bSeatCol = new TableColumn<>("Seat ID");
        bSeatCol.setCellValueFactory(d -> d.getValue().seatId);

        TableColumn<BookingRow, String> bNameCol = new TableColumn<>("Customer");
        bNameCol.setCellValueFactory(d -> d.getValue().customerName);

        TableColumn<BookingRow, String> bPhoneCol = new TableColumn<>("Phone");
        bPhoneCol.setCellValueFactory(d -> d.getValue().phone);

        TableColumn<BookingRow, String> bMatchCol = new TableColumn<>("Match");
        bMatchCol.setCellValueFactory(d -> d.getValue().match);

        TableColumn<BookingRow, String> bTimeCol = new TableColumn<>("Booked At");
        bTimeCol.setCellValueFactory(d -> d.getValue().time);

        TableColumn<BookingRow, Double> bAmtCol = new TableColumn<>("Amount");
        bAmtCol.setCellValueFactory(d -> d.getValue().amount.asObject());

        bookingTable.getColumns().addAll(bIdCol, bSeatCol, bNameCol, bPhoneCol, bMatchCol, bTimeCol, bAmtCol);

        tab.setOnSelectionChanged(e -> {
            if (tab.isSelected()) {
                loadData(table);
                loadBookingData(bookingTable);
            }
        });

        loadData(table);
        loadBookingData(bookingTable);

        VBox content = new VBox(10);
        content.setPadding(new Insets(10));

        Label seatsTitle = new Label("All Seats");
        seatsTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 13px;");

        content.getChildren().addAll(seatsTitle, refreshBtn, table, bookingsTitle, bookingTable);
        tab.setContent(content);
        return tab;
    }

    private void loadData() {
        if (table != null) loadData(table);
    }

    private void loadData(TableView<SeatRow> t) {
        List<Seat> seatList = service.getAllSeats();
        var rows = FXCollections.<SeatRow>observableArrayList();
        for (Seat s : seatList) {
            rows.add(new SeatRow(s.getSeatId(), s.getType().name(), s.getStand().name(),
                    s.getRow(), s.getNumber(), s.getPrice(), s.getAvailabilityStatus()));
        }
        t.setItems(rows);
    }

    private void loadBookingData(TableView<BookingRow> t) {
        var rows = FXCollections.<BookingRow>observableArrayList();
        for (Booking b : service.getAllBookings()) {
            rows.add(new BookingRow(b.getBookingId(), b.getSeat().getSeatId(),
                    b.getCustomerName(), b.getCustomerPhone(),
                    b.getMatchName(), b.getBookingTimeFormatted(), b.getTotalAmount()));
        }
        t.setItems(rows);
    }

    public static class SeatRow {
        SimpleStringProperty seatId, type, stand, status;
        SimpleIntegerProperty row, number;
        SimpleDoubleProperty price;

        SeatRow(String seatId, String type, String stand, int row, int number, double price, String status) {
            this.seatId = new SimpleStringProperty(seatId);
            this.type = new SimpleStringProperty(type);
            this.stand = new SimpleStringProperty(stand);
            this.row = new SimpleIntegerProperty(row);
            this.number = new SimpleIntegerProperty(number);
            this.price = new SimpleDoubleProperty(price);
            this.status = new SimpleStringProperty(status);
        }
    }

    public static class BookingRow {
        SimpleStringProperty bookingId, seatId, customerName, phone, match, time;
        SimpleDoubleProperty amount;

        BookingRow(String bookingId, String seatId, String customerName, String phone,
                   String match, String time, double amount) {
            this.bookingId = new SimpleStringProperty(bookingId);
            this.seatId = new SimpleStringProperty(seatId);
            this.customerName = new SimpleStringProperty(customerName);
            this.phone = new SimpleStringProperty(phone);
            this.match = new SimpleStringProperty(match);
            this.time = new SimpleStringProperty(time);
            this.amount = new SimpleDoubleProperty(amount);
        }
    }
}
