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

    @Value("${aws.accessKey}")
    private String accessKey;
    @Value("${aws.secretKey}")
    private String secretKey;

    @Value("${aws.serviceEndpoint}")
    private String serviceEndpoint;
    @Value("${aws.region}")
    private String region;

    @Bean
    private AWSCredentials getAwsCredentials() {
        log.info("Recuperando credenciais da AWS");
        return new BasicAWSCredentials(accessKey, secretKey);
    }

    @Bean
    private AWSCredentialsProvider getAwsCredentialsProvider() {
        log.info("Gerando o provedor de credenciais da AWS");
        return new AWSStaticCredentialsProvider(getAwsCredentials());
    }

    @Bean
    private EndpointConfiguration getEndpointConfiguration() {
        log.info("Configurando o endpoint do S3");
        return new EndpointConfiguration(serviceEndpoint, region);
    }

    @Bean
    public AmazonS3 amazonS3Client() {
        log.info("Criando client do S3");
        return AmazonS3ClientBuilder
                .standard()
                .withEndpointConfiguration(getEndpointConfiguration())
                .withCredentials(getAwsCredentialsProvider())
                .build();
    }

}
