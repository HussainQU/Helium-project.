package Warehouse;
import java.time.LocalDate;
import java.util.ArrayList;

public class WarehouseSystem {


    LocalDate today = App.TODAY;

    private ArrayList<Customer> customers = new ArrayList<>();
    private ArrayList<Product> products = new ArrayList<>();
    private ArrayList<Discount> discounts = new ArrayList<>();
    private ArrayList<Order> orders = new ArrayList<>();
    private ArrayList<Shipment> shipments = new ArrayList<>();
    private RateTable rateTable = new RateTable(
            new double[]{2, 5, 10, Double.MAX_VALUE},
            new double[]{10, 20, 35, 50}
    );



    public void addCustomer(Customer c) {
        customers.add(c);
    }

    public ArrayList<Customer> getCustomers() {
        return customers;
    }

    public Customer findCustomerById(String id) {
        for (Customer c : customers) {
            if (c.getId().equals(id))
            	return c;
        }
        return null;
    }

    public void addProduct(Product product) {
        products.add(product);
    }

    public ArrayList<Product> getProducts() {
        return products;
    }

    public Product findProductById(String id) {
        for (Product p : products) {
            if (p.getId().equals(id)) return p;
        }
        return null;
    }

    public void addDiscount(Discount discount) {
        discounts.add(discount);
        if (discount.isActive()) {
            deactivateOverlaps(discount);
        }
    }

    public ArrayList<Discount> getDiscounts() {
        return discounts;
    }

    public void setDiscountActive(Discount target, boolean active) {
        target.setActive(active);
        if (active) {
            deactivateOverlaps(target);
        }
    }

    public void deactivateOverlaps(Discount newcomer) {
        for (Discount d : discounts) {
            if (d != newcomer && d.isActive() &&
                    Discount.overlaps(newcomer, d)) {
                d.setActive(false);
            }
        }
    }

    public Discount findApplicableDiscount(LocalDate date) {
        for (int i = discounts.size() - 1; i >= 0; i--) {
            Discount d = discounts.get(i);
            if (d.isActive() && d.isValidOn(date)) {
                return d;
            }
        }
        return null;
    }

    public void addOrder(Order order) {
        orders.add(order);
    }

    public ArrayList<Order> getOrders() {
        return orders;
    }

    public void addShipment(Shipment shipment) {
        shipments.add(shipment);
    }

    public ArrayList<Shipment> getShipments() {
        return shipments;
    }

    public RateTable getRateTable() {
        return rateTable;
    }
}
