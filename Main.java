package com.handle_one_gate.gate1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.net.URL;

public class Main {
    private static TrayIcon trayIcon;
    private static SystemTray systemTray;
    private static JFrame frame;

    private static ConfigurableApplicationContext springContext; // ✅ لحفظ Spring Context

    private static String gateIp = "127.0.0.1";
    private static String httpPort = "8080";
    private static String gatePort = "12345";

    private static final String LOCK_FILE = "app.lock";

    public static void main(String[] args) {
        System.out.println("⚠ Main should NOT be run directly.");
    }

    public static synchronized JFrame getFrameInstance() {
        return frame;
    }

    public static void createGUI() {
        if (frame != null && frame.isShowing()) {
            System.out.println("⚠ GUI is already running! Ignoring duplicate call.");
            frame.setVisible(true);
            frame.toFront();
            return;
        }

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        frame = new JFrame("🚀 Gate Control Server");
        frame.setSize(600, 400);
        frame.setLayout(new BorderLayout());
        frame.setLocationRelativeTo(null);

        // Set Window Icon
        setApplicationIcon(frame);

        JTextArea statusArea = new JTextArea(10, 50);
        statusArea.setEditable(false);
        statusArea.setFont(new Font("Arial", Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(statusArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("📜 Server Logs"));

        JPanel inputPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        inputPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "🔧 Server Configuration", TitledBorder.CENTER, TitledBorder.TOP));

        JLabel ipLabel = new JLabel("Gate IP:");
        JTextField ipField = new JTextField(gateIp, 15);
        JLabel httpPortLabel = new JLabel("HTTP Port:");
        JTextField httpPortField = new JTextField(httpPort, 6);
        JLabel gatePortLabel = new JLabel("Gate Port:");
        JTextField gatePortField = new JTextField(gatePort, 6);

        inputPanel.add(ipLabel);
        inputPanel.add(ipField);
        inputPanel.add(httpPortLabel);
        inputPanel.add(httpPortField);
        inputPanel.add(gatePortLabel);
        inputPanel.add(gatePortField);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        JButton startButton = new JButton("▶ Start Server");
        JButton stopButton = new JButton("⏹ Stop Server");
        stopButton.setEnabled(false);

        startButton.setFont(new Font("Arial", Font.BOLD, 14));
        stopButton.setFont(new Font("Arial", Font.BOLD, 14));

        startButton.addActionListener(e -> {
            int httpPortVal, gatePortVal;

            try {
                httpPortVal = Integer.parseInt(httpPortField.getText().trim());
                gatePortVal = Integer.parseInt(gatePortField.getText().trim());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Invalid port numbers. Please enter valid integers.", "Port Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String inputIp = ipField.getText().trim();

            // ✅ Set properties before starting Spring Boot
            System.setProperty("server.port", String.valueOf(httpPortVal));
            ServerControl.setGateIp(inputIp);
            ServerControl.setGatePort(gatePortVal);

            // ✅ Start Spring Boot and store context
            new Thread(() -> {
                ServerControl.setSpringContext(SpringApplication.run(MainApplication.class));
            }).start();

            // ✅ Start socket logic
            ServerControl.startServer(httpPortVal, gatePortVal);

            startButton.setEnabled(false);
            stopButton.setEnabled(true);
        });

        stopButton.addActionListener(e -> {
            System.out.println("🔴 Stopping server...");
            ServerControl.stopServer();
            stopSpringApplication();
            startButton.setEnabled(true);
            stopButton.setEnabled(false);
        });

        buttonPanel.add(startButton);
        buttonPanel.add(stopButton);

        frame.add(inputPanel, BorderLayout.NORTH);
        frame.add(scrollPane, BorderLayout.CENTER);
        frame.add(buttonPanel, BorderLayout.SOUTH);

        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.setVisible(true);

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int choice = JOptionPane.showConfirmDialog(
                        frame,
                        "هل أنت متأكد أنك تريد الخروج؟",
                        "تأكيد الخروج",
                        JOptionPane.YES_NO_OPTION
                );

                if (choice == JOptionPane.YES_OPTION) {
                    playExitSound();
                    showExitNotification();
                    ServerControl.stopServer();
                    stopSpringApplication();
                    deleteLockFile();
                    System.out.println("🔴 Exiting application...");
                    System.exit(0);
                } else {
                    System.out.println("🔵 Exit cancelled by user.");
                }
            }
        });

        setupSystemTray();
        ServerControl.setStatusArea(statusArea);
    }

    private static void setupSystemTray() {
        if (!SystemTray.isSupported()) {
            System.out.println("❌ System Tray is not supported on this system.");
            return;
        }

        try {
            systemTray = SystemTray.getSystemTray();
            Image iconImage = getApplicationIcon();

            PopupMenu trayMenu = new PopupMenu();
            MenuItem openItem = new MenuItem("Open");
            openItem.addActionListener(e -> restoreFromTray());
            trayMenu.add(openItem);

            MenuItem exitItem = new MenuItem("Exit");
            exitItem.addActionListener(e -> {
                playExitSound();
                showExitNotification();
                ServerControl.stopServer();
                stopSpringApplication();
                deleteLockFile();
                systemTray.remove(trayIcon);
                System.out.println("🔴 Exiting application...");
                System.exit(0);
            });
            trayMenu.add(exitItem);

            trayIcon = new TrayIcon(iconImage, "Gate Control Server", trayMenu);
            trayIcon.setImageAutoSize(true);
            trayIcon.addActionListener(e -> restoreFromTray());

            if (systemTray.getTrayIcons().length == 0) {
                systemTray.add(trayIcon);
            }

        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    private static void restoreFromTray() {
        if (frame == null) {
            createGUI();
        } else {
            frame.setVisible(true);
            frame.toFront();
            frame.setState(Frame.NORMAL);
        }
    }

    private static void setApplicationIcon(JFrame frame) {
        Image iconImage = getApplicationIcon();
        if (iconImage != null) {
            frame.setIconImage(iconImage);
        } else {
            System.out.println("⚠ Warning: Icon file not found!");
        }
    }

    private static Image getApplicationIcon() {
        try {
            URL iconURL = Main.class.getResource("/images.png");
            if (iconURL != null) {
                return Toolkit.getDefaultToolkit().getImage(iconURL);
            } else {
                System.out.println("❌ Error: Icon file not found in resources.");
                return null;
            }
        } catch (Exception e) {
            System.out.println("❌ Error loading icon: " + e.getMessage());
            return null;
        }
    }

    private static void deleteLockFile() {
        File lock = new File(LOCK_FILE);
        if (lock.exists()) {
            if (lock.delete()) {
                System.out.println("✅ Lock file deleted successfully.");
            } else {
                System.out.println("⚠ Failed to delete lock file.");
            }
        }
    }

    private static void playExitSound() {
        try {
            Toolkit.getDefaultToolkit().beep();
        } catch (Exception e) {
            System.out.println("⚠ Failed to play sound: " + e.getMessage());
        }
    }

    private static void showExitNotification() {
        if (trayIcon != null) {
            trayIcon.displayMessage(
                    "Gate Control Server",
                    "🛑 تم إغلاق التطبيق بنجاح.",
                    TrayIcon.MessageType.INFO
            );
        }
    }

    private static void stopSpringApplication() {
        if (springContext != null) {
            try {
                springContext.close();
                System.out.println("✅ Spring Boot server stopped.");
            } catch (Exception e) {
                System.out.println("⚠ Failed to stop Spring Application: " + e.getMessage());
            }
        }
    }
}