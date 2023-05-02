package com.github.gasfgrv.storage.s3.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.github.gasfgrv.storage.s3.exception.DownloadException;
import com.github.gasfgrv.storage.s3.exception.UploadException;
import com.github.gasfgrv.storage.s3.model.Arquivo;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ArquivosService implements IArquivosService {

    private static final String BUCKET = "teste-spring-bucket";

    private final AmazonS3 s3;

    @Override
    public String upload(Arquivo arquivo) {
        try {
            var nome = arquivo.getNome();

            log.info("Fazendo o upload do arquivo {} no bucket", nome);

            var metadata = new ObjectMetadata();
            metadata.setContentLength(arquivo.getDados().length);

            s3.putObject(BUCKET, nome, new ByteArrayInputStream(arquivo.getDados()), metadata);

            return "Arquivo salvo no bucket";
        } catch (Exception e) {
            log.error("Erro ao fazer o upload: ", e);
            throw new UploadException(e);
        }
    }

    @Override
    public byte[] dowload(Arquivo arquivo) {
        try {
            var nome = arquivo.getNome();

            log.info("Fazendo o download do arquivo {} no bucket", nome);

            var s3Object = s3.getObject(BUCKET, nome);
            return s3Object.getObjectContent().readAllBytes();
        } catch (IOException e) {
            log.error("Erro ao fazer o download: ", e);
            throw new DownloadException(e);
        }
    }

}
