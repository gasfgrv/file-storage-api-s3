package com.github.gasfgrv.storage.s3.utils;

import com.amazonaws.services.s3.model.S3Object;
import com.github.gasfgrv.storage.s3.model.Arquivo;
import org.springframework.http.MediaType;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.UUID;

import static com.github.gasfgrv.storage.s3.utils.ConstantesUtils.BUCKET;
import static com.github.gasfgrv.storage.s3.utils.ConstantesUtils.CAMINHO_ARQUIVO;

public class MockUtils {

    public static S3Object gerarS3Object() throws IOException {
        var inputStream = new FileInputStream(CAMINHO_ARQUIVO.toFile());

        var s3object = new S3Object();
        s3object.setKey(gerarArquivo().getNome());
        s3object.setBucketName(BUCKET);
        s3object.setObjectContent(inputStream);
        return s3object;
    }

    public static Arquivo gerarArquivo() throws IOException {
        return new Arquivo(UUID.randomUUID(),
                "teste",
                MediaType.TEXT_PLAIN_VALUE,
                Files.readAllBytes(CAMINHO_ARQUIVO));
    }

}
