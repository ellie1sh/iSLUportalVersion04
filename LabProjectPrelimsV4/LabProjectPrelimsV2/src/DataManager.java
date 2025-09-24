import java.io.*;
import java.util.*;
import java.net.URL;
import java.net.URISyntaxException;

/**
 * Centralized data management class for the Student Portal system
 * Handles all file operations and data persistence
 */
public class DataManager {
    
    // File paths
    private static final String DATABASE_FILE = "Database.txt";
    private static final String USER_PASSWORD_FILE = "UserPasswordID.txt";
    private static final String PAYMENT_LOGS_FILE = "paymentLogs.txt";
    private static final String ATTENDANCE_RECORDS_FILE = "attendanceRecords.txt";
    private static final String COURSE_SCHEDULES_FILE = "courseSchedules.txt";
    private static final String GRADE_RECORDS_FILE = "gradeRecords.txt";
    
    /**
     * Resolve a data file by searching from the working directory and then walking up
     * from the compiled classes location. This makes file access robust regardless
     * of where the application is launched from.
     */
    private static File resolveFile(String filename) {
        // 1) Try working directory
        File direct = new File(filename);
        if (direct.exists()) {
            return direct.getAbsoluteFile();
        }

        // 2) Try walking up from the code source (e.g., out/production/...)
        try {
            URL codeSourceUrl = DataManager.class.getProtectionDomain().getCodeSource().getLocation();
            File location = new File(codeSourceUrl.toURI());
            File dir = location.isFile() ? location.getParentFile() : location;

            for (int i = 0; i < 8 && dir != null; i++) {
                File candidate = new File(dir, filename);
                if (candidate.exists()) {
                    return candidate.getAbsoluteFile();
                }
                dir = dir.getParentFile();
            }
        } catch (URISyntaxException ignored) {
        }

        // 3) Fallback to working directory path (even if it does not exist yet)
        return direct.getAbsoluteFile();
    }

    private static File getDatabaseFile() { return resolveFile(DATABASE_FILE); }
    private static File getUserPasswordFile() { return resolveFile(USER_PASSWORD_FILE); }
    private static File getPaymentLogsFile() { return resolveFile(PAYMENT_LOGS_FILE); }
    private static File getAttendanceRecordsFile() { return resolveFile(ATTENDANCE_RECORDS_FILE); }
    private static File getCourseSchedulesFile() { return resolveFile(COURSE_SCHEDULES_FILE); }
    private static File getGradeRecordsFile() { return resolveFile(GRADE_RECORDS_FILE); }

    public static boolean databaseExists() {
        return getDatabaseFile().exists();
    }
    
    /**
     * Authenticates user credentials against the database
     * @param studentID The student ID to authenticate
     * @param password The password to authenticate
     * @return true if credentials are valid, false otherwise
     */
    public static boolean authenticateUser(String studentID, String password) {
        try {
            File databaseFile = getDatabaseFile();
            if (!databaseFile.exists()) {
                return false;
            }

            try (BufferedReader reader = new BufferedReader(new FileReader(databaseFile))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    // Skip empty lines and header lines
                    if (line.trim().isEmpty() || line.startsWith("===") || line.startsWith("Format:")) {
                        continue;
                    }
                    
                    // Handle lines with profile data (containing | separator)
                    String[] mainParts = line.split("\\|");
                    String basicInfo = mainParts[0]; // Everything before the |
                    
                    String[] parts = basicInfo.split(",");
                    if (parts.length >= 6) {
                        String storedID = parts[0].trim();
                        String storedPassword = parts[5].trim();
                        
                        if (studentID.equals(storedID) && password.equals(storedPassword)) {
                            return true;
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading database: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Retrieves student information from the database
     * @param studentID The student ID to look up
     * @return StudentInfo object containing student details, or null if not found
     */
    public static StudentInfo getStudentInfo(String studentID) {
        try {
            File databaseFile = getDatabaseFile();
            if (!databaseFile.exists()) {
                return null;
            }

            try (BufferedReader reader = new BufferedReader(new FileReader(databaseFile))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    // Skip empty lines and header lines
                    if (line.trim().isEmpty() || line.startsWith("===") || line.startsWith("Format:")) {
                        continue;
                    }
                    
                    // Handle lines with profile data (containing | separator)
                    String[] mainParts = line.split("\\|");
                    String basicInfo = mainParts[0]; // Everything before the |
                    
                    String[] parts = basicInfo.split(",");
                    if (parts.length >= 6) {
                        String storedID = parts[0].trim();
                        
                        if (studentID.equals(storedID)) {
                            return new StudentInfo(
                                parts[0].trim(), // ID
                                parts[1].trim(), // Last Name
                                parts[2].trim(), // First Name
                                parts[3].trim(), // Middle Name
                                parts[4].trim(), // Date of Birth
                                parts[5].trim()  // Password
                            );
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading database: " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Saves a new student account to the database
     * @param studentInfo The student information to save
     * @return true if successful, false otherwise
     */
    public static boolean saveStudentAccount(StudentInfo studentInfo) {
        try {
            // Save to Database.txt
            File dbFile = getDatabaseFile();
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(dbFile, true))) {
                String dbEntry = studentInfo.toDatabaseFormat();
                writer.write(dbEntry);
                writer.newLine();
                writer.flush(); // Ensure data is written immediately
            }
            
            // Save to UserPasswordID.txt
            File credsFile = getUserPasswordFile();
            try (BufferedWriter logWriter = new BufferedWriter(new FileWriter(credsFile, true))) {
                String credsEntry = "ID: " + studentInfo.getId() + " | Password: " + studentInfo.getPassword();
                logWriter.write(credsEntry);
                logWriter.newLine();
                logWriter.flush(); // Ensure data is written immediately
            }
            
            return true;
        } catch (IOException e) {
            System.err.println("Error saving student account: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Generates a unique student ID
     * @return A unique 7-digit ID starting with "225"
     */
    public static String generateUniqueStudentID() {
        Set<String> usedIDs = new HashSet<>();
        
        try {
            File file = getDatabaseFile();
            if (file.exists()) {
                try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        String[] parts = line.split(",");
                        if (parts.length > 0) {
                            usedIDs.add(parts[0]);
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading existing IDs: " + e.getMessage());
        }
        
        Random rand = new Random();
        String newID;
        do {
            int lastFour = rand.nextInt(10000);
            newID = "225" + String.format("%04d", lastFour);
        } while (usedIDs.contains(newID));
        
        return newID;
    }
    
    /**
     * Logs a payment transaction
     * @param channelName The payment channel used
     * @param amount The amount paid
     * @param studentID The student ID making the payment
     */
    public static void logPaymentTransaction(String channelName, double amount, String studentID) {
        try {
            File logFile = getPaymentLogsFile();
            
            java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("MM/dd/yyyy hh:mm a");
            String currentDateTime = dateFormat.format(new java.util.Date());
            
            String reference = "FIRST SEMESTER 2025-2026 Enrollme.";
            String formattedAmount = String.format("P %,.2f", amount);
            
            String logEntry = currentDateTime + "," + channelName + "," + reference + "," + formattedAmount + "," + studentID;
            
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(logFile, true))) {
                writer.write(logEntry);
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error writing to payment log: " + e.getMessage());
        }
    }
    
    /**
     * Loads payment transactions for a specific student
     * @param studentID The student ID to load transactions for
     * @return List of payment transactions
     */
    public static List<PaymentTransaction> loadPaymentTransactions(String studentID) {
        List<PaymentTransaction> transactions = new ArrayList<>();
        
        try {
            File logFile = getPaymentLogsFile();
            if (logFile.exists()) {
                try (BufferedReader reader = new BufferedReader(new FileReader(logFile))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        // Skip empty lines and header lines
                        if (line.trim().isEmpty() || line.startsWith("===") || line.startsWith("Format:")) {
                            continue;
                        }
                        String[] parts = line.split(",");
                        if (parts.length >= 5) {
                            String transactionStudentID = parts[4].trim();
                            if (studentID.equals(transactionStudentID)) {
                                // Parse amount (remove P and commas)
                                String amountStr = parts[3].trim().replaceAll("[P, ]", "");
                                double amount = Double.parseDouble(amountStr);
                                
                                transactions.add(new PaymentTransaction(
                                    parts[0].trim(), // Date
                                    parts[1].trim(), // paymentChannel
                                    parts[2].trim(), // Reference
                                    amount,          // Amount
                                    parts[4].trim()  // studentID
                                ));
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading payment logs: " + e.getMessage());
        }
        
        return transactions;
    }
    
    /**
     * Gets all students from the database
     * @return List of all student information
     */
    public static List<StudentInfo> getAllStudents() {
        List<StudentInfo> students = new ArrayList<>();
        
        try {
            File databaseFile = getDatabaseFile();
            if (!databaseFile.exists()) {
                return students;
            }

            try (BufferedReader reader = new BufferedReader(new FileReader(databaseFile))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    // Skip empty lines and header lines
                    if (line.trim().isEmpty() || line.startsWith("===") || line.startsWith("Format:")) {
                        continue;
                    }
                    
                    // Handle lines with profile data (containing | separator)
                    String[] mainParts = line.split("\\|");
                    String basicInfo = mainParts[0]; // Everything before the |
                    
                    String[] parts = basicInfo.split(",");
                    if (parts.length >= 6) {
                        students.add(new StudentInfo(
                            parts[0].trim(), // ID
                            parts[1].trim(), // Last Name
                            parts[2].trim(), // First Name
                            parts[3].trim(), // Middle Name
                            parts[4].trim(), // Date of Birth
                            parts[5].trim()  // Password
                        ));
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading all students: " + e.getMessage());
        }
        
        return students;
    }
    
    /**
     * Gets student profile information from Database.txt
     * @param studentID The student ID to get profile for
     * @return Profile data as a formatted string
     */
    public static String getStudentProfile(String studentID) {
        try {
            File dbFile = getDatabaseFile();
            if (dbFile.exists()) {
                try (BufferedReader reader = new BufferedReader(new FileReader(dbFile))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        if (line.trim().isEmpty()) continue;
                        
                        System.out.println("DEBUG: Checking line: " + line);
                        
                        // Handle lines with profile data (containing | separator)
                        String[] mainParts = line.split("\\|");
                        String basicInfo = mainParts[0]; // Everything before the |
                        
                        String[] parts = basicInfo.split(",");
                        if (parts.length > 0 && parts[0].equals(studentID)) {
                            System.out.println("DEBUG: Found matching student ID: " + studentID);
                            // Check if profile data exists (after the | separator)
                            if (line.contains("|") && mainParts.length > 1) {
                                System.out.println("DEBUG: Returning profile data: " + mainParts[1]);
                                return mainParts[1]; // Return the profile data part
                            } else {
                                System.out.println("DEBUG: No profile data found for student " + studentID);
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading student profile: " + e.getMessage());
        }
        System.out.println("DEBUG: No profile data found for student " + studentID);
        return null;
    }
    
    /**
     * Updates a student's profile information in Database.txt
     * @param studentID The student ID to update
     * @param profileData The profile data to save
     * @return true if successful, false otherwise
     */
    public static boolean updateStudentProfile(String studentID, String profileData) {
        try {
            // Update Database.txt with profile information
            File dbFile = getDatabaseFile();
            if (dbFile.exists()) {
                java.util.List<String> lines = new java.util.ArrayList<>();
                try (BufferedReader reader = new BufferedReader(new FileReader(dbFile))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        if (line.trim().isEmpty()) continue;
                        String[] parts = line.split(",");
                        if (parts.length > 0 && parts[0].equals(studentID)) {
                            // Append profile data to the existing line
                            line = line + "|" + profileData;
                        }
                        lines.add(line);
                    }
                }
                
                // Write back to file
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(dbFile))) {
                    for (String line : lines) {
                        writer.write(line);
                        writer.newLine();
                    }
                }
            }
            
            return true;
        } catch (IOException e) {
            System.err.println("Error updating profile: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Updates a student's password in both Database.txt and UserPasswordID.txt
     * @param studentID The student ID to update
     * @param newPassword The new password
     * @return true if successful, false otherwise
     */
    public static boolean updateStudentPassword(String studentID, String newPassword) {
        try {
            // Update Database.txt
            File dbFile = getDatabaseFile();
            if (dbFile.exists()) {
                java.util.List<String> lines = new java.util.ArrayList<>();
                try (BufferedReader reader = new BufferedReader(new FileReader(dbFile))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        if (line.trim().isEmpty()) continue;
                        String[] parts = line.split(",");
                        if (parts.length > 0 && parts[0].equals(studentID)) {
                            // Update the password (last field)
                            parts[parts.length - 1] = newPassword;
                            line = String.join(",", parts);
                        }
                        lines.add(line);
                    }
                }
                
                // Write back to file
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(dbFile))) {
                    for (String line : lines) {
                        writer.write(line);
                        writer.newLine();
                    }
                }
            }
            
            // Update UserPasswordID.txt
            File credsFile = getUserPasswordFile();
            if (credsFile.exists()) {
                java.util.List<String> lines = new java.util.ArrayList<>();
                try (BufferedReader reader = new BufferedReader(new FileReader(credsFile))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        if (line.trim().isEmpty()) continue;
                        if (line.contains("ID: " + studentID)) {
                            line = "ID: " + studentID + " | Password: " + newPassword;
                        }
                        lines.add(line);
                    }
                }
                
                // Write back to file
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(credsFile))) {
                    for (String line : lines) {
                        writer.write(line);
                        writer.newLine();
                    }
                }
            }
            
            return true;
        } catch (IOException e) {
            System.err.println("Error updating password: " + e.getMessage());
            return false;
        }
    }

    /**
     * Loads attendance records for a specific student
     * @param studentID The student ID to load attendance for
     * @return List of attendance records
     */

    public static List<AttendanceRecord> loadAttendanceRecords(String studentID) {
        List<AttendanceRecord> records = new ArrayList<>();
        
        try {
            File attendanceFile = getAttendanceRecordsFile();
            if (attendanceFile.exists()) {
                try (BufferedReader reader = new BufferedReader(new FileReader(attendanceFile))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        // Skip empty lines and header lines
                        if (line.trim().isEmpty() || line.startsWith("===") || line.startsWith("Format:")) {
                            continue;
                        }
                        
                        AttendanceRecord record = AttendanceRecord.fromCsvFormat(line);
                        if (record != null && studentID.equals(record.getStudentID())) {
                            records.add(record);
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error reading attendance records: " + e.getMessage());
        }
        
        return records;
    }


    
    /**
     * Calculates attendance summary for a student
     * @param studentID The student ID
     * @return Map with subject as key and attendance summary as value
     */

    public static Map<String, AttendanceSummary> getAttendanceSummary(String studentID) {
        List<AttendanceRecord> records = loadAttendanceRecords(studentID);
        Map<String, AttendanceSummary> summaryMap = new HashMap<>();
        
        for (AttendanceRecord record : records) {
            String subject = record.getSubjectName();
            AttendanceSummary summary = summaryMap.getOrDefault(subject, new AttendanceSummary(subject));
            
            switch (record.getStatus()) {
                case "Present":
                    summary.incrementPresent();
                    break;
                case "Absent":
                    summary.incrementAbsent();
                    break;
                case "Late":
                    summary.incrementLate();
                    break;
            }
            
            summaryMap.put(subject, summary);
        }
        
        return summaryMap;
    }



    /**
     * Faculty function to update attendance (PLACEHOLDER - waiting for faculty portal integration)
     * This will be called by the faculty portal in real-time during class to mark students as:
     * - Present, Absent, or Late
     * 
     * @param studentID The student ID
     * @param subjectCode The subject code
     * @param subjectName The subject name
     * @param date The date
     * @param status The attendance status (Present/Absent/Late)
     * @param remarks Optional remarks from faculty
     * @return true if successful, false otherwise
     */
    public static boolean updateAttendanceRecord(String studentID, String subjectCode, 
            String subjectName, java.time.LocalDate date, String status, String remarks) {
        // TODO: This function will be implemented when faculty portal code is ready
        // Faculty will mark attendance in real-time during class
        // This will immediately update the student's attendance view
        System.out.println("PLACEHOLDER: Faculty attendance marking system");
        System.out.println("Will integrate with faculty portal for real-time attendance updates");
        System.out.println("Parameters: " + studentID + ", " + subjectCode + ", " + 
                          subjectName + ", " + date + ", " + status + ", " + remarks);
        return false;
    }
    
    /**
     * Student function to submit reason for absence/tardiness
     * This allows students to provide explanations for their absences or tardiness
     * 
     * @param studentID The student ID
     * @param subjectCode The subject code
     * @param date The date of absence/tardiness
     * @param reason The student's reason/explanation
     * @return true if successful, false otherwise
     */

    public static boolean submitAttendanceReason(String studentID, String subjectCode, 
            java.time.LocalDate date, String reason) {
        try {
            // Update the attendance record with student's reason
            File attendanceFile = getAttendanceRecordsFile();
            if (attendanceFile.exists()) {
                java.util.List<String> lines = new java.util.ArrayList<>();
                try (BufferedReader reader = new BufferedReader(new FileReader(attendanceFile))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        if (line.trim().isEmpty() || line.startsWith("===") || line.startsWith("Format:")) {
                            lines.add(line);
                            continue;
                        }
                        
                        AttendanceRecord record = AttendanceRecord.fromCsvFormat(line);
                        if (record != null && 
                            studentID.equals(record.getStudentID()) && 
                            subjectCode.equals(record.getSubjectCode()) && 
                            date.equals(record.getDate())) {
                            // Update the record with student's reason
                            record.setRemarks(reason);
                            lines.add(record.toCsvFormat());
                        } else {
                            lines.add(line);
                        }
                    }
                }
                
                // Write back to file
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(attendanceFile))) {
                    for (String line : lines) {
                        writer.write(line);
                        writer.newLine();
                    }
                }
                return true;
            }
        } catch (Exception e) {
            System.err.println("Error updating attendance reason: " + e.getMessage());
        }
        return false;
    }


    
    /**
     * Loads course schedules for a specific student
     * @param studentID The student ID to load schedules for
     * @return List of course schedules
     */

    public static List<CourseSchedule> loadCourseSchedules(String studentID) {
        List<CourseSchedule> schedules = new ArrayList<>();
        
        try {
            File scheduleFile = getCourseSchedulesFile();
            if (scheduleFile.exists()) {
                try (BufferedReader reader = new BufferedReader(new FileReader(scheduleFile))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        // Skip empty lines and header lines
                        if (line.trim().isEmpty() || line.startsWith("===") || line.startsWith("Format:")) {
                            continue;
                        }
                        
                        CourseSchedule schedule = CourseSchedule.fromCsvFormat(line);
                        if (schedule != null && studentID.equals(schedule.getStudentID())) {
                            schedules.add(schedule);
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error reading course schedules: " + e.getMessage());
        }
        
        return schedules;
    }


    
    /**
     * Gets the current semester for a student
     * @param studentID The student ID
     * @return Current semester string
     */

    public static String getCurrentSemester(String studentID) {
        List<CourseSchedule> schedules = loadCourseSchedules(studentID);
        if (!schedules.isEmpty()) {
            return schedules.get(0).getSemester();
        }
        return "FIRST SEMESTER 2025-2026"; // Default
    }


    
    /**
     * Loads grade records for a specific student
     * @param studentID The student ID to load grades for
     * @return List of grade records
     */

    public static List<GradeRecord> loadGradeRecords(String studentID) {
        List<GradeRecord> records = new ArrayList<>();
        
        try {
            File gradeFile = getGradeRecordsFile();
            if (gradeFile.exists()) {
                try (BufferedReader reader = new BufferedReader(new FileReader(gradeFile))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        // Skip empty lines and header lines
                        if (line.trim().isEmpty() || line.startsWith("===") || line.startsWith("Format:")) {
                            continue;
                        }
                        
                        GradeRecord record = GradeRecord.fromCsvFormat(line);
                        if (record != null && studentID.equals(record.getStudentID())) {
                            records.add(record);
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error reading grade records: " + e.getMessage());
        }
        
        return records;
    }
    
    /**
     * Gets current semester grade records for a student
     * @param studentID The student ID
     * @return List of current semester grade records
     */

    public static List<GradeRecord> getCurrentSemesterGrades(String studentID) {
        List<GradeRecord> allGrades = loadGradeRecords(studentID);
        String currentSemester = getCurrentSemester(studentID);
        
        return allGrades.stream()
                .filter(grade -> currentSemester.equals(grade.getSemester()))
                .collect(java.util.stream.Collectors.toList());
    }

    /**
     * Updates a grade record (placeholder for faculty integration)
     * @param studentID The student ID
     * @param subjectCode The subject code
     * @param gradeType The type of grade (prelim, midterm, tentative_final, final)
     * @param grade The grade value
     * @return true if successful, false otherwise
     */

    public static boolean updateGrade(String studentID, String subjectCode, String gradeType, Double grade) {
        // TODO: This function will be implemented when faculty account system is integrated
        System.out.println("Faculty grade update function called - not yet implemented");
        System.out.println("Parameters: " + studentID + ", " + subjectCode + ", " + gradeType + ", " + grade);
        return false;
    }


    /**
     * Gets completed grade records for transcript
     * @param studentID The student ID
     * @return List of completed grade records grouped by semester
     */

    public static Map<String, List<GradeRecord>> getTranscriptRecords(String studentID) {
        List<GradeRecord> allGrades = loadGradeRecords(studentID);
        Map<String, List<GradeRecord>> transcriptMap = new LinkedHashMap<>();
        
        // Filter only completed courses and group by semester
        for (GradeRecord grade : allGrades) {
            if ("Completed".equals(grade.getStatus()) && grade.getFinalGrade() != null) {
                transcriptMap.computeIfAbsent(grade.getSemester(), k -> new ArrayList<>()).add(grade);
            }
        }
        
        return transcriptMap;
    }
    
    /**
     * Gets student schedule for the current semester
     */

    public static List<CourseSchedule> getStudentSchedule(String studentID) {
        List<CourseSchedule> schedules = new ArrayList<>();
        
        try {
            File scheduleFile = getCourseSchedulesFile();
            if (!scheduleFile.exists()) {
                return schedules;
            }
            
            try (BufferedReader reader = new BufferedReader(new FileReader(scheduleFile))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.trim().isEmpty() || line.startsWith("===") || line.startsWith("Format:")) {
                        continue;
                    }
                    
                    String[] parts = line.split(",");
                    if (parts.length >= 11) {
                        String storedStudentID = parts[0].trim();
                        if (studentID.equals(storedStudentID)) {
                            // Parse time strings to LocalTime
                            java.time.format.DateTimeFormatter timeFormatter = java.time.format.DateTimeFormatter.ofPattern("H:mm");
                            java.time.LocalTime startTime = java.time.LocalTime.parse(parts[5].trim(), timeFormatter);
                            java.time.LocalTime endTime = java.time.LocalTime.parse(parts[6].trim(), timeFormatter);
                            
                            CourseSchedule schedule = new CourseSchedule(
                                parts[0].trim(),  // studentID
                                parts[1].trim(),  // classCode
                                parts[2].trim(),  // courseNumber
                                parts[3].trim(),  // courseDescription
                                Integer.parseInt(parts[4].trim()),  // units
                                startTime,        // startTime
                                endTime,          // endTime
                                parts[7].trim(),  // days
                                parts[8].trim(),  // room
                                parts[9].trim(),  // instructor
                                parts[10].trim() // semester
                            );
                            schedules.add(schedule);
                        }
                    }
                }
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println("Error reading course schedules: " + e.getMessage());
        }
        
        return schedules;
    }
    
    /**
     * Gets student attendance records
     */

    public static List<AttendanceRecord> getStudentAttendance(String studentID) {
        List<AttendanceRecord> attendanceRecords = new ArrayList<>();
        
        try {
            File attendanceFile = getAttendanceRecordsFile();
            if (!attendanceFile.exists()) {
                return attendanceRecords;
            }
            
            try (BufferedReader reader = new BufferedReader(new FileReader(attendanceFile))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.trim().isEmpty() || line.startsWith("===") || line.startsWith("Format:")) {
                        continue;
                    }
                    
                    String[] parts = line.split(",");
                    if (parts.length >= 6) {
                        String storedStudentID = parts[0].trim();
                        if (studentID.equals(storedStudentID)) {
                            // Parse date string to LocalDate
                            java.time.format.DateTimeFormatter dateFormatter = java.time.format.DateTimeFormatter.ofPattern("M/d/yyyy");
                            java.time.LocalDate date = java.time.LocalDate.parse(parts[3].trim(), dateFormatter);
                            
                            AttendanceRecord record = new AttendanceRecord(
                                parts[0].trim(),  // studentID
                                parts[1].trim(),  // subjectCode
                                parts[2].trim(),  // subjectName
                                date,             // date
                                parts[4].trim(),  // status
                                parts.length > 5 ? parts[5].trim() : ""  // remarks
                            );
                            attendanceRecords.add(record);
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading attendance records: " + e.getMessage());
        }
        
        return attendanceRecords;
    }

    /**
     * Gets payment transactions for a student
     */

    public static List<PaymentTransaction> getPaymentTransactions(String studentID) {
        List<PaymentTransaction> transactions = new ArrayList<>();
        
        try {
            File paymentFile = getPaymentLogsFile();
            if (!paymentFile.exists()) {
                return transactions;
            }
            
            try (BufferedReader reader = new BufferedReader(new FileReader(paymentFile))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.trim().isEmpty() || line.startsWith("===") || line.startsWith("Format:")) {
                        continue;
                    }
                    
                    String[] parts = line.split(",");
                    if (parts.length >= 5) {
                        String storedStudentID = parts[4].trim();
                        if (studentID.equals(storedStudentID)) {
                            // Parse amount (remove P and commas)
                            String amountStr = parts[3].trim().replaceAll("[P, ]", "");
                            double amount = Double.parseDouble(amountStr);
                            
                            PaymentTransaction transaction = new PaymentTransaction(
                                parts[0].trim(),  // date
                                parts[1].trim(),  // paymentChannel
                                parts[2].trim(),  // reference
                                amount,           // amount
                                parts[4].trim()   // studentID
                            );
                            transactions.add(transaction);
                        }
                    }
                }
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println("Error reading payment transactions: " + e.getMessage());
        }
        
        return transactions;
    }

    /**
     * Gets attendance summary for a student (fixed return type)
     */

    public static AttendanceSummary getAttendanceSummaryForStudent(String studentID) {
        List<AttendanceRecord> records = getStudentAttendance(studentID);
        
        int presentCount = 0;
        int absentCount = 0;
        int lateCount = 0;
        
        for (AttendanceRecord record : records) {
            String status = record.getStatus().toLowerCase();
            if (status.equals("present")) {
                presentCount++;
            } else if (status.equals("absent")) {
                absentCount++;
            } else if (status.equals("late")) {
                lateCount++;
            }
        }
        
        int totalClasses = presentCount + absentCount + lateCount;
        double percentage = totalClasses > 0 ? (double) presentCount / totalClasses * 100 : 0.0;
        
        // Create a summary with overall attendance data
        AttendanceSummary summary = new AttendanceSummary("Overall Attendance");
        
        // Set the counts using the increment methods
        for (int i = 0; i < presentCount; i++) summary.incrementPresent();
        for (int i = 0; i < absentCount; i++) summary.incrementAbsent();
        for (int i = 0; i < lateCount; i++) summary.incrementLate();
        
        return summary;
    }


}

