package Services.Hotels;

import Services.Customer.Customer;
import Services.Reservation.HotelReservation;
import Services.Utils.ConsoleInput;
import Services.Utils.CurrencyFormatter;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

/** Tampilan (view) menu hotel. */
public class HotelMenu {
    private final HotelService hotelService;
    private final ConsoleInput input;

    public HotelMenu(HotelService hotelService, ConsoleInput input) {
        this.hotelService = hotelService;
        this.input = input;
    }

    public void displayMenu() {
        while (true) {
            System.out.println("\n--- Menu Hotel ---");
            System.out.println("21. Cari Hotel");
            System.out.println("22. Lihat Semua Hotel");
            System.out.println("23. Pesan Hotel (langsung dengan ID)");
            System.out.println("0.  Kembali ke Menu Utama");

            int choice = input.readInt("Pilih opsi: ");
            switch (choice) {
                case 21 -> handleSearchHotel();
                case 22 -> hotelService.getAllHotels().forEach(System.out::println);
                case 23 -> handleBookHotel();
                case 0 -> { return; }
                default -> System.out.println("Pilihan tidak valid. Silakan coba lagi.");
            }
        }
    }

    private void handleSearchHotel() {
        System.out.println("\n[ Pencarian Hotel ]");
        String city = input.readNonEmpty("Masukkan kota (lokasi): ");
        LocalDate checkIn = input.readDate("Masukkan tanggal check-in (yyyy-MM-dd): ", LocalDate.now());
        LocalDate checkOut = input.readDate("Masukkan tanggal check-out (yyyy-MM-dd): ", checkIn.plusDays(1));
        int guests = input.readPositiveInt("Masukkan jumlah tamu: ");

        List<Hotel> results = hotelService.searchHotels(city, checkIn, checkOut, guests);
        if (results.isEmpty()) {
            System.out.println("\n[!] Tidak ada hotel tersedia. Coba kota atau tanggal lain.");
            return;
        }

        long nights = ChronoUnit.DAYS.between(checkIn, checkOut);
        System.out.println("\n[ Hasil Pencarian Hotel ]");
        for (Hotel hotel : results) {
            int rooms = hotel.roomsNeeded(guests);
            System.out.print(hotel);
            System.out.println("Estimasi Total    : " + CurrencyFormatter.rupiah(hotel.getPricePerNight() * nights * rooms)
                    + " (" + nights + " malam, " + rooms + " kamar)\n");
        }

        // Pengguna memilih hotel berdasarkan ID dari daftar hasil
        while (true) {
            int id = input.readInt("Masukkan ID hotel untuk dipesan (0 = kembali): ");
            if (id == 0) {
                return;
            }
            if (results.stream().anyMatch(h -> h.getId() == id)) {
                processBooking(id, checkIn, checkOut, guests);
                return;
            }
            System.out.println("[!] ID " + id + " tidak ada dalam hasil pencarian.");
        }
    }

    private void handleBookHotel() {
        System.out.println("\n[ Pemesanan Hotel ]");
        int id = input.readInt("Masukkan ID hotel yang ingin dipesan: ");
        Hotel hotel;
        try {
            hotel = hotelService.getHotelById(id);
        } catch (IllegalArgumentException e) {
            System.out.println("\n[!] " + e.getMessage());
            return;
        }
        System.out.println(hotel);

        LocalDate checkIn = input.readDate("Masukkan tanggal check-in (yyyy-MM-dd): ", LocalDate.now());
        LocalDate checkOut = input.readDate("Masukkan tanggal check-out (yyyy-MM-dd): ", checkIn.plusDays(1));
        int guests = input.readPositiveInt("Masukkan jumlah tamu: ");

        long nights = ChronoUnit.DAYS.between(checkIn, checkOut);
        int rooms = hotel.roomsNeeded(guests);
        System.out.println("Estimasi Total    : " + CurrencyFormatter.rupiah(hotel.getPricePerNight() * nights * rooms)
                + " (" + nights + " malam, " + rooms + " kamar)");

        processBooking(id, checkIn, checkOut, guests);
    }

    private void processBooking(int hotelId, LocalDate checkIn, LocalDate checkOut, int guests) {
        Customer customer = input.readCustomer();
        try {
            HotelReservation reservation = hotelService.bookHotel(hotelId, customer, checkIn, checkOut, guests);
            System.out.println("\n[OK] Pemesanan hotel berhasil!");
            System.out.println(reservation);
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println("\n[!] Pemesanan gagal: " + e.getMessage());
        }
    }
}
