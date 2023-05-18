# File storage api (S3)

API para upload e download de arquivos no AWS S3.

## Status do Projeto

![](https://img.shields.io/badge/Status-Finalizado-green?style=for-the-badge&logo=appveyor)

## Funcionalidades

- Download de arquivos;
- Upload de arquivos;

## Documentação da API

```yaml
openapi: 3.0.1
info:
  title: File Storage api S3
  description: Api para fazer upload/download de arquivos no S3
  contact:
    name: gasfgrv
    url: https://github.com/gasfgrv
    email: gustavo_almeida11@hotmail.com
  version: V1
servers:
  - url: http://localhost:8080
    description: Generated server url
paths:
  /v1/arquivos/upload:
    post:
      tags:
        - arquivos-controller
      operationId: upload
      requestBody:
        content:
          multipart/form-data:
            schema:
              required:
                - arquivo
              type: object
              properties:
                arquivo:
                  type: string
                  format: binary
      responses:
        "200":
          description: OK
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/Resposta'
  /v1/arquivos/download:
    get:
      tags:
        - arquivos-controller
      operationId: download
      parameters:
        - name: nomeArquivo
          in: query
          required: true
          schema:
            type: string
      responses:
        "200":
          description: OK
          content:
            application/octet-stream:
              schema:
                type: string
                format: binary
components:
  schemas:
    Links:
      type: object
      additionalProperties:
        $ref: '#/components/schemas/Link'
    Resposta:
      type: object
      properties:
        mensagem:
          type: string
        _links:
          $ref: '#/components/schemas/Links'
    Link:
      type: object
      properties:
        href:
          type: string
        hreflang:
          type: string
        title:
          type: string
        type:
          type: string
        deprecation:
          type: string
        profile:
          type: string
        name:
          type: string
        templated:
          type: boolean
```

## Variáveis de Ambiente

Para rodar esse projeto, você vai precisar adicionar as seguintes variáveis de ambiente.

- `AWS_ACCESS_KEY`: ID de chave de acesso para autenticação na AWS
- `AWS_REGION`: Região onde se encontram recursos na AWS
- `AWS_SECRET_KEY`: Chave de acesso secreta para autenticação na AWS
- `AWS_SERVICE_ENDPOINT`: URL do ponto de entrada para um serviço da Web da AWS

## Rodando localmente

Clone o projeto

```bash
  git clone git@github.com:gasfgrv/file-storage-api.git
```

Entre no diretório do projeto

```bash
  cd file-storage-api
```

Instale as dependências

```bash
  mvn clean package -DskipTests
```

Subir recursos na AWS

```bash
  cd infra/
  terraform init
  terraform plan
  terraform apply -auto-approve
```

Inicie a aplicação, pode ser pela IDE (Intellij ou Eclipse), ou rodando o seguinte comando:

```bash
    java \
    -Daws.serviceEndpoint=${AWS_SERVICE_ENDPOINT} \
    -Daws.region=${AWS_REGION} \
    -Daws.accessKey=${AWS_ACCESS_KEY} \
    -Daws.secretKey=${AWS_SECRET_KEY} \
    -jar target/file-storage-api-s3-0.0.1-SNAPSHOT.jar
```

Use a collection para ajudar com os endpoints:

[![Run in Insomnia}](https://insomnia.rest/images/run.svg)](https://insomnia.rest/run/?label=File%20storage%20api%20(S3)&uri=https%3A%2F%2Fraw.githubusercontent.com%2Fgasfgrv%2Ffile-storage-api-s3%2Fmaster%2Frequests-collection.yaml)

**Obs:** Para remover os recursos da aws, evitando custos desncessários, use o comando `terraform destroy -auto-approve`
dentro da pasta de infra

## Rodando os testes

Para rodar os testes, rode o seguinte comando

```bash
  mvn tests
```

## Stack utilizada

- Java 17
- Spring Boot 3
- Maven

### Dependências:

- spring hateoas
- spring web
- lombok
- springdoc-openapi-starter-webmvc-ui
- spring validation
- testcontainers
- localstack
- aws sdk

## Autores

- [@gasfgrv](https://www.github.com/gasfgrv)

