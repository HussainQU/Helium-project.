package Warehouse;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Scanner;


public class StaffMenu {


    public static void run(Scanner sc, WarehouseSystem system) {

        boolean back = false;

        while (!back) {
            // --- MENU OPTIONS ---
            System.out.println("\n--- Staff Menu ---");
            System.out.println("1) Add Customer");
            System.out.println("2) List / Toggle Discounts");
            System.out.println("3) Create Discount");
            System.out.println("4) Add Product");
            System.out.println("5) Update Shipment Status");
            System.out.println("0) Back");
            System.out.print("Choose: > ");

            String choice = sc.nextLine().trim();

            // --- SELECT ACTION ---
            switch (choice) {
                case "1":
                    addCustomer(sc, system);
                    break;

                case "2":
                    listToggleDiscounts(sc, system);
                    break;

                case "3":
                    createDiscount(sc, system);
                    break;

                case "4":
                    addProduct(sc, system);
                    break;

                case "5":
                    updateShipment(sc, system);
                    break;

                case "0":
                    back = true;
                    break;

                default:
                    System.out.println("Invalid choice.\n");
            }
        }
    }

    private static void addCustomer(Scanner sc, WarehouseSystem sys) {
        System.out.print("\nCustomer ID: > ");
        String id = sc.nextLine().trim();

        System.out.print("Customer Name: > ");
        String name = sc.nextLine().trim();

        sys.addCustomer(new Customer(id, name));

        System.out.printf("Added customer %s (ID: %s).\n\n", name, id);
    }

    private static void listToggleDiscounts(Scanner sc, WarehouseSystem sys) {

        ArrayList<Discount> list = sys.getDiscounts();

        if (list.isEmpty()) {
            System.out.println("\nNo discounts defined yet.\n");
            return;
        }

        // ===== استخدمنا foreach مع عدّاد يدوي =====
        System.out.println();
        int idxPrint = 0;
        for (Discount d : list) {  // enhanced for
            System.out.printf("%d) %s | Active: %b%n",
                    idxPrint, d.getDetails(), d.isActive());
            idxPrint++;
        }

        // Ask for index to toggle
        System.out.print("\nEnter index to toggle (or blank to cancel): > ");
        String in = sc.nextLine().trim();
        if (in.isEmpty()) return;

        int idx;
        try {
            idx = Integer.parseInt(in);
        } catch (NumberFormatException e) {
            System.out.println("Invalid index.\n");
            return;
        }

        if (idx < 0 || idx >= list.size()) {
            System.out.println("Index out of range.\n");
            return;
        }

        Discount d = list.get(idx);

        // Toggle the status
        boolean newActive = !d.isActive();
        sys.setDiscountActive(d, newActive);

        System.out.println("Now Active: " + d.isActive() + "\n");
    }

    private static void createDiscount(Scanner sc, WarehouseSystem sys) {

        System.out.println("\nDiscount type: 1) Fixed Amount  2) Percentage");
        System.out.print("> ");
        String type = sc.nextLine().trim();

        System.out.print("Code/Name: > ");
        String code = sc.nextLine().trim();

        System.out.print("Start date (YYYY-MM-DD): > ");
        LocalDate start = LocalDate.parse(sc.nextLine().trim());

        System.out.print("End date   (YYYY-MM-DD): > ");
        LocalDate end = LocalDate.parse(sc.nextLine().trim());

        System.out.print("Set Active? (y/n): > ");
        boolean active = sc.nextLine().trim().toLowerCase().startsWith("y");

        Discount d;

        if ("1".equals(type)) {
            System.out.print("Fixed amount (QAR): > ");
            double amount = Double.parseDouble(sc.nextLine().trim());
            d = new FixedAmountDiscount(code, start, end, active, amount);
        } else {
            System.out.print("Percent (10 = 10%): > ");
            double percent = Double.parseDouble(sc.nextLine().trim());
            d = new PercentageDiscount(code, start, end, active, percent);
        }

        sys.addDiscount(d);

        System.out.println("Created: " + d.getDetails() +
                " | Active: " + d.isActive() + "\n");
    }

    private static void addProduct(Scanner sc, WarehouseSystem sys) {

        System.out.println("\nCategory: 1) Book  2) Electronic  3) Grocery");
        System.out.print("> ");
        String type = sc.nextLine().trim();

        System.out.print("ID: > ");
        String id = sc.nextLine().trim();

        System.out.print("Name: > ");
        String name = sc.nextLine().trim();

        System.out.print("Price (QAR): > ");
        double price = Double.parseDouble(sc.nextLine().trim());

        System.out.print("Weight (kg): > ");
        double weight = Double.parseDouble(sc.nextLine().trim());

        System.out.print("Stock quantity: > ");
        int stock = Integer.parseInt(sc.nextLine().trim());

        Product p;

        if ("1".equals(type)) {
            p = new BookProduct(id, name, price, weight, stock);
        } else if ("2".equals(type)) {
            p = new ElectronicProduct(id, name, price, weight, stock);
        } else {
            p = new GroceryProduct(id, name, price, weight, stock);
        }

        sys.addProduct(p);

        System.out.printf("Product added: %s (%s)\n\n",
                p.getName(), p.getCategory());
    }

    private static void updateShipment(Scanner sc, WarehouseSystem sys) {

        ArrayList<Shipment> ships = sys.getShipments();

        if (ships.isEmpty()) {
            System.out.println("\nNo shipments found.\n");
            return;
        }

        // ===== استخدمنا foreach مع عدّاد يدوي =====
        System.out.println();
        int idxPrint = 0;
        for (Shipment sh : ships) {   // enhanced for
            System.out.printf("%d) %s%n", idxPrint, sh.basicInfo());
            idxPrint++;
        }

        System.out.print("\nChoose shipment index: > ");
        String input = sc.nextLine().trim();
        if (input.isEmpty()) return;

        int idx;
        try {
            idx = Integer.parseInt(input);
        } catch (Exception e) {
            System.out.println("Invalid index.\n");
            return;
        }

        if (idx < 0 || idx >= ships.size()) {
            System.out.println("Index out of range.\n");
            return;
        }

        Shipment shipment = ships.get(idx);

        System.out.println("\nNew Status:");
        System.out.println("0) CREATED");
        System.out.println("1) PACKED");
        System.out.println("2) IN_TRANSIT");
        System.out.println("3) OUT_FOR_DELIVERY");
        System.out.println("4) DELIVERED");
        System.out.print("> ");

        int sIdx;
        try {
            sIdx = Integer.parseInt(sc.nextLine().trim());
        } catch (Exception e) {
            System.out.println("Invalid status.\n");
            return;
        }

        if (sIdx < 0 || sIdx >= ShipmentStatus.values().length) {
            System.out.println("Status out of range.\n");
            return;
        }

        ShipmentStatus newStatus = ShipmentStatus.values()[sIdx];
        shipment.setStatus(newStatus);

        System.out.println("Updated: " + shipment.basicInfo() + "\n");
    }
}
