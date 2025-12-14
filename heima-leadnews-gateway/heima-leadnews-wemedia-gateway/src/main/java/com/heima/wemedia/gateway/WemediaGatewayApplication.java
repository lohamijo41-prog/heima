package com.heima.wemedia.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;



@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class})
public class WemediaGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(WemediaGatewayApplication.class,args);
    }
}
