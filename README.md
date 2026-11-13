# 🌱 EcoLog Alimentos

### Sistema Logístico de Resgate e Redistribuição de Excedentes Alimentares

O **EcoLog Alimentos** é um projeto acadêmico desenvolvido no curso de **Análise e Desenvolvimento de Sistemas da UniCesumar**.

O sistema tem como objetivo auxiliar na organização do **resgate, transporte e redistribuição de alimentos excedentes**, conectando pontos comerciais que possuem alimentos disponíveis para doação a instituições receptoras.

A proposta busca reduzir o desperdício de alimentos e tornar o processo de distribuição mais eficiente, priorizando produtos próximos da validade e alimentos que necessitam de refrigeração.

---

## 🎯 Problema

Diariamente, alimentos ainda próprios para consumo são descartados por supermercados, feiras, cooperativas e outros estabelecimentos por estarem próximos do término de sua validade comercial.

Ao mesmo tempo, instituições assistenciais enfrentam dificuldades para localizar esses alimentos e organizar sua coleta antes que eles se deteriorem.

O **EcoLog Alimentos** busca solucionar esse problema através de uma plataforma responsável por organizar as informações e controlar o processo logístico de coleta e entrega.

---

## 🌎 ODS 2 — Fome Zero e Agricultura Sustentável

O projeto está alinhado ao **Objetivo de Desenvolvimento Sustentável 2 da ONU**, com foco na **Meta 2.1**:

> Erradicar a fome e garantir o acesso de todas as pessoas a alimentos seguros, nutritivos e suficientes durante todo o ano.

O EcoLog contribui para esse objetivo ao facilitar o reaproveitamento e a redistribuição de alimentos que poderiam ser desperdiçados.

---

## 💡 Solução Proposta

O sistema permite gerenciar toda a cadeia de resgate de alimentos.

O fluxo principal funciona da seguinte forma:

```text
Ponto de Coleta
      │
      ▼
Registro dos Lotes
      │
      ▼
Ordem de Transporte
      │
      ▼
Priorização dos Alimentos
      │
      ▼
Transporte
      │
      ▼
Instituição Receptora
```

Alimentos com **menor prazo de validade** ou que necessitam de **cadeia refrigerada** recebem maior prioridade no transporte.

---

## 📋 Lista de Requisitos

Requisitos definidos na 1ª entrega (documento `docs/EcoLog Alimentos.pdf`), na ordem do fluxo operacional:

1. **O sistema deve permitir o cadastro de pontos de coleta parceiros**, com razão social, CNPJ, endereço completo de coleta, telefone e e-mail de contato.
2. **O sistema deve permitir o cadastro de instituições receptoras de alimentos**, com razão social, CNPJ, capacidade de armazenamento (refrigerado e seco), endereço de entrega, telefone, e-mail e responsável institucional.
3. **O sistema deve permitir o cadastro de transportadores parceiros**, com razão social, CNPJ/CPF, tipo de veículo (caminhão, van ou furgão), capacidade volumétrica/peso de carga, motorista responsável, telefone e e-mail.
4. **O sistema deve permitir o registro de lotes de alimentos para resgate**, com descrição do produto, categoria, data de validade, quantidade em quilogramas e exigência de conservação térmica.
5. **O sistema deve permitir a criação de uma ordem de transporte logístico**, com código identificador, data agendada de coleta, ponto de coleta de origem, instituição de destino, status operacional inicial e a listagem de lotes de alimentos vinculados à carga.
6. **O sistema deve permitir a consulta e atualização do status de uma ordem de transporte**, com data da alteração, novo status (Pendente, Em Trânsito, Concluída ou Cancelada) e observações operacionais.
7. **O sistema deve permitir o cancelamento de uma ordem de transporte pendente**, com data do cancelamento e justificativa operacional detalhada do cancelamento.

---

## ⚙️ Funcionalidades

Entre as principais funcionalidades previstas estão:

* Cadastro de pontos de coleta parceiros.
* Cadastro de instituições receptoras.
* Cadastro de transportadores parceiros.
* Registro de lotes de alimentos disponíveis para resgate.
* Classificação entre alimentos perecíveis e não perecíveis.
* Criação de ordens de transporte.
* Associação de vários lotes a uma ordem.
* Definição do ponto de coleta de origem.
* Definição da instituição receptora de destino.
* Atualização do status da ordem.
* Cancelamento de ordens pendentes.
* Priorização automática dos lotes para transporte.
* Controle de produtos que necessitam de refrigeração.

---

## 🚚 Status das Ordens

Uma ordem de transporte poderá possuir os seguintes estados:

```text
Pendente
   │
   ▼
Em Trânsito
   │
   ▼
Concluída
```

Também será possível alterar uma ordem para:

```text
Cancelada
```

O cancelamento deverá possuir uma justificativa operacional.

---

## 🧠 Regras de Priorização

Cada lote possui uma prioridade de transporte.

A prioridade poderá considerar fatores como:

* Data de validade.
* Quantidade de dias restantes até o vencimento.
* Necessidade de refrigeração.
* Faixa de temperatura.
* Tipo de alimento.
* Tipo de embalagem.

Os lotes perecíveis possuem regras específicas e podem receber prioridade superior aos alimentos não perecíveis.

### Como a prioridade é calculada (escala de 0 a 100)

| Fator | `LotePerecivel` | `LoteNaoPerecivel` |
| --- | --- | --- |
| Vence hoje | 70 | 35 |
| Vence em 1 dia | 60 | 35 |
| Vence em 2 a 3 dias | 45 | 35 |
| Vence em 4 a 7 dias | 30 | 35 |
| Vence em 8 a 30 dias | 15 | 20 |
| Vence em mais de 30 dias | 15 | 10 |
| Conservação | +30 congelado (máx. ≤ 0 °C), +20 refrigerado (máx. ≤ 8 °C), +5 ambiente | — |
| Volume | — | +10 (≥ 500 kg), +5 (≥ 100 kg) |
| Embalagem | — | +5 se for de vidro (frágil) |

* Lotes vencidos ficam com prioridade **0** e não podem ser cadastrados.
* A prioridade da ordem (`calcularPrioridadeGeral`) é a do seu lote mais urgente.
* A opção **Fila de prioridade** lista as ordens pendentes da mais urgente para a menos urgente.

---

# 🏗️ Arquitetura

O sistema utiliza uma arquitetura em camadas:

```text
Controller
    │
    ▼
 Service
    │
    ▼
Repository
    │
    ▼
  Model
    │
    ▼
  MySQL
```

### Controller

Responsável por receber as solicitações e encaminhá-las para a camada de serviço.

### Service

Responsável pelas regras de negócio, validações e operações do sistema.

### Repository

Responsável pela comunicação e persistência dos dados no banco.

### Model

Contém as entidades e objetos que representam o domínio do sistema.

---

# 🧩 Programação Orientada a Objetos

O projeto utiliza conceitos importantes de **POO**.

## Encapsulamento

Os atributos das entidades são protegidos e manipulados através de métodos.

## Abstração

`LoteAlimento` funciona como uma classe abstrata contendo informações comuns a todos os tipos de alimento.

```java
public abstract int calcularPrioridadeTransporte();
```

## Herança

Existem dois tipos principais de lote:

```text
             LoteAlimento
             <<abstract>>
                /     \
               /       \
              ▼         ▼
    LotePerecivel   LoteNaoPerecivel
```

## Polimorfismo

Cada tipo de lote possui sua própria implementação do método:

```java
calcularPrioridadeTransporte();
```

Dessa forma, cada alimento pode determinar sua prioridade utilizando regras específicas.

## Composição

Uma `OrdemTransporte` possui **um ou mais lotes de alimentos**.

```text
OrdemTransporte
      ◆
      │
      │ 1:N
      ▼
 LoteAlimento
```

---

# 📦 Principais Entidades

### `PontoColeta`

Representa empresas ou locais que disponibilizam alimentos excedentes.

Principais dados:

* Razão social
* CNPJ
* Endereço
* Telefone
* E-mail

---

### `InstituicaoReceptora`

Representa organizações responsáveis por receber os alimentos.

Principais dados:

* Razão social
* CNPJ
* Endereço
* Capacidade de armazenamento
* Capacidade refrigerada
* Telefone
* E-mail
* Responsável institucional

---

### `Transportador`

Representa o parceiro responsável pelo transporte dos alimentos.

Principais dados:

* Razão social
* CNPJ ou CPF
* Tipo de veículo
* Capacidade de carga
* Motorista responsável
* Telefone
* E-mail

---

### `OrdemTransporte`

Entidade principal do sistema.

Responsável por relacionar:

```text
PontoColeta
     │
     ▼
OrdemTransporte
     │
     ▼
InstituicaoReceptora
```

Também contém os lotes transportados e informações como data e status da operação.

---

### `LoteAlimento`

Classe abstrata utilizada como base para os diferentes tipos de alimentos.

Contém:

* Descrição
* Categoria
* Quantidade
* Data de validade

---

### `LotePerecivel`

Representa alimentos que necessitam de maior controle de conservação.

Possui informações adicionais como:

* Temperatura mínima.
* Temperatura máxima.

---

### `LoteNaoPerecivel`

Representa alimentos que não necessitam de cadeia refrigerada.

Possui informações como:

* Tipo de embalagem.

---

# 🗂️ Diagrama de Classes

O diagrama UML representa as principais entidades e seus relacionamentos.

📄 [Abrir o Diagrama de Classes (PDF)](docs/Diagrama%20de%20Classes%20UML.pdf)

---

# 🗄️ Banco de Dados

O sistema utiliza **MySQL 8.0** para persistência dos dados.

Principais tabelas:

```text
tb_ponto_coleta
tb_instituicao_receptora
tb_ordem_transporte
tb_lote_alimento
```

As tabelas utilizam **Primary Keys** e **Foreign Keys** para garantir a integridade dos relacionamentos.

Os lotes estão vinculados às ordens de transporte através de uma chave estrangeira.

```text
tb_ordem_transporte
        │
        │ 1:N
        ▼
tb_lote_alimento
```

A exclusão de uma ordem poderá remover seus lotes relacionados através de:

```sql
ON DELETE CASCADE
```

---

# 🗃️ Diagrama Entidade-Relacionamento

📄 [Abrir o DER (PDF)](docs/DER.pdf)

O script de criação das tabelas está em [`database/schema.sql`](database/schema.sql) e segue exatamente o DER. A herança de `LoteAlimento` é gravada em uma única tabela (`tb_lote_alimento`), e a coluna `tipo_lote` (`PERECIVEL` ou `NAO_PERECIVEL`) indica qual subclasse Java deve ser recriada na leitura.

---

# 🛠️ Tecnologias

### Backend

* Java
* JDK 17 ou 21
* JDBC (driver `mysql-connector-j`)
* Maven (via Maven Wrapper — não precisa instalar)
* JUnit 5 (testes automatizados)

### Banco de Dados

* MySQL 8.0 ou superior

### Modelagem

* UML
* Diagrama de Classes
* Diagrama Entidade-Relacionamento

### Controle de versão

* Git
* GitHub

---

# 📁 Estrutura do Projeto

```text
EcoLog-Alimentos/
├── database/
│   ├── schema.sql                  # criação do banco e das tabelas (DER)
│   └── dados-exemplo.sql           # dados opcionais para demonstração
├── docs/
│   ├── Diagrama de Classes UML.pdf
│   ├── DER.pdf
│   └── EcoLog Alimentos.pdf
├── src/
│   ├── main/
│   │   ├── java/br/com/ecolog/
│   │   │   ├── App.java            # ponto de entrada (monta as camadas)
│   │   │   ├── config/             # ConexaoBanco (JDBC)
│   │   │   ├── controller/         # menus do terminal
│   │   │   ├── model/              # entidades do diagrama de classes
│   │   │   ├── repository/         # acesso ao MySQL (CRUD)
│   │   │   ├── service/            # regras de negócio e validações
│   │   │   └── view/               # leitura/escrita no terminal
│   │   └── resources/
│   │       └── database.properties # configuração padrão da conexão
│   └── test/java/br/com/ecolog/    # testes JUnit
├── .mvn/wrapper/                   # Maven Wrapper
├── mvnw / mvnw.cmd                 # Maven Wrapper (Linux-macOS / Windows)
├── pom.xml
└── README.md
```

---

# ▶️ Como Executar

### 1. Pré-requisitos

| Ferramenta | Versão | Observação |
| --- | --- | --- |
| Java JDK | 17 ou 21 | `java -version` deve mostrar 17+ |
| MySQL Server | 8.0 ou superior | rodando na porta 3306 |
| Git | qualquer | para clonar o repositório |

O Maven **não** precisa ser instalado: o projeto traz o Maven Wrapper (`mvnw`), que baixa a versão correta na primeira execução (requer internet).

<details>
<summary>Instalação rápida das dependências</summary>

**Windows (PowerShell):**

```powershell
winget install EclipseAdoptium.Temurin.17.JDK
winget install Oracle.MySQL
```

**Ubuntu/Debian:**

```bash
sudo apt install openjdk-17-jdk mysql-server
```

**macOS (Homebrew):**

```bash
brew install openjdk@17 mysql
brew services start mysql
```

</details>

### 2. Clonar o repositório

```bash
git clone https://github.com/judesune/EcoLog-Alimentos.git
cd EcoLog-Alimentos
```

### 3. Criar o banco de dados

```bash
mysql -u root -p < database/schema.sql
```

Opcional — carregar dados de exemplo (rode uma única vez):

```bash
mysql -u root -p --default-character-set=utf8mb4 < database/dados-exemplo.sql
```

### 4. Configurar a conexão

Por padrão o sistema conecta em `jdbc:mysql://localhost:3306/ecolog_alimentos` com o usuário `root` **sem senha**. Se o seu MySQL tiver senha (ou outro usuário/porta), defina as variáveis de ambiente antes de executar:

| Variável | Exemplo |
| --- | --- |
| `ECOLOG_DB_URL` | `jdbc:mysql://localhost:3306/ecolog_alimentos` |
| `ECOLOG_DB_USUARIO` | `root` |
| `ECOLOG_DB_SENHA` | `minhaSenha` |

```powershell
# Windows (PowerShell)
$env:ECOLOG_DB_SENHA = "minhaSenha"
```

```bash
# Linux / macOS
export ECOLOG_DB_SENHA="minhaSenha"
```

### 5. Compilar e executar

**Windows:**

```powershell
.\mvnw.cmd package
java -jar target\ecolog-alimentos.jar
```

**Linux / macOS:**

```bash
./mvnw package
java -jar target/ecolog-alimentos.jar
```

O `package` compila, roda os testes e gera um único `.jar` executável (com o driver do MySQL embutido). Como o sistema roda na JVM, o mesmo `.jar` funciona em Windows, Linux e macOS.

### 6. Testes automatizados

```bash
./mvnw test
```

Os testes de modelo rodam sempre. O teste de integração com o MySQL (`PersistenciaMySqlTest`, que executa o CRUD completo e confere o `ON DELETE CASCADE`) só roda quando a variável `ECOLOG_DB_TESTE=true` está definida e o banco já foi criado; ele apaga os registros que cria.

---

# ✅ Atendimento à 2ª Entrega

| Critério | Onde está no código |
| --- | --- |
| **Classe abstrata** | `model/LoteAlimento` (método abstrato `calcularPrioridadeTransporte()`), `repository/RepositorioJdbc`, `controller/MenuController` |
| **Interface** | `repository/Repositorio<T>` — contrato do CRUD implementado pelos repositórios |
| **Herança** | `LotePerecivel` e `LoteNaoPerecivel` estendem `LoteAlimento`; repositórios estendem `RepositorioJdbc`; controllers estendem `MenuController` |
| **Polimorfismo (`@Override`)** | `calcularPrioridadeTransporte()`, `validar()`, `getTipoLote()` e `toString()` em cada tipo de lote; `OrdemTransporte.calcularPrioridadeGeral()` chama o método de cada lote sem saber seu tipo |
| **Composição 1:N** | `OrdemTransporte` possui a lista de `LoteAlimento` (só alterada por `adicionarLote`/`removerLote`); no banco, `ON DELETE CASCADE` |
| **Encapsulamento** | atributos `private` com getters/setters; a lista de lotes é exposta como somente leitura |
| **CRUD da entidade principal** | `OrdemTransporte`: criar, listar, consultar, alterar dados, gerenciar lotes, atualizar status, cancelar e excluir |
| **CRUD de apoio** | `PontoColeta` e `InstituicaoReceptora`: cadastrar, listar, consultar, alterar e excluir |
| **Persistência em BD** | JDBC com `PreparedStatement` e transações (`repository/`), script em `database/schema.sql` compatível com as classes |
| **Arquitetura em camadas** | `controller` → `service` → `repository` → `model` → MySQL |

### Regras de negócio implementadas

* Fluxo de status: **Pendente → Em Trânsito → Concluída**, e **Pendente → Cancelada**. Concluída e Cancelada são finais.
* Somente ordens **pendentes** podem ter data, origem, destino ou lotes alterados, e somente elas podem ser canceladas.
* Toda ordem precisa ter **pelo menos um lote** (composição 1..*).
* Lotes vencidos ou que vencem antes da data agendada são recusados.
* A carga refrigerada de uma ordem não pode exceder a **capacidade refrigerada** da instituição de destino.
* Pontos de coleta e instituições com ordens vinculadas não podem ser excluídos; CNPJ não pode se repetir.
* Ordens em trânsito não podem ser excluídas.

---

# 📅 Cronograma

| Data       | Atividade                                    | Etapa      | Responsável     |
| ---------- | -------------------------------------------- | ---------- | --------------- |
| 06/09/2026 | Definição da ODS 2.1, escopo e requisitos    | 1ª Entrega | Vitor           |
| 07/09/2026 | Modelagem do Diagrama de Classes UML e DER   | 1ª Entrega | João            |
| 10/09/2026 | Justificativa técnica, GitHub e documentação | 1ª Entrega | Juliano         |
| 15/10/2026 | Script MySQL e configuração JDBC             | 2ª Entrega | Juliano e João  |
| 25/10/2026 | Implementação das classes e conceitos de POO | 2ª Entrega | Vitor e João    |
| 25/10/2026 | Implementação do CRUD de OrdemTransporte     | 2ª Entrega | Vitor e Juliano |
| 30/10/2026 | Testes, homologação e revisão dos commits    | 2ª Entrega | Equipe          |

---

# 👨‍💻 Equipe

Projeto desenvolvido por:

* **João Gabriel Caraçato Pastorelli**
* **Juliano Rafael de Oliveira Hauari**
* **Vitor Gabriel da Silva Duarte**

Curso de **Análise e Desenvolvimento de Sistemas — UniCesumar**.

---

# 📚 Projeto Acadêmico

**Atividade de Estudo Programada (AEP)**
**4º Semestre — 2026.2**

O projeto possui finalidade acadêmica e busca aplicar conceitos de:

* Programação Orientada a Objetos.
* Modelagem UML.
* Banco de Dados Relacional.
* Arquitetura de Software.
* CRUD.
* Persistência de dados.
* Desenvolvimento colaborativo utilizando Git e GitHub.

---

## 🔗 Repositório

[GitHub — EcoLog Alimentos](https://github.com/judesune/EcoLog-Alimentos)
