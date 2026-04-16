package com.cricket;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class StadiumService {

    private static final String DATA_DIR = System.getProperty("user.home") + File.separator + "cricket-booking-data";
    private static final String SEATS_FILE = DATA_DIR + File.separator + "seats.csv";
    private static final String BOOKINGS_FILE = DATA_DIR + File.separator + "bookings.csv";

    private List<Seat> seats = new ArrayList<>();
    private List<Booking> bookings = new ArrayList<>();

    public StadiumService() {
        ensureDataDirExists();
        loadSeats();
        loadBookings();
    }

    private void ensureDataDirExists() {
        File dir = new File(DATA_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    // ── Persistence: Seats ──────────────────────────────────────────────────

    private void loadSeats() {
        File file = new File(SEATS_FILE);
        if (!file.exists()) return;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty()) {
                    seats.add(Seat.fromCSV(line));
                }
            }
        } catch (IOException e) {
            System.err.println("Could not load seats: " + e.getMessage());
        }
    }

    private void saveSeats() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(SEATS_FILE))) {
            for (Seat s : seats) {
                pw.println(s.toCSV());
            }
        } catch (IOException e) {
            System.err.println("Could not save seats: " + e.getMessage());
        }
    }

    // ── Persistence: Bookings ───────────────────────────────────────────────

    private void loadBookings() {
        File file = new File(BOOKINGS_FILE);
        if (!file.exists()) return;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty()) {
                    Booking b = Booking.fromCSV(line, seats);
                    if (b != null) bookings.add(b);
                }
            }
        } catch (IOException e) {
            System.err.println("Could not load bookings: " + e.getMessage());
        }
    }

    private void saveBookings() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(BOOKINGS_FILE))) {
            for (Booking b : bookings) {
                pw.println(b.toCSV());
            }
        } catch (IOException e) {
            System.err.println("Could not save bookings: " + e.getMessage());
        }
    }

    // ── Service Methods ─────────────────────────────────────────────────────

    public void addSeat(Seat seat) {
        seats.add(seat);
        saveSeats();
    }

    public boolean seatIdExists(String id) {
        return seats.stream().anyMatch(s -> s.getSeatId().equalsIgnoreCase(id));
    }

    public Booking bookSeat(String seatId, String customerName, String customerPhone, String matchName) {
        Seat seat = seats.stream()
                .filter(s -> s.getSeatId().equalsIgnoreCase(seatId) && s.isAvailable())
                .findFirst().orElse(null);
        if (seat == null) return null;

        seat.setAvailable(false);
        Booking booking = new Booking(seat, customerName, customerPhone, matchName);
        bookings.add(booking);
        saveSeats();    // seat availability changed
        saveBookings(); // new booking added
        return booking;
    }

    public List<Seat> getAllSeats() { return seats; }

    public List<Booking> getAllBookings() { return bookings; }

    public List<Seat> searchSeats(String typeFilter, String standFilter, String availabilityFilter) {
        return seats.stream().filter(s -> {
            boolean matchType = typeFilter.equals("All") || s.getType().name().equals(typeFilter);
            boolean matchStand = standFilter.equals("All") || s.getStand().name().equals(standFilter);
            boolean matchAvail = availabilityFilter.equals("All")
                    || (availabilityFilter.equals("Available") && s.isAvailable())
                    || (availabilityFilter.equals("Booked") && !s.isAvailable());
            return matchType && matchStand && matchAvail;
        }).collect(Collectors.toList());
    }
}
