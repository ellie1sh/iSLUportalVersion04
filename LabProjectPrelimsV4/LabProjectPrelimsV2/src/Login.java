import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.io.*;

public class Login extends JFrame {

    private JLabel logoLabel;
    private Image originalLogoImage;
    private int originalLogoWidth;
    private int originalLogoHeight;

    public Login() {
        setTitle("Login Page");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Background panel (acts as content pane)
        JPanel backgroundPanel = new JPanel(new GridBagLayout());
        backgroundPanel.setBackground(Color.WHITE);
        setContentPane(backgroundPanel);

        // Content panel to hold both logo and login form
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);

        // ================== SLU Logo ==================
        URL logoUrl = getClass().getResource("/photos/SLULoginLogo.png");
        if (logoUrl != null) {
            ImageIcon originalIcon = new ImageIcon(logoUrl);
            originalLogoImage = originalIcon.getImage();
            originalLogoWidth = originalIcon.getIconWidth();
            originalLogoHeight = originalIcon.getIconHeight();
            Image scaledImage = originalLogoImage.getScaledInstance(360, 180, Image.SCALE_SMOOTH);
            ImageIcon scaledIcon = new ImageIcon(scaledImage);
            logoLabel = new JLabel(scaledIcon);
        } else {
            logoLabel = new JLabel("SLU portal");
        }
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        logoLabel.setMaximumSize(new Dimension(360, 180));

        // ================== Login Panel ==================
        JPanel loginPanel = new JPanel();
        loginPanel.setLayout(new BoxLayout(loginPanel, BoxLayout.Y_AXIS));
        loginPanel.setBackground(new Color(40, 40, 40));
        loginPanel.setBorder(BorderFactory.createEmptyBorder(25, 40, 25, 40));
        loginPanel.setMaximumSize(new Dimension(400, 350));
        loginPanel.setPreferredSize(new Dimension(400, 350));

        // Title (aligned left with icons)
        JLabel titleLabel = new JLabel("User Login");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 15));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        // ================== ID Number row ==================
        JPanel idPanel = new JPanel(new BorderLayout());
        idPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        idPanel.setBackground(Color.WHITE);

        JLabel idIcon = new JLabel("👤");
        idIcon.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));

        JTextField idField = new JTextField();
        idField.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        idField.setText("ID Number"); // placeholder
        idField.setForeground(Color.GRAY);

        // Placeholder behavior
        idField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (idField.getText().equals("ID Number")) {
                    idField.setText("");
                    idField.setForeground(Color.BLACK);
                }
            }
            @Override
            public void focusLost(FocusEvent e) {
                if (idField.getText().isEmpty()) {
                    idField.setText("ID Number");
                    idField.setForeground(Color.GRAY);
                }
            }
        });

        idPanel.add(idIcon, BorderLayout.WEST);
        idPanel.add(idField, BorderLayout.CENTER);

        // ================== Password row ==================
        JPanel passPanel = new JPanel(new BorderLayout());
        passPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        passPanel.setBackground(Color.WHITE);

        JLabel passIcon = new JLabel("🔑");
        passIcon.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));

        JPasswordField passwordField = new JPasswordField();
        passwordField.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        passwordField.setEchoChar((char) 0); // show text initially
        passwordField.setText("Password");   // placeholder
        passwordField.setForeground(Color.GRAY);

        // Placeholder behavior
        passwordField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (String.valueOf(passwordField.getPassword()).equals("Password")) {
                    passwordField.setText("");
                    passwordField.setEchoChar('•');
                    passwordField.setForeground(Color.BLACK);
                }
            }
            @Override
            public void focusLost(FocusEvent e) {
                if (passwordField.getPassword().length == 0) {
                    passwordField.setEchoChar((char) 0);
                    passwordField.setText("Password");
                    passwordField.setForeground(Color.GRAY);
                }
            }
        });

        passPanel.add(passIcon, BorderLayout.WEST);
        passPanel.add(passwordField, BorderLayout.CENTER);

        // ================== Login button ==================
        JButton loginButton = new JButton("Login");
        loginButton.setBackground(new Color(70, 130, 180));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        loginButton.setFont(new Font("Arial", Font.BOLD, 16));
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        // Login button logic
        loginButton.addActionListener(e -> {
            String enteredID = idField.getText().trim();
            String enteredPassword = String.valueOf(passwordField.getPassword()).trim();
            
            // Check if fields are empty or contain placeholder text
            if (enteredID.isEmpty() || enteredID.equals("ID Number")) {
                idField.requestFocusInWindow();
                JOptionPane.showMessageDialog(this, "Please enter your ID Number.", "Validation", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (enteredPassword.isEmpty() || enteredPassword.equals("Password")) {
                passwordField.requestFocusInWindow();
                JOptionPane.showMessageDialog(this, "Please enter your Password.", "Validation", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Authenticate against Database.txt
            if (authenticateUser(enteredID, enteredPassword)) {
                // If authentication successful → go to HomePage
                dispose(); // close login
                new ISLUStudentPortal(enteredID).setVisible(true); // open your HomePage class with student ID
            } else {
                // If authentication failed
                JOptionPane.showMessageDialog(this, 
                    "Invalid ID Number or Password.\nPlease check your credentials and try again.", 
                    "Authentication Failed", 
                    JOptionPane.ERROR_MESSAGE);
                // Clear password field for security
                passwordField.setText("");
                passwordField.requestFocusInWindow();
            }
        });

        // ================== Labels (forgot + request) ==================
        JLabel forgotLabel = new JLabel("Forgot Password?");
        forgotLabel.setForeground(Color.WHITE);
        forgotLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        forgotLabel.setBorder(BorderFactory.createEmptyBorder(10, 5, 0, 0));
        forgotLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        forgotLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                dispose();
                new reqAcc().setVisible(true);
            }
        });

        JLabel requestLabel = new JLabel("- Account Request");
        requestLabel.setForeground(Color.WHITE);
        requestLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        requestLabel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
        requestLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        requestLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                dispose();
                new reqAcc().setVisible(true);
            }
        });

        // ================== Add components to login panel ==================
        loginPanel.add(titleLabel);
        loginPanel.add(Box.createVerticalStrut(20));
        loginPanel.add(idPanel);
        loginPanel.add(Box.createVerticalStrut(15));
        loginPanel.add(passPanel);
        loginPanel.add(Box.createVerticalStrut(20));
        loginPanel.add(loginButton);
        loginPanel.add(Box.createVerticalStrut(20));
        loginPanel.add(forgotLabel);
        loginPanel.add(requestLabel);

        // ================== Add components to content panel ==================
        contentPanel.add(Box.createVerticalStrut(30));
        contentPanel.add(logoLabel);
        contentPanel.add(Box.createVerticalStrut(30));
        contentPanel.add(loginPanel);

        // Center content panel
        GridBagConstraints gbcContent = new GridBagConstraints();
        gbcContent.gridx = 0;
        gbcContent.gridy = 0;
        gbcContent.weightx = 1.0;
        gbcContent.weighty = 1.0;
        gbcContent.anchor = GridBagConstraints.CENTER;
        gbcContent.fill = GridBagConstraints.NONE;
        backgroundPanel.add(contentPanel, gbcContent);

        // Window setup
        setSize(600, 600);
        setMinimumSize(new Dimension(500, 550));
        setLocationRelativeTo(null);

        // Dynamically scale logo width with window size
        ComponentAdapter resizeHandler = new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                updateLogoSize(contentPanel.getWidth());
            }
        };
        addComponentListener(resizeHandler);
        contentPanel.addComponentListener(resizeHandler);
        SwingUtilities.invokeLater(() -> updateLogoSize(contentPanel.getWidth()));
    }

    private void updateLogoSize(int availableWidth) {
        if (originalLogoImage == null || logoLabel == null) {
            return;
        }
        int targetWidth = Math.max(360, Math.min(availableWidth - 120, 1000));
        if (targetWidth <= 0) {
            return;
        }
        int targetHeight = (int) Math.round((double) originalLogoHeight * targetWidth / (double) originalLogoWidth);
        Image scaled = originalLogoImage.getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH);
        logoLabel.setIcon(new ImageIcon(scaled));
        logoLabel.setMaximumSize(new Dimension(targetWidth, targetHeight));
        logoLabel.revalidate();
        logoLabel.repaint();
    }

    /**
     * Authenticates user credentials using DataManager
     * @param enteredID The ID entered by user
     * @param enteredPassword The password entered by user
     * @return true if credentials match, false otherwise
     */
    private boolean authenticateUser(String enteredID, String enteredPassword) {
        // Check database availability first for clearer error messaging
        if (!DataManager.databaseExists()) {
            JOptionPane.showMessageDialog(this,
                "Database not found. Please contact administrator.",
                "Database Error",
                JOptionPane.ERROR_MESSAGE);
            return false;
        }

        boolean isValid = DataManager.authenticateUser(enteredID, enteredPassword);
        return isValid;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Login().setVisible(true));
    }
}
