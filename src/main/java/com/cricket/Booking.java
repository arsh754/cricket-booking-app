package com.cricket;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class Booking {

    private static int counter = 1;
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

    private String bookingId;
    private Seat seat;
    private String customerName;
    private String customerPhone;
    private String matchName;
    private LocalDateTime bookingTime;
    private double totalAmount;

    public Booking(Seat seat, String customerName, String customerPhone, String matchName) {
        this.bookingId = "BKG" + String.format("%04d", counter++);
        this.seat = seat;
        this.customerName = customerName;
        this.customerPhone = customerPhone;
        this.matchName = matchName;
        this.bookingTime = LocalDateTime.now();
        this.totalAmount = seat.getPrice();
    }

    // Private constructor used only by fromCSV
    private Booking() {}

    public String getBookingId() { return bookingId; }
    public Seat getSeat() { return seat; }
    public String getCustomerName() { return customerName; }
    public String getCustomerPhone() { return customerPhone; }
    public String getMatchName() { return matchName; }
    public double getTotalAmount() { return totalAmount; }

    public String getBookingTimeFormatted() {
        return bookingTime.format(FMT);
    }

    public String toCSV() {
        return String.join(",", bookingId, seat.getSeatId(), customerName,
                customerPhone, matchName, getBookingTimeFormatted(), String.valueOf(totalAmount));
    }

    /**
     * Reconstruct a Booking from a CSV line.
     * Format: bookingId, seatId, customerName, customerPhone, matchName, bookingTime, totalAmount
     * Returns null if the referenced seat cannot be found.
     */
    public static Booking fromCSV(String line, List<Seat> seats) {
        // split on comma but only 7 parts (matchName may not contain commas in practice)
        String[] parts = line.split(",", 7);
        if (parts.length < 7) return null;

        String bookingId = parts[0].trim();
        String seatId    = parts[1].trim();
        String custName  = parts[2].trim();
        String custPhone = parts[3].trim();
        String matchName = parts[4].trim();
        String timeStr   = parts[5].trim();
        double amount    = Double.parseDouble(parts[6].trim());

        Seat seat = seats.stream()
                .filter(s -> s.getSeatId().equalsIgnoreCase(seatId))
                .findFirst().orElse(null);
        if (seat == null) return null;

        Booking b = new Booking();
        b.bookingId   = bookingId;
        b.seat        = seat;
        b.customerName  = custName;
        b.customerPhone = custPhone;
        b.matchName   = matchName;
        b.bookingTime = LocalDateTime.parse(timeStr, FMT);
        b.totalAmount = amount;

        // Keep the counter ahead of any loaded booking IDs to avoid duplicates
        try {
            int num = Integer.parseInt(bookingId.replace("BKG", ""));
            if (num >= counter) counter = num + 1;
        } catch (NumberFormatException ignored) {}

        return b;
    }

    @Override
    public String toString() {
        return bookingId + " | " + seat.getSeatId() + " | " + customerName + " | " + matchName;
    }
}
