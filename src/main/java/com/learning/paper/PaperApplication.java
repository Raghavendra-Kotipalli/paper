package com.learning.paper;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

@SpringBootApplication
public class PaperApplication {

	public static void main(String[] args) {
		SpringApplication.run(PaperApplication.class, args);
	}

	@Bean
	public RestTemplate restTemplate() {
		RestTemplate restTemplate = new RestTemplate();
		restTemplate.setMessageConverters(Collections.singletonList(new ByteArrayHttpMessageConverter()));
		restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
		return restTemplate;
	}
}
