package com.infnet.infnetPB;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignAutoConfiguration;

@SpringBootApplication
@EnableFeignClients(basePackages = "com.infnet.infnetPB.client")
@ImportAutoConfiguration({FeignAutoConfiguration.class})

public class InfnetPbApplication {

	public static void main(String[] args) {
		SpringApplication.run(InfnetPbApplication.class, args);
	}
}