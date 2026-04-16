package com.cricket;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.List;

public class SearchFilterTab {

    private StadiumService service;

    public SearchFilterTab(StadiumService service) {
        this.service = service;
    }

    public Tab createTab() {
        Tab tab = new Tab("Search & Filter Seats");
        tab.setClosable(false);

        ComboBox<String> typeFilter = new ComboBox<>();
        typeFilter.getItems().addAll("All", "GENERAL", "PREMIUM", "VIP");
        typeFilter.setValue("All");

        ComboBox<String> standFilter = new ComboBox<>();
        standFilter.getItems().addAll("All", "NORTH", "SOUTH", "EAST", "WEST");
        standFilter.setValue("All");

        ComboBox<String> availFilter = new ComboBox<>();
        availFilter.getItems().addAll("All", "Available", "Booked");
        availFilter.setValue("All");

        Button searchBtn = new Button("Search");
        Button resetBtn = new Button("Reset");

        HBox filters = new HBox(10);
        filters.setPadding(new Insets(10));
        filters.getChildren().addAll(
                new Label("Type:"), typeFilter,
                new Label("Stand:"), standFilter,
                new Label("Availability:"), availFilter,
                searchBtn, resetBtn
        );

        TableView<ViewAllSeatsTab.SeatRow> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<ViewAllSeatsTab.SeatRow, String> idCol = new TableColumn<>("Seat ID");
        idCol.setCellValueFactory(d -> d.getValue().seatId);

        TableColumn<ViewAllSeatsTab.SeatRow, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(d -> d.getValue().type);

        TableColumn<ViewAllSeatsTab.SeatRow, String> standCol = new TableColumn<>("Stand");
        standCol.setCellValueFactory(d -> d.getValue().stand);

        TableColumn<ViewAllSeatsTab.SeatRow, Integer> rowCol = new TableColumn<>("Row");
        rowCol.setCellValueFactory(d -> d.getValue().row.asObject());

        TableColumn<ViewAllSeatsTab.SeatRow, Integer> numCol = new TableColumn<>("Number");
        numCol.setCellValueFactory(d -> d.getValue().number.asObject());

        TableColumn<ViewAllSeatsTab.SeatRow, Double> priceCol = new TableColumn<>("Price (INR)");
        priceCol.setCellValueFactory(d -> d.getValue().price.asObject());

        TableColumn<ViewAllSeatsTab.SeatRow, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(d -> d.getValue().status);

        table.getColumns().addAll(idCol, typeCol, standCol, rowCol, numCol, priceCol, statusCol);

        Label resultLabel = new Label("");

        Runnable doSearch = () -> {
            List<Seat> results = service.searchSeats(typeFilter.getValue(), standFilter.getValue(), availFilter.getValue());
            var rows = FXCollections.<ViewAllSeatsTab.SeatRow>observableArrayList();
            for (Seat s : results) {
                rows.add(new ViewAllSeatsTab.SeatRow(s.getSeatId(), s.getType().name(), s.getStand().name(),
                        s.getRow(), s.getNumber(), s.getPrice(), s.getAvailabilityStatus()));
            }
            table.setItems(rows);
            resultLabel.setText("Showing " + results.size() + " seat(s).");
        };

        searchBtn.setOnAction(e -> doSearch.run());

        resetBtn.setOnAction(e -> {
            typeFilter.setValue("All");
            standFilter.setValue("All");
            availFilter.setValue("All");
            doSearch.run();
        });

        tab.setOnSelectionChanged(e -> {
            if (tab.isSelected()) doSearch.run();
        });

        VBox content = new VBox(10, filters, resultLabel, table);
        content.setPadding(new Insets(10));
        tab.setContent(content);
        return tab;
    }
}
