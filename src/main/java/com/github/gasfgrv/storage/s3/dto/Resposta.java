package com.github.gasfgrv.storage.s3.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.hateoas.RepresentationModel;

@AllArgsConstructor
@Builder
@Getter
@EqualsAndHashCode(callSuper = false)
public class Resposta extends RepresentationModel<Resposta> {
    private String mensagem;
}
