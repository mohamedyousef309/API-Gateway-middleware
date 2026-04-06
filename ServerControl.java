package com.handle_one_gate.gate1;

import javax.swing.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import org.springframework.context.ConfigurableApplicationContext;

public class ServerControl {
    private static JTextArea statusArea;
    private static int gateServerPort = 12345;
    private static final String LOG_FILE_PATH = "server_logs.txt";

    private static String gateIp = "127.0.0.1";
    private static int maxConnectionAttempts = 30;
    private static int retryIntervalMillis = 3000;

    // ✅ Spring Boot context
    private static ConfigurableApplicationContext context;

    public static void setSpringContext(ConfigurableApplicationContext ctx) {
        context = ctx;
    }

    public static void setGateIp(String ip) {
        gateIp = ip;
    }

    public static String getGateIp() {
        return gateIp;
    }

    public static void setGatePort(int port) {
        gateServerPort = port;
    }

    public static void setStatusArea(JTextArea area) {
        statusArea = area;
    }

    public static boolean isPortAvailable(int port) {
        try (ServerSocket socket = new ServerSocket(port)) {
            socket.setReuseAddress(true);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public static void startServer(int httpPort, int gatePort) {
        System.setProperty("server.port", String.valueOf(httpPort));
        logToGUI("🔍 Checking ports before starting server...");

        if (!isPortAvailable(httpPort)) {
            logToGUI("❌ HTTP Port " + httpPort + " is already in use.");
            return;
        }

        gateServerPort = gatePort;
        logToGUI("✅ HTTP Port " + httpPort + " and Gate Port " + gatePort + " are available.");

        connectToGateAtStartup();
        logToGUI("🚀 Server logic ready (Spring Boot now handles HTTP).");
    }

    public static void stopServer() {
        logToGUI("🛑 Server stopped (Spring Boot is shutting down...)");

        // ✅ Shut down Spring Boot
        if (context != null) {
            context.close();
            context = null;
        }
    }

    public static int getGateServerPort() {
        return gateServerPort;
    }

    public static boolean sendRawIntegerCommandToGateController(int value) {
        try (Socket socket = new Socket(gateIp, gateServerPort);
             DataOutputStream dos = new DataOutputStream(socket.getOutputStream())) {

            dos.writeInt(value);
            dos.flush();
            return true;
        } catch (IOException e) {
            logToGUI("❌ Failed to send raw integer: " + e.getMessage());
            return false;
        }
    }

    public static String forwardCommandToGate(String command) {
        try (Socket socket = new Socket(gateIp, gateServerPort);
             PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            writer.println(command);

            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line).append("\n");
            }

            String result = response.toString().trim();
            logToGUI("📨 Sent command: \"" + command + "\" and received: " + result);
            return result;

        } catch (IOException e) {
            String error = "❌ Error forwarding command to gate: " + e.getMessage();
            logToGUI(error);
            return error;
        }
    }

    public static void connectToGateAtStartup() {
        new Thread(() -> {
            logToGUI("⏳ Waiting for Gate Controller to become available...");

            int attempts = 0;
            boolean connected = false;

            while (!connected && attempts < maxConnectionAttempts) {
                try (Socket socket = new Socket(gateIp, gateServerPort)) {
                    logToGUI("✅ Successfully connected to Gate Controller at startup.");
                    connected = true;
                    break;
                } catch (IOException e) {
                    attempts++;
                    try {
                        Thread.sleep(retryIntervalMillis);
                    } catch (InterruptedException ignored) {}
                }
            }

            if (!connected) {
                logToGUI("❌ Could not connect to Gate Controller after " + maxConnectionAttempts + " attempts.");
            }
        }).start();
    }

    public static void logToGUI(String message) {
        String timestampedMessage = "[" + new java.util.Date() + "] " + message;

        if (statusArea != null) {
            SwingUtilities.invokeLater(() -> statusArea.append(timestampedMessage + "\n"));
        }

        logToFile(timestampedMessage);
    }

    private static void logToFile(String message) {
        try (FileWriter fw = new FileWriter(LOG_FILE_PATH, true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {
            out.println(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}