import Services.Flights.FlightMenu;
import Services.Flights.FlightService;
import Services.Hotels.HotelMenu;
import Services.Hotels.HotelService;
import Services.Reservation.ReservationMenu;
import Services.Reservation.ReservationService;
import Services.Utils.ConsoleInput;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        ConsoleInput input = new ConsoleInput(scanner);

        // Inisialisasi Services (ReservationService dipakai bersama oleh penerbangan & hotel)
        ReservationService reservationService = new ReservationService();
        FlightService flightService = new FlightService(reservationService);
        HotelService hotelService = new HotelService(reservationService);

        // Inisialisasi Menus (Views)
        FlightMenu flightMenu = new FlightMenu(flightService, input);
        HotelMenu hotelMenu = new HotelMenu(hotelService, input);
        ReservationMenu reservationMenu = new ReservationMenu(reservationService, input);

        System.out.println("==================================================");
        System.out.println("   SELAMAT DATANG DI MINITRAVELOKA   ");
        System.out.println("==================================================");

        boolean running = true;
        while (running) {
            displayMainMenu();
            int choice = input.readInt("Pilih opsi menu: ");

            switch (choice) {
                case 1 -> flightMenu.displayMenu();
                case 2 -> hotelMenu.displayMenu();
                case 3 -> reservationMenu.handleCancelReservation();
                case 4 -> reservationMenu.handleViewAllReservations();
                case 99 -> {
                    System.out.println("\nTerima kasih telah menggunakan sistem ini. Sampai jumpa!");
                    running = false;
                }
                default -> System.out.println("Pilihan tidak valid. Silakan coba lagi.");
            }
        }
        scanner.close();
    }

    private static void displayMainMenu() {
        System.out.println("\n========== MENU UTAMA ==========");
        System.out.println("1. Pesan Tiket Pesawat");
        System.out.println("2. Pesan Hotel");
        System.out.println("3. Batalkan Reservasi");
        System.out.println("4. Lihat Semua Pemesanan");
        System.out.println("99. Keluar");
        System.out.println("================================");
    }
}
