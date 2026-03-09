package com.example.blogStudy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class BlogStudyApplication {

	public static void main(String[] args) {
		SpringApplication.run(BlogStudyApplication.class, args);
	}

}
