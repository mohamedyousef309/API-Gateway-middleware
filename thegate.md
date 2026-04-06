# 🚀 Gate Control Server

A Java-based server that acts as a middleware between clients and a gate controller system. It offers an HTTP interface, a GUI for live monitoring, and uses TCP sockets to send commands to physical gates.

---

## 📁 Project Overview

This project has **three main layers**:
- **GUI Layer**: Allows users to control and monitor gates.
- **HTTP Middleware Layer**: Exposes REST APIs for external clients.
- **Socket Communication Layer**: Sends commands to the actual Gate Controller device via TCP.

---

## ⚙️ Architecture & Class Responsibilities

### 🧩 MainApplication.java
- **Entry point** of the application.
- Starts Spring Boot and initializes the GUI (if not already running).

### 🖥 Main.java
- Builds and displays the **Swing GUI**.
- Manages system tray integration.
- Prevents multiple GUI instances using a lock file and singleton pattern.

### 🌐 ServerControl.java
- Manages the **embedded HTTP server** (`com.sun.net.httpserver.HttpServer`).
- Registers all endpoint handlers.
- Sends TCP commands to the gate controller.
- Logs messages to both GUI and a log file.

### 🔁 GateCommandHandler.java
- Handles pre-defined GET commands (`Open entry gate`, `LED ON`, etc.).
- Maps friendly commands to raw command strings and sends them over socket.

### 🧠 GateHandler.java
- A **generic gate controller** for `/gates/{id}/{open|close}` endpoints.
- Uses the **GateFactory** to get the right gate object (`Gate1`, `Gate2`...).
- Executes `.open()` or `.close()` on that gate.
- Logs updates to the GUI.

### 🏭 GateFactory.java
- Central **factory** that returns a `Gate` instance (`Gate1`, `Gate2`, etc.)
- Supports future expansion (e.g. adding `Gate3`, `Gate4`...).

### 🧩 Gate.java (Interface)
- Defines the standard contract:
  - `open()`
  - `close()`

### 🚪 Gate1.java / Gate2.java
- Each class controls a specific gate.
- Implements the `Gate` interface.

---

## 🧬 UML Class Diagram 

```mermaid
classDiagram
    class MainApplication {
        +main(String[] args)
    }

    class Main {
        +createGUI()
        +getFrameInstance()
    }

    class ServerControl {
        +startServer(httpPort, gatePort)
        +stopServer()
        +isPortAvailable(port)
        +sendRawIntegerCommandToGateController(value)
    }

    class GateHandler {
        +handle(HttpExchange exchange)
    }

    class GateCommandHandler {
        +handle(HttpExchange exchange)
    }

    class GateFactory {
        +getGate(gateNumber)
    }

    class Gate {
        <<interface>>
        +open()
        +close()
    }

    class Gate1 {
        +open()
        +close()
    }

    class Gate2 {
        +open()
        +close()
    }

    MainApplication --> Main
    Main --> ServerControl
    ServerControl --> GateCommandHandler
    ServerControl --> GateHandler
    GateHandler --> GateFactory
    GateFactory --> Gate1
    GateFactory --> Gate2
    Gate1 ..|> Gate
    Gate2 ..|> Gate