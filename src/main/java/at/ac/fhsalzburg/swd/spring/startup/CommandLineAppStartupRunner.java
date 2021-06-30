package at.ac.fhsalzburg.swd.spring.startup;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import at.ac.fhsalzburg.swd.spring.dao.Customer;
import at.ac.fhsalzburg.swd.spring.services.CustomerServiceInterface;
import at.ac.fhsalzburg.swd.spring.services.OrderServiceInterface;
import at.ac.fhsalzburg.swd.spring.services.ProductServiceInterface;

@Component
public class CommandLineAppStartupRunner implements CommandLineRunner {
    @Autowired
    CustomerServiceInterface customerService;
    
    @Autowired
    ProductServiceInterface productService;
    
    @Autowired
    OrderServiceInterface orderService;


   // Initialize System with preset accounts and stocks
    @Override
    public void run(String...args) throws Exception {

    	customerService.addCustomer("Max", "Mustermann", "max@muster.man", "123");
    	productService.addProduct("first product", 3.30f);
    	Customer customer = customerService.getAll().iterator().next();
    	customer.setCredit(100l);
    	orderService.addOrder(new Date(),customer, productService.getAll());
    }
}
