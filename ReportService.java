package Warehouse;
// ReportService.java
import java.util.HashMap;
import java.util.Map;

public class ReportService {

    public static void runAllReports(WarehouseSystem s) {

        System.out.println("\n=== Reports Output (Staff) ===");

        // [1] All Discounts
        System.out.println("[1] All Discounts:");
        for (Discount d : s.getDiscounts()) {
            System.out.println(" - " + d.getDetails()
                    + " | Active: " + d.isActive());
        }
        System.out.println();

        // [2] Active Discounts today
        System.out.println("[2] Active Discounts (today "
                + s.today + "):");
        Discount act = s.findApplicableDiscount(s.today);
        if (act != null) {
            System.out.println(" - " + act.getDetails());
        } else {
            System.out.println(" - None");
        }
        System.out.println();

        // [3] Products by Category
        System.out.println("[3] Products by Category:");
        ProductListView.printCategorized(s.getProducts());

        // [4] Low Stock (<= 5)
        System.out.println("[4] Low Stock (<= 5):");
        boolean anyLow = false;
        for (Product p : s.getProducts()) {
            if (p.getStock() <= 5) {
                anyLow = true;
                System.out.printf(" - %s (%s) stock %d%n",
                        p.getName(), p.getId(), p.getStock());
            }
        }
        if (!anyLow) System.out.println(" None.");
        System.out.println();

        // [5] Out of Stock
        System.out.println("[5] Out of Stock:");
        boolean anyOut = false;
        for (Product p : s.getProducts()) {
            if (p.getStock() == 0) {
                anyOut = true;
                System.out.printf(" - %s (%s)%n",
                        p.getName(), p.getId());
            }
        }
        if (!anyOut) System.out.println(" None.");
        System.out.println();

        // [6] Inventory Valuation
        double inv = 0;
        for (Product p : s.getProducts()) {
            inv += p.getPrice() * p.getStock();
        }
        System.out.println("[6] Inventory Valuation (QAR):");
        System.out.printf(" Total: QAR %.2f%n%n", inv);

        // [7] Orders Today
        System.out.println("[7] Orders Today (" + s.today + "):");
        for (Order o : s.getOrders()) {
            if (o.getDate().equals(s.today)) {
                System.out.printf(" - %s | %s   | QAR %.2f%n",
                        o.getId(), o.getCustomer().getName(),
                        o.getTotal());
            }
        }
        System.out.println();

        // [8] Sales by Customer
        System.out.println("[8] Sales by Customer (QAR):");
        Map<String, Double> sales = new HashMap<>();
        for (Order o : s.getOrders()) {
            String name = o.getCustomer().getName();
            sales.put(name,
                    sales.getOrDefault(name, 0.0) + o.getTotal());
        }
        for (String name : sales.keySet()) {
            System.out.printf(" - %s: QAR %.2f%n",
                    name, sales.get(name));
        }
        System.out.println();

        // [9] Shipments by Status
        System.out.println("[9] Shipments by Status:");
        for (Shipment sh : s.getShipments()) {
            System.out.println(" - " + sh.basicInfo());
        }
        System.out.println();

        // [10] Shipments not yet DELIVERED
        System.out.println("[10] Shipments are not yet DELIVERED:");
        for (Shipment sh : s.getShipments()) {
            if (sh.getStatus() != ShipmentStatus.DELIVERED) {
                System.out.println(" - " + sh.basicInfo());
            }
        }
        System.out.println();

        // [11] Simple Top-Selling (by quantity)
        System.out.println("[11] Simple Top-Selling (counts):");
        Map<String, Integer> counts = new HashMap<>();
        for (Order o : s.getOrders()) {
            for (OrderItem item : o.getItems()) {
                String key = item.getProduct().getName()
                        + " (" + item.getProduct().getId() + ")";
                counts.put(key,
                        counts.getOrDefault(key, 0)
                                + item.getQuantity());
            }
        }
        for (String key : counts.keySet()) {
            System.out.printf(" - %s: %d units%n",
                    key, counts.get(key));
        }
        System.out.println();

        // [12] Total Revenue
        System.out.println("[12] Total Revenue (QAR, all time):");
        double rev = 0;
        for (Order o : s.getOrders()) rev += o.getTotal();
        System.out.printf(" Total: QAR %.2f%n%n", rev);

        // [13] Payments Summary
        System.out.println("[13] Payments Summary (from Orders):");
        System.out.printf(" Collected: QAR %.2f%n", rev);
        System.out.println(" (mix of card / cash)\n");

        // [14] Discount Usage
        System.out.println("[14] Discount Usage:");
        Map<String, Integer> useCount = new HashMap<>();
        Map<String, Double> useTotal = new HashMap<>();
        for (Order o : s.getOrders()) {
            Discount d = o.getAppliedDiscount();
            if (d != null) {
                String code = d.getCode();
                useCount.put(code,
                        useCount.getOrDefault(code, 0) + 1);
                useTotal.put(code,
                        useTotal.getOrDefault(code, 0.0)
                                + o.getDiscountAmount());
            }
        }
        for (String code : useCount.keySet()) {
            System.out.printf(
                    " - %s: times %d, total discount QAR %.2f%n",
                    code, useCount.get(code), useTotal.get(code));
        }
        System.out.println();

        // [15] Active discount overlap info (today)
        System.out.println("[15] Active Discount Overlaps (today "
                + s.today + "):");
        Discount activeToday = s.findApplicableDiscount(s.today);
        if (activeToday != null) {
            System.out.println(" - " + activeToday.getDetails());
        } else {
            System.out.println(" - None");
        }

        System.out.println("=== End of Reports ===\n");
    }
}
