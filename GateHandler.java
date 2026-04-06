/**
 * GateHandler - A flexible handler for opening or closing gates via the Gate interface.
 * - Takes a gate number and action (open/close).
 * - Uses the GateFactory to obtain the correct gate instance.
 * - Updates the GUI status area with the result.
 * - Ensures Swing UI updates are thread-safe.
 */
package com.handle_one_gate.gate1;

import javax.swing.*;
import java.io.IOException;
import java.io.OutputStream;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;


public class GateHandler implements HttpHandler {

    private final String gateNumber;
    private final String action;
    private final JTextArea statusArea;

    /**
     * Creates a GateHandler instance for a specific gate and action.
     *
     * @param statusArea JTextArea to display status messages in the GUI.
     * @param gateNumber The number of the gate to control (e.g., "1", "2").
     * @param action     The action to perform: "open" or "close".
     */
    public GateHandler(JTextArea statusArea, String gateNumber, String action) {
        this.statusArea = statusArea;
        this.gateNumber = gateNumber;
        this.action = action;
    }

    /**
     * Handles incoming HTTP requests to open or close a gate.
     * It executes the action and sends a plain-text response to the client.
     *
     * @param exchange The HttpExchange object representing the request and response.
     * @throws IOException if there's an issue with output stream or data handling.
     */
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String response;

        Gate gate = GateFactory.getGate(gateNumber);

        if (gate == null) {
            response = "Invalid gate number.";
            exchange.sendResponseHeaders(400, response.getBytes().length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }
            return;
        }

        if ("open".equalsIgnoreCase(action)) {
            gate.open();
            response = "Gate " + gateNumber + " is OPEN.";
            updateStatus("Gate " + gateNumber + " is OPEN.");
        } else if ("close".equalsIgnoreCase(action)) {
            gate.close();
            response = "Gate " + gateNumber + " is CLOSED.";
            updateStatus("Gate " + gateNumber + " is CLOSED.");
        } else {
            response = "Invalid action.";
        }

        exchange.sendResponseHeaders(200, response.getBytes().length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }

    /**
     * Updates the GUI status area with a message.
     * This method is thread-safe and uses SwingUtilities.invokeLater.
     *
     * @param message The message to append to the status area.
     */
    private void updateStatus(String message) {
        SwingUtilities.invokeLater(() -> statusArea.append(message + "\n"));
    }
}