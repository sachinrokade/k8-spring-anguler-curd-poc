package com.example.k8poc;

import java.util.List;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.k8poc.model.Employee;

@SpringBootApplication
@RestController
public class SpringbootBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringbootBackendApplication.class, args);
	}
	
	@GetMapping("/")
	public String test(){
		System.out.println("=======Test Main");
		return "Succefuliy Deployed and application Access now";
	}

}
