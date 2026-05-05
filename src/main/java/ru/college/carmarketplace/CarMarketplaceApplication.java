package ru.college.carmarketplace;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CarMarketplaceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CarMarketplaceApplication.class, args);
    }


}
