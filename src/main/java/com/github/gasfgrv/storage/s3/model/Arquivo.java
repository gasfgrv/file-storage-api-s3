package com.github.gasfgrv.storage.s3.model;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
@Builder
@EqualsAndHashCode(callSuper = false, of = "id")
public class Arquivo {
    private UUID id;
    private String nome;
    private String tipo;
    private byte[] dados;
}
