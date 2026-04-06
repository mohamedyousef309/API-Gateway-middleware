package com.handle_one_gate.gate1;

import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.swing.*;

@SpringBootApplication
public class MainApplication {
 public static void main(String[] args) {
  System.setProperty("mainApp", "true");
  SwingUtilities.invokeLater(Main::createGUI); // GUI only - No server here
 }
}