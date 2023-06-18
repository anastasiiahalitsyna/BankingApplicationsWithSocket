package com.anastasiia.banking.core.mapper;

import com.anastasiia.banking.model.Customer;

import java.util.function.Function;

public class CustomerToLineMapper implements Function<Customer, String> {

    @Override
    public String apply(Customer customer) {
        return customer.getName() +
                "," +
                customer.getSurname() +
                "," +
                customer.getPesel() +
                "," +
                customer.getAccountNumber() +
                "," +
                customer.getBalance() +
                "," +
                customer.getPassword();
    }
}
