package com.github.gasfgrv.storage.s3.controller;

import com.github.gasfgrv.storage.s3.dto.Resposta;
import com.github.gasfgrv.storage.s3.exception.UploadException;
import com.github.gasfgrv.storage.s3.model.Arquivo;
import com.github.gasfgrv.storage.s3.service.IArquivosService;
import java.io.IOException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.APPLICATION_OCTET_STREAM_VALUE;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

@RestController
@RequestMapping("/v1/arquivos")
@RequiredArgsConstructor
public class ArquivosController {

    private final IArquivosService service;

    @PostMapping(value = "/upload", consumes = MULTIPART_FORM_DATA_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Resposta> upload(@RequestParam("nomeArquivo") String nomeArquivo,
                                           @RequestParam("arquivo") MultipartFile arquivo) {
        try {
            var upload = service.upload(Arquivo
                    .builder()
                    .id(UUID.randomUUID())
                    .nome(nomeArquivo)
                    .tipo(arquivo.getContentType())
                    .dados(arquivo.getBytes())
                    .build());

            var resposta = Resposta
                    .builder()
                    .mensagem(upload)
                    .build();

            resposta.add(linkTo(methodOn(ArquivosController.class).dowload(nomeArquivo)).withRel("download"));

            return ResponseEntity.ok(resposta);
        } catch (IOException e) {
            throw new UploadException(e);
        }

    }

    @GetMapping(value = "/download", produces = APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<byte[]> dowload(@RequestParam("nomeArquivo") String nomeArquivo) {
        return ResponseEntity.ok(service.dowload(Arquivo
                .builder()
                .nome(nomeArquivo)
                .build()));
    }

}