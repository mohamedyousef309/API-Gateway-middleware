/**
 * GateHandlerFactory - Spring component that creates instances of GateHandler.
 * - Helps inject dependencies like the JTextArea from the GUI.
 */
package com.handle_one_gate.gate1;

import org.springframework.stereotype.Component;
import javax.swing.*;

@Component
public class GateHandlerFactory {

    // Factory method to create a GateHandler instance
    public GateHandler getHandler(JTextArea statusArea, String gateNumber, String action) {
        return new GateHandler(statusArea, gateNumber, action);  // Creating a new instance of GateHandler with JTextArea
    }
}
