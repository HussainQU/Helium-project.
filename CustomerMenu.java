package Warehouse;
import java.time.LocalDate;
import java.util.Scanner;


public class CustomerMenu {

    public static void run(Scanner sc, WarehouseSystem system) {

        if (system.getCustomers().isEmpty()) {
            System.out.println("No customers. Ask staff to add one.\n");
            return;
        }

        System.out.println("\nAvailable Customer IDs:");
        for (Customer c : system.getCustomers()) {
            System.out.printf("- %s (%s)%n",
                    c.getId(), c.getName());
        }
        System.out.print("\nEnter Customer ID to login: > ");
        String id = sc.nextLine().trim();
        Customer customer = system.findCustomerById(id);
        if (customer == null) {
            System.out.println("Unknown ID.\n");
            return;
        }

        ShoppingCart cart = new ShoppingCart();
        boolean logout = false;

        while (!logout) {
            System.out.printf("%n--- Customer Menu (ID: %s, %s) ---%n",
                    customer.getId(), customer.getName());
            System.out.println("1) List Products (by Category)");
            System.out.println("2) Add to Cart");
            System.out.println("3) Remove from Cart");
            System.out.println("4) View Cart");
            System.out.println("5) Checkout");
            System.out.println("0) Logout\n");
            System.out.print("Choose: > ");

            String c = sc.nextLine().trim();

            switch (c) {
                case "1":
                    ProductListView.printCategorized(system.getProducts());
                    break;
                case "2":
                    addToCart(sc, system, cart);
                    break;
                case "3":
                    removeFromCart(sc, cart);
                    break;
                case "4":
                    cart.print();
                    break;
                case "5":
                    checkout(sc, system, customer, cart);
                    break;
                case "0":
                    logout = true;
                    break;
                default:
                    System.out.println("Invalid\n");
            }
        }
    }

    private static void addToCart(Scanner sc, WarehouseSystem s,
                                  ShoppingCart cart) {
        System.out.print("Enter Product ID: > ");
        String pid = sc.nextLine().trim();
        Product p = s.findProductById(pid);
        if (p == null) {
            System.out.println("Not found");
            return;
        }
        System.out.print("Quantity: > ");
        int q = Integer.parseInt(sc.nextLine().trim());
        if (q <= 0 || q > p.getStock()) {
            System.out.println("Invalid quantity");
            return;
        }
        cart.addItem(p, q);
        System.out.println("Added to cart.");
    }

    private static void removeFromCart(Scanner sc, ShoppingCart cart) {
        cart.print();
        System.out.print("Index to remove: > ");
        String in = sc.nextLine().trim();
        if (in.isEmpty()) return;

        int idx = Integer.parseInt(in);
        cart.removeIndex(idx);
        System.out.println("Removed.");
    }

    private static void checkout(Scanner sc, WarehouseSystem s,
                                 Customer customer, ShoppingCart cart) {

       
        if (cart.isEmpty()) {
            System.out.println("Cart empty\n");
            return;
        }

        System.out.println("\n--- Shipping Address ---");
        System.out.print("Street: > ");
        String street = sc.nextLine().trim();
        System.out.print("City: > ");
        String city = sc.nextLine().trim();
        System.out.print("Country: > ");
        String country = sc.nextLine().trim();
        Address addr = new Address(street, city, country);

        double subtotal = cart.subtotal();
        Discount applicable =
                s.findApplicableDiscount(LocalDate.now());
        double discount = (applicable == null)
                ? 0
                : applicable.calculateDiscount(subtotal);
        double weight = cart.totalWeight();
        double shipping =
                s.getRateTable().shippingFeeFor(weight);
        double total = Math.max(0, subtotal - discount) + shipping;

        System.out.println("Payment method: 1) Card  2) Cash");
        System.out.print("> ");
        String pm = sc.nextLine().trim();
        Payment payment;
        if ("1".equals(pm)) {
            System.out.print("Card Holder Name: > ");
            String holder = sc.nextLine().trim();
            System.out.print("Card Number (masked ok): > ");
            String masked = sc.nextLine().trim();
            payment = new CardPayment(total, holder, masked);
        } else {
            payment = new CashPayment(total);
        }

        java.util.ArrayList<OrderItem> orderItems = cart.toOrderItems();
        
        for (CartItem ci : cart.getItems()) {
            Product p = ci.getProduct();
            p.setStock(p.getStock() - ci.getQuantity());
        }
        String orderId = OrderIdGenerator.nextId();
        Order order = new Order(
                orderId, customer, LocalDate.now(),
                orderItems, subtotal, discount,
                shipping, total, applicable, payment);
        s.addOrder(order);

        Shipment sh = new Shipment(orderId, customer, addr,
                ShipmentStatus.CREATED, weight);
        s.addShipment(sh);

        System.out.println("\n--- Checkout Summary ---");
        System.out.println("--- Cart ---");
        int i = 0;
        for (CartItem ci : cart.getItems()) {
            System.out.printf("%d) %s x%d | QAR %.2f%n",
                    i++, ci.getProduct().getId(),
                    ci.getQuantity(), ci.lineSubtotal());
        }
        System.out.printf("Subtotal: QAR %.2f%n", subtotal);
        if (applicable != null) {
            System.out.printf("Discount (%s): - QAR %.2f%n",
                    applicable.getDetails(), discount);
        } else {
            System.out.println("Discount: QAR 0.00");
        }
        System.out.printf("Shipping (%.2f kg): QAR %.2f%n",
                weight, shipping);
        System.out.printf("TOTAL: QAR %.2f%n", total);
        System.out.println("Payment: " + payment.summary());
        System.out.println("Order ID: " + orderId);
        System.out.println("Shipment: " + sh.basicInfo());

        cart.clear();
    }
}
