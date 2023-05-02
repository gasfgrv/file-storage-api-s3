package com.github.gasfgrv.storage.s3;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
public class FileStorageApiS3Application {

	public static void main(String[] args) {
		SpringApplication.run(FileStorageApiS3Application.class, args);
	}

}
