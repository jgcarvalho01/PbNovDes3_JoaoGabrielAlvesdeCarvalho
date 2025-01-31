
# Desafio III - Microsserviços de Gerenciamento de Eventos e Ingressos

Este projeto consiste em dois microsserviços para gerenciamento de **eventos** e **ingressos** utilizando **Spring Boot**, **MongoDB**, **RabbitMQ** e **Docker**. O sistema permite a criação, consulta, atualização e exclusão de eventos e ingressos, garantindo a comunicação entre os serviços via **OpenFeign**.

## Tecnologias Utilizadas

-   **Java 17 (Amazon Corretto)**
-   **Spring Boot 3.3.x**
-   **MongoDB** (Compass ou Atlas, ou local via Docker)
-   **RabbitMQ** (Mensageria para envio de e-mails)
-   **OpenFeign** (Comunicação entre microsserviços)
-   **Docker & Docker Compose**
-   **AWS EC2** (Para deploy)

## Estrutura do Projeto

```
PbNovDes3_JoaoGabrielAlvesdeCarvalho/
├── ms-event-management/  # Microsserviço de eventos
├── ms-ticket-management/ # Microsserviço de ingressos
├── docker-compose.yml    # Configuração para rodar os serviços via Docker

```

## Como Rodar o Projeto

### Rodando Localmente (Sem Docker)

1.  **Subir o MongoDB e RabbitMQ**:
    -   MongoDB: Utilize um banco local ou MongoDB Compass ou Atlas
    -   RabbitMQ: Utilize um servidor local ou em nuvem
2.  **Rodar cada serviço manualmente**:

    ```sh
    cd ms-event-management
    mvn spring-boot:run

    ```

    ```sh
    cd ms-ticket-management
    mvn spring-boot:run

    ```

3.  **Acessar a API**:
    -   Event Management: `http://localhost:8080/swagger-ui.html`
    -   Ticket Management: `http://localhost:8081/swagger-ui.html`

### Rodando com Docker

```sh
docker-compose up -d --build

```

Isso irá levantar todos os serviços, incluindo MongoDB e RabbitMQ.

### Acessando no Navegador

-   **Swagger Event**: `http://localhost:8080/swagger-ui.html`
-   **Swagger Ticket**: `http://localhost:8081/swagger-ui.html`
-   **Painel do RabbitMQ**: `http://localhost:15672` (Usuário: guest / Senha: guest)

##  Configuração do MongoDB
Os serviços utilizam **credenciais para autenticação no MongoDB**. No `docker-compose.yml`, o banco é configurado com:
- **Usuário**: `admin`
- **Senha**: `admin`

Certifique-se de que seu `application-prod.yml` contém a configuração correta:
```yaml
spring:
  data:
    mongodb:
      uri: mongodb://admin:admin@mongodb_container:27017/db_event?authSource=admin
```


## Microsserviço: ms-event-management

### **Endpoints**

#### 1 - Criar um Evento

-   **`POST /create-event`**
-   **Payload:**

    ```json
    {
      "eventName": "Show da Xuxa",
      "dateTime": "2024-12-30T21:00:00",
      "cep": "01020-000"
    }

    ```

-   **Resposta:**

    ```json
    {
      "id": "string",
      "eventName": "Show da Xuxa",
      "dateTime": "2024-12-30T21:00:00",
      "cep": "01020-000",
      "logradouro": "string",
      "bairro": "string",
      "cidade": "string",
      "uf": "string"
    }

    ```


#### 2 - Consultar Todos os Eventos

-   **`GET /get-all-events`**
-   **Resposta:** Lista de eventos cadastrados.

#### 3 - Atualizar Evento

-   **`PUT /update-event/{id}`**
-   Retorna `409 Conflict` caso existam ingressos vinculados.

#### 4 - Deletar Evento

-   **`DELETE /delete-event/{id}`**
-   Verifica no `ms-ticket-management` se existem ingressos vinculados.
-   Retorna `409 Conflict` caso existam ingressos vendidos.

## Microsserviço: ms-ticket-management

###  **Endpoints**

#### 1 - Criar um Ingresso

-   **`POST /create-ticket`**
-   **Payload:**

    ```json
    {
      "customerName": "John Smith",
      "cpf": "12345678900",
      "customerMail": "john@email.com",
      "eventId": "6793d75634df9c74ccfff4b5",
      "brlAmount": 100.0,
      "usdAmount": 20.0
    }

    ```

-   **Resposta:**

    ```json
    {
      "ticketId": "string",
      "cpf": "12345678900",
      "customerName": "John Smith",
      "customerMail": "john@email.com",
      {
        "eventId": "6793d75634df9c74ccfff4b5",
        "eventName": "string",
        "eventDateTime": "2024-12-30T21:00:00",
        "logradouro": "string",
        "bairro": "string",
        "cidade": "string",
        "uf": "string"
      },
      "brlTotalAmount": "R$ 100,00",
      "usdTotalAmount": "$ 20.0",
      "status": "Concluído"
    }

    ```


#### 2 - Consultar Ticket por ID

-   **`GET /get-ticket/{id}`**
-   Retorna os detalhes do ingresso.

#### 3 - Cancelar Ticket (Soft-Delete)

-   **`DELETE /cancel-ticket/{id}`**
-   Apenas altera o status do ticket para `Cancelado`.

##  Integração com RabbitMQ

Após a compra de um ingresso, uma **mensagem personalizada** é enviada para a fila `ticket-queue` no RabbitMQ.

**Exemplo da mensagem:**

```
🎉 Ei John Smith, seu ingresso está confirmado! 🎟️
Detalhes:
🎤 Evento: Show da Xuxa
📅 Data: 2024-12-30T21:00:00
📍 Local: Rua X, Bairro Y - Cidade/UF
💰 Valor: R$ 100,00 (ou $ 20,00)

Aproveite o show e não esqueça de contar pros amigos! 🤩
```

## Deploy na AWS EC2

1.  **Conectar na Instância**

    ```sh
    ssh -i sua-chave.pem ubuntu@seu-ip-publico

    ```

2.  **Instalar Dependências**

    ```sh
    sudo apt update && sudo apt install -y docker docker-compose

    ```
    ```sh
    sudo apt install -y git
    ```

3.  **Clonar o Repositório**

    ```sh
    git clone https://github.com/seu-repositorio.git
    cd seu-repositorio

    ```

4.  **Subir os Containers**

    ```sh
    sudo systemctl start docker

    ```
    ```sh
    docker-compose up -d --build
    ```


Agora acesse:

-   `http://18.116.68.240:8080/swagger-ui.html`
-   `http://18.116.68.240:8081/swagger-ui.html`

----------

## Conclusão

Este projeto implementa um sistema de gerenciamento de eventos e ingressos utilizando microsserviços, mensageria com RabbitMQ e deploy na AWS EC2. Se tiver dúvidas, entre em contato pelo [e-mail](mailto:joao.gabriel.pb@compasso.com.br)!
