package com.github.gasfgrv.storage.s3.service;

import com.github.gasfgrv.storage.s3.model.Arquivo;

public interface IArquivosService {
    String upload(Arquivo arquivo);

    byte[] download(String nomeArquivo);
}
