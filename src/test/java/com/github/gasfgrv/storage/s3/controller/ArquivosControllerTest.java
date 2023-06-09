package com.github.gasfgrv.storage.s3.controller;

import com.github.gasfgrv.storage.s3.exception.UploadException;
import com.github.gasfgrv.storage.s3.model.Arquivo;
import com.github.gasfgrv.storage.s3.service.ArquivosService;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;

import static com.github.gasfgrv.storage.s3.utils.ConstantesUtils.CAMINHO_ARQUIVO;
import static com.github.gasfgrv.storage.s3.utils.MockUtils.gerarArquivo;
import static com.github.gasfgrv.storage.s3.utils.MockUtils.gerarMultipartFile;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
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

        var response = arquivosController.upload(gerarMultipartFile());

        var headers = response.getHeaders();
        assertThat(headers)
                .isEmpty();

        var statusCode = response.getStatusCode();
        assertThat(statusCode)
                .isEqualTo(HttpStatus.OK);

        var body = response.getBody();
        assertThat(body)
                .isNotNull();
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
                .isThrownBy(() -> arquivosController.upload(multipart))
                .withCauseInstanceOf(UploadException.class);
    }

    @Test
    @DisplayName("Deve baixar um arquivo")
    void deveBaixarUmArquivo() throws IOException {
        var arquivo = gerarArquivo();
        given(arquivosService.download(anyString()))
                .willReturn(Files.readAllBytes(CAMINHO_ARQUIVO));

        var download = arquivosController.download(arquivo.getNome());

        var headers = download.getHeaders();
        assertThat(headers)
                .isNotEmpty();

        assertThat(headers.keySet().stream())
                .contains("Content-Disposition", "Content-Length");

        var statusCode = download.getStatusCode();
        assertThat(statusCode)
                .isEqualTo(HttpStatus.OK);

        var body = download.getBody();
        assertThat(body)
                .isNotNull();

        assertThat(body.getContentAsByteArray())
                .isNotEmpty()
                .hasSize(arquivo.getDados().length);
    }

}