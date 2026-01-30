# Lançamentos Bancários API

API RESTful desenvolvida em **Java 21** com **Spring Boot** para realizar lançamentos bancários de **débito e crédito** em contas de clientes, garantindo **consistência, concorrência segura e escalabilidade**.

---

## Funcionalidades

- Realizar lançamentos de **débito e crédito**
- Permite **múltiplos lançamentos em uma única requisição**
- Consulta de **saldo atual da conta**
- Processamento **thread-safe**
- Persistência em banco **Oracle**
- Execução via **Docker e Docker Compose**

---

## Tecnologias Utilizadas (Requisito 7)

- **Java 21** & **Spring Boot 3**
- **Spring Data JPA** & **Hibernate**
- **Oracle Database 21c**
- **Docker** & **Docker Compose**
- **Maven**
- **JUnit 5**, **Mockito** & **AssertJ** (Testes)

---

## Arquitetura e Decisões Técnicas

- API REST seguindo boas práticas (RESTful).
- **Controle de concorrência**: Utilização de **Lock Otimista (@Version)** para garantir a integridade do saldo em acessos simultâneos, atendendo aos requisitos de thread-safety.
- **Transacionalidade**: Uso de `@Transactional` para garantir que listas de lançamentos sejam processadas de forma atômica (tudo ou nada).
- Camadas bem definidas: Controller, Service, Domain e Repository.
- Banco relacional para garantir **consistência financeira (ACID)**.

---

## Documentação da API (Requisito 5)

### 1. Processar Lançamentos
Realiza operações de débito e crédito em uma conta bancária. 
*Processamento atômico: caso qualquer lançamento falhe, nenhum lançamento será persistido.*

* **Endpoint:** `POST /v1/contas/{numeroConta}/lancamentos`
* **Método:** `POST`
* **Headers:** `Content-Type: application/json`
* **Parâmetros de Path:** `numeroConta` (String): número da conta destino.

#### Formatos de Resposta:
* **200 OK**: Sucesso no processamento.
* **400 Bad Request**: Erro de validação (ex: valor zero ou negativo).
* **404 Not Found**: Conta informada não encontrada.
* **422 Unprocessable Entity**: Regra de negócio (ex: saldo insuficiente).

---

### 2. Consulta de Saldo Atual
Recupera o saldo consolidado de uma conta bancária:

* **Endpoint:** `GET /v1/contas/{numeroConta}/saldo`
* **Método:** `GET`
* **Parâmetros de Path:** `numeroConta` (String): número da conta.
* **Resposta:** Status 200 OK contendo o número da conta, saldo e data da consulta.

---

## Estratégia de Testes

A aplicação conta com uma suíte de testes automatizados para garantir a estabilidade:
- **Testes Unitários:** Validação das regras de saldo no domínio e lógica de Enums.
- **Testes de Integração:** Validação do fluxo completo (MockMvc -> Service -> Repository).
- **Testes de Concorrência:** Simulação de múltiplas threads para validar a proteção do `@Version`.

**Para executar os testes:** `mvn test`

---

## Pré-requisitos

Antes de começar, você precisa ter instalado:
- Docker
- Docker Compose
- Git

---

## Como rodar o projeto com Docker

### 1. Clone o repositório
Execute o comando git clone seguido do diretório da aplicação.

### 2. Suba os containers
Execute o comando `docker-compose up --build` para iniciar a aplicação e o banco de dados Oracle.


#### Exemplo de Payload:
```json
{
  "numeroConta": "54321",
  "lancamentos": [
    { "valor": 100.50, "tipo": "CREDITO", "descricao": "Deposito PIX" },
    { "valor": 50.00, "tipo": "DEBITO", "descricao": "Pagamento Boleto" }
  ]
}
