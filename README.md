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

Esta seção apresenta o procedimento completo para configurar o ambiente e executar o **EcoLog Alimentos** localmente em Windows, Linux ou macOS.

## 1. Pré-requisitos

Antes de iniciar, instale os seguintes componentes:

| Ferramenta | Versão | Finalidade |
|---|---|---|
| Java JDK | 17 ou superior | Compilação e execução do sistema |
| MySQL Server | 8.0 ou superior | Banco de dados |
| Git | Qualquer versão recente | Clonagem do repositório |

O Maven não precisa ser instalado separadamente. O projeto utiliza o **Maven Wrapper**, que baixa automaticamente a versão necessária na primeira execução.

É necessário ter conexão com a internet durante a primeira compilação para baixar o Maven e as dependências do projeto.

### Instalação no Windows

A instalação pode ser realizada pelos comandos:

```powershell
winget install EclipseAdoptium.Temurin.17.JDK
winget install Oracle.MySQL
```

O Git pode ser instalado em:

https://git-scm.com/downloads

### Instalação no Ubuntu/Debian

```bash
sudo apt update
sudo apt install openjdk-17-jdk mysql-server git
```

Inicie o MySQL:

```bash
sudo systemctl start mysql
```

### Instalação no macOS

Utilizando Homebrew:

```bash
brew install openjdk@17 mysql git
brew services start mysql
```

Consulte as documentações oficiais para outras distribuições e métodos de instalação:

- Java: https://adoptium.net/
- MySQL: https://dev.mysql.com/downloads/mysql/
- Git: https://git-scm.com/downloads

## 2. Verificar os pré-requisitos

Abra um terminal e verifique se as ferramentas estão disponíveis:

**Windows (PowerShell):**

```powershell
java -version
git --version
Get-Service *mysql*
```

**Linux/macOS:**

```bash
java -version
git --version
mysql --version
```

O Java deve apresentar a versão 17 ou superior, e o serviço MySQL deve estar em execução.

> **Importante:** no Windows, o servidor MySQL pode estar instalado e funcionando mesmo quando o comando `mysql` não é reconhecido. Nesse caso, será necessário adicionar a pasta `bin` da instalação do MySQL ao PATH.

Exemplo para MySQL 8.4 no Windows:

```powershell
$mysqlBin = "C:\Program Files\MySQL\MySQL Server 8.4\bin"
$env:Path += ";$mysqlBin"
```

O caminho pode variar conforme a versão e o local escolhido durante a instalação.

Confirme a disponibilidade do cliente:

```powershell
mysql --version
```

## 3. Clonar o repositório

Escolha uma pasta para armazenar o projeto.

**Windows (PowerShell):**

```powershell
New-Item -ItemType Directory -Force "$HOME\Projetos" | Out-Null
cd "$HOME\Projetos"

git clone https://github.com/judesune/EcoLog-Alimentos.git
cd EcoLog-Alimentos
```

**Linux/macOS:**

```bash
mkdir -p ~/Projetos
cd ~/Projetos

git clone https://github.com/judesune/EcoLog-Alimentos.git
cd EcoLog-Alimentos
```

Caso o repositório já esteja clonado, não execute `git clone` novamente na mesma pasta. Entre no diretório existente.

Para atualizar uma cópia local sem descartar alterações, utilize:

```bash
git pull --ff-only
```

## 4. Criar o banco de dados

O EcoLog utiliza o MySQL para armazenar os pontos de coleta, instituições receptoras, ordens de transporte e lotes de alimentos.

O script de criação está disponível em:

```text
database/schema.sql
```

Execute-o uma única vez, na primeira configuração do banco.

### Windows (PowerShell)

```powershell
cmd /c "mysql -u root -p < database\schema.sql"
```

### Linux/macOS

```bash
mysql -u root -p < database/schema.sql
```

O terminal solicitará a senha do usuário MySQL escolhido.

Se estiver utilizando o usuário `root` com senha, informe a senha definida durante a instalação do MySQL.

Caso o usuário não possua senha, basta pressionar Enter.

O script cria automaticamente o banco `ecolog_alimentos` e suas tabelas.

### Verificar a criação do banco

Execute:

```bash
mysql -u root -p -e "SHOW DATABASES LIKE 'ecolog_alimentos';"
```

O banco deverá aparecer entre os resultados.

### Dados de demonstração (opcional)

O projeto disponibiliza registros de exemplo para facilitar os primeiros testes.

O arquivo está localizado em:

```text
database/dados-exemplo.sql
```

**Windows (PowerShell):**

```powershell
cmd /c "mysql -u root -p --default-character-set=utf8mb4 < database\dados-exemplo.sql"
```

**Linux/macOS:**

```bash
mysql -u root -p --default-character-set=utf8mb4 < database/dados-exemplo.sql
```

Os dados de demonstração devem ser importados somente uma vez, preferencialmente logo após a criação das tabelas em um banco vazio.

> **Atenção:** o script utiliza comandos INSERT convencionais. Executá-lo novamente poderá duplicar registros e provocar conflitos nos relacionamentos. Não o execute repetidamente em um banco que já possui dados.

## 5. Configurar a conexão com o MySQL

O arquivo de configuração padrão é:

```text
src/main/resources/database.properties
```

Seu conteúdo inicial é:

```properties
db.url=jdbc:mysql://localhost:3306/ecolog_alimentos?allowPublicKeyRetrieval=true&serverTimezone=America/Sao_Paulo
db.usuario=root
db.senha=
```

Por padrão, o sistema tenta conectar ao banco local utilizando o usuário `root` sem senha.

Caso seu MySQL utilize senha ou outro usuário, existem duas alternativas.

### Alternativa A — Variáveis de ambiente (recomendada)

O sistema permite sobrescrever as configurações padrão através das variáveis:

| Variável | Finalidade | Exemplo |
|---|---|---|
| `ECOLOG_DB_URL` | Endereço e parâmetros da conexão | `jdbc:mysql://localhost:3306/ecolog_alimentos?allowPublicKeyRetrieval=true&serverTimezone=America/Sao_Paulo` |
| `ECOLOG_DB_USUARIO` | Usuário do MySQL | `root` |
| `ECOLOG_DB_SENHA` | Senha do usuário MySQL | `minhaSenha` |

**Windows (PowerShell):**

```powershell
$env:ECOLOG_DB_URL = "jdbc:mysql://localhost:3306/ecolog_alimentos?allowPublicKeyRetrieval=true&serverTimezone=America/Sao_Paulo"
$env:ECOLOG_DB_USUARIO = "root"
$env:ECOLOG_DB_SENHA = "minhaSenha"
```

**Linux/macOS:**

```bash
export ECOLOG_DB_URL="jdbc:mysql://localhost:3306/ecolog_alimentos?allowPublicKeyRetrieval=true&serverTimezone=America/Sao_Paulo"
export ECOLOG_DB_USUARIO="root"
export ECOLOG_DB_SENHA="minhaSenha"
```

Substitua os exemplos pelas credenciais configuradas no seu servidor MySQL.

As variáveis permanecem disponíveis durante a sessão atual do terminal. Em uma nova sessão, será necessário configurá-las novamente.

Não compartilhe suas credenciais e não as armazene em arquivos versionados no GitHub.

### Alternativa B — Autenticação interativa

Também é possível iniciar o sistema sem configurar variáveis de ambiente.

Se a conexão inicial falhar, o EcoLog apresentará:

```text
[ERRO] Não foi possível conectar ao MySQL...
Deseja informar o usuário e a senha do MySQL? (s/n):
```

Responda `s` para informar as credenciais.

Em seguida:

```text
Usuário do MySQL [root]:
```

Pressione Enter para manter o usuário `root` ou informe outro usuário MySQL já existente.

Depois:

```text
Senha do MySQL (Enter se não tiver):
```

Informe a senha correspondente ao usuário escolhido.

**A senha não será exibida durante a digitação quando o programa estiver executando em um terminal interativo.** Não aparecerão letras nem asteriscos. Digite normalmente e pressione Enter.

> A senha solicitada é a senha do usuário MySQL, não a senha do Windows, do macOS ou uma senha própria do EcoLog. O usuário `root` é criado pelo MySQL durante sua configuração. O usuário `admin`, por exemplo, só poderá ser utilizado se tiver sido criado previamente no servidor.

## 6. Compilar e executar o projeto

Com os pré-requisitos instalados e o banco configurado, entre na pasta raiz do projeto.

### Windows (PowerShell)

**Compilar:**

```powershell
.\mvnw.cmd clean package
```

**Executar:**

```powershell
java -jar target\ecolog-alimentos.jar
```

### Linux/macOS

**Compilar:**

```bash
./mvnw clean package
```

Caso seja necessário conceder permissão de execução ao Maven Wrapper:

```bash
chmod +x mvnw
```

**Executar:**

```bash
java -jar target/ecolog-alimentos.jar
```

O comando `package` realiza as seguintes operações:

1. Remove arquivos de compilações anteriores.
2. Compila os arquivos Java.
3. Executa os testes automatizados.
4. Empacota as dependências necessárias.
5. Gera o JAR executável.

O arquivo final será:

```text
target/ecolog-alimentos.jar
```

A classe principal e o driver JDBC do MySQL já estão configurados no pacote executável.

Após a inicialização e a conexão bem-sucedida com o banco, o menu principal será apresentado no terminal.

### Codificação do terminal no Windows

Caso os caracteres acentuados apareçam incorretamente, configure o terminal para UTF-8 antes de iniciar o Java:

```powershell
chcp 65001
```

Depois, execute novamente:

```powershell
java -jar target\ecolog-alimentos.jar
```

## 7. Executar os testes automatizados

O projeto utiliza JUnit 5 para verificar o comportamento das classes e das operações de persistência.

### Testes unitários

Os testes unitários são executados normalmente e não exigem uma conexão com o MySQL.

**Windows:**

```powershell
.\mvnw.cmd test
```

**Linux/macOS:**

```bash
./mvnw test
```

### Teste de integração com MySQL

O projeto também possui o teste `PersistenciaMySqlTest`, que verifica operações de persistência no banco, incluindo:

- Cadastro e consulta de pontos de coleta.
- Cadastro e consulta de instituições receptoras.
- Criação e atualização de ordens de transporte.
- Associação e remoção de lotes.
- Atualização de status.
- Integridade referencial e exclusão em cascata.

Esse teste é ignorado por padrão.

Para habilitá-lo, defina a variável de ambiente `ECOLOG_DB_TESTE=true` e configure as credenciais de conexão.

**Windows (PowerShell):**

```powershell
$env:ECOLOG_DB_TESTE = "true"
$env:ECOLOG_DB_USUARIO = "root"
$env:ECOLOG_DB_SENHA = "minhaSenha"

.\mvnw.cmd test
```

**Linux/macOS:**

```bash
export ECOLOG_DB_TESTE=true
export ECOLOG_DB_USUARIO=root
export ECOLOG_DB_SENHA="minhaSenha"

./mvnw test
```

Se o usuário MySQL não possuir senha, a variável `ECOLOG_DB_SENHA` pode ser omitida.

> **Atenção:** o teste de integração cria registros temporários e realiza operações de exclusão durante a verificação. Execute-o somente em um banco de desenvolvimento ou testes, nunca em um banco de produção.

Os testes unitários continuam sendo executados normalmente mesmo quando o teste de integração está desabilitado.

## 8. Resolução de problemas comuns

### `mysql` não é reconhecido como comando

O cliente MySQL pode estar instalado sem que seu diretório esteja configurado no PATH do Windows.

Adicione a pasta `bin` da instalação à sessão atual do PowerShell:

```powershell
$env:Path += ";C:\Program Files\MySQL\MySQL Server 8.4\bin"
```

Adapte o caminho caso sua instalação esteja em outro diretório.

### `Access denied for user 'root'@'localhost'`

O usuário ou a senha informados não correspondem às credenciais do servidor MySQL.

Verifique a autenticação diretamente pelo terminal:

```bash
mysql -u root -p
```

Depois utilize as mesmas credenciais na configuração do EcoLog.

Não é necessário criar um usuário `admin`. O usuário informado precisa existir no MySQL e possuir as permissões necessárias para acessar o banco.

### `Could not find or load main class`

Verifique se a compilação terminou com:

```text
BUILD SUCCESS
```

Caso contrário, confira os erros apresentados pelo Maven.

Execute novamente:

```powershell
.\mvnw.cmd clean package
```

No Windows. No Linux/macOS:

```bash
./mvnw clean package
```

Confirme também a existência do arquivo:

```text
target/ecolog-alimentos.jar
```

### Banco de dados não encontrado

Confirme se o script `database/schema.sql` foi executado e se o banco `ecolog_alimentos` existe.

```sql
SHOW DATABASES LIKE 'ecolog_alimentos';
```

Verifique também se o MySQL está ativo e se a URL JDBC aponta para o endereço, porta e banco corretos.

### Pasta do repositório já existe

Caso o Git apresente:

```text
fatal: destination path 'EcoLog-Alimentos' already exists
```

A pasta já existe. Entre nela em vez de clonar novamente:

```bash
cd EcoLog-Alimentos
```

Para atualizar uma cópia existente:

```bash
git pull --ff-only
```

---

## 9. Resumo dos comandos

Depois da configuração inicial, estes são os comandos utilizados normalmente para compilar e iniciar o EcoLog.

**Windows (PowerShell):**

```powershell
cd "$HOME\Projetos\EcoLog-Alimentos"

.\mvnw.cmd clean package

java -jar target\ecolog-alimentos.jar
```

**Linux/macOS:**

```bash
cd ~/Projetos/EcoLog-Alimentos

./mvnw clean package

java -jar target/ecolog-alimentos.jar
```

O MySQL precisa estar em execução, e as credenciais devem estar configuradas para a sessão atual ou ser informadas durante a inicialização do programa.

**Após a primeira configuração do ambiente, não é necessário recriar o banco de dados nem reinstalar as dependências a cada execução.**

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

*Datas são apenas uma estimativa.

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
