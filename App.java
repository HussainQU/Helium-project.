package Warehouse;
// App.java
import java.time.LocalDate;
import java.util.Scanner;

public class App {

    public static LocalDate TODAY = LocalDate.now();

    public static void main(String[] args) {

        WarehouseSystem system = new WarehouseSystem();

        DataStore.loadAll(system);

        Scanner sc = new Scanner(System.in);
        boolean running = true;

        while (running) {
            System.out.println("=== Single-Warehouse System (QAR) ===");
            System.out.println("1) Staff Menu");
            System.out.println("2) Customer Menu");
            System.out.println("0) Exit\n");
            System.out.print("Choose: > ");

            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1":
                    StaffMenu.run(sc, system);
                    break;
                case "2":
                    CustomerMenu.run(sc, system);
                    break;
                case "0":
                    running = false;
                    break;
                default:
                    System.out.println("Invalid choice.\n");
            }
        }

        DataStore.saveAll(system);

        System.out.println("Goodbye.");
        sc.close();
    }
}
