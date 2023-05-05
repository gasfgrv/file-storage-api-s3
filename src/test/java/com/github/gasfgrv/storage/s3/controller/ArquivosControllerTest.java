package com.github.gasfgrv.storage.s3.controller;

import com.github.gasfgrv.storage.s3.exception.UploadException;
import com.github.gasfgrv.storage.s3.model.Arquivo;
import com.github.gasfgrv.storage.s3.service.ArquivosService;
import com.github.gasfgrv.storage.s3.utils.ConstantesUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.nio.file.Files;

import static com.github.gasfgrv.storage.s3.utils.ConstantesUtils.CAMINHO_ARQUIVO;
import static com.github.gasfgrv.storage.s3.utils.MockUtils.gerarArquivo;
import static com.github.gasfgrv.storage.s3.utils.MockUtils.gerarMultipartFile;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ArquivosControllerTest {

    @Mock
    ArquivosService arquivosService;

    @InjectMocks
    ArquivosController arquivosController;

    @Test
    @DisplayName("Deve salvar um arquivo com sucesso")
    void deveSalvarUmArquivoComSucesso() throws IOException {
        var arquivo = gerarArquivo();
        given(arquivosService.upload(any(Arquivo.class)))
                .willReturn("Arquivo salvo no bucket");

        var response = arquivosController.upload(arquivo.getNome(), gerarMultipartFile());

        var headers = response.getHeaders();
        assertThat(headers)
                .isEmpty();

        var statusCode = response.getStatusCode();
        assertThat(statusCode)
                .isEqualTo(HttpStatus.OK);

        var body = response.getBody();
        assertThat(body.getLink("download"))
                .isPresent()
                .map(Link::getHref)
                .contains("/v1/arquivos/download?nomeArquivo=%s".formatted(arquivo.getNome()));
        assertThat(body.getMensagem())
                .isNotNull()
                .isEqualTo("Arquivo salvo no bucket");

    }

    @Test
    @DisplayName("Deve salvar um arquivo com sucesso")
    void deveLancarExececaoSalvarUmArquivo() throws IOException {
        var nome = gerarArquivo().getNome();
        var multipart = gerarMultipartFile();

        given(arquivosService.upload(any(Arquivo.class)))
                .willThrow(new UploadException(new RuntimeException("Erro ao fazer o upload do arquivo")));

        assertThatExceptionOfType(UploadException.class)
                .isThrownBy(() -> arquivosController.upload(nome, multipart))
                .withCauseInstanceOf(UploadException.class);
    }

    @Test
    @DisplayName("Deve baixar um arquivo")
    void deveBaixarUmArquivo() throws IOException {
        var arquivo = gerarArquivo();
        given(arquivosService.dowload(any(Arquivo.class)))
                .willReturn(Files.readAllBytes(CAMINHO_ARQUIVO));

        var dowload = arquivosController.dowload(arquivo.getNome());

        var headers = dowload.getHeaders();
        assertThat(headers)
                .isEmpty();

        var statusCode = dowload.getStatusCode();
        assertThat(statusCode)
                .isEqualTo(HttpStatus.OK);

        var body = dowload.getBody();
        assertThat(body)
                .isNotEmpty()
                .hasSize(arquivo.getDados().length);
    }

}