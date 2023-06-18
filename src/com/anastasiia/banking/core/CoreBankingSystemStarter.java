package com.anastasiia.banking.core;

import com.anastasiia.banking.core.db.BankingDatabaseService;
import com.anastasiia.banking.core.service.BankingBankerOperations;
import com.anastasiia.banking.core.service.BankingCustomerOperations;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

public class CoreBankingSystemStarter {

    public static void main(String[] args) {
        ServerSocket serverSocket = null;
        Socket socket = null;
        try {
            serverSocket = new ServerSocket(6666);
        } catch (IOException e) {
            System.err.println("Error happened while creating socket: " + e);
            System.exit(-1);
        }
        System.out.println("Initializing socket...");
        System.out.println("Socket parameters: " + serverSocket);
        while (true) {
            try {
                socket = serverSocket.accept();
            } catch (IOException e) {
                System.err.println("Error while accepting socket: " + e);
            }
            System.out.println("Connecting...");
            System.out.println("Connection parameters: " + socket);

            BankingDatabaseService bankingDatabaseService = new BankingDatabaseService();
            BankingCustomerOperations bankingClientOperation = new BankingCustomerOperations(bankingDatabaseService);
            BankingBankerOperations bankingBankerOperations = new BankingBankerOperations(bankingDatabaseService, new Random());
            new BankingThreadPerClient(socket, bankingClientOperation, bankingBankerOperations).start();
        }
    }

}
