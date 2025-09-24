import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.net.URL;
import java.text.SimpleDateFormat;

public class reqAcc extends JFrame {

    // ArrayList to hold created accounts (ID + Password)
    private static final java.util.List<String> accountLog = new ArrayList<>();

    public reqAcc() {
        setTitle("Student Portal");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());

        // ================= Header Panel =================
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        headerPanel.setBackground(new Color(10, 45, 90));
        headerPanel.setPreferredSize(new Dimension(getWidth(), 100));

        JLabel logoLabel = new JLabel();
        URL logoUrl = getClass().getResource("/photos/SLULoginLogo.png");
        if (logoUrl != null) {
            ImageIcon icon = new ImageIcon(logoUrl);
            Image scaled = icon.getImage().getScaledInstance(180, 90, Image.SCALE_SMOOTH);
            logoLabel.setIcon(new ImageIcon(scaled));
        } else {
            logoLabel.setText("SLU portal");
            logoLabel.setForeground(Color.WHITE);
            logoLabel.setFont(new Font("Arial", Font.BOLD, 18));
        }
        
        // Add click handler to logo to navigate to login page
        logoLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logoLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Dispose current reqAcc window
                dispose();
                // Open login page
                new Login().setVisible(true);
            }
        });
        
        headerPanel.add(logoLabel);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // ================= Form Panel =================
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel formTitle = new JLabel("Account Request [For Students Only]");
        formTitle.setFont(new Font("Arial", Font.BOLD, 18));
        formTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        formTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        formPanel.add(formTitle);

        // ===== Personal Information =====
        JPanel namePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        namePanel.setBorder(BorderFactory.createTitledBorder("Personal Information"));

        JTextField lnameField = new JTextField(10);
        JTextField fnameField = new JTextField(10);
        JTextField mnameField = new JTextField(10);

        namePanel.add(new JLabel("Last Name:"));
        namePanel.add(lnameField);
        namePanel.add(new JLabel("First Name:"));
        namePanel.add(fnameField);
        namePanel.add(new JLabel("Middle Name:"));
        namePanel.add(mnameField);
        formPanel.add(namePanel);

        // Date of Birth
        JPanel dobPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        dobPanel.setBorder(BorderFactory.createTitledBorder("Date of Birth"));
        JTextField dobField = new JTextField(10);
        JButton calendarBtn = new JButton("📅");
        calendarBtn.setToolTipText("Select Date");
        
        // Calendar button action
        calendarBtn.addActionListener(e -> {
            showDatePicker(dobField);
        });
        
        dobPanel.add(new JLabel("Date of Birth (MM/DD/YY):"));
        dobPanel.add(dobField);
        dobPanel.add(calendarBtn);
        formPanel.add(dobPanel);

        // Create Password JPanel
        JPanel passwordPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        passwordPanel.setBorder(BorderFactory.createTitledBorder("Create Password"));
        JPasswordField passwordField = new JPasswordField(15);
        passwordField.setEchoChar('*');
        
        passwordPanel.add(new JLabel("Create Password:"));
        passwordPanel.add(passwordField);
        formPanel.add(passwordPanel);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton submitBtn = new JButton("Submit");

        // === Submit Action with Unique ID ===
        submitBtn.addActionListener(e -> {
            String lname = lnameField.getText().trim();
            String fname = fnameField.getText().trim();
            String mname = mnameField.getText().trim();
            
            // Get date from text field
            String dateText = dobField.getText().trim();
            if (dateText.isEmpty()) {
                JOptionPane.showMessageDialog(this, "⚠️ Please select a birth date.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Parse the selected date
            Date selectedDate;
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
                selectedDate = dateFormat.parse(dateText);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "⚠️ Invalid date format. Please select a date again.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Validate date is not in the future
            Calendar cal = Calendar.getInstance();
            cal.setTime(selectedDate);
            int year = cal.get(Calendar.YEAR);
            int currentYear = Calendar.getInstance().get(Calendar.YEAR);
            if (year > currentYear) {
                JOptionPane.showMessageDialog(this, "⚠️ Birth year cannot be in the future.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            String dob = dateText; // Use the formatted date string
            
            // Get password from user input
            String password = new String(passwordField.getPassword()).trim();
            
            if (lname.isEmpty() || fname.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "⚠️ Please fill in all required fields!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                // Generate unique ID using DataManager
                String idNum = DataManager.generateUniqueStudentID();

                // Create StudentInfo object
                StudentInfo studentInfo = new StudentInfo(idNum, lname, fname, mname, dob, password);

                // Save using DataManager
                if (DataManager.saveStudentAccount(studentInfo)) {
                    // Add to local log for display
                    String logEntry = "ID: " + idNum + " | Password: " + password;
                    accountLog.add(logEntry);

                    // Show success message
                    JOptionPane.showMessageDialog(this,
                            "✅ Account request saved!\nYour new ID Number is: " + idNum +
                                    "\nPlease complete your profile information.");

                    // Close this window and open User Profile form
                    dispose();
                    String fullName = fname + " " + lname;
                    openUserProfile(idNum, fullName, password);
                } else {
                    JOptionPane.showMessageDialog(this, "❌ Error saving account request. Please try again.");
                }

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "❌ Error saving account request: " + ex.getMessage());
            }
        });

        buttonPanel.add(submitBtn);
        formPanel.add(buttonPanel);

        JScrollPane scrollPane = new JScrollPane(formPanel);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // ================= Footer =================
        JLabel footerLabel = new JLabel("Copyright © 2021 TMDD - Software Development. All rights reserved.");
        footerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        footerLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        mainPanel.add(footerLabel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    // This method is now handled by DataManager.generateUniqueStudentID()
    // Keeping for backward compatibility but delegating to DataManager
    private String generateUniqueID(String filename) {
        return DataManager.generateUniqueStudentID();
    }
    
    // Method to show date picker
    private void showDatePicker(JTextField dobField) {
        try {
            JDialog popup = new JDialog(this, "Select Birth Date", true);
            popup.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            popup.setSize(350, 300);
            popup.setLocationRelativeTo(dobField);
            
            JPanel popupPanel = new JPanel(new BorderLayout());
            
            // Create calendar widget for popup
            CalendarWidget calendarWidget = new CalendarWidget();
            popupPanel.add(calendarWidget, BorderLayout.CENTER);
            
            // Add buttons panel
            JPanel buttonPanel = new JPanel(new FlowLayout());
            JButton okButton = new JButton("OK");
            JButton cancelButton = new JButton("Cancel");
            
            okButton.addActionListener(e -> {
                Date selectedDate = calendarWidget.getSelectedDate();
                if (selectedDate != null) {
                    try {
                        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
                        dobField.setText(dateFormat.format(selectedDate));
                    } catch (Exception ex) {
                        // Handle date formatting error
                        dobField.setText("Invalid Date");
                    }
                }
                popup.dispose();
            });
            
            cancelButton.addActionListener(e -> popup.dispose());
            
            buttonPanel.add(okButton);
            buttonPanel.add(cancelButton);
            popupPanel.add(buttonPanel, BorderLayout.SOUTH);
            
            popup.add(popupPanel);
            popup.setVisible(true);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error opening calendar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void openUserProfile(String studentID, String studentName, String studentPassword) {
        JFrame profileFrame = new JFrame("User Profile");
        profileFrame.setSize(800, 700);
        profileFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        profileFrame.setLocationRelativeTo(null);
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        
        // Header Panel
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(10, 45, 90));
        headerPanel.setPreferredSize(new Dimension(800, 60));
        
        JLabel headerLabel = new JLabel("User Profile");
        headerLabel.setForeground(Color.WHITE);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 24));
        headerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        headerPanel.add(headerLabel);
        
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Content Panel
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        contentPanel.setBackground(Color.WHITE);
        
        // Student Info Panel
        JPanel studentInfoPanel = new JPanel();
        studentInfoPanel.setLayout(new BoxLayout(studentInfoPanel, BoxLayout.Y_AXIS));
        studentInfoPanel.setBackground(Color.WHITE);
        studentInfoPanel.setBorder(BorderFactory.createTitledBorder("Student Information"));
        
        JLabel studentLabel = new JLabel("Student: " + studentName + " (ID: " + studentID + ")");
        studentLabel.setFont(new Font("Arial", Font.BOLD, 14));
        studentInfoPanel.add(studentLabel);
        
        contentPanel.add(studentInfoPanel);
        contentPanel.add(Box.createVerticalStrut(20));
        
        // General Information Panel
        JPanel generalPanel = createGeneralInformationPanel();
        contentPanel.add(generalPanel);
        contentPanel.add(Box.createVerticalStrut(20));
        
        // Contact Information Panel
        JPanel contactPanel = createContactInformationPanel();
        contentPanel.add(contactPanel);
        contentPanel.add(Box.createVerticalStrut(20));
        
        // Contact Persons Panel
        JPanel contactPersonsPanel = createContactPersonsPanel();
        contentPanel.add(contactPersonsPanel);
        contentPanel.add(Box.createVerticalStrut(20));
        
        // Buttons Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(Color.WHITE);
        
        JButton saveButton = new JButton("Save Profile");
        saveButton.setBackground(new Color(70, 130, 180));
        saveButton.setForeground(Color.WHITE);
        saveButton.setFont(new Font("Arial", Font.BOLD, 14));
        saveButton.setPreferredSize(new Dimension(120, 40));
        saveButton.addActionListener(e -> saveProfile(profileFrame, studentID, studentName));
        
        JButton cancelButton = new JButton("Cancel");
        cancelButton.setBackground(new Color(220, 220, 220));
        cancelButton.setForeground(Color.BLACK);
        cancelButton.setFont(new Font("Arial", Font.BOLD, 14));
        cancelButton.setPreferredSize(new Dimension(120, 40));
        cancelButton.addActionListener(e -> profileFrame.dispose());
        
        buttonPanel.add(saveButton);
        buttonPanel.add(Box.createHorizontalStrut(10));
        buttonPanel.add(cancelButton);
        
        contentPanel.add(buttonPanel);
        
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
        profileFrame.add(mainPanel);
        profileFrame.setVisible(true);
    }
    
    private JPanel createGeneralInformationPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder("GENERAL INFORMATION"));
        
        // Gender
        JPanel genderPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        genderPanel.setBackground(Color.WHITE);
        JLabel genderLabel = new JLabel("Gender:");
        genderLabel.setPreferredSize(new Dimension(120, 25));
        genderLabel.setFont(new Font("Arial", Font.BOLD, 12));
        
        String[] genderOptions = {"Male", "Female"};
        JComboBox<String> genderCombo = new JComboBox<>(genderOptions);
        genderCombo.setPreferredSize(new Dimension(150, 25));
        genderCombo.addActionListener(e -> userGender = (String) genderCombo.getSelectedItem());
        
        genderPanel.add(genderLabel);
        genderPanel.add(genderCombo);
        panel.add(genderPanel);
        
        
        // Citizenship
        JPanel citizenshipPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        citizenshipPanel.setBackground(Color.WHITE);
        JLabel citizenshipLabel = new JLabel("Citizenship:");
        citizenshipLabel.setPreferredSize(new Dimension(120, 25));
        citizenshipLabel.setFont(new Font("Arial", Font.BOLD, 12));
        
        JTextField citizenshipField = new JTextField(15);
        citizenshipField.setText("Filipino");
        citizenshipField.setPreferredSize(new Dimension(150, 25));
        addDocumentListener(citizenshipField, text -> userCitizenship = text);
        
        citizenshipPanel.add(citizenshipLabel);
        citizenshipPanel.add(citizenshipField);
        panel.add(citizenshipPanel);
        
        // Religion
        JPanel religionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        religionPanel.setBackground(Color.WHITE);
        JLabel religionLabel = new JLabel("Religion:");
        religionLabel.setPreferredSize(new Dimension(120, 25));
        religionLabel.setFont(new Font("Arial", Font.BOLD, 12));
        
        JTextField religionField = new JTextField(15);
        religionField.setText("Roman Catholic");
        religionField.setPreferredSize(new Dimension(150, 25));
        addDocumentListener(religionField, text -> userReligion = text);
        
        religionPanel.add(religionLabel);
        religionPanel.add(religionField);
        panel.add(religionPanel);
        
        // Civil Status
        JPanel civilStatusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        civilStatusPanel.setBackground(Color.WHITE);
        JLabel civilStatusLabel = new JLabel("Civil Status:");
        civilStatusLabel.setPreferredSize(new Dimension(120, 25));
        civilStatusLabel.setFont(new Font("Arial", Font.BOLD, 12));
        
        String[] civilStatusOptions = {"Single", "Married"};
        JComboBox<String> civilStatusCombo = new JComboBox<>(civilStatusOptions);
        civilStatusCombo.setPreferredSize(new Dimension(150, 25));
        civilStatusCombo.addActionListener(e -> userCivilStatus = (String) civilStatusCombo.getSelectedItem());
        
        civilStatusPanel.add(civilStatusLabel);
        civilStatusPanel.add(civilStatusCombo);
        panel.add(civilStatusPanel);
        
        // Birthplace
        JPanel birthplacePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        birthplacePanel.setBackground(Color.WHITE);
        JLabel birthplaceLabel = new JLabel("Birthplace:");
        birthplaceLabel.setPreferredSize(new Dimension(120, 25));
        birthplaceLabel.setFont(new Font("Arial", Font.BOLD, 12));
        
        JTextField birthplaceField = new JTextField(15);
        birthplaceField.setText("None");
        birthplaceField.setPreferredSize(new Dimension(150, 25));
        addDocumentListener(birthplaceField, text -> userBirthplace = text);
        
        birthplacePanel.add(birthplaceLabel);
        birthplacePanel.add(birthplaceField);
        panel.add(birthplacePanel);
        
        // Nationality
        JPanel nationalityPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        nationalityPanel.setBackground(Color.WHITE);
        JLabel nationalityLabel = new JLabel("Nationality:");
        nationalityLabel.setPreferredSize(new Dimension(120, 25));
        nationalityLabel.setFont(new Font("Arial", Font.BOLD, 12));
        
        JTextField nationalityField = new JTextField(15);
        nationalityField.setText("Filipino");
        nationalityField.setPreferredSize(new Dimension(150, 25));
        addDocumentListener(nationalityField, text -> userNationality = text);
        
        nationalityPanel.add(nationalityLabel);
        nationalityPanel.add(nationalityField);
        panel.add(nationalityPanel);
        
        return panel;
    }
    
    private JPanel createContactInformationPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder("CONTACT INFORMATION"));
        
        // Home Address
        JPanel homeAddressPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        homeAddressPanel.setBackground(Color.WHITE);
        JLabel homeAddressLabel = new JLabel("Home Address:");
        homeAddressLabel.setPreferredSize(new Dimension(120, 25));
        homeAddressLabel.setFont(new Font("Arial", Font.BOLD, 12));
        
        JTextField homeAddressField = new JTextField(15);
        homeAddressField.setText("None");
        homeAddressField.setPreferredSize(new Dimension(150, 25));
        addDocumentListener(homeAddressField, text -> userHomeAddress = text);
        
        homeAddressPanel.add(homeAddressLabel);
        homeAddressPanel.add(homeAddressField);
        panel.add(homeAddressPanel);
        
        // Home Telephone No
        JPanel homeTelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        homeTelPanel.setBackground(Color.WHITE);
        JLabel homeTelLabel = new JLabel("Home Telephone No:");
        homeTelLabel.setPreferredSize(new Dimension(120, 25));
        homeTelLabel.setFont(new Font("Arial", Font.BOLD, 12));
        
        JTextField homeTelField = new JTextField(15);
        homeTelField.setText("None");
        homeTelField.setPreferredSize(new Dimension(150, 25));
        addDocumentListener(homeTelField, text -> userHomeTel = text);
        
        homeTelPanel.add(homeTelLabel);
        homeTelPanel.add(homeTelField);
        panel.add(homeTelPanel);
        
        // Baguio Address
        JPanel baguioAddressPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        baguioAddressPanel.setBackground(Color.WHITE);
        JLabel baguioAddressLabel = new JLabel("Baguio Address:");
        baguioAddressLabel.setPreferredSize(new Dimension(120, 25));
        baguioAddressLabel.setFont(new Font("Arial", Font.BOLD, 12));
        
        JTextField baguioAddressField = new JTextField(15);
        baguioAddressField.setText("None");
        baguioAddressField.setPreferredSize(new Dimension(150, 25));
        addDocumentListener(baguioAddressField, text -> userBaguioAddress = text);
        
        baguioAddressPanel.add(baguioAddressLabel);
        baguioAddressPanel.add(baguioAddressField);
        panel.add(baguioAddressPanel);
        
        // Baguio Telephone No
        JPanel baguioTelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        baguioTelPanel.setBackground(Color.WHITE);
        JLabel baguioTelLabel = new JLabel("Baguio Telephone No:");
        baguioTelLabel.setPreferredSize(new Dimension(120, 25));
        baguioTelLabel.setFont(new Font("Arial", Font.BOLD, 12));
        
        JTextField baguioTelField = new JTextField(15);
        baguioTelField.setText("None");
        baguioTelField.setPreferredSize(new Dimension(150, 25));
        addDocumentListener(baguioTelField, text -> userBaguioTel = text);
        
        baguioTelPanel.add(baguioTelLabel);
        baguioTelPanel.add(baguioTelField);
        panel.add(baguioTelPanel);
        
        // Cellphone No
        JPanel cellphonePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        cellphonePanel.setBackground(Color.WHITE);
        JLabel cellphoneLabel = new JLabel("Cellphone No:");
        cellphoneLabel.setPreferredSize(new Dimension(120, 25));
        cellphoneLabel.setFont(new Font("Arial", Font.BOLD, 12));
        
        JTextField cellphoneField = new JTextField(15);
        cellphoneField.setText("None");
        cellphoneField.setPreferredSize(new Dimension(150, 25));
        addDocumentListener(cellphoneField, text -> userCellphone = text);
        
        cellphonePanel.add(cellphoneLabel);
        cellphonePanel.add(cellphoneField);
        panel.add(cellphonePanel);
        
        return panel;
    }
    
    private JPanel createContactPersonsPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder("CONTACT PERSONS"));
        
        // Father's Name
        JPanel fatherNamePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        fatherNamePanel.setBackground(Color.WHITE);
        JLabel fatherNameLabel = new JLabel("Father's Name:");
        fatherNameLabel.setPreferredSize(new Dimension(120, 25));
        fatherNameLabel.setFont(new Font("Arial", Font.BOLD, 12));
        
        JTextField fatherNameField = new JTextField(15);
        fatherNameField.setText("None");
        fatherNameField.setPreferredSize(new Dimension(150, 25));
        addDocumentListener(fatherNameField, text -> userFatherName = text);
        
        fatherNamePanel.add(fatherNameLabel);
        fatherNamePanel.add(fatherNameField);
        panel.add(fatherNamePanel);
        
        // Father's Occupation
        JPanel fatherOccPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        fatherOccPanel.setBackground(Color.WHITE);
        JLabel fatherOccLabel = new JLabel("Father's Occupation:");
        fatherOccLabel.setPreferredSize(new Dimension(120, 25));
        fatherOccLabel.setFont(new Font("Arial", Font.BOLD, 12));
        
        JTextField fatherOccField = new JTextField(15);
        fatherOccField.setText("None");
        fatherOccField.setPreferredSize(new Dimension(150, 25));
        addDocumentListener(fatherOccField, text -> userFatherOcc = text);
        
        fatherOccPanel.add(fatherOccLabel);
        fatherOccPanel.add(fatherOccField);
        panel.add(fatherOccPanel);
        
        // Mother's Maiden Name
        JPanel motherNamePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        motherNamePanel.setBackground(Color.WHITE);
        JLabel motherNameLabel = new JLabel("Mother's Maiden Name:");
        motherNameLabel.setPreferredSize(new Dimension(120, 25));
        motherNameLabel.setFont(new Font("Arial", Font.BOLD, 12));
        
        JTextField motherNameField = new JTextField(15);
        motherNameField.setText("None");
        motherNameField.setPreferredSize(new Dimension(150, 25));
        addDocumentListener(motherNameField, text -> userMotherName = text);
        
        motherNamePanel.add(motherNameLabel);
        motherNamePanel.add(motherNameField);
        panel.add(motherNamePanel);
        
        // Mother's Occupation
        JPanel motherOccPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        motherOccPanel.setBackground(Color.WHITE);
        JLabel motherOccLabel = new JLabel("Mother's Occupation:");
        motherOccLabel.setPreferredSize(new Dimension(120, 25));
        motherOccLabel.setFont(new Font("Arial", Font.BOLD, 12));
        
        JTextField motherOccField = new JTextField(15);
        motherOccField.setText("None");
        motherOccField.setPreferredSize(new Dimension(150, 25));
        addDocumentListener(motherOccField, text -> userMotherOcc = text);
        
        motherOccPanel.add(motherOccLabel);
        motherOccPanel.add(motherOccField);
        panel.add(motherOccPanel);
        
        // Guardian Name
        JPanel guardianNamePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        guardianNamePanel.setBackground(Color.WHITE);
        JLabel guardianNameLabel = new JLabel("Guardian Name:");
        guardianNameLabel.setPreferredSize(new Dimension(120, 25));
        guardianNameLabel.setFont(new Font("Arial", Font.BOLD, 12));
        
        JTextField guardianNameField = new JTextField(15);
        guardianNameField.setText("None");
        guardianNameField.setPreferredSize(new Dimension(150, 25));
        addDocumentListener(guardianNameField, text -> userGuardianName = text);
        
        guardianNamePanel.add(guardianNameLabel);
        guardianNamePanel.add(guardianNameField);
        panel.add(guardianNamePanel);
        
        // Guardian Address
        JPanel guardianAddressPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        guardianAddressPanel.setBackground(Color.WHITE);
        JLabel guardianAddressLabel = new JLabel("Guardian Address:");
        guardianAddressLabel.setPreferredSize(new Dimension(120, 25));
        guardianAddressLabel.setFont(new Font("Arial", Font.BOLD, 12));
        
        JTextField guardianAddressField = new JTextField(15);
        guardianAddressField.setText("None");
        guardianAddressField.setPreferredSize(new Dimension(150, 25));
        addDocumentListener(guardianAddressField, text -> userGuardianAddress = text);
        
        guardianAddressPanel.add(guardianAddressLabel);
        guardianAddressPanel.add(guardianAddressField);
        panel.add(guardianAddressPanel);
        
        return panel;
    }
    
    private void saveProfile(JFrame profileFrame, String studentID, String studentName) {
        // Get all profile information from the form
        String profileData = collectProfileData(profileFrame);
        
        // Save profile data to Database.txt
        if (DataManager.updateStudentProfile(studentID, profileData)) {
            JOptionPane.showMessageDialog(profileFrame, 
                "Profile saved successfully!\nStudent ID: " + studentID + "\nStudent Name: " + studentName + "\nYou can now login with your credentials.",
                "Profile Saved", 
                JOptionPane.INFORMATION_MESSAGE);
            
            // Close this window and open the login form
            profileFrame.dispose();
            new Login().setVisible(true);
        } else {
            JOptionPane.showMessageDialog(profileFrame, "❌ Error saving profile. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    // Instance variables to store user input from profile form
    private String userGender = "Male";
    private String userCitizenship = "Filipino";
    private String userReligion = "Roman Catholic";
    private String userCivilStatus = "Single";
    private String userBirthplace = "None";
    private String userNationality = "Filipino";
    private String userHomeAddress = "None";
    private String userHomeTel = "None";
    private String userBaguioAddress = "None";
    private String userBaguioTel = "None";
    private String userCellphone = "None";
    private String userFatherName = "None";
    private String userFatherOcc = "None";
    private String userMotherName = "None";
    private String userMotherOcc = "None";
    private String userGuardianName = "None";
    private String userGuardianAddress = "None";

    // Helper method to add document listener to text fields
    private void addDocumentListener(JTextField field, java.util.function.Consumer<String> setter) {
        field.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { setter.accept(field.getText()); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { setter.accept(field.getText()); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { setter.accept(field.getText()); }
        });
    }

    private String collectProfileData(JFrame profileFrame) {
        // Return the stored user input data
        StringBuilder profileData = new StringBuilder();
        
        profileData.append("Gender=").append(userGender).append(";");
        profileData.append("Citizenship=").append(userCitizenship).append(";");
        profileData.append("Religion=").append(userReligion).append(";");
        profileData.append("CivilStatus=").append(userCivilStatus).append(";");
        profileData.append("Birthplace=").append(userBirthplace).append(";");
        profileData.append("Nationality=").append(userNationality).append(";");
        profileData.append("HomeAddress=").append(userHomeAddress).append(";");
        profileData.append("HomeTel=").append(userHomeTel).append(";");
        profileData.append("BaguioAddress=").append(userBaguioAddress).append(";");
        profileData.append("BaguioTel=").append(userBaguioTel).append(";");
        profileData.append("Cellphone=").append(userCellphone).append(";");
        profileData.append("FatherName=").append(userFatherName).append(";");
        profileData.append("FatherOcc=").append(userFatherOcc).append(";");
        profileData.append("MotherName=").append(userMotherName).append(";");
        profileData.append("MotherOcc=").append(userMotherOcc).append(";");
        profileData.append("GuardianName=").append(userGuardianName).append(";");
        profileData.append("GuardianAddress=").append(userGuardianAddress).append(";");
        
        return profileData.toString();
    }

}

// Custom Calendar Widget Class
class CalendarWidget extends JPanel {
    private Calendar calendar;
    private JButton[][] dayButtons;
    private Date selectedDate;
    private JPanel calendarPanel;
    private JComboBox<String> monthCombo;
    private JComboBox<String> yearCombo;
    
    public CalendarWidget() {
        try {
            setLayout(new BorderLayout());
            calendar = Calendar.getInstance();
            // Default to current year and month for realistic experience
            Calendar now = Calendar.getInstance();
            calendar.set(Calendar.YEAR, now.get(Calendar.YEAR));
            calendar.set(Calendar.MONTH, now.get(Calendar.MONTH));
            calendar.set(Calendar.DAY_OF_MONTH, 1);
            
            initializeComponents();
            updateCalendar();
        } catch (Exception e) {
            // Handle initialization error
            System.err.println("Error initializing calendar widget: " + e.getMessage());
        }
    }
    
    private void initializeComponents() {
        try {
            // Header with month/year dropdowns - realistic styling
            JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 8));
            headerPanel.setBackground(new Color(248, 248, 248));
            headerPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)
            ));
            headerPanel.setPreferredSize(new Dimension(300, 45));
        
            // Month dropdown with real-time sync
            String[] months = {"January", "February", "March", "April", "May", "June",
                              "July", "August", "September", "October", "November", "December"};
            monthCombo = new JComboBox<>(months);
            monthCombo.setSelectedIndex(calendar.get(Calendar.MONTH)); // Sync with current month
            monthCombo.setBackground(Color.WHITE);
            monthCombo.addActionListener(e -> {
                int selectedMonth = monthCombo.getSelectedIndex();
                calendar.set(Calendar.MONTH, selectedMonth);
                updateCalendar();
            });
            
            // Year dropdown (1950-2030) with real-time sync - more realistic range
            String[] years = new String[81]; // 1950 to 2030 (81 years)
            for (int i = 0; i < 81; i++) {
                years[i] = String.valueOf(1950 + i);
            }
            yearCombo = new JComboBox<>(years);
            int currentYear = calendar.get(Calendar.YEAR);
            int yearIndex = currentYear - 1950;
            if (yearIndex >= 0 && yearIndex < years.length) {
                yearCombo.setSelectedIndex(yearIndex); // Sync with current year
            } else {
                // Default to current year if out of range
                Calendar now = Calendar.getInstance();
                int defaultYear = now.get(Calendar.YEAR);
                int defaultIndex = defaultYear - 1950;
                if (defaultIndex >= 0 && defaultIndex < years.length) {
                    yearCombo.setSelectedIndex(defaultIndex);
                } else {
                    yearCombo.setSelectedIndex(30); // Fallback to 1980
                }
            }
            yearCombo.setBackground(Color.WHITE);
            yearCombo.addActionListener(e -> {
                String selectedYear = (String) yearCombo.getSelectedItem();
                if (selectedYear != null) {
                    try {
                        int year = Integer.parseInt(selectedYear);
                        calendar.set(Calendar.YEAR, year);
                        updateCalendar();
                    } catch (NumberFormatException ex) {
                        // Handle invalid year
                    }
                }
            });
            
            headerPanel.add(new JLabel("Month:"));
            headerPanel.add(monthCombo);
            headerPanel.add(new JLabel("Year:"));
            headerPanel.add(yearCombo);
        
        // Calendar grid with realistic styling
        calendarPanel = new JPanel(new GridLayout(7, 7, 1, 1));
        calendarPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createRaisedBevelBorder(),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        calendarPanel.setBackground(Color.WHITE);
        
        // Day headers with realistic styling
        String[] dayHeaders = {"Su", "Mo", "Tu", "We", "Th", "Fr", "Sa"};
        for (String day : dayHeaders) {
            JLabel dayLabel = new JLabel(day, SwingConstants.CENTER);
            dayLabel.setBackground(new Color(240, 240, 240));
            dayLabel.setOpaque(true);
            dayLabel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(3, 3, 3, 3)
            ));
            dayLabel.setFont(new Font("Arial", Font.BOLD, 11));
            dayLabel.setForeground(new Color(80, 80, 80));
            calendarPanel.add(dayLabel);
        }
        
        // Day buttons (6 rows x 7 columns) with realistic styling
        dayButtons = new JButton[6][7];
        for (int row = 0; row < 6; row++) {
            for (int col = 0; col < 7; col++) {
                JButton dayButton = new JButton();
                dayButton.setPreferredSize(new Dimension(35, 25));
                dayButton.setMargin(new Insets(2, 2, 2, 2));
                dayButton.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                    BorderFactory.createEmptyBorder(2, 2, 2, 2)
                ));
                dayButton.setBackground(Color.WHITE);
                dayButton.setForeground(Color.BLACK);
                dayButton.setFont(new Font("Arial", Font.PLAIN, 11));
                dayButton.setFocusPainted(false);
                dayButton.setContentAreaFilled(true);
                dayButton.addActionListener(e -> {
                    JButton clickedButton = (JButton) e.getSource();
                    selectDate(clickedButton);
                });
                dayButtons[row][col] = dayButton;
                calendarPanel.add(dayButton);
            }
        }
        
            add(headerPanel, BorderLayout.NORTH);
            add(calendarPanel, BorderLayout.CENTER);
        } catch (Exception e) {
            // Handle component initialization error
            System.err.println("Error initializing components: " + e.getMessage());
        }
    }
    
    private void updateCalendar() {
        try {
            if (calendar == null) {
                return; // Exit if calendar is not initialized
            }
            
            // Sync dropdowns with current calendar state in real-time
            if (monthCombo != null) {
                monthCombo.setSelectedIndex(calendar.get(Calendar.MONTH));
            }
            if (yearCombo != null) {
                int currentYear = calendar.get(Calendar.YEAR);
                int yearIndex = currentYear - 1950;
                if (yearIndex >= 0 && yearIndex < yearCombo.getItemCount()) {
                    yearCombo.setSelectedIndex(yearIndex);
                }
            }
            
            // Clear all buttons with realistic styling
            if (dayButtons != null) {
                for (int row = 0; row < 6; row++) {
                    for (int col = 0; col < 7; col++) {
                        if (dayButtons[row][col] != null) {
                            dayButtons[row][col].setText("");
                            dayButtons[row][col].setBackground(Color.WHITE);
                            dayButtons[row][col].setForeground(Color.BLACK);
                            dayButtons[row][col].setEnabled(true);
                            dayButtons[row][col].setFont(new Font("Arial", Font.PLAIN, 11));
                        }
                    }
                }
            }
            
            // Get first day of month and number of days
            Calendar tempCal = (Calendar) calendar.clone();
            tempCal.set(Calendar.DAY_OF_MONTH, 1);
            int firstDayOfWeek = tempCal.get(Calendar.DAY_OF_WEEK);
            int daysInMonth = tempCal.getActualMaximum(Calendar.DAY_OF_MONTH);
            
            // Fill in the days
            if (dayButtons != null) {
                int day = 1;
                for (int row = 0; row < 6; row++) {
                    for (int col = 0; col < 7; col++) {
                        if (dayButtons[row][col] != null) {
                            if (row == 0 && col < firstDayOfWeek - 1) {
                                // Previous month days - grayed out
                                dayButtons[row][col].setText("");
                                dayButtons[row][col].setBackground(new Color(245, 245, 245));
                                dayButtons[row][col].setForeground(new Color(150, 150, 150));
                                dayButtons[row][col].setEnabled(false);
                            } else if (day <= daysInMonth) {
                                // Current month days - normal styling
                                dayButtons[row][col].setText(String.valueOf(day));
                                dayButtons[row][col].setBackground(Color.WHITE);
                                dayButtons[row][col].setForeground(Color.BLACK);
                                dayButtons[row][col].setEnabled(true);
                                dayButtons[row][col].setFont(new Font("Arial", Font.PLAIN, 11));
                                day++;
                            } else {
                                // Next month days - grayed out
                                dayButtons[row][col].setText("");
                                dayButtons[row][col].setBackground(new Color(245, 245, 245));
                                dayButtons[row][col].setForeground(new Color(150, 150, 150));
                                dayButtons[row][col].setEnabled(false);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            // Handle calendar update error
            System.err.println("Error updating calendar: " + e.getMessage());
        }
    }
    
    private void selectDate(JButton button) {
        // Reset all button colors to normal state
        if (dayButtons != null) {
            for (int row = 0; row < 6; row++) {
                for (int col = 0; col < 7; col++) {
                    if (dayButtons[row][col] != null && dayButtons[row][col].isEnabled()) {
                        dayButtons[row][col].setBackground(Color.WHITE);
                        dayButtons[row][col].setForeground(Color.BLACK);
                    }
                }
            }
        }
        
        // Highlight selected button with realistic selection styling
        button.setBackground(new Color(0, 120, 215)); // Modern blue selection
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 11));
        
        // Create selected date
        String dayText = button.getText();
        if (!dayText.isEmpty() && calendar != null) {
            try {
                Calendar selectedCal = (Calendar) calendar.clone();
                selectedCal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dayText));
                selectedDate = selectedCal.getTime();
            } catch (NumberFormatException e) {
                // Handle invalid day text
                selectedDate = null;
            }
        }
    }
    
    public Date getSelectedDate() {
        return selectedDate;
    }
}