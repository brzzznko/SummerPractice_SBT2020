package com.team3.collections;

import com.team3.collections.Model.GatewayWork;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class CollectionsApplication {

	public static void main(String[] args) {
		ApplicationContext ctx = SpringApplication.run(CollectionsApplication.class, args);

		GatewayWork printThread1 = (GatewayWork) ctx.getBean("gatewayWork");
		printThread1.start();
	}

}
