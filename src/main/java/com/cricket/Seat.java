package com.cricket;

public class Seat {

    public enum SeatType { GENERAL, PREMIUM, VIP }
    public enum Stand { NORTH, SOUTH, EAST, WEST }

    private String seatId;
    private SeatType type;
    private Stand stand;
    private int row;
    private int number;
    private double price;
    private boolean available;

    public Seat(String seatId, SeatType type, Stand stand, int row, int number, double price) {
        this.seatId = seatId;
        this.type = type;
        this.stand = stand;
        this.row = row;
        this.number = number;
        this.price = price;
        this.available = true;
    }

    public String getSeatId() { return seatId; }
    public SeatType getType() { return type; }
    public Stand getStand() { return stand; }
    public int getRow() { return row; }
    public int getNumber() { return number; }
    public double getPrice() { return price; }
    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }

    public String getAvailabilityStatus() { return available ? "Available" : "Booked"; }

    public String toCSV() {
        return String.join(",", seatId, type.name(), stand.name(),
                String.valueOf(row), String.valueOf(number),
                String.valueOf(price), String.valueOf(available));
    }

    public static Seat fromCSV(String line) {
        String[] parts = line.split(",");
        Seat s = new Seat(parts[0], SeatType.valueOf(parts[1]), Stand.valueOf(parts[2]),
                Integer.parseInt(parts[3]), Integer.parseInt(parts[4]), Double.parseDouble(parts[5]));
        s.setAvailable(Boolean.parseBoolean(parts[6]));
        return s;
    }

    @Override
    public String toString() {
        return seatId + " [" + type + " - " + stand + " Stand, Row " + row + ", Seat " + number + "]";
    }
}
