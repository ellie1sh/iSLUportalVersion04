import java.util.LinkedList;
import java.util.List;

/**
 * Utility class for the ISLU Student Portal system
 * Provides helper methods for creating menu systems and managing portal data
 * All methods work with pure Java implementations - no HTML/CSS dependencies
 */
public class PortalUtils {
    
    /**
     * Creates a comprehensive menu system using MyDoublyLinkedList and MenuItem
     * @return A doubly linked list containing all menu items
     */
    public static MyDoublyLinkedList<MenuItem> createIntegratedMenuSystem() {
        MyDoublyLinkedList<MenuItem> menu = new MyDoublyLinkedList<>();
        
        // Create sub-items for each menu category
        MySinglyLinkedList<String> homeSubList = createHomeSublist();
        MySinglyLinkedList<String> attendanceSubList = createAttendanceSubList();
        MySinglyLinkedList<String> scheduleSubList = createScheduleSubList();
        MySinglyLinkedList<String> gradesSubList = createGradeSubList();
        MySinglyLinkedList<String> soaSubList = createSOASubList();
        MySinglyLinkedList<String> torSubList = createTORSubList();
        MySinglyLinkedList<String> personalDetailsSubList = createPersonalDetailsSubList();
        MySinglyLinkedList<String> curriculumChecklistSubList = createCurriculumChecklistSubList();
        MySinglyLinkedList<String> medicalSubList = createMedicalSubList();
        MySinglyLinkedList<String> journalSubList = createJournalSubList();
        MySinglyLinkedList<String> downloadableSubList = createDownloadableSubList();

        
        // Add menu items to the doubly linked list in proper order
        menu.add(new MenuItem("🏠 Home", homeSubList));
        menu.add(new MenuItem("📅 Schedule", scheduleSubList));
        menu.add(new MenuItem("📌 Attendance", attendanceSubList));
        menu.add(new MenuItem("🧮 Statement of Accounts", soaSubList));
        menu.add(new MenuItem("📊 Grades", gradesSubList));
        menu.add(new MenuItem("📋 Transcript of Records", torSubList));
        menu.add(new MenuItem("✅ Curriculum Checklist", curriculumChecklistSubList));
        menu.add(new MenuItem("🏥 Medical Record", medicalSubList));
        menu.add(new MenuItem("👤 Personal Details", personalDetailsSubList));
        menu.add(new MenuItem("📚 Journal/Periodical", journalSubList));
        menu.add(new MenuItem("ℹ️ Downloadable/ About iSLU", downloadableSubList));
        
        return menu;
    }
    
    /**
     * Demonstrates data integration by creating a student management system
     * @return A doubly linked list of student information
     */
    public static MyDoublyLinkedList<StudentInfo> createStudentManagementSystem() {
        MyDoublyLinkedList<StudentInfo> students = new MyDoublyLinkedList<>();
        
        // Get all students from DataManager
        List<StudentInfo> allStudents = DataManager.getAllStudents();
        
        // Add students to the doubly linked list
        for (StudentInfo student : allStudents) {
            students.add(student);
        }
        
        return students;
    }
    
    /**
     * Demonstrates menu navigation using the doubly linked list
     * @param menu The menu system
     * @param currentIndex Current menu index
     * @param direction Direction to navigate (1 for next, -1 for previous)
     * @return The menu item at the new position
     */
    public static MenuItem navigateMenu(MyDoublyLinkedList<MenuItem> menu, int currentIndex, int direction) {
        int newIndex = currentIndex + direction;
        
        if (newIndex < 0) {
            newIndex = menu.getSize() - 1; // Wrap to last item
        } else if (newIndex >= menu.getSize()) {
            newIndex = 0; // Wrap to first item
        }
        
        return menu.get(newIndex);
    }
    
    /**
     * Validates student data using integrated systems
     * @param studentID The student ID to validate
     * @param password The password to validate
     * @return true if valid, false otherwise
     */
    public static boolean validateStudentCredentials(String studentID, String password) {
        // Use DataManager for authentication
        boolean isValid = DataManager.authenticateUser(studentID, password);
        
        if (isValid) {
            // Get student info for additional validation
            StudentInfo studentInfo = DataManager.getStudentInfo(studentID);
            if (studentInfo != null) {
                System.out.println("Welcome, " + studentInfo.getFullName() + "!");
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Creates a comprehensive student portal session
     * @param studentID The student ID
     * @return A portal session object containing all integrated data
     */
    public static PortalSession createPortalSession(String studentID) {
        StudentInfo studentInfo = DataManager.getStudentInfo(studentID);
        MyDoublyLinkedList<MenuItem> menu = createIntegratedMenuSystem();
        List<PaymentTransaction> transactions = DataManager.getPaymentTransactions(studentID);
        
        return new PortalSession(studentInfo, menu, transactions);
    }
    
    // =================================================================
    // HELPER METHODS FOR CREATING MENU SUB-LISTS
    // These methods create sub-items for each main menu category
    // =================================================================
    
    /**
     * Creates sub-list for Home menu
     */
    public static MySinglyLinkedList<String> createHomeSublist() {
        MySinglyLinkedList<String> homeSubList = new MySinglyLinkedList<>();
        homeSubList.add("📰 Events, News & Announcements");
        homeSubList.add("📌 Student Status");
        return homeSubList;
    }
    
    /**
     * Creates sub-list for Schedule menu
     */
    public static MySinglyLinkedList<String> createScheduleSubList() {
        MySinglyLinkedList<String> scheduleSubList = new MySinglyLinkedList<>();
        scheduleSubList.add("📅 Class Schedule");
        scheduleSubList.add("🕒 Time Table View");
        return scheduleSubList;
    }
    
    /**
     * Creates sub-list for Attendance menu
     */
    public static MySinglyLinkedList<String> createAttendanceSubList() {
        MySinglyLinkedList<String> attendanceSubList = new MySinglyLinkedList<>();
        attendanceSubList.add("📊 Attendance Summary");
        attendanceSubList.add("📋 Detailed Records");
        return attendanceSubList;
    }
    
    /**
     * Creates sub-list for Statement of Accounts menu
     */
    public static MySinglyLinkedList<String> createSOASubList() {
        MySinglyLinkedList<String> soaSubList = new MySinglyLinkedList<>();
        soaSubList.add("💰 Account Balance");
        soaSubList.add("💳 Payment History");
        soaSubList.add("🏦 Payment Channels");
        return soaSubList;
    }

    /**
     * Creates sub-list for Grades menu
     */
    public static MySinglyLinkedList<String> createGradeSubList() {
        MySinglyLinkedList<String> gradesSublist = new MySinglyLinkedList<>();
        gradesSublist.add("📊 Current Grades");
        gradesSublist.add("📈 Grade Progress");
        return gradesSublist;
    }
    
    /**
     * Creates sub-list for Transcript of Records menu
     */
    public static MySinglyLinkedList<String> createTORSubList() {
        MySinglyLinkedList<String> torSubList = new MySinglyLinkedList<>();
        torSubList.add("📋 Academic Transcript");
        torSubList.add("📄 Download PDF");
        return torSubList;
    }
    
    /**
     * Creates sub-list for Curriculum Checklist menu
     */
    public static MySinglyLinkedList<String> createCurriculumChecklistSubList() {
        MySinglyLinkedList<String> curriculumSubList = new MySinglyLinkedList<>();
        curriculumSubList.add("✅ Course Requirements");
        curriculumSubList.add("📚 Completed Courses");
        curriculumSubList.add("📝 Remaining Requirements");
        return curriculumSubList;
    }

    /**
     * Creates sub-list for Medical Record menu
     */
    public static MySinglyLinkedList<String> createMedicalSubList() {
        MySinglyLinkedList<String> medicalSubList = new MySinglyLinkedList<>();
        medicalSubList.add("🏥 Medical Examination");
        medicalSubList.add("💉 Health Clearance");
        medicalSubList.add("📋 Medical Certificates");
        return medicalSubList;
    }

    /**
     * Creates sub-list for Personal Details menu
     */
    public static MySinglyLinkedList<String> createPersonalDetailsSubList() {
        MySinglyLinkedList<String> personalSubList = new MySinglyLinkedList<>();
        personalSubList.add("👤 Profile Information");
        personalSubList.add("📞 Contact Details");
        personalSubList.add("🏠 Address Information");
        return personalSubList;
    }

    /**
     * Creates sub-list for Journal/Periodical menu
     */
    public static MySinglyLinkedList<String> createJournalSubList() {
        MySinglyLinkedList<String> journalSubList = new MySinglyLinkedList<>();
        journalSubList.add("📚 Academic Journals");
        journalSubList.add("📖 Research Papers");
        journalSubList.add("🔍 Library Resources");
        return journalSubList;
    }

    /**
     * Creates sub-list for Downloadables/About iSLU menu
     */
    public static MySinglyLinkedList<String> createDownloadableSubList() {
        MySinglyLinkedList<String> downloadableSubList = new MySinglyLinkedList<>();
        downloadableSubList.add("📥 Forms & Documents");
        downloadableSubList.add("ℹ️ About iSLU");
        downloadableSubList.add("📜 University History");
        return downloadableSubList;
    }
    
    // =================================================================
    // UTILITY METHODS FOR PORTAL OPERATIONS
    // =================================================================
    
    /**
     * Gets the display name for a menu item (removes emoji for cleaner display)
     */
    public static String getCleanMenuName(String menuName) {
        return menuName.replaceAll("[^\\w\\s/]", "").trim();
    }
    
    /**
     * Validates if a menu item exists in the system
     */
    public static boolean isValidMenuItem(String menuName) {
        MyDoublyLinkedList<MenuItem> menu = createIntegratedMenuSystem();
        for (MenuItem item : menu) {
            if (item.getName().equals(menuName)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Gets the total number of menu items
     */
    public static int getTotalMenuItems() {
        return createIntegratedMenuSystem().getSize();
    }
}

