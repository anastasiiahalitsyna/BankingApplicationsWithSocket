package com.anastasiia.banking.core.service;

import com.anastasiia.banking.core.db.BankingDatabaseService;
import com.anastasiia.banking.model.Customer;

import java.math.BigDecimal;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import static com.anastasiia.banking.utils.StringUtils.ifBlankThenThrow;
import static com.anastasiia.banking.utils.StringUtils.isNotBlank;

public class BankingBankerOperations {

    private static final String ALPHA_NUMERIC_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final String NUMERIC_STRING = "0123456789";

    private static final int MIN_PASSWORD_LENGTH = 5;
    private static final int MAX_PASSWORD_LENGTH = 8;

    private final BankingDatabaseService bankingDatabaseService;
    private final Random random;

    public BankingBankerOperations(BankingDatabaseService bankingDatabaseService, Random random) {
        this.bankingDatabaseService = bankingDatabaseService;
        this.random = random;
    }

    public void modifyCustomerData(String accountNumber, String pesel, String firstName, String lastName) {

        Customer customer = bankingDatabaseService.findCustomer(accountNumber)
                .orElseThrow(() -> new IllegalArgumentException("Incorrect account number"));

        boolean isChangeExist = false;
        if (isNotBlank(pesel) && !customer.getPesel().equals(pesel)) {
            customer.setPesel(pesel);
            isChangeExist = true;
        }

        if (isNotBlank(firstName) && !customer.getName().equals(firstName)) {
            customer.setName(firstName);
            isChangeExist = true;
        }

        if (isNotBlank(lastName) && !customer.getSurname().equals(lastName)) {
            customer.setSurname(lastName);
            isChangeExist = true;
        }

        if (isChangeExist) {
            bankingDatabaseService.modifyCustomer(customer);
        }
    }

    public void createCustomer(String pesel, String firstName, String lastName) {
        ifBlankThenThrow(pesel, new IllegalArgumentException("Pesel is required"));
        ifBlankThenThrow(firstName, new IllegalArgumentException("First name is required"));
        ifBlankThenThrow(lastName, new IllegalArgumentException("Last name is required"));

        String accountNumber = generateAccountNumber();
        String password = generatePassword();

        Customer customer = new Customer();
        customer.setAccountNumber(accountNumber);
        customer.setPassword(password);
        customer.setPesel(pesel);
        customer.setName(firstName);
        customer.setSurname(lastName);
        customer.setBalance(BigDecimal.ZERO);
        bankingDatabaseService.addCustomer(customer);
    }

    private String generatePassword() {
        int passwordLength = random.nextInt(MAX_PASSWORD_LENGTH - MIN_PASSWORD_LENGTH + 1) + MIN_PASSWORD_LENGTH;
        StringBuilder password = new StringBuilder();
        for (int i = 0; i < passwordLength; i++) {
            int index = random.nextInt(ALPHA_NUMERIC_STRING.length());
            password.append(ALPHA_NUMERIC_STRING.charAt(index));
        }

        return password.toString();
    }

    private String generateAccountNumber() {
        String randomString;
        final Set<String> generatedStrings = bankingDatabaseService.getCustomers()
                .stream()
                .map(Customer::getAccountNumber)
                .collect(Collectors.toSet());
        do {

            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < 16; i++) {
                int randomIndex = random.nextInt(NUMERIC_STRING.length());
                char randomChar = NUMERIC_STRING.charAt(randomIndex);
                stringBuilder.append(randomChar);
            }
            randomString = stringBuilder.toString();

        } while (generatedStrings.contains(randomString));

        return randomString;
    }
}