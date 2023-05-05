package com.github.gasfgrv.storage.s3.service;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.github.gasfgrv.storage.s3.exception.DownloadException;
import com.github.gasfgrv.storage.s3.exception.UploadException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;

import java.io.IOException;
import java.io.InputStream;

import static com.github.gasfgrv.storage.s3.utils.ConstantesUtils.BUCKET;
import static com.github.gasfgrv.storage.s3.utils.MockUtils.gerarArquivo;
import static com.github.gasfgrv.storage.s3.utils.MockUtils.gerarS3Object;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

@ExtendWith({MockitoExtension.class, OutputCaptureExtension.class})
class ArquivosServiceTest {

    @Mock
    AmazonS3 amazonS3;

    @InjectMocks
    ArquivosService arquivosService;

    @Test
    @DisplayName("Deve retonar um array de bytes ao solicitar um arquivo")
    void deveRetonarUmArrayDeBytesAoSolicitarUmArquivo(CapturedOutput output) throws IOException {
        var arquivo = gerarArquivo();

        given(amazonS3.getObject(BUCKET, arquivo.getNome()))
                .willReturn(gerarS3Object());

        var bytes = arquivosService.download(arquivo);
        assertThat(bytes)
                .contains(arquivo.getDados());

        assertThat(output)
                .contains("Fazendo o download do arquivo %s no bucket".formatted(arquivo.getNome()));
    }

    @Test
    @DisplayName("Deve lançar DownloadException ao solicitar um arquivo")
    void deveLancarDownloadExceptionAoSolicitarUmArquivo(CapturedOutput output) throws IOException {
        var arquivo = gerarArquivo();
        given(amazonS3.getObject(BUCKET, arquivo.getNome()))
                .willThrow(new AmazonServiceException("Erro ao obter o arquivo", new RuntimeException()));

        assertThatExceptionOfType(DownloadException.class)
                .isThrownBy(() -> arquivosService.download(arquivo))
                .withCauseInstanceOf(AmazonServiceException.class);

        assertThat(output)
                .contains("Erro ao fazer o download");
    }

    @Test
    @DisplayName("Deve retornar uma String informando que foi feito o upload")
    void deveRetornarUmaStringInformandoQueFoiFeitoOUpload(CapturedOutput output) throws IOException {
        var arquivo = gerarArquivo();

        given(amazonS3.putObject(anyString(), anyString(), any(InputStream.class), any(ObjectMetadata.class)))
                .willReturn(new PutObjectResult());

        var upload = arquivosService.upload(arquivo);
        assertThat(upload)
                .isEqualTo("Arquivo salvo no bucket");

        assertThat(output)
                .contains("Fazendo o upload do arquivo %s no bucket".formatted(arquivo.getNome()));
    }

    @Test
    @DisplayName("Deve lançar uma UploadException ao fazer o upload")
    void deveLancarUmaUploadExceptionAoFazerOUpload(CapturedOutput output) throws IOException {
        var arquivo = gerarArquivo();

        given(amazonS3.putObject(anyString(), anyString(), any(InputStream.class), any(ObjectMetadata.class)))
                .willThrow(new AmazonServiceException("Erro ao obter o arquivo", new RuntimeException()));

        assertThatExceptionOfType(UploadException.class)
                .isThrownBy(() -> arquivosService.upload(arquivo))
                .withCauseInstanceOf(AmazonServiceException.class);

        assertThat(output)
                .contains("Erro ao fazer o upload");
    }

}