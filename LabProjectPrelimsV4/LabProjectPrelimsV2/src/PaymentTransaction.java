/**
 * Data class to hold payment transaction information
 */
public class PaymentTransaction {
    private String date;
    private String paymentChannel;
    private String reference;
    private double amount;
    private String studentID;
    
    public PaymentTransaction(String date, String paymentChannel, String reference, double amount, String studentID) {
        this.date = date;
        this.paymentChannel = paymentChannel;
        this.reference = reference;
        this.amount = amount;
        this.studentID = studentID;
    }
    
    // Getters
    public String getDate() { return date; }
    public String getPaymentChannel() { return paymentChannel; }
    public String getChannel() { return paymentChannel; } // Backward compatibility
    public String getReference() { return reference; }
    public double getAmount() { return amount; }
    public String getStudentID() { return studentID; }
    
    public Object[] toTableRow() {
        return new Object[]{date, paymentChannel, reference, String.format("₱ %.2f", amount)};
    }
}