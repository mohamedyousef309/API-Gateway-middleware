// ✅ GateController.java - Replaces native HttpServer with Spring Boot REST Controller
package com.handle_one_gate.gate1;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import javax.annotation.PostConstruct;
import java.io.IOException;

@RestController
public class GateController {

    @GetMapping("/gate/Entry/open")
    public ResponseEntity<String> openEntryGate() {
        return forwardCommand("Open entry gate");
    }

    @GetMapping("/gate/Entry/close")
    public ResponseEntity<String> closeEntryGate() {
        return forwardCommand("Close entry gate");
    }

    @GetMapping("/gate/Exit/open")
    public ResponseEntity<String> openExitGate() {
        return forwardCommand("Open exit gate");
    }

    @GetMapping("/gate/Exit/close")
    public ResponseEntity<String> closeExitGate() {
        return forwardCommand("Close exit gate");
    }

    @GetMapping("/gate/status")
    public ResponseEntity<String> getStatus() {
        return forwardCommand("Get status");
    }

    @GetMapping("/gate/config")
    public ResponseEntity<String> getConfig() {
        return forwardCommand("Get configuration");
    }

    @GetMapping("/LED/ON")
    public ResponseEntity<String> ledOn() {
        return forwardCommand("LED ON");
    }

    @GetMapping("/LED/OFF")
    public ResponseEntity<String> ledOff() {
        return forwardCommand("LED OFF");
    }

    @GetMapping("/gate/send-int")
    public ResponseEntity<String> sendRawInt() {
        boolean success = ServerControl.sendRawIntegerCommandToGateController(101);
        if (success) {
            return ResponseEntity.ok("✅ Integer command sent successfully.");
        } else {
            return ResponseEntity.status(500).body("❌ Failed to send integer command to Gate Controller.");
        }
    }

    @GetMapping("/gate/send")
    public ResponseEntity<String> sendCustomCommand(@RequestParam String cmd) {
        String response = ServerControl.forwardCommandToGate(cmd);
        return ResponseEntity.ok(response);
    }

    private ResponseEntity<String> forwardCommand(String commandText) {
        String response = ServerControl.forwardCommandToGate(commandText);
        return ResponseEntity.ok(response);
    }
}