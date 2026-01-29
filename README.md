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

## Tecnologias Utilizadas

- Java 21
- Spring Boot
- Spring Data JPA
- Hibernate
- Oracle Database
- Docker
- Docker Compose
- Maven

---

## Arquitetura e Decisões Técnicas

- API REST seguindo boas práticas (RESTful)
- Controle de concorrência utilizando **transações e lock otimista/pessimista**
- Camadas bem definidas:
  - Controller
  - Service
  - Domain
  - Repository
- Banco relacional para garantir **consistência financeira**

---

## Pré-requisitos

Antes de começar, você precisa ter instalado:

- Docker
- Docker Compose
- Git

---

## Como rodar o projeto com Docker

## Clone o repositório

```bash
git clone https://github.com/seu-usuario/lancamentos-bancarios-api.git
cd lancamentos-bancarios-api

docker-compose up --build
