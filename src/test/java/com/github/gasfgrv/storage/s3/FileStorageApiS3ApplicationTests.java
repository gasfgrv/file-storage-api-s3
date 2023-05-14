package com.github.gasfgrv.storage.s3;

import java.io.File;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
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

import static com.github.gasfgrv.storage.s3.utils.ConstantesUtils.BUCKET;
import static com.github.gasfgrv.storage.s3.utils.ConstantesUtils.CAMINHO_ARQUIVO;
import static com.github.gasfgrv.storage.s3.utils.MockUtils.gerarMultipartFile;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.testcontainers.containers.localstack.LocalStackContainer.Service.S3;

@Slf4j
@Testcontainers
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class FileStorageApiS3ApplicationTests {

    static final String IMAGE_NAME = "localstack/localstack:latest";
    public static final DockerImageName DOCKER_IMAGE_NAME = DockerImageName.parse(IMAGE_NAME);

    @Container
    static LocalStackContainer localStack = new LocalStackContainer(DOCKER_IMAGE_NAME)
            .withServices(S3);

    @Autowired
    private MockMvc mockMvc;

    private File arquivo;

    @DynamicPropertySource
    static void awsProperties(DynamicPropertyRegistry registry) {
        registry.add("aws.serviceEndpoint", () -> localStack.getEndpointOverride(S3));
        registry.add("aws.region", localStack::getRegion);
        registry.add("aws.accessKey", localStack::getAccessKey);
        registry.add("aws.secretKey", localStack::getSecretKey);
    }

    @BeforeAll
    static void beforeAll() throws IOException, InterruptedException {
        log.info("Criando o Bucket: {}", BUCKET);
        localStack.execInContainer("awslocal", "s3", "mb", "s3://%s".formatted(BUCKET));
    }

    @AfterAll
    static void afterAll() throws IOException, InterruptedException {
        log.info("Destruindo o Bucket: {}", BUCKET);
        localStack.execInContainer("awslocal", "s3", "rb", "s3://%s".formatted(BUCKET), "--force");
    }

    @BeforeEach
    void setUp() {
        arquivo = CAMINHO_ARQUIVO.toFile();
    }

    @Test
    @Order(1)
    @DisplayName("Deve salvar um arquivo no bucket")
    void deveSalvarUmArquivoNoBucket() throws Exception {
        mockMvc.perform(multipart("/v1/arquivos/upload")
                        .file((MockMultipartFile) gerarMultipartFile())
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensagem").value("Arquivo salvo no bucket"));

        var arquivosBucket = localStack
                .execInContainer("awslocal", "s3", "ls", "s3://%s".formatted(BUCKET))
                .getStdout();
        assertThat(arquivosBucket)
                .contains(arquivo.getName());
    }

    @Test
    @Order(2)
    @DisplayName("Deve fazer o download de um arquivo")
    void deveFazerODownloadDeUmArquivo() throws Exception {
        var downloadArquivo = mockMvc.perform(get("/v1/arquivos/download")
                        .param("nomeArquivo", "teste.txt")
                        .accept(MediaType.APPLICATION_OCTET_STREAM_VALUE)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

        var tamanhoArquivo = downloadArquivo.andReturn()
                .getResponse()
                .getContentAsString()
                .getBytes()
                .length;
        assertThat(tamanhoArquivo)
                .isEqualTo(arquivo.length());
    }

}
