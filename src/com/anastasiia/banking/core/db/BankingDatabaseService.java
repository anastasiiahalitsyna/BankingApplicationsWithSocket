package com.anastasiia.banking.core.db;

import com.anastasiia.banking.core.mapper.CustomerToLineMapper;
import com.anastasiia.banking.core.mapper.LineToCustomerMapper;
import com.anastasiia.banking.model.Customer;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class BankingDatabaseService {

    public List<Customer> getCustomers() {
        try {
            return Files.readAllLines(getDatabaseFilePath())
                    .stream()
                    .map(new LineToCustomerMapper())
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Path getDatabaseFilePath() {
        try {
            Path dir = Paths.get(getClass().getResource("./../../../../../").toURI());
            dir = dir.getParent();
            dir = dir.getParent();
            dir = dir.getParent();
            return dir.resolve(Paths.get("resources", "accounts.txt"));
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }


    public Optional<Customer> findCustomer(String accountNumber, String password) {
        return getCustomers()
                .stream()
                .filter(customer -> customer.getAccountNumber().equals(accountNumber)
                        && customer.getPassword().equals(password))
                .findAny();
    }

    public Optional<Customer> findCustomer(String accountNumber) {
        return getCustomers()
                .stream()
                .filter(customer -> customer.getAccountNumber().equals(accountNumber))
                .findAny();
    }

    public void modifyCustomer(Customer customer) {
        List<Customer> customers = getCustomers();
        int targetIndex = -1;
        for (int i = 0; i < customers.size(); i++) {
            if (customer.getAccountNumber().equals(customers.get(i).getAccountNumber())) {
                targetIndex = i;
                break;
            }
        }
        customers.set(targetIndex, customer);
        List<String> lines = customers.stream()
                .map(new CustomerToLineMapper())
                .collect(Collectors.toList());
        try {
            Files.write(getDatabaseFilePath(), lines);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void deleteCustomer(Customer customer) {
        List<Customer> customers = getCustomers();
        customers.removeIf(c -> c.getAccountNumber().equals(customer.getAccountNumber()));

        List<String> lines = customers.stream()
                .map(new CustomerToLineMapper())
                .collect(Collectors.toList());
        try {
            Files.write(getDatabaseFilePath(), lines);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addCustomer(Customer customer) {
        List<Customer> customers = getCustomers();
        customers.add(customer);
        List<String> lines = customers.stream()
                .map(new CustomerToLineMapper())
                .collect(Collectors.toList());
        try {
            Files.write(getDatabaseFilePath(), lines);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
