/**
 * Gate1 / Gate2 - Specific implementations of the Gate interface.
 * - Encapsulate the logic for opening and closing individual gate units.
 */
package com.handle_one_gate.gate1;

public class Gate2 implements Gate {
    @Override
    public void open() {
        // Logic for opening Gate2
        System.out.println("Gate 2 is now OPEN");
    }

    @Override
    public void close() {
        // Logic for closing Gate2
        System.out.println("Gate 2 is now CLOSED");
    }
}
