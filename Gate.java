/**
 * Gate - Interface defining a generic gate behavior.
 * - Declares basic operations: open() and close().
 * - Implemented by specific gate types like Gate1, Gate2, etc.
 */
package com.handle_one_gate.gate1;

public interface Gate {
    void open();
    void close();
}
