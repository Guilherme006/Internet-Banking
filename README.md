# Projeto Bancario

Aplicacao de internet banking para consulta de extrato e pagamento de boletos.

## Estrutura

- `apps/payment-service/`: API Spring Boot organizada em Clean Architecture/Hexagonal.
- `apps/internet-banking/`: aplicacao Angular com camadas de `core`, `data`, `domain`, `presentation` e `store`.
- `docker-compose.yml`: orquestracao local de MySQL, Kafka, API e web app.

## Como rodar

```bash
docker compose up --build -d
```

Depois acesse:

- Frontend: `http://localhost:4200`
- API: `http://localhost:8080`

Para acompanhar os containers:

```bash
docker compose ps
```

Para parar:

```bash
docker compose down
```
