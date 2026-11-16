# 🏍️ JM Motos — Gestão de Frota e Locação Rent-to-Own

Sistema completo para gestão de frotas de motocicletas com modalidade **Rent-to-Own** (locação com aquisição progressiva). Desenvolvido com **Backend Spring Boot (Java 17)** e **Frontend Angular Standalone (TypeScript)**, integrado a banco **Supabase PostgreSQL** via JDBC com migrações **Flyway**.

A arquitetura do projeto segue o padrão rigoroso de camadas limpas inspirado no repositório de referência [`alvaraes-moderna`](https://github.com/Marcos-Ramoss/alvaraes-moderna):  
`Controller` ➔ `UseCase (@Service)` ➔ `Repository`

---

## 🛠️ Stack Tecnológica

### Backend (Java 17)
- **Framework:** Spring Boot 3.3.5
- **Camadas & Invariantes:** UseCases individuais por fluxo de negócio (`@Service`), isolando regras do Controller e Repository.
- **Segurança & Autenticação:** Spring Security + JWT (JJWT 0.12.6) com perfis `ADMIN`, `OPERADOR`, `INVESTIDOR`, `MECANICO`, `LOCATARIO`.
- **Persistência:** Spring Data JPA / Hibernate com tipos monetários em `BigDecimal`.
- **Banco de Dados:** Supabase PostgreSQL (em produção/dev) e H2 in-memory (para testes automatizados rápidos e isolados).
- **Controle de Versão do Banco:** Flyway Migrations (`V1__init_schema.sql`, `V2__seed_initial_data.sql`).
- **Validação:** Bean Validation (`jakarta.validation`).
- **Documentação da API:** OpenAPI 3 / Swagger (`/swagger-ui.html`).

### Frontend (Angular Standalone)
- **Framework:** Angular 20 + TypeScript
- **Paradigma:** Componentes Standalone, Formulários Reativos (`ReactiveFormsModule`), Signals e Roteamento Modular.
- **Experiência do Usuário (UX):** Mobile-first, layout adaptativo (desktop sidebar recolhível + mobile bottom navigation), skeletons contra layout shifts, botões de busca e limpeza explícitos e carrossel de tags horizontais.

---

## 📋 Regras de Negócio Implementadas (Incremento 1)

1. **Modelos Contratuais Parametrizados (Sem Hard Code):**
   - **Modelo A (Semanal):** 156 semanas, R$ 210,00/semana, caução de R$ 500,00.
   - **Modelo B (Diário Uarini):** 1.460 diárias, R$ 50,00/dia (R$ 45 aluguel + R$ 5 taxa de aquisição), caução de R$ 400,00.
2. **Snapshot Imutável:** Condições contratuais salvas no momento da assinatura. Edições posteriores nos planos não alteram contratos ativos.
3. **Exclusividade de Locação:** Uma motocicleta em estado `ALUGADA` ou com contrato ativo não pode receber outra locação simultânea (bloqueio por validação de integridade).
4. **Progresso de Aquisição Rent-to-Own:** Calculado rigorosamente como $\frac{\text{unidades integralmente quitadas}}{\text{total contratado}} \times 100$. Pagamentos parciais não incrementam o contador até a liquidação integral do saldo da cobrança.
5. **Isenção por Oficina:** Cobranças isentas por retenção da moto para manutenção pelo locador recebem motivo obrigatório, não geram receita e não contam como diária quitada (prorrogando a previsão).
6. **Segregação de Caução:** A caução é registrada em livro auxiliar (`caucao_movimentos`) e **não compõe** a receita operacional no dashboard.
7. **Estorno Auditado:** Cancelamento de pagamentos com justificativa obrigatória e recálculo automático do progresso de aquisição.
8. **Transparência de Rastreamento GPS:** Ausência de hardware/rastreador físico sinalizada com clareza ("Dispositivo não vinculado / Provedor não configurado"), sem gerar coordenadas geográficas falsas. Bloqueio remoto automático desativado por segurança jurídica e física.

---

## 🚀 Como Executar Localmente

### Pré-requisitos
- **Java 17 LTS** instalado (`java -version`).
- **Maven 3.8+** instalado (`mvn -v`).
- **Node.js 18+** e **npm** instalados (`node -v`).

---

### 1. Backend Spring Boot

Acesse o diretório do backend:
```bash
cd backend
```

Configure as variáveis de ambiente baseadas no `.env.example`:
```bash
cp .env.example .env
```

Para conectar ao **Supabase PostgreSQL**, configure em `application.yml` ou variáveis de ambiente:
```properties
DATABASE_URL=jdbc:postgresql://<SEU_HOST_SUPABASE>:6543/postgres?sslmode=require
DATABASE_USER=postgres
DATABASE_PASSWORD=<SUA_SENHA_SUPABASE>
```
> *Nota:* Se não houver banco PostgreSQL ativo no momento, o backend executa perfeitamente usando o perfil `test` com banco H2 em memória.

**Executar os testes automatizados do backend:**
```bash
mvn test
```

**Iniciar a aplicação backend:**
```bash
mvn spring-boot:run
```
O backend estará disponível em `http://localhost:8080`.  
A documentação Swagger estará em `http://localhost:8080/swagger-ui.html`.

---

### 2. Frontend Angular

Abra um novo terminal e acesse a pasta do frontend:
```bash
cd frontend
```

Instale as dependências:
```bash
npm install
```

Inicie o servidor de desenvolvimento:
```bash
npm start
# ou: npx ng serve
```
Acesse a aplicação no navegador em `http://localhost:4200`.

---

### 🔑 Credenciais Iniciais de Acesso (Seed Seguro)

| Perfil | E-mail | Senha Padrão |
|---|---|---|
| **Administrador** | `admin@jmmotos.com.br` | `admin123` |
| **Operador** | `operador@jmmotos.com.br` | `admin123` |

---

## 🧪 Testes Automatizados Executados

O backend conta com testes cobrindo todas as regras críticas em `RentToOwnBusinessRulesTest.java` e `JmMotosApplicationTests.java`:
1. `deveImpedirContratacaoSimultaneaDeMotoJaAlugada`: Impede aluguel simultâneo da mesma moto.
2. `deveCalcularPagamentoParcialSemAvancarAquisicao`: Pagamento parcial não incrementa progresso Rent-to-Own.
3. `deveAvancarAquisicaoAposQuitacaoIntegral`: Pagamento integral avança unidades quitadas e percentual de aquisição.
4. `deveIsentarCobrancaSemIncrementarProgressoAquisicao`: Isenção de oficina suspende cobrança sem gerar diária de compra.
5. `deveEstornarPagamentoAuditadoERecalcularSaldoEProgresso`: Estorno recalcula o saldo da parcela e rebaixa o progresso de aquisição.
6. `deveBloquearPagamentoDuplicadoComMesmaChaveIdempotencia`: Bloqueia cobrança duplicada por chave de idempotência.
7. `contextLoads`: Valida subida do contexto Spring Boot e aplicação das migrações Flyway.

---

## 📁 Estrutura de Pastas

```text
jm-motos/
├── backend/
│   ├── src/main/java/br/com/jmmotos/
│   │   ├── config/             # Segurança JWT, CORS, OpenAPI
│   │   ├── exception/          # Exceções de negócio e Handler Global
│   │   └── modules/
│   │       ├── auth/           # Login, JWT, Controller, UseCases
│   │       ├── fleet/          # Motocicletas, status operacional, odômetro
│   │       ├── customer/       # Clientes, CNH, CPF
│   │       ├── plan/           # Modelos de plano e versões
│   │       ├── rental/         # Contratos, snapshot, Rent-to-Own
│   │       ├── billing/        # Cobranças, pagamentos, isenções, caução
│   │       ├── dashboard/      # Métricas reais da frota e caixa
│   │       └── tracking/       # Provedor GPS e status de telemetria
│   └── src/main/resources/
│       ├── db/migration/       # V1__init_schema.sql, V2__seed_initial_data.sql
│       ├── application.yml
│       └── application-test.yml
├── frontend/
│   └── src/app/
│       ├── core/
│       │   ├── guards/         # auth.guard.ts
│       │   ├── interceptors/   # auth.interceptor.ts
│       │   ├── models/         # models.ts
│       │   └── services/       # auth, moto, customer, contract, billing...
│       ├── layout/             # Sidebar desktop e bottom nav mobile
│       └── pages/
│           ├── login/
│           ├── dashboard/
│           ├── motos/
│           ├── clientes/
│           ├── contratos/
│           ├── cobrancas/
│           └── rastreamento/
└── docs/
    ├── CONTEXTO_E_REQUISITOS_GESTAO_FROTA.md
    ├── ARQUITETURA.md
    ├── REGRAS_DE_NEGOCIO.md
    ├── PENDENCIAS.md
    └── ROADMAP.md
```

