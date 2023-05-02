package com.github.gasfgrv.storage.s3.configs;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class AwsConfig {

    @Bean
    public AmazonS3 amazonS3Client(@Value("${aws.accessKey}") String accessKey,
                                   @Value("${aws.secretKey}") String secretKey,
                                   @Value("${aws.serviceEndpoint}") String serviceEndpoint,
                                   @Value("${aws.region}") String region) {

        log.info("Recuperando credenciais da AWS");
        AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
        AWSCredentialsProvider provider = new AWSStaticCredentialsProvider(credentials);

        log.info("Configurando o endpoint do S3");
        EndpointConfiguration endpointConfiguration = new EndpointConfiguration(serviceEndpoint, region);

        log.info("Criando client do S3");
        return AmazonS3ClientBuilder.standard()
                .withEndpointConfiguration(endpointConfiguration)
                .withCredentials(provider)
                .build();
    }

}
