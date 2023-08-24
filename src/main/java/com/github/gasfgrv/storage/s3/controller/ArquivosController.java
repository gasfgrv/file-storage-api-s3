package com.github.gasfgrv.storage.s3.controller;

import com.github.gasfgrv.storage.s3.dto.Resposta;
import com.github.gasfgrv.storage.s3.exception.UploadException;
import com.github.gasfgrv.storage.s3.model.Arquivo;
import com.github.gasfgrv.storage.s3.service.IArquivosService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
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
    public ResponseEntity<Resposta> upload(@RequestParam("arquivo") MultipartFile arquivo) {
        try {
            var nomeArquivo = arquivo
                    .getOriginalFilename();

            var arquivoParaUpload = Arquivo
                    .builder()
                    .id(UUID.randomUUID())
                    .nome(nomeArquivo)
                    .tipo(arquivo.getContentType())
                    .dados(arquivo.getBytes())
                    .build();

            var resposta = Resposta
                    .builder()
                    .mensagem(service.upload(arquivoParaUpload))
                    .build();

            resposta
                    .add(linkTo(methodOn(ArquivosController.class)
                            .download(nomeArquivo)).withRel("download"));

            return ResponseEntity.ok(resposta);
        } catch (Exception e) {
            throw new UploadException(e);
        }

    }

    @GetMapping(value = "/download", produces = APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<Resource> download(@RequestParam("nomeArquivo") String nomeArquivo) {
        var resource = new ByteArrayResource(service.download(nomeArquivo));

        return ResponseEntity
                .ok()
                .contentLength(resource.contentLength())
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=%s".formatted(nomeArquivo))
                .body(resource);
    }

}
