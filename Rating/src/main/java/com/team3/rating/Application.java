package com.team3.rating;

import com.team3.rating.Model.GatewayWork;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		ApplicationContext ctx = SpringApplication.run(Application.class, args);

		GatewayWork printThread1 = (GatewayWork) ctx.getBean("gatewayWork");
		printThread1.start();
	}

}
