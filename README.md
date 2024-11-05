# DEISI World Meter![deisi world meter](https://github.com/user-attachments/assets/9787619e-9c97-4b4f-9d47-fd608f8da49c)


## Descrição do Projeto

O **DEISI World Meter** é uma aplicação Java que permite a manipulação e análise de dados geográficos e demográficos de países e cidades ao redor do mundo. O sistema é projetado para carregar dados de arquivos CSV, processar as informações e executar consultas específicas sobre essas informações.

## Estrutura do Projeto

O projeto é organizado em várias classes, cada uma com sua própria responsabilidade:

- **Cidade**: Representa uma cidade, contendo atributos como:
  - Código alfa de dois caracteres do país (`alfa2`)
  - Nome da cidade
  - Região
  - População
  - Latitude
  - Longitude
  
  A classe possui um método `toString` para exibir as informações da cidade em um formato legível.

- **InputInvalido**: Armazena informações sobre entradas inválidas durante o processamento de arquivos, incluindo:
  - Nome do arquivo
  - Número de linhas válidas
  - Número de linhas inválidas
  - Primeira linha inválida encontrada

- **Pais**: Representa um país com atributos como:
  - ID
  - Códigos alfa de dois e três caracteres
  - Nome do país
  - Número de ocorrências e cidades associadas

- **Populacao**: Armazena informações demográficas, incluindo:
  - ID
  - Ano
  - População masculina
  - População feminina
  - Densidade populacional

- **Query**: Representa uma consulta a ser executada, contendo:
  - Nome da consulta
  - Argumentos

- **Result**: Armazena o resultado de uma consulta, incluindo:
  - String de resultado
  - Tempo de execução
  - Sucesso
  - Possíveis erros

- **Main**: A classe principal do aplicativo. É responsável por:
  - Carregar arquivos CSV contendo dados de países, cidades e população
  - Processar e validar os dados
  - Permitir ao usuário executar comandos e consultas por meio de uma interface de linha de comando
  - Gerenciar a interação do usuário e fornecer feedback

- **TipoEntidade**: Um enum que define os tipos de entidades que podem ser geridas, como `PAIS`, `CIDADE` e `INPUT_INVALIDO`.

## Funcionalidades

O sistema oferece as seguintes funcionalidades:

- Carregamento de dados de países, cidades e populações a partir de arquivos CSV.
- Validação de dados de entrada e registro de entradas inválidas.
- Execução de várias consultas, incluindo:
  - Contagem de cidades com base em uma população mínima.
  - Recuperação de cidades por país.
  - Cálculo de populações somadas de uma lista de países.
  - E muitas mais funcionalidades, com a possibilidade de adicionar novas consultas facilmente.

## Exemplos de Entrada

Os dados de cidades são carregados a partir de um arquivo CSV com o seguinte formato:

```csv
alfa2,cidade,regiao,populacao,latitude,longitude
ad,andorra la vella,07,20430.0,42.5,1.5166667
ad,canillo,02,3292.0,42.5666667,1.6
ad,encamp,03,11224.0,42.533333299999995,1.5833333

Para executar o projeto, é necessário:

Ter o JDK instalado.
Compilar as classes Java.
Executar a classe Main em um ambiente que suporte entrada de linha de comando.
O projeto espera que os arquivos CSV estejam localizados em um diretório chamado test-files no diretório atual.
