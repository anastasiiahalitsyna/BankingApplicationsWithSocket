package com.anastasiia.banking.core.service;

import com.anastasiia.banking.core.db.BankingDatabaseService;
import com.anastasiia.banking.model.Customer;

import java.math.BigDecimal;
import java.util.Optional;

public class BankingCustomerOperations {

    private final BankingDatabaseService bankingDatabaseService;

    public BankingCustomerOperations(BankingDatabaseService bankingDatabaseService) {
        this.bankingDatabaseService = bankingDatabaseService;
    }

    public void transfer(String accountFrom, String accountTo, String password, BigDecimal amount) {
        this.withdrawMoney(accountFrom, password, amount);
        this.depositMoney(accountTo, amount);
    }

    public BigDecimal getBalance(String accountNumber, String password) {
        return checkAndGetCustomer(accountNumber, password).getBalance();
    }


    public void withdrawMoney(String accountNumber, String password, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) > 0) {
            Customer customer = checkAndGetCustomer(accountNumber, password);
            if (customer.getBalance().compareTo(amount) < 0) {
                throw new IllegalStateException("Not enough money in the account to withdraw");
            }
            customer.setBalance(customer.getBalance().subtract(amount));
            bankingDatabaseService.modifyCustomer(customer);
        } else {
            throw new IllegalStateException("Amount must be positive");
        }
    }

    public void depositMoney(String accountNumber, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) > 0) {
            Customer customer = checkAndGetCustomer(accountNumber);
            customer.setBalance(customer.getBalance().add(amount));
            bankingDatabaseService.modifyCustomer(customer);
        } else {
            throw new IllegalStateException("Amount must be positive");
        }
    }


    private Customer checkAndGetCustomer(String accountNumber, String password) {
        Optional<Customer> customerOpt = bankingDatabaseService.findCustomer(accountNumber, password);
        if (customerOpt.isEmpty()) {
            throw new IllegalStateException("No Such customer");
        }
        return customerOpt.get();
    }

    private Customer checkAndGetCustomer(String accountNumber) {
        Optional<Customer> customerOpt = bankingDatabaseService.findCustomer(accountNumber);
        if (customerOpt.isEmpty()) {
            throw new IllegalStateException("No Such customer");
        }
        return customerOpt.get();
    }
}
