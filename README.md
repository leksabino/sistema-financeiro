# Sistema Financeiro — Java 21

Mini sistema bancário construído como projeto de portfólio
para transição de carreira para o mercado de fintechs.

## O que aprendi construindo este projeto

- Modelagem de dados com **Records** imutáveis (Java 16+)
- Validação de regras de negócio no construtor
- Processamento de coleções com **Streams API**
- Exceções de domínio com contexto financeiro
- Testes automatizados com **JUnit 5**
- Gerenciamento de dependências com **Maven**

## Funcionalidades

- Abertura de conta com saldo inicial
- Processamento de créditos e débitos
- Bloqueio automático de saldo insuficiente
- Detecção de comportamento suspeito (antifraude)
- Relatório de transações por tipo
- Filtro de transações acima de um valor

## Como executar
```bash
git clone https://github.com/SEU-USUARIO/sistema-financeiro.git
cd sistema-financeiro
mvn compile exec:java -Dexec.mainClass="com.meufinanceiro.App"
```

## Como rodar os testes
```bash
mvn test
```

## Tecnologias

- Java 21 (Temurin)
- Maven 3.9
- JUnit Jupiter 5.10

## Próximas melhorias

- [ ] Expor como API REST com Spring Boot
- [ ] Persistir dados com PostgreSQL
- [ ] Adicionar autenticação JWT
- [ ] Containerizar com Docker

## Contexto

Estou em transição de carreira para tecnologia com foco
em fintechs. Este projeto faz parte da minha jornada de
aprendizado em Java, Kotlin e sistemas financeiros.