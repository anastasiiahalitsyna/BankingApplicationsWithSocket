package com.anastasiia.banking.core.mapper;

import com.anastasiia.banking.model.Customer;

import java.math.BigDecimal;
import java.util.function.Function;

public class LineToCustomerMapper implements Function<String, Customer> {
    @Override
    public Customer apply(String line) {
        String[] columns = line.split(",");
        Customer customer = new Customer();
        customer.setName(columns[0].trim());
        customer.setSurname(columns[1].trim());
        customer.setPesel(columns[2].trim());
        customer.setAccountNumber(columns[3].trim());
        customer.setBalance(new BigDecimal(columns[4].trim()));
        customer.setPassword(columns[5].trim());
        return customer;
    }
}
