import java.util.Scanner;

public class PharmacyManagementSystem {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter customer name: ");
        String name = scanner.nextLine();

        System.out.print("Enter phone number: ");
        String phoneNo = scanner.nextLine();

        System.out.print("Enter email ID: ");
        String emailId = scanner.nextLine();

        Order order = new Order(name, phoneNo, emailId);

        while (true) {
            System.out.print("Enter product name (or 'done' to finish): ");
            String productName = scanner.nextLine();
            if (productName.equalsIgnoreCase("done")) break;

            System.out.print("Enter price per product: ");
            double pricePerProduct = scanner.nextDouble();

            System.out.print("Enter quantity: ");
            int quantity = scanner.nextInt();
            scanner.nextLine();  // Consume newline

            Product product = new Product(productName, pricePerProduct);
            product.saveToDatabase();  // Save product to database to generate productId

            order.addProduct(product, quantity);
        }

        order.saveOrderToDatabase();
        order.displayCustomerInfo();
        order.displayOrderSummary(); // Show the order summary with price details
    }
}
