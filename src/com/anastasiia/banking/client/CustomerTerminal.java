package com.anastasiia.banking.client;

import java.io.*;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

public class CustomerTerminal {

    public static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");

    public static void main(String[] args) {
        Socket clientSocket = connectToSocketServer();

        try (BufferedReader socketReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             PrintWriter socketWriter = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream()), true)) {
            listenCommandLineAndSendToSocketServer(clientSocket, socketReader, socketWriter);
        } catch (IOException e) {
            System.out.println("Error while creating writer/reader: " + e);
            System.exit(-1);
        }


    }

    private static void listenCommandLineAndSendToSocketServer(Socket clientSocket,
                                                               BufferedReader socketReader,
                                                               PrintWriter socketWriter) {
        String socketResponse;
        String commandLineInput;
        Scanner scanner = new Scanner(System.in);
        while (true) {
            try {
                String currentDate = "\u001b[32m" + SIMPLE_DATE_FORMAT.format(new Date());

                do {
                    commandLineInput = scanner.next();
                } while (commandLineInput.trim().isEmpty());

                if (commandLineInput.startsWith("GET_BALANCE")
                        || commandLineInput.startsWith("WITHDRAW_ACCOUNT")
                        || commandLineInput.startsWith("DEPOSIT_ACCOUNT")
                        || commandLineInput.startsWith("TRANSFER_MONEY")) {
                    System.out.printf("%s Sending following command: %s\n", currentDate, commandLineInput);
                    socketWriter.println(commandLineInput);
                } else if ("quit".equals(commandLineInput)) {
                    System.out.println("Finish work...");
                    clientSocket.close();
                    System.exit(0);
                } else {
                    continue;
                }

                socketResponse = socketReader.readLine();
                System.out.printf("%s Response: %s\n", currentDate, socketResponse);

            } catch (IOException e) {
                System.err.println("Input-output error " + e);
                System.exit(-1);
            }
        }
    }

    private static Socket connectToSocketServer() {
        String host = "localhost";
        int port = 0;
        try {
            port = Integer.parseInt("6666");
        } catch (NumberFormatException e) {
            System.out.println("Wrong argument: port");
            System.exit(-1);
        }

        Socket clientSocket = null;
        try {
            clientSocket = new Socket(host, port);
        } catch (UnknownHostException e) {
            System.out.println("Unknown host.");
            System.exit(-1);
        } catch (ConnectException e) {
            System.out.println("Connection rejected.");
            System.exit(-1);
        } catch (IOException e) {
            System.out.println("Input-output error " + e);
            System.exit(-1);
        }
        System.out.println("Connected with... " + clientSocket);
        return clientSocket;
    }
}

