package com.anastasiia.banking.core;

import com.anastasiia.banking.core.service.BankingBankerOperations;
import com.anastasiia.banking.core.service.BankingCustomerOperations;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.net.Socket;

import static java.lang.String.format;

public class BankingThreadPerClient extends Thread {

    private final Socket clientSocket;
    private final BankingCustomerOperations bankingCustomerOperations;
    private final BankingBankerOperations bankingBankerOperations;


    public BankingThreadPerClient(Socket clientSocket,
                                  BankingCustomerOperations bankingCustomerOperations,
                                  BankingBankerOperations bankingBankerOperations) {
        this.clientSocket = clientSocket;
        this.bankingCustomerOperations = bankingCustomerOperations;
        this.bankingBankerOperations = bankingBankerOperations;
    }

    @Override
    public void run() {
        String threadName = Thread.currentThread().getName();
        System.out.printf("Echo Thread: %s Started...%s", threadName, System.lineSeparator());

        try (BufferedReader socketReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             PrintWriter socketWriter = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream()), true);
             clientSocket) {

            String inputLine;
            while ((inputLine = socketReader.readLine()) != null) {
                System.out.println(threadName + "| Received Input: " + inputLine);

                if ("quit".equals(inputLine)) {
                    System.out.println(threadName + "| Closing the client socket: " + clientSocket);
                    clientSocket.close();
                    return;
                }

                String[] commandFields = inputLine.split(",");
                String command = commandFields[0];
                switch (command) {
                    case "GET_BALANCE": {
                        try {
                            BigDecimal balance = bankingCustomerOperations.getBalance(commandFields[1], commandFields[2]);
                            String response = format("Balance is %s", balance);
                            socketWriter.println(response);
                        } catch (RuntimeException re) {
                            socketWriter.println(re.getMessage());
                        }
                        break;
                    }
                    case "WITHDRAW_ACCOUNT": {
                        try {
                            bankingCustomerOperations.withdrawMoney(commandFields[1],
                                    commandFields[2],
                                    new BigDecimal(commandFields[3]));
                            BigDecimal balance = bankingCustomerOperations.getBalance(commandFields[1], commandFields[2]);
                            String response = format("Withdrawal is successful.Amount left in balance is: %s", balance);
                            socketWriter.println(response);
                        } catch (RuntimeException re) {
                            socketWriter.println(re.getMessage());
                        }
                        break;
                    }
                    case "DEPOSIT_ACCOUNT": {
                        try {
                            bankingCustomerOperations.depositMoney(commandFields[1], new BigDecimal(commandFields[3]));
                            BigDecimal balance = bankingCustomerOperations.getBalance(commandFields[1], commandFields[2]);
                            String response = format("Deposit is successful. New Balance is: %s", balance);
                            socketWriter.println(response);
                        } catch (RuntimeException re) {
                            socketWriter.println(re.getMessage());
                        }
                        break;
                    }
                    case "TRANSFER_MONEY": {
                        try {
                            bankingCustomerOperations.transfer(commandFields[1],
                                    commandFields[2],
                                    commandFields[3],
                                    new BigDecimal(commandFields[4]));
                            BigDecimal balance = bankingCustomerOperations.getBalance(commandFields[1], commandFields[3]);
                            String response = format("Transfer is successful.Amount left in balance is: %s", balance);
                            socketWriter.println(response);
                        } catch (RuntimeException re) {
                            socketWriter.println(re.getMessage());
                        }
                        break;
                    }
                    case "MODIFY_CUSTOMER": {
                        try {
                            bankingBankerOperations.modifyCustomerData(commandFields[1],
                                    commandFields[2],
                                    commandFields[3],
                                    commandFields[4]);
                            String response = format("Customer associate with %s account is modified successfully", commandFields[1]);
                            socketWriter.println(response);
                        } catch (RuntimeException re) {
                            socketWriter.println(re.getMessage());
                        }
                        break;
                    }
                    case "ADD_CUSTOMER": {
                        try {
                            bankingBankerOperations.createCustomer(commandFields[1],
                                    commandFields[2],
                                    commandFields[3]);
                            socketWriter.println("Customer has been created successfully");
                        } catch (RuntimeException re) {
                            socketWriter.println(re.getMessage());
                        }
                        break;
                    }
                    default:
                        socketWriter.println("Command is not recognized");
                        break;
                }
            }

        } catch (Exception e) {
            System.err.println(threadName + "| Error, exiting" + e);
        }
    }

}