/**
 * Gate1 / Gate2 - Specific implementations of the Gate interface.
 * - Encapsulate the logic for opening and closing individual gate units.
 */
package com.handle_one_gate.gate1;

public class Gate1 implements Gate {
    @Override
    public void open() {
        // Logic for opening Gate1
        System.out.println("Gate 1 is now OPEN");
    }

    @Override
    public void close() {
        // Logic for closing Gate1
        System.out.println("Gate 1 is now CLOSED");
    }
}
