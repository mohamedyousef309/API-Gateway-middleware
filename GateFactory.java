/**
 * GateFactory - Factory class to create specific Gate implementations.
 * - Returns Gate1, Gate2, etc., based on the provided gate number.
 */
package com.handle_one_gate.gate1;


public class GateFactory {

    /**
     * Returns the Gate implementation corresponding to the specified gate number.
     *
     * @param gateNumber The identifier of the gate (e.g., "1", "2").
     * @return A Gate object like Gate1 or Gate2 that implements the Gate interface.
     * @throws IllegalArgumentException if the gate number is not recognized.
     */
    public static Gate getGate(String gateNumber) {
        switch (gateNumber) {
            case "1":
                return new Gate1();
            case "2":
                return new Gate2();
            // Extend this switch block if more gates are added
            default:
                throw new IllegalArgumentException("Unknown gate number: " + gateNumber);
        }
    }
}