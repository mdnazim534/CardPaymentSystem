import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

public class CardPaymentSystem extends JFrame implements Serializable {
    // App constants
    private static final double MIN_BALANCE = 100.0;
    private static final String DATA_FILE = "users.dat";
    private static final String LOG_FILE = "app.log";

    // In memory state
    private static List<User> users = new ArrayList<>();
    private static User currentUser = null;

    // UI shared
    private CardLayout cardLayout;
    private JPanel cardPanel;
    private JLabel welcomeLabel;
    private JTextArea transactionArea;

    public CardPaymentSystem() {
        setTitle("1 Card Payment System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 650);
        setLocationRelativeTo(null);

        // Load user data
        loadUsers();

        // Initialize UI
        initUI();

        setVisible(true);
    }

    private void initUI() {
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        cardPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Create panels
        createWelcomePanel();
        createLoginPanel();
        createRegistrationPanel();
        createUserMenuPanel();
        createBalancePanel();
        createDepositPanel();
        createWithdrawPanel();
        createTransferPanel();
        createTransactionHistoryPanel();
        createConvocationPanel();
        createBillPaymentPanel();
        createUpdateInfoPanel();       // completed
        createChangePinPanel();        // completed
        createDeleteAccountPanel();    // completed
        createUserDetailsPanel();      // completed

        add(cardPanel);
        cardLayout.show(cardPanel, "Welcome");
    }
    // Panels
    private void createWelcomePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));
        panel.setBackground(new Color(240, 248, 255));

        JLabel title = new JLabel("Welcome to 1 Card Payment System", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 28));
        title.setForeground(new Color(0, 102, 204));

        JLabel subtitle = new JLabel("Secure and convenient digital payments", JLabel.CENTER);
        subtitle.setFont(new Font("Arial", Font.PLAIN, 16));
        subtitle.setForeground(new Color(100, 100, 100));

        JPanel buttonPanel = new JPanel(new GridLayout(2, 1, 15, 15));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));
        buttonPanel.setOpaque(false);

        JButton loginBtn = createStyledButton("Login", new Color(0, 153, 204), 40);
        JButton registerBtn = createStyledButton("Create Account", new Color(76, 175, 80), 40);

        loginBtn.addActionListener(e -> cardLayout.show(cardPanel, "Login"));
        registerBtn.addActionListener(e -> cardLayout.show(cardPanel, "Register"));

        buttonPanel.add(loginBtn);
        buttonPanel.add(registerBtn);

        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        titlePanel.add(title, BorderLayout.NORTH);
        titlePanel.add(subtitle, BorderLayout.CENTER);

        panel.add(titlePanel, BorderLayout.NORTH);
        panel.add(buttonPanel, BorderLayout.CENTER);

        cardPanel.add(panel, "Welcome");
    }

    private void createLoginPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(240, 248, 255));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);

        JLabel title = new JLabel("Login to Your Account", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setForeground(new Color(0, 102, 204));

        JLabel phoneLabel = new JLabel("Phone Number:");
        phoneLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        JTextField phoneField = new JTextField(20);
        phoneField.setFont(new Font("Arial", Font.PLAIN, 16));

        JLabel pinLabel = new JLabel("PIN:");
        pinLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        JPasswordField pinField = new JPasswordField(20);
        pinField.setFont(new Font("Arial", Font.PLAIN, 16));

        JButton loginBtn = createStyledButton("Login", new Color(0, 153, 204), 16);
        JButton backBtn = createStyledButton("Back", new Color(158, 158, 158), 16);

        loginBtn.addActionListener(e -> {
            String phone = phoneField.getText().trim();
            String pin = new String(pinField.getPassword());

            if (phone.isEmpty() || pin.isEmpty()) {
                showError("Please enter both phone number and PIN");
                return;
            }

            User user = findUserByPhone(phone);
            if (user != null && user.getPin().equals(pin)) {
                currentUser = user;
                Logger.log("User logged in: " + currentUser.getUsername());
                welcomeLabel.setText("Welcome, " + currentUser.getFullName() + " (" + currentUser.getUsername() + ")");
                cardLayout.show(cardPanel, "UserMenu");
                showSuccess("Login successful!");
                phoneField.setText("");
                pinField.setText("");
            } else {
                showError("Invalid phone number or PIN");
            }
        });

        backBtn.addActionListener(e -> {
            phoneField.setText("");
            pinField.setText("");
            cardLayout.show(cardPanel, "Welcome");
        });

        gbc.gridwidth = 2;
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(title, gbc);

        gbc.gridwidth = 1;
        gbc.gridy = 1;
        panel.add(phoneLabel, gbc);

        gbc.gridx = 1;
        panel.add(phoneField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(pinLabel, gbc);

        gbc.gridx = 1;
        panel.add(pinField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(backBtn, gbc);

        gbc.gridx = 1;
        panel.add(loginBtn, gbc);

        cardPanel.add(panel, "Login");
    }

    private void createRegistrationPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(new Color(240, 248, 255));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel title = new JLabel("Create New Account", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setForeground(new Color(0, 102, 204));

        JPanel formPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        formPanel.setBackground(new Color(240, 248, 255));

        // Personal Information
        JTextField usernameField = new JTextField();
        JTextField phoneField = new JTextField();
        JPasswordField pinField = new JPasswordField();
       // JTextField fullNameField = new JTextField();
       // JTextField dobField = new JTextField();
       // JTextField genderField = new JTextField();
        JTextField emailField = new JTextField();

        // Family Information
       // JTextField motherField = new JTextField();
       // JTextField fatherField = new JTextField();

        // Identification
        JTextField nidField = new JTextField();
      //  JTextField birthCertField = new JTextField();

        // Address
       // JTextField permAddrField = new JTextField();
       // JTextField presAddrField = new JTextField();

        addFormField(formPanel, "Username*:", usernameField);
        addFormField(formPanel, "Phone Number*:", phoneField);
        addFormField(formPanel, "Email:", emailField);
        addFormField(formPanel, "National ID:", nidField);
        addFormField(formPanel, "PIN*:", pinField);
        //addFormField(formPanel, "Full Name*:", fullNameField);
        //addFormField(formPanel, "Date of Birth:", dobField);
        //addFormField(formPanel, "Gender:", genderField);
        //addFormField(formPanel, "Email:", emailField);
        //addFormField(formPanel, "Mother's Name:", motherField);
        //addFormField(formPanel, "Father's Name:", fatherField);
        //addFormField(formPanel, "National ID:", nidField);
        //addFormField(formPanel, "Birth Certificate:", birthCertField);
        //addFormField(formPanel, "Permanent Address:", permAddrField);
        //addFormField(formPanel, "Present Address:", presAddrField);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        buttonPanel.setOpaque(false);

        JButton registerBtn = createStyledButton("Register", new Color(76, 175, 80), 16);
        JButton backBtn = createStyledButton("Back", new Color(158, 158, 158), 16);

        registerBtn.addActionListener(e -> {
            String username = usernameField.getText().trim();
            String phone = phoneField.getText().trim();
            String pin = new String(pinField.getPassword());
           // String fullName = fullNameField.getText().trim();

            if (username.isEmpty() || phone.isEmpty() || pin.isEmpty() ) {
                showError("Please fill all required fields (marked with *)");
                return;
            }

            if (findUserByPhone(phone) != null) {
                showError("Phone number already registered");
                return;
            }

            if (pin.length() < 4) {
                showError("PIN must be at least 4 characters");
                return;
            }

            User user = new RegularUser(
                    username, phone, pin,
                    //dobField.getText().trim(),
                    //genderField.getText().trim(),
                    emailField.getText().trim(),
                    //motherField.getText().trim(),
                   // fatherField.getText().trim(),
                    nidField.getText().trim()
                   // birthCertField.getText().trim(),
                  //  permAddrField.getText().trim(),
                   // presAddrField.getText().trim()
            );

            users.add(user);
            saveUsers();
            Logger.log("New user created: " + user.getUsername());

            // Clear fields
            for (Component comp : formPanel.getComponents()) {
                if (comp instanceof JTextField) ((JTextField) comp).setText("");
                else if (comp instanceof JPasswordField) ((JPasswordField) comp).setText("");
            }

            showSuccess("Account created successfully!");
            cardLayout.show(cardPanel, "Welcome");
        });

        backBtn.addActionListener(e -> cardLayout.show(cardPanel, "Welcome"));

        buttonPanel.add(backBtn);
        buttonPanel.add(registerBtn);

        panel.add(title, BorderLayout.NORTH);
        panel.add(new JScrollPane(formPanel), BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        cardPanel.add(panel, "Register");
    }

    private void createUserMenuPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(240, 248, 255));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        welcomeLabel = new JLabel("", JLabel.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 22));
        welcomeLabel.setForeground(new Color(0, 102, 204));

        JPanel buttonPanel = new JPanel(new GridLayout(0, 2, 15, 15));
        buttonPanel.setBackground(new Color(240, 248, 255));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        String[] buttonLabels = {
                "Show Details", "Check Balance", "Deposit Money", "Withdraw Money",
                "Transfer Money", "Transaction History", "Convocation Payment",
                "Bill Payment", "Update Information", "Change PIN", "Delete Account", "Logout"
        };

        Color[] buttonColors = {
                new Color(102, 187, 106), new Color(129, 199, 132),
                new Color(56, 142, 60), new Color(46, 125, 50),
                new Color(0, 150, 136), new Color(0, 137, 123),
                new Color(0, 188, 212), new Color(0, 172, 193),
                new Color(121, 134, 203), new Color(92, 107, 192),
                new Color(229, 57, 53), new Color(239, 83, 80)
        };

        for (int i = 0; i < buttonLabels.length; i++) {
            JButton button = createStyledButton(buttonLabels[i], buttonColors[i], 14);
            button.addActionListener(new UserMenuListener(buttonLabels[i]));
            buttonPanel.add(button);
        }

        panel.add(welcomeLabel, BorderLayout.NORTH);
        panel.add(new JScrollPane(buttonPanel), BorderLayout.CENTER);

        cardPanel.add(panel, "UserMenu");
    }

    private void createBalancePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(240, 248, 255));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("Account Balance", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setForeground(new Color(0, 102, 204));

        JLabel balanceLabel = new JLabel("", JLabel.CENTER);
        balanceLabel.setFont(new Font("Arial", Font.BOLD, 36));
        balanceLabel.setForeground(new Color(46, 125, 50));

        JButton backBtn = createStyledButton("Back to Menu", new Color(158, 158, 158), 16);

        panel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                if (currentUser != null) {
                    balanceLabel.setText("BDT " + String.format("%,.2f", currentUser.getBalance()));
                }
            }
        });

        backBtn.addActionListener(e -> cardLayout.show(cardPanel, "UserMenu"));

        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBackground(new Color(240, 248, 255));
        centerPanel.add(balanceLabel);

        panel.add(title, BorderLayout.NORTH);
        panel.add(centerPanel, BorderLayout.CENTER);

        JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        southPanel.setBackground(new Color(240, 248, 255));
        southPanel.add(backBtn);
        panel.add(southPanel, BorderLayout.SOUTH);

        cardPanel.add(panel, "Balance");
    }

    private void createDepositPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(240, 248, 255));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("Deposit Money", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setForeground(new Color(0, 102, 204));

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(new Color(240, 248, 255));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);

        JLabel amountLabel = new JLabel("Amount (BDT):");
        amountLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        JTextField amountField = new JTextField(15);
        amountField.setFont(new Font("Arial", Font.PLAIN, 16));

        JButton depositBtn = createStyledButton("Deposit", new Color(56, 142, 60), 16);
        JButton backBtn = createStyledButton("Back", new Color(158, 158, 158), 16);

        depositBtn.addActionListener(e -> {
            try {
                double amount = Double.parseDouble(amountField.getText());
                if (amount <= 0) {
                    showError("Amount must be positive");
                    return;
                }

                currentUser.setBalance(currentUser.getBalance() + amount);
                currentUser.transactions.add(new Transaction("Deposit", amount, "Cash deposit"));
                saveUsers();
                Logger.log(currentUser.getUsername() + " deposited BDT " + amount);

                showSuccess(String.format("Successfully deposited BDT %,.2f", amount));
                amountField.setText("");
                cardLayout.show(cardPanel, "UserMenu");
            } catch (NumberFormatException ex) {
                showError("Please enter a valid amount");
            }
        });

        backBtn.addActionListener(e -> {
            amountField.setText("");
            cardLayout.show(cardPanel, "UserMenu");
        });

        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(amountLabel, gbc);

        gbc.gridx = 1;
        formPanel.add(amountField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(backBtn, gbc);

        gbc.gridx = 1;
        formPanel.add(depositBtn, gbc);

        panel.add(title, BorderLayout.NORTH);
        panel.add(formPanel, BorderLayout.CENTER);

        cardPanel.add(panel, "Deposit");
    }

    private void createWithdrawPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(240, 248, 255));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("Withdraw Money", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setForeground(new Color(0, 102, 204));

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(new Color(240, 248, 255));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);

        JLabel pinLabel = new JLabel("Enter PIN:");
        pinLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        JPasswordField pinField = new JPasswordField(15);
        pinField.setFont(new Font("Arial", Font.PLAIN, 16));

        JLabel amountLabel = new JLabel("Amount (BDT):");
        amountLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        JTextField amountField = new JTextField(15);
        amountField.setFont(new Font("Arial", Font.PLAIN, 16));

        JButton withdrawBtn = createStyledButton("Withdraw", new Color(46, 125, 50), 16);
        JButton backBtn = createStyledButton("Back", new Color(158, 158, 158), 16);

        withdrawBtn.addActionListener(e -> {
            String pin = new String(pinField.getPassword());
            if (!currentUser.getPin().equals(pin)) {
                showError("Incorrect PIN");
                return;
            }

            try {
                double amount = Double.parseDouble(amountField.getText());
                if (amount <= 0) {
                    showError("Amount must be positive");
                    return;
                }

                double newBalance = currentUser.getBalance() - amount;
                if (newBalance < MIN_BALANCE) {
                    showError(String.format("Withdrawal denied. Minimum balance BDT %,.2f required", MIN_BALANCE));
                    return;
                }

                currentUser.setBalance(newBalance);
                currentUser.transactions.add(new Transaction("Withdraw", amount, "Self withdrawal"));
                saveUsers();
                Logger.log(currentUser.getUsername() + " withdrew BDT " + amount);

                showSuccess(String.format("Successfully withdrew BDT %,.2f", amount));
                pinField.setText("");
                amountField.setText("");
                cardLayout.show(cardPanel, "UserMenu");
            } catch (NumberFormatException ex) {
                showError("Please enter a valid amount");
            }
        });

        backBtn.addActionListener(e -> {
            pinField.setText("");
            amountField.setText("");
            cardLayout.show(cardPanel, "UserMenu");
        });

        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(pinLabel, gbc);

        gbc.gridx = 1;
        formPanel.add(pinField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(amountLabel, gbc);

        gbc.gridx = 1;
        formPanel.add(amountField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(backBtn, gbc);

        gbc.gridx = 1;
        formPanel.add(withdrawBtn, gbc);

        panel.add(title, BorderLayout.NORTH);
        panel.add(formPanel, BorderLayout.CENTER);

        cardPanel.add(panel, "Withdraw");
    }

    private void createTransferPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(240, 248, 255));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("Transfer Money", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setForeground(new Color(0, 102, 204));

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(new Color(240, 248, 255));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);

        JLabel pinLabel = new JLabel("Enter PIN:");
        pinLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        JPasswordField pinField = new JPasswordField(15);
        pinField.setFont(new Font("Arial", Font.PLAIN, 16));

        JLabel phoneLabel = new JLabel("Recipient Phone:");
        phoneLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        JTextField phoneField = new JTextField(15);
        phoneField.setFont(new Font("Arial", Font.PLAIN, 16));

        JLabel amountLabel = new JLabel("Amount (BDT):");
        amountLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        JTextField amountField = new JTextField(15);
        amountField.setFont(new Font("Arial", Font.PLAIN, 16));

        JButton transferBtn = createStyledButton("Transfer", new Color(0, 150, 136), 16);
        JButton backBtn = createStyledButton("Back", new Color(158, 158, 158), 16);

        transferBtn.addActionListener(e -> {
            String pin = new String(pinField.getPassword());
            if (!currentUser.getPin().equals(pin)) {
                showError("Incorrect PIN");
                return;
            }

            try {
                double amount = Double.parseDouble(amountField.getText());
                if (amount <= 0) {
                    showError("Amount must be positive");
                    return;
                }

                User receiver = findUserByPhone(phoneField.getText().trim());
                if (receiver == null) {
                    showError("Recipient not found");
                    return;
                }

                if (receiver.getPhoneNumber().equals(currentUser.getPhoneNumber())) {
                    showError("Cannot transfer to yourself");
                    return;
                }

                double newBalance = currentUser.getBalance() - amount;
                if (newBalance < MIN_BALANCE) {
                    showError(String.format("Transfer denied. Keep at least BDT %,.2f", MIN_BALANCE));
                    return;
                }

                currentUser.setBalance(newBalance);
                receiver.setBalance(receiver.getBalance() + amount);

                currentUser.transactions.add(new Transaction("Transfer Out", amount, "To " + receiver.getPhoneNumber()));
                receiver.transactions.add(new Transaction("Transfer In", amount, "From " + currentUser.getPhoneNumber()));

                saveUsers();
                Logger.log(currentUser.getUsername() + " transferred BDT " + amount + " to " + receiver.getUsername());

                showSuccess(String.format("Successfully transferred BDT %,.2f to %s", amount, receiver.getPhoneNumber()));
                pinField.setText("");
                phoneField.setText("");
                amountField.setText("");
                cardLayout.show(cardPanel, "UserMenu");
            } catch (NumberFormatException ex) {
                showError("Please enter a valid amount");
            }
        });

        backBtn.addActionListener(e -> {
            pinField.setText("");
            phoneField.setText("");
            amountField.setText("");
            cardLayout.show(cardPanel, "UserMenu");
        });

        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(pinLabel, gbc);

        gbc.gridx = 1;
        formPanel.add(pinField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(phoneLabel, gbc);

        gbc.gridx = 1;
        formPanel.add(phoneField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(amountLabel, gbc);

        gbc.gridx = 1;
        formPanel.add(amountField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(backBtn, gbc);

        gbc.gridx = 1;
        formPanel.add(transferBtn, gbc);

        panel.add(title, BorderLayout.NORTH);
        panel.add(formPanel, BorderLayout.CENTER);

        cardPanel.add(panel, "Transfer");
    }

    private void createTransactionHistoryPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(240, 248, 255));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("Transaction History", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setForeground(new Color(0, 102, 204));

        transactionArea = new JTextArea();
        transactionArea.setEditable(false);
        transactionArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        transactionArea.setBackground(new Color(255, 255, 255));
        JScrollPane scrollPane = new JScrollPane(transactionArea);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));

        JButton refreshBtn = createStyledButton("Refresh", new Color(0, 137, 123), 16);
        JButton backBtn = createStyledButton("Back", new Color(158, 158, 158), 16);

        refreshBtn.addActionListener(e -> {
            String pin = JOptionPane.showInputDialog(this, "Enter PIN to view transactions:");
            if (pin == null) return;

            if (!currentUser.getPin().equals(pin)) {
                showError("Incorrect PIN");
                return;
            }

            if (currentUser.transactions.isEmpty()) {
                transactionArea.setText("No transactions yet.");
            } else {
                StringBuilder sb = new StringBuilder();
                for (Transaction t : currentUser.transactions) {
                    sb.append(t.toString()).append("\n\n");
                }
                transactionArea.setText(sb.toString());
            }
        });

        backBtn.addActionListener(e -> cardLayout.show(cardPanel, "UserMenu"));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setBackground(new Color(240, 248, 255));
        buttonPanel.add(refreshBtn);
        buttonPanel.add(backBtn);

        panel.add(title, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        cardPanel.add(panel, "TransactionHistory");
    }

    private void createConvocationPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(240, 248, 255));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("Convocation Payment", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setForeground(new Color(0, 102, 204));

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(new Color(240, 248, 255));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);

        JLabel pinLabel = new JLabel("Enter PIN:");
        pinLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        JPasswordField pinField = new JPasswordField(15);
        pinField.setFont(new Font("Arial", Font.PLAIN, 16));

        JLabel amountLabel = new JLabel("Fee Amount (BDT):");
        amountLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        JTextField amountField = new JTextField(15);
        amountField.setFont(new Font("Arial", Font.PLAIN, 16));

        JButton payBtn = createStyledButton("Pay Fee", new Color(0, 172, 193), 16);
        JButton backBtn = createStyledButton("Back", new Color(158, 158, 158), 16);

        payBtn.addActionListener(e -> {
            String pin = new String(pinField.getPassword());
            if (!currentUser.getPin().equals(pin)) {
                showError("Incorrect PIN");
                return;
            }

            try {
                double amount = Double.parseDouble(amountField.getText());
                if (amount <= 0) {
                    showError("Amount must be positive");
                    return;
                }

                double newBalance = currentUser.getBalance() - amount;
                if (newBalance < MIN_BALANCE) {
                    showError(String.format("Payment denied. Keep at least BDT %,.2f", MIN_BALANCE));
                    return;
                }

                currentUser.setBalance(newBalance);
                currentUser.transactions.add(new Transaction("Convocation Payment", amount, "University convocation fee"));
                saveUsers();
                Logger.log(currentUser.getUsername() + " paid convocation fee BDT " + amount);

                showSuccess(String.format("Successfully paid convocation fee of BDT %,.2f", amount));
                pinField.setText("");
                amountField.setText("");
                cardLayout.show(cardPanel, "UserMenu");
            } catch (NumberFormatException ex) {
                showError("Please enter a valid amount");
            }
        });

        backBtn.addActionListener(e -> {
            pinField.setText("");
            amountField.setText("");
            cardLayout.show(cardPanel, "UserMenu");
        });

        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(pinLabel, gbc);

        gbc.gridx = 1;
        formPanel.add(pinField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(amountLabel, gbc);

        gbc.gridx = 1;
        formPanel.add(amountField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(backBtn, gbc);

        gbc.gridx = 1;
        formPanel.add(payBtn, gbc);

        panel.add(title, BorderLayout.NORTH);
        panel.add(formPanel, BorderLayout.CENTER);

        cardPanel.add(panel, "Convocation");
    }

    private void createBillPaymentPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(240, 248, 255));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("Bill Payment", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setForeground(new Color(0, 102, 204));

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(new Color(240, 248, 255));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);

        JLabel pinLabel = new JLabel("Enter PIN:");
        pinLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        JPasswordField pinField = new JPasswordField(15);
        pinField.setFont(new Font("Arial", Font.PLAIN, 16));

        JLabel billerLabel = new JLabel("Biller Name:");
        billerLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        JTextField billerField = new JTextField(15);
        billerField.setFont(new Font("Arial", Font.PLAIN, 16));

        JLabel amountLabel = new JLabel("Amount (BDT):");
        amountLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        JTextField amountField = new JTextField(15);
        amountField.setFont(new Font("Arial", Font.PLAIN, 16));

        JButton payBtn = createStyledButton("Pay Bill", new Color(0, 172, 193), 16);
        JButton backBtn = createStyledButton("Back", new Color(158, 158, 158), 16);

        payBtn.addActionListener(e -> {
            String pin = new String(pinField.getPassword());
            if (!currentUser.getPin().equals(pin)) {
                showError("Incorrect PIN");
                return;
            }

            try {
                double amount = Double.parseDouble(amountField.getText());
                if (amount <= 0) {
                    showError("Amount must be positive");
                    return;
                }

                double newBalance = currentUser.getBalance() - amount;
                if (newBalance < MIN_BALANCE) {
                    showError(String.format("Payment denied. Keep at least BDT %,.2f", MIN_BALANCE));
                    return;
                }

                currentUser.setBalance(newBalance);
                currentUser.transactions.add(new Transaction("Bill Payment", amount, "Biller: " + billerField.getText()));
                saveUsers();
                Logger.log(currentUser.getUsername() + " paid bill (" + billerField.getText() + ") BDT " + amount);

                showSuccess(String.format("Successfully paid bill to %s for BDT %,.2f",
                        billerField.getText(), amount));
                pinField.setText("");
                billerField.setText("");
                amountField.setText("");
                cardLayout.show(cardPanel, "UserMenu");
            } catch (NumberFormatException ex) {
                showError("Please enter a valid amount");
            }
        });

        backBtn.addActionListener(e -> {
            pinField.setText("");
            billerField.setText("");
            amountField.setText("");
            cardLayout.show(cardPanel, "UserMenu");
        });

        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(pinLabel, gbc);

        gbc.gridx = 1;
        formPanel.add(pinField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(billerLabel, gbc);

        gbc.gridx = 1;
        formPanel.add(billerField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(amountLabel, gbc);

        gbc.gridx = 1;
        formPanel.add(amountField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(backBtn, gbc);

        gbc.gridx = 1;
        formPanel.add(payBtn, gbc);

        panel.add(title, BorderLayout.NORTH);
        panel.add(formPanel, BorderLayout.CENTER);

        cardPanel.add(panel, "BillPayment");
    }

    // Update Info
    private void createUpdateInfoPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(new Color(240, 248, 255));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel title = new JLabel("Update Information", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setForeground(new Color(0, 102, 204));

        JPanel form = new JPanel(new GridLayout(0, 2, 10, 10));
        form.setBackground(new Color(240, 248, 255));

        JTextField fullName = new JTextField();
        JTextField email = new JTextField();
        JTextField dob = new JTextField();
        JTextField gender = new JTextField();
        JTextField mother = new JTextField();
        JTextField father = new JTextField();
        JTextField nid = new JTextField();
        JTextField birth = new JTextField();
        JTextField permAddr = new JTextField();
        JTextField presAddr = new JTextField();

        addFormField(form, "Full Name:", fullName);
        addFormField(form, "Email:", email);
        addFormField(form, "Date of Birth:", dob);
        addFormField(form, "Gender:", gender);
        addFormField(form, "Mother's Name:", mother);
        addFormField(form, "Father's Name:", father);
        addFormField(form, "National ID:", nid);
        addFormField(form, "Birth Certificate:", birth);
        addFormField(form, "Permanent Address:", permAddr);
        addFormField(form, "Present Address:", presAddr);

        // Pre-fill on show
        panel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                if (currentUser != null) {
                    fullName.setText(currentUser.getFullName());
                    email.setText(currentUser.getEmail());
                    dob.setText(currentUser.getDob());
                    gender.setText(currentUser.getGender());
                    mother.setText(currentUser.getMotherName());
                    father.setText(currentUser.getFatherName());
                    nid.setText(currentUser.getNationalId());
                    birth.setText(currentUser.getBirthCertificate());
                    permAddr.setText(currentUser.getPermanentAddress());
                    presAddr.setText(currentUser.getPresentAddress());
                }
            }
        });

        JPanel south = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        south.setBackground(new Color(240, 248, 255));
        JButton back = createStyledButton("Back", new Color(158, 158, 158), 16);
        JButton save = createStyledButton("Save Changes", new Color(121, 134, 203), 16);

        save.addActionListener(e -> {
            String pin = JOptionPane.showInputDialog(this, "Enter PIN to confirm changes:");
            if (pin == null) return;
            if (!Objects.equals(currentUser.getPin(), pin)) {
                showError("Incorrect PIN");
                return;
            }
            currentUser.setFullName(fullName.getText().trim());
            currentUser.setEmail(email.getText().trim());
            currentUser.setDob(dob.getText().trim());
            currentUser.setGender(gender.getText().trim());
            currentUser.setMotherName(mother.getText().trim());
            currentUser.setFatherName(father.getText().trim());
            currentUser.setNationalId(nid.getText().trim());
            currentUser.setBirthCertificate(birth.getText().trim());
            currentUser.setPermanentAddress(permAddr.getText().trim());
            currentUser.setPresentAddress(presAddr.getText().trim());
            saveUsers();
            Logger.log(currentUser.getUsername() + " updated profile information");
            showSuccess("Information updated successfully.");
            cardLayout.show(cardPanel, "UserMenu");
        });

        back.addActionListener(e -> cardLayout.show(cardPanel, "UserMenu"));

        south.add(back);
        south.add(save);

        panel.add(title, BorderLayout.NORTH);
        panel.add(new JScrollPane(form), BorderLayout.CENTER);
        panel.add(south, BorderLayout.SOUTH);

        cardPanel.add(panel, "UpdateInfo");
    }

    // Change PIN
    private void createChangePinPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(240, 248, 255));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("Change PIN", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setForeground(new Color(0, 102, 204));

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(new Color(240, 248, 255));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 12, 12, 12);
        gbc.gridx = 0; gbc.gridy = 0;

        JLabel oldLbl = new JLabel("Old PIN:");
        JPasswordField oldPin = new JPasswordField(15);
        JLabel newLbl = new JLabel("New PIN:");
        JPasswordField newPin = new JPasswordField(15);
        JLabel confLbl = new JLabel("Confirm New PIN:");
        JPasswordField confPin = new JPasswordField(15);

        oldLbl.setFont(new Font("Arial", Font.PLAIN, 16));
        newLbl.setFont(new Font("Arial", Font.PLAIN, 16));
        confLbl.setFont(new Font("Arial", Font.PLAIN, 16));
        oldPin.setFont(new Font("Arial", Font.PLAIN, 16));
        newPin.setFont(new Font("Arial", Font.PLAIN, 16));
        confPin.setFont(new Font("Arial", Font.PLAIN, 16));

        form.add(oldLbl, gbc); gbc.gridx = 1; form.add(oldPin, gbc);
        gbc.gridx = 0; gbc.gridy++; form.add(newLbl, gbc); gbc.gridx = 1; form.add(newPin, gbc);
        gbc.gridx = 0; gbc.gridy++; form.add(confLbl, gbc); gbc.gridx = 1; form.add(confPin, gbc);

        JPanel south = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        south.setBackground(new Color(240, 248, 255));
        JButton back = createStyledButton("Back", new Color(158, 158, 158), 16);
        JButton save = createStyledButton("Change PIN", new Color(92, 107, 192), 16);

        save.addActionListener(e -> {
            String old = new String(oldPin.getPassword());
            String np = new String(newPin.getPassword());
            String cp = new String(confPin.getPassword());

            if (!Objects.equals(currentUser.getPin(), old)) {
                showError("Old PIN is incorrect");
                return;
            }
            if (np.length() < 4) {
                showError("New PIN must be at least 4 characters");
                return;
            }
            if (!Objects.equals(np, cp)) {
                showError("New PIN and confirmation do not match");
                return;
            }
            currentUser.setPin(np);
            saveUsers();
            Logger.log(currentUser.getUsername() + " changed PIN");
            showSuccess("PIN changed successfully.");
            oldPin.setText(""); newPin.setText(""); confPin.setText("");
            cardLayout.show(cardPanel, "UserMenu");
        });

        back.addActionListener(e -> {
            oldPin.setText(""); newPin.setText(""); confPin.setText("");
            cardLayout.show(cardPanel, "UserMenu");
        });

        south.add(back);
        south.add(save);

        panel.add(title, BorderLayout.NORTH);
        panel.add(form, BorderLayout.CENTER);
        panel.add(south, BorderLayout.SOUTH);

        cardPanel.add(panel, "ChangePIN");
    }

    //Delete Account
    private void createDeleteAccountPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(240, 248, 255));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("Delete Account", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setForeground(new Color(229, 57, 53));

        JTextArea info = new JTextArea(
                "Warning: This action will permanently delete your account and all data.\n" +
                        "You will not be able to recover it.");
        info.setLineWrap(true);
        info.setWrapStyleWord(true);
        info.setEditable(false);
        info.setFont(new Font("Arial", Font.PLAIN, 14));
        info.setBackground(new Color(240, 248, 255));

        JPanel south = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        south.setBackground(new Color(240, 248, 255));
        JButton back = createStyledButton("Back", new Color(158, 158, 158), 16);
        JButton delete = createStyledButton("Delete My Account", new Color(229, 57, 53), 16);

        delete.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to permanently delete this account?",
                    "Confirm Deletion", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (confirm != JOptionPane.YES_OPTION) return;

            String pin = JOptionPane.showInputDialog(this, "Enter PIN to confirm deletion:");
            if (pin == null) return;
            if (!Objects.equals(currentUser.getPin(), pin)) {
                showError("Incorrect PIN");
                return;
            }

            users.remove(currentUser);
            saveUsers();
            Logger.log("Account deleted for user: " + currentUser.getUsername());
            currentUser = null;
            showSuccess("Account deleted successfully.");
            cardLayout.show(cardPanel, "Welcome");
        });

        back.addActionListener(e -> cardLayout.show(cardPanel, "UserMenu"));

        south.add(back);
        south.add(delete);

        panel.add(title, BorderLayout.NORTH);
        panel.add(info, BorderLayout.CENTER);
        panel.add(south, BorderLayout.SOUTH);

        cardPanel.add(panel, "DeleteAccount");
    }

    // User Details
    private void createUserDetailsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(240, 248, 255));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("User Details", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setForeground(new Color(0, 102, 204));

        JTextArea details = new JTextArea();
        details.setEditable(false);
        details.setFont(new Font("Monospaced", Font.PLAIN, 14));
        JScrollPane scroll = new JScrollPane(details);

        panel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                if (currentUser == null) return;
                String fmt = ""
                        + "Username        : %s%n"
                        + "Full Name       : %s%n"
                        + "Phone Number    : %s%n"
                        + "Email           : %s%n"
                        + "DOB             : %s%n"
                        + "Gender          : %s%n"
                        + "Mother's Name   : %s%n"
                        + "Father's Name   : %s%n"
                        + "National ID     : %s%n"
                        + "Birth Certificate: %s%n"
                        + "Permanent Addr  : %s%n"
                        + "Present Addr    : %s%n"
                        + "Balance         : BDT %,.2f%n";
                details.setText(String.format(fmt,
                        currentUser.getUsername(),
                        currentUser.getFullName(),
                        currentUser.getPhoneNumber(),
                        nonNull(currentUser.getEmail()),
                        nonNull(currentUser.getDob()),
                        nonNull(currentUser.getGender()),
                        nonNull(currentUser.getMotherName()),
                        nonNull(currentUser.getFatherName()),
                        nonNull(currentUser.getNationalId()),
                        nonNull(currentUser.getBirthCertificate()),
                        nonNull(currentUser.getPermanentAddress()),
                        nonNull(currentUser.getPresentAddress()),
                        currentUser.getBalance()
                ));
            }
        });

        JButton backBtn = createStyledButton("Back", new Color(158, 158, 158), 16);
        backBtn.addActionListener(e -> cardLayout.show(cardPanel, "UserMenu"));

        JPanel south = new JPanel(new FlowLayout(FlowLayout.CENTER));
        south.setBackground(new Color(240, 248, 255));
        south.add(backBtn);

        panel.add(title, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);
        panel.add(south, BorderLayout.SOUTH);

        cardPanel.add(panel, "UserDetails");
    }

    // Helpers & utilities

    private static String nonNull(String s) { return (s == null ? "" : s); }

    private void addFormField(JPanel parent, String label, JComponent field) {
        JLabel l = new JLabel(label);
        l.setFont(new Font("Arial", Font.PLAIN, 16));
        field.setFont(new Font("Arial", Font.PLAIN, 16));
        parent.add(l);
        parent.add(field);
    }

    private JButton createStyledButton(String text, Color bg, int fontSize) {
        JButton b = new JButton(text);
        b.setFocusPainted(false);
        b.setFont(new Font("Arial", Font.BOLD, fontSize));
        b.setBackground(bg);
        b.setForeground(Color.WHITE);
        b.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { b.setBackground(b.getBackground().darker()); }
            @Override public void mouseExited(MouseEvent e) { b.setBackground(bg); }
        });
        return b;
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void showSuccess(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    private User findUserByPhone(String phone) {
        for (User u : users) {
            if (u.getPhoneNumber().equals(phone)) return u;
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private void loadUsers() {
        File f = new File(DATA_FILE);
        if (!f.exists()) {
            users = new ArrayList<>();
            return;
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f))) {
            Object obj = ois.readObject();
            if (obj instanceof List<?>) {
                users = (List<User>) obj;
            } else {
                users = new ArrayList<>();
            }
        } catch (Exception e) {
            users = new ArrayList<>();
        }
    }

    private void saveUsers() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
            oos.writeObject(users);
        } catch (IOException e) {
            showError("Failed to save data: " + e.getMessage());
        }
    }

    // Route button clicks from the user menu
    private class UserMenuListener implements ActionListener {
        private final String action;
        UserMenuListener(String action) { this.action = action; }
        @Override
        public void actionPerformed(ActionEvent e) {
            if (currentUser == null) {
                showError("Please login first.");
                cardLayout.show(cardPanel, "Welcome");
                return;
            }
            switch (action) {
                case "Show Details":           cardLayout.show(cardPanel, "UserDetails"); break;
                case "Check Balance":          cardLayout.show(cardPanel, "Balance"); break;
                case "Deposit Money":          cardLayout.show(cardPanel, "Deposit"); break;
                case "Withdraw Money":         cardLayout.show(cardPanel, "Withdraw"); break;
                case "Transfer Money":         cardLayout.show(cardPanel, "Transfer"); break;
                case "Transaction History":    cardLayout.show(cardPanel, "TransactionHistory"); break;
                case "Convocation Payment":    cardLayout.show(cardPanel, "Convocation"); break;
                case "Bill Payment":           cardLayout.show(cardPanel, "BillPayment"); break;
                case "Update Information":     cardLayout.show(cardPanel, "UpdateInfo"); break;
                case "Change PIN":             cardLayout.show(cardPanel, "ChangePIN"); break;
                case "Delete Account":         cardLayout.show(cardPanel, "DeleteAccount"); break;
                case "Logout":
                    Logger.log("User logged out: " + currentUser.getUsername());
                    currentUser = null;
                    showSuccess("Logged out successfully.");
                    cardLayout.show(cardPanel, "Welcome");
                    break;
                default: break;
            }
        }
    }


    // Inner classes (Serializable)


    private static class Transaction implements Serializable {
        private static final long serialVersionUID = 1L;
        private final String type;
        private final double amount;
        private final String description;
        private final long timestamp;

        Transaction(String type, double amount, String description) {
            this.type = type;
            this.amount = amount;
            this.description = description;
            this.timestamp = System.currentTimeMillis();
        }

        @Override
        public String toString() {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return String.format("[%s] %-16s BDT %,.2f  |  %s",
                    sdf.format(new Date(timestamp)), type, amount, description);
        }
    }

    private static abstract class User implements Serializable {
        private static final long serialVersionUID = 2L;

        private String username;
        private String phoneNumber;
        private String pin;
        private String fullName;

        private String dob;
        private String gender;
        private String email;

        private String motherName;
        private String fatherName;

        private String nationalId;
        private String birthCertificate;

        private String permanentAddress;
        private String presentAddress;

        private double balance = 0.0;

        List<Transaction> transactions = new ArrayList<>();

        public User(String username, String phoneNumber, String pin) {
            this.username = username;
            this.phoneNumber = phoneNumber;
            this.pin = pin;
            //this.fullName = fullName;
        }

        // Getters/Setters
        public String getUsername() { return username; }
        public String getPhoneNumber() { return phoneNumber; }
        public String getPin() { return pin; }
        public String getFullName() { return fullName; }
        public String getDob() { return dob; }
        public String getGender() { return gender; }
        public String getEmail() { return email; }
        public String getMotherName() { return motherName; }
        public String getFatherName() { return fatherName; }
        public String getNationalId() { return nationalId; }
        public String getBirthCertificate() { return birthCertificate; }
        public String getPermanentAddress() { return permanentAddress; }
        public String getPresentAddress() { return presentAddress; }
        public double getBalance() { return balance; }

        public void setPin(String pin) { this.pin = pin; }
        public void setFullName(String s) { this.fullName = s; }
        public void setDob(String s) { this.dob = s; }
        public void setGender(String s) { this.gender = s; }
        public void setEmail(String s) { this.email = s; }
        public void setMotherName(String s) { this.motherName = s; }
        public void setFatherName(String s) { this.fatherName = s; }
        public void setNationalId(String s) { this.nationalId = s; }
        public void setBirthCertificate(String s) { this.birthCertificate = s; }
        public void setPermanentAddress(String s) { this.permanentAddress = s; }
        public void setPresentAddress(String s) { this.presentAddress = s; }
        public void setBalance(double b) { this.balance = b; }
    }

    private static class RegularUser extends User {
        private static final long serialVersionUID = 1L;
        public RegularUser(String username, String phoneNumber, String pin,
                            String email,
                           String nationalId) {
            super(username, phoneNumber, pin);
            //setDob(dob);
           // setGender(gender);
            setEmail(email);
            //setMotherName(motherName);
            //setFatherName(fatherName);
            setNationalId(nationalId);
           // setBirthCertificate(birthCertificate);
            //setPermanentAddress(permanentAddress);
           // setPresentAddress(presentAddress);
        }
    }

    private static class Logger {
        static void log(String message) {
            try (FileWriter fw = new FileWriter(LOG_FILE, true);
                 BufferedWriter bw = new BufferedWriter(fw);
                 PrintWriter out = new PrintWriter(bw)) {
                String ts = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                out.println(ts + " | " + message);
            } catch (IOException ignored) {}
        }
    }
    // Main
    public static void main(String[] args) {
        SwingUtilities.invokeLater(CardPaymentSystem::new);
    }
}
