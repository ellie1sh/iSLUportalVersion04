import java.util.List;

/**
 * Portal session class that integrates all components
 */
public class PortalSession {
    private StudentInfo studentInfo;
    private MyDoublyLinkedList<MenuItem> menu;
    private List<PaymentTransaction> transactions;
    private int currentMenuIndex;
    
    public PortalSession(StudentInfo studentInfo, MyDoublyLinkedList<MenuItem> menu, List<PaymentTransaction> transactions) {
        this.studentInfo = studentInfo;
        this.menu = menu;
        this.transactions = transactions;
        this.currentMenuIndex = 0;
    }
    
    public StudentInfo getStudentInfo() { return studentInfo; }
    public MyDoublyLinkedList<MenuItem> getMenu() { return menu; }
    public List<PaymentTransaction> getTransactions() { return transactions; }
    public int getCurrentMenuIndex() { return currentMenuIndex; }
    
    public void setCurrentMenuIndex(int index) {
        if (index >= 0 && index < menu.getSize()) {
            this.currentMenuIndex = index;
        }
    }
    
    public MenuItem getCurrentMenuItem() {
        return menu.get(currentMenuIndex);
    }
    
    public MenuItem navigateNext() {
        currentMenuIndex = (currentMenuIndex + 1) % menu.getSize();
        return getCurrentMenuItem();
    }
    
    public MenuItem navigatePrevious() {
        currentMenuIndex = (currentMenuIndex - 1 + menu.getSize()) % menu.getSize();
        return getCurrentMenuItem();
    }
    
    public String getSessionSummary() {
        StringBuilder summary = new StringBuilder();
        summary.append("Portal Session Summary:\n");
        summary.append("Student: ").append(studentInfo.getFullName()).append("\n");
        summary.append("Student ID: ").append(studentInfo.getId()).append("\n");
        summary.append("Available Menu Items: ").append(menu.getSize()).append("\n");
        summary.append("Payment Transactions: ").append(transactions.size()).append("\n");
        summary.append("Current Menu: ").append(getCurrentMenuItem().getName()).append("\n");
        return summary.toString();
    }
}