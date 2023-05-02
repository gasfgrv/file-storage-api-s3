package com.github.gasfgrv.storage.s3;

import java.io.IOException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import lombok.extern.slf4j.Slf4j;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.testcontainers.containers.localstack.LocalStackContainer.Service.S3;

@Slf4j
@Testcontainers
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class FileStorageApiS3ApplicationTests {

    static final String IMAGE_NAME = "localstack/localstack:latest";
    static final String BUCKET_NAME = "teste-spring-bucket";

    @Container
    static LocalStackContainer localStack = new LocalStackContainer(DockerImageName.parse(IMAGE_NAME))
            .withServices(S3);

    @Autowired
    private MockMvc mockMvc;

    @DynamicPropertySource
    static void awsProperties(DynamicPropertyRegistry registry) {
        registry.add("aws.serviceEndpoint", () -> localStack.getEndpointOverride(S3));
        registry.add("aws.region", localStack::getRegion);
        registry.add("aws.accessKey", localStack::getAccessKey);
        registry.add("aws.secretKey", localStack::getSecretKey);
    }

    @BeforeAll
    static void beforeAll() throws IOException, InterruptedException {
        log.debug("Criando o Bucket: {}", BUCKET_NAME);
        localStack.execInContainer("awslocal", "s3", "mb", "s3://%s".formatted(BUCKET_NAME));
    }

    @AfterAll
    static void afterAll() throws IOException, InterruptedException {
        log.debug("Destruindo o Bucket: {}", BUCKET_NAME);
        localStack.execInContainer("awslocal", "s3", "rb", "s3://%s".formatted(BUCKET_NAME), "--force");
    }

    @Test
    @DisplayName("Deve salvar um arquivo no bucket")
    void deveSalvarUmArquivoNoBucket() throws Exception {
        mockMvc.perform(multipart("/v1/arquivos/upload")
                        .file(new MockMultipartFile("arquivo", new byte[20]))
                        .param("nomeArquivo", "batata")
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensagem").value("Arquivo salvo no bucket"));
    }

}
