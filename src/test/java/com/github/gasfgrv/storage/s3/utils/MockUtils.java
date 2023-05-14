package com.github.gasfgrv.storage.s3.utils;

import com.amazonaws.services.s3.model.S3Object;
import com.github.gasfgrv.storage.s3.model.Arquivo;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.UUID;

import static com.github.gasfgrv.storage.s3.utils.ConstantesUtils.BUCKET;
import static com.github.gasfgrv.storage.s3.utils.ConstantesUtils.CAMINHO_ARQUIVO;

public class MockUtils {

    public static S3Object gerarS3Object() throws IOException {
        var s3object = new S3Object();
        s3object.setKey("teste.txt");
        s3object.setBucketName(BUCKET);
        s3object.setObjectContent(gerarInputStream());
        return s3object;
    }

    public static Arquivo gerarArquivo() throws IOException {
        return new Arquivo(UUID.randomUUID(),
                "teste.txt",
                MediaType.TEXT_PLAIN_VALUE,
                Files.readAllBytes(CAMINHO_ARQUIVO));
    }

    public static MultipartFile gerarMultipartFile() throws IOException {
        return new MockMultipartFile("arquivo", "teste.txt", "text/plain", gerarInputStream());
    }

    @NotNull
    private static FileInputStream gerarInputStream() throws FileNotFoundException {
        return new FileInputStream(CAMINHO_ARQUIVO.toFile());
    }

}
