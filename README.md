# spec-api-pix
Repositório de estudos da spec api pix do bacen - https://github.com/bacen/pix-api/releases/tag/2.8.2.
- Documento abaixo gerado a partir de Inteligencia Artificial após análise do Guia de Implementação do Pix Automatico e do Manual de Tempos do Pix do Bacen.

# 📄 Documento Completo de Fluxos do Pix Automático

_Foco: PSP Recebedor via API Pix_  
_Versão: 1.0 (baseado em Guia v1.2 e Manual de Tempos v6.1)_

---

## 🧩 1. Participantes do Ecossistema

| Sigla   | Nome                               | Papel                                                      |
| ------- | ---------------------------------- | ---------------------------------------------------------- |
| **UP**  | Usuário Pagador                    | Cliente do PSP pagador que autoriza débitos recorrentes    |
| **UR**  | Usuário Recebedor                  | Cliente do PSP recebedor que oferece cobranças recorrentes |
| **PP**  | PSP Pagador                        | Instituição financeira do pagador (banco, fintech, etc.)   |
| **PR**  | PSP Recebedor                      | Sua instituição, implementando via API Pix                 |
| **API** | API Pix (BACEN)                    | Interface para criar e gerenciar cobranças e recorrências  |
| **SPI** | Sistema de Pagamentos Instantâneos | Infraestrutura de mensageria entre PSPs                    |

---

## ✅ 2. Jornadas de Autorização

### 🔹 Jornada 1 – Autorização sem QR Code (Notificação via App)

> **Fonte:** Guia do Pix Automático, seção 3.2  
> _"Jornada em que o usuário pagador escolhe o Pix Automático como forma de pagamento por meio de relação direta com o usuário recebedor..."_

#### ✅ Jornada 1 – Sucesso na Autorização

```mermaid
sequenceDiagram
    autonumber
    UR->>PR: Informa interesse do pagador
    PR->>API: POST /rec {dados da recorrência}
    API-->>PR: 201 Created + id, location, status="criada"
    PR->>PP: pain.009 (solicita autorização)
    PP->>UP: Notifica: "Confirme o Pix Automático"
    UP->>PP: Confirma autorização
    PP->>SPI: Cria pain.012 (status=true, MandateStatus='CFDB')
    SPI->>PR: Disponibiliza pain.012
    PR->>API: PATCH /rec/{id} (status="confirmada")
    API-->>PR: 200 OK
    PR->>UR: Notifica sucesso
```

#### ❌ Jornada 1 – Rejeição da Autorização

```mermaid
sequenceDiagram
    autonumber
    UR->>PR: Informa interesse do pagador
    PR->>API: POST /rec {dados da recorrência}
    API-->>PR: 201 Created + status="criada"
    PR->>PP: pain.009 (solicita autorização)
    PP->>UP: Notifica: "Confirme o Pix Automático"
    UP->>PP: Rejeita autorização (motivo: não reconhece recebedor)
    PP->>SPI: Cria pain.012 (status=false, motivoRejeicao=1)
    SPI->>PR: Disponibiliza pain.012 (rejeição)
    PR->>API: PATCH /rec/{id} (status="rejeitada", motivoCancelamento="REJEITADA_PAGADOR")
    API-->>PR: 200 OK
    PR->>UR: Notifica: "Autorização rejeitada"
```

---

### 🔹 Jornada 2 – Autorização com QR Code (somente dados da recorrência)

> **Fonte:** Guia do Pix Automático, seção 3.3  
> _"Jornada em que o usuário pagador lê um QR Code contendo as informações da recorrência..."_

#### ✅ Jornada 2 – Sucesso na Autorização

```mermaid
sequenceDiagram
    autonumber
    PR->>API: POST /rec {dados da recorrência}
    API-->>PR: 201 Created + location
    PR->>UR: Gera QR Code com location (faixa 80-99)
    UR->>UP: Exibe QR Code
    UP->>PP: Lê QR Code
    PP->>API: GET /rec/{id} (busca dados da recorrência)
    API-->>PP: Retorna dados
    PP->>UP: Mostra detalhes e pede confirmação
    UP->>PP: Confirma autorização
    PP->>SPI: pain.012 (status=true, MandateStatus='CFDB')
    SPI->>PR: Disponibiliza pain.012
    PR->>API: PATCH /rec/{id} (status="confirmada")
    PR->>UR: Notifica sucesso
```

---

### 🔹 Jornada 3 – QR Code com Pagamento Imediato + Autorização (obrigatória)

> **Fonte:** Guia do Pix Automático, seção 3.4  
> _"O pagamento da cobrança imediata implica a assunção do Pix Automático como forma de pagamento para as cobranças recorrentes subsequentes..."_

#### ✅ Jornada 3 – Sucesso na Autorização

```mermaid
sequenceDiagram
    autonumber
    PR->>API: POST /cob (cobrança imediata)
    PR->>API: POST /rec (recorrência)
    API-->>PR: txid, location
    PR->>UR: Gera QR Code composto (faixa 26-51 + 80-99)
    UR->>UP: Exibe QR Code
    UP->>PP: Lê QR Code
    PP->>API: GET /cob/{txid} + GET /rec/{id}
    API-->>PP: Dados da cobrança e recorrência
    PP->>UP: Mostra pagamento + checkbox (obrigatório)
    UP->>PP: Confirma pagamento e autorização
    PP->>SPI: pacs.008 (Pix imediato)
    SPI->>PP: pacs.002 (liquidação)
    PP->>SPI: pain.012 (status=true, MandateStatus='CFDB')
    SPI->>PR: Disponibiliza pain.012
    PR->>API: PATCH /rec/{id} (status="confirmada")
    PR->>UR: Notifica sucesso
```

---

### 🔹 Jornada 4 – Pagamento/Agendamento + Oferta de Pix Automático (opcional)

> **Fonte:** Guia do Pix Automático, seção 3.5  
> _"O usuário pagador realiza ou agenda o pagamento de uma cobrança e apenas APÓS concluída a operação é ofertada a possibilidade de autorizar o Pix Automático."_

#### ✅ Jornada 4 – Sucesso na Autorização

```mermaid
sequenceDiagram
    autonumber
    PR->>API: POST /cobv (cobrança com vencimento)
    PR->>API: POST /rec (recorrência)
    API-->>PR: txid, location
    PR->>UR: Gera QR Code composto (26-51 + 80-99)
    UR->>UP: Exibe QR Code
    UP->>PP: Lê e paga
    PP->>SPI: pacs.008
    SPI->>PP: pacs.002 (liquidação)
    PP->>UP: Oferta: "Quer usar Pix Automático nas próximas cobranças?"
    UP->>PP: Aceita
    PP->>SPI: pain.012 (status=true)
    SPI->>PR: Disponibiliza pain.012
    PR->>API: PATCH /rec/{id} (status="confirmada")
    PR->>UR: Notifica sucesso
```

---

## 🔴 3. Fluxos de Cancelamento da Recorrência

### 🔹 Cancelamento por Iniciativa do Recebedor (`pain.011`)

> **Fonte:** Guia do Pix Automático, seção 3.7  
> _"O PSP recebedor deve enviar ao PSP pagador uma mensagem pain.011 de cancelamento da recorrência."_

```mermaid
sequenceDiagram
    autonumber
    UR->>PR: Solicita cancelamento
    PR->>API: PATCH /rec/{id} (status="cancelada", motivoCancelamento)
    API-->>PR: 200 OK
    PR->>PP: pain.011 (domínio='AUTO', finalidade="CANCEL")
    PP->>UP: Notifica: "Pix Automático cancelado"
    PP->>PR: camt.029 (confirmação)
    PR->>UR: Confirma cancelamento
```

---

## 📌 4. Resumo das Mensagens ISO 20022

| Mensagem   | Sentido  | Finalidade                                         | Fonte       |
| ---------- | -------- | -------------------------------------------------- | ----------- |
| `pain.009` | PR → PP  | Solicita autorização (Jornada 1)                   | Guia, 3.2   |
| `pain.011` | PR → PP  | Cancela recorrência (iniciativa do recebedor)      | Guia, 3.7   |
| `pain.012` | PP → PR  | Confirma/rejeita autorização                       | Guia, 3.2   |
| `pain.013` | PR → PP  | Instrução de pagamento ou cancelamento             | Guia, 4.1   |
| `pain.014` | PP → PR  | Resposta ao agendamento                            | Guia, 4.1   |
| `camt.029` | PP → PR  | Confirmação de recebimento da pain.011 ou pain.013 | Guia, 3.7   |
| `camt.055` | PP → PR  | Cancela instrução por falha pós-envio              | Guia, 5.2   |
| `pacs.008` | PP → SPI | Ordem de pagamento                                 | Manual, 4.1 |
| `pacs.002` | SPI → PP | Confirmação de liquidação                          | Manual, 4.1 |

---

## 🕒 5. Prazos Regulatórios (ANS)

| Fluxo                       | Mensagem                | Tempo Máximo |
| --------------------------- | ----------------------- | ------------ |
| Autorização (Jornada 1)     | `pain.009` → `pain.012` | 1 minuto     |
| Agendamento                 | `pain.013` → `pain.014` | 2 horas      |
| Cancelamento da recorrência | `pain.011` → `camt.029` | 12 horas     |

---

## 📎 6. Boas Práticas para o PSP Recebedor

1. Use Webhooks da API Pix para receber atualizações de status.
2. Trate `pain.012` com `status=true/false` — ambas são válidas.
3. Respeite os ANS rigorosamente.
4. Gere QR Codes corretamente:
   - Jornada 2: preencha **faixa 26-51** mesmo sem cobrança.
   - Jornada 4: use **dados estáticos + location** para cobrança com vencimento offline.
5. Notifique o UR em todos os eventos (sucesso, rejeição, cancelamento).

