import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class Order extends Customer {
    private Map<Product, Integer> products = new HashMap<>();
    private int orderId;

    public Order(String name, String phoneNo, String emailId) {
        super(name, phoneNo, emailId);
    }

    public void addProduct(Product product, int quantity) {
        products.put(product, quantity);
    }

    public void saveOrderToDatabase() {
        this.saveToDatabase(); // Save the customer first
        String orderSql = "INSERT INTO Orders (customer_id) VALUES (?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement orderStmt = conn.prepareStatement(orderSql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            
            orderStmt.setInt(1, this.getCustomerId());
            
            int rowsAffected = orderStmt.executeUpdate();
            if (rowsAffected > 0) {
                ResultSet rs = orderStmt.getGeneratedKeys();
                if (rs.next()) {
                    this.orderId = rs.getInt(1);
                }
                
                for (Map.Entry<Product, Integer> entry : products.entrySet()) {
                    Product product = entry.getKey();
                    int quantity = entry.getValue();
                    
                    String orderDetailsSql = "INSERT INTO OrderDetails (order_id, product_id, quantity) VALUES (?, ?, ?)";
                    try (PreparedStatement detailsStmt = conn.prepareStatement(orderDetailsSql)) {
                        detailsStmt.setInt(1, this.orderId);
                        detailsStmt.setInt(2, product.getProductId());
                        detailsStmt.setInt(3, quantity);
                        detailsStmt.executeUpdate();
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error saving order: " + e.getMessage());
        }
    }

    public void displayOrderSummary() {
        System.out.println("\n\n"+String.valueOf('-').repeat(30));
        System.out.println("\nOrder Summary:\n");
        System.out.println(String.valueOf('-').repeat(30));
        double totalOrderPrice = 0.0;

        for (Map.Entry<Product, Integer> entry : products.entrySet()) {
            Product product = entry.getKey();
            int quantity = entry.getValue();
            double totalPrice = product.calculatePrice(quantity);
            totalOrderPrice += totalPrice;

            System.out.println("Product: " + product.getProductName());
            System.out.println("Price per Product: " + product.getPricePerProduct());
            System.out.println("Quantity: " + quantity);
            System.out.println("Total Price for " + product.getProductName() + ": " + totalPrice);
            System.out.println();
        }

        System.out.println("Overall Total Price: " + totalOrderPrice);
    }
}
