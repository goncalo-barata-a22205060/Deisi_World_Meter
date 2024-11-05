package pt.ulusofona.aed.deisiworldmeter;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;


public class Main {

    private static final float EARTH_RADIUS = 6371.0f; // Raio da Terra em KM
    static ArrayList<Pais> paises = new ArrayList();
    static ArrayList<Cidade> cidades = new ArrayList();
    static ArrayList<InputInvalido> invalidos = new ArrayList();

    static ArrayList<Populacao> populacao = new ArrayList<>();


    public static void main(String[] args) {
        System.out.println("Welcome to DEISI World Meter");

        long start = System.currentTimeMillis();
        boolean parseok = parseFiles(new File("test-files"));
        if (!parseok) {
            System.out.println("Error loading files");
            return;
        }
        long end = System.currentTimeMillis();
        System.out.println("Loaded files in " + (end - start) + " ms");
        System.out.println();
        Result result = execute("HELP");
        System.out.println(result.result);
        Scanner in = new Scanner(System.in);
        String line;

        ArrayList teste = getObjects(TipoEntidade.INPUT_INVALIDO);

        for (Object o : teste) {
            System.out.println(o);

        }


        do {
            System.out.print("> ");
            line = in.nextLine(); // Read input at the start of the loop

            if (line != null && !line.equals("QUIT")) {
                start = System.currentTimeMillis();
                result = execute(line);
                end = System.currentTimeMillis();

                if (!result.success) {
                    System.out.println("Error; " + result.error);
                } else {
                    System.out.println(result.result);
                    System.out.println(("(took " + (end - start) + " ms)"));
                }
            }
        } while (line != null && !line.equals("QUIT"));
    }


    public static boolean parseFiles(File folder){


        paises = new ArrayList<>();
        cidades = new ArrayList<>();
        invalidos = new ArrayList<>();
        populacao = new ArrayList<>();



        File paisesFile = new File(folder,"paises.csv");
        Scanner scanner1 = null;

        try {
            scanner1 = new Scanner(paisesFile);
        } catch (FileNotFoundException e){
            return false;
        }

        int linhaOK_paises = 0;
        int linhaNOK_paises = 0;
        int primeiraLinhaNOK_paises = -1;


        String linhaInicial1 = scanner1.nextLine();

        if (linhaInicial1.startsWith("id,alfa2,alfa3,nome")){
            while (scanner1.hasNextLine()){
                String linha = scanner1.nextLine();
                String[] partes = linha.split(",");
                if (partes.length == 4) {
                    int id = Integer.parseInt(partes[0].trim());
                    String alfa2 = partes[1].trim();
                    String alfa3 = partes[2].trim();
                    String nomePais = partes[3].trim();




                    boolean idRepetido = true;
                    for (Pais pais : paises) {
                        if (pais.id == id) {
                            idRepetido = false;
                        }
                    }
                    if (idRepetido) {
                        Pais pais = new Pais(id, alfa2, alfa3, nomePais);
                        paises.add(pais);
                        linhaOK_paises++;
                    } else {
                        if (primeiraLinhaNOK_paises == -1) {
                            primeiraLinhaNOK_paises = linhaOK_paises + 2;
                        }
                        linhaNOK_paises++;
                    }

                } else {
                    if (primeiraLinhaNOK_paises == -1) {
                        primeiraLinhaNOK_paises = linhaOK_paises + 2;
                    }
                    linhaNOK_paises++;
                }

            }


        }

        //ficheiro cidades.csv


        int linhaOK_cidades = 0;
        int linhaNOK_cidades = 0;
        int primeiraLinhaNOK_cidades = -1;



        File cidadesFile = new File(folder,"cidades.csv");
        Scanner scanner2 = null;

        try {
            scanner2 = new Scanner(cidadesFile);
        } catch (FileNotFoundException e){
            return false;
        }

        scanner2.nextLine();

        while (scanner2.hasNextLine()) {
            String linha = scanner2.nextLine();
            String[] partes = linha.split(",");

            if (partes.length != 6){
                if (primeiraLinhaNOK_cidades == -1) {
                    primeiraLinhaNOK_cidades = linhaOK_cidades + 2;
                }
                linhaNOK_cidades++;
                continue;
            }

            if (partes[0].isEmpty() || partes[2].isEmpty() || partes[3].isEmpty() || partes[4].isEmpty() || partes[5].isEmpty()){
                if (primeiraLinhaNOK_cidades == -1) {
                    primeiraLinhaNOK_cidades = linhaOK_cidades + 2;
                }
                linhaNOK_cidades++;
                continue;
            }

            if(partes.length == 6){
                String alfa2 = partes[0].trim();
                String cidade = partes[1].trim();
                String regiao = partes[2].trim();
                float populacao = Float.parseFloat(partes[3].trim());
                double latitude = Double.parseDouble(partes[4].trim());
                double longitude = Double.parseDouble(partes[5].trim());
                boolean paisExiste = false;
                for (Pais pais : paises) {
                    if (pais.alfa2.equals(alfa2)) {
                        paisExiste = true;
                        break;
                    }
                }
                if (paisExiste) {
                    Cidade objetoCidade = new Cidade(alfa2, cidade, regiao, (int)populacao, latitude, longitude);
                    cidades.add(objetoCidade);
                    linhaOK_cidades++;
                } else {
                    if (primeiraLinhaNOK_cidades == -1) {
                        primeiraLinhaNOK_cidades = linhaOK_cidades + 2;
                    }
                    linhaNOK_cidades++;
                }
            } else {
                if (primeiraLinhaNOK_cidades == -1) {
                    primeiraLinhaNOK_cidades = linhaOK_cidades + 2;
                }
                linhaNOK_cidades++;
            }

        }

        ArrayList<Pais> paisesRemover = new ArrayList<>();
        for (Pais pais : paises) {
            boolean cidadeExiste = false;
            for (Cidade cidade : cidades) {
                if (cidade.alfa2.equals(pais.alfa2)) {
                    cidadeExiste = true;
                    break;
                }
            }
            if (!cidadeExiste) {
                paisesRemover.add(pais);
                linhaOK_paises--;


                if (primeiraLinhaNOK_paises == -1) {
                    primeiraLinhaNOK_paises = linhaOK_paises + 2;
                }
                linhaNOK_paises++;

            }
        }
        paises.removeAll(paisesRemover);


        //ficheiro populacao.csv


        int linhaOK_populacao = 0;
        int linhaNOK_populacao = 0;
        int primeiraLinhaNOK_populacao = -1;



        File populacaoFile = new File(folder,"populacao.csv");
        Scanner scanner3 = null;

        try {
            scanner3 = new Scanner(populacaoFile);
        } catch (FileNotFoundException e){
            return false;
        }

        String linhaInicial3 = scanner3.nextLine();
        if (linhaInicial3.startsWith("id,ano,populacao masculina,populacao feminina,densidade")) {
            while (scanner3.hasNextLine()) {
                String linha = scanner3.nextLine();
                String[] partes = linha.split(",");
                if (partes.length == 5) {
                    if(!partes[1].trim().equals("Medium")){
                        linhaOK_populacao++;
                        int id = Integer.parseInt(partes[0].trim());
                        int ano = Integer.parseInt(partes[1].trim());
                        int masculino = Integer.parseInt(partes[2].trim());
                        int feminino = Integer.parseInt(partes[3].trim());
                        float densidade = Float.parseFloat(partes[4].trim());

                        if (id > 700) {
                            for (Pais pais : paises) {
                                if (pais.id == id) {
                                    pais.ocorrencias++;
                                }
                            }
                        }
                        Populacao pop = new Populacao(id, ano, masculino, feminino, densidade);
                        populacao.add(pop);
                        linhaOK_populacao++;
                    }

                } else {
                    if (primeiraLinhaNOK_populacao == -1) {
                        primeiraLinhaNOK_populacao = linhaOK_populacao + 2;
                    }
                    linhaNOK_populacao++;
                }
            }
        }

        InputInvalido invalido1 = new InputInvalido("paises.csv", linhaOK_paises, linhaNOK_paises, primeiraLinhaNOK_paises);
        invalidos.add(invalido1);

        InputInvalido invalido2 = new InputInvalido("cidades.csv", linhaOK_cidades, linhaNOK_cidades, primeiraLinhaNOK_cidades);
        invalidos.add(invalido2);

        InputInvalido invalido3 = new InputInvalido("populacao.csv", linhaOK_populacao, linhaNOK_populacao, primeiraLinhaNOK_populacao);
        invalidos.add(invalido3);




        System.out.println("Paises carregados: " + paises);



        /*for (Pais pais : paises) {
            for (Cidade cidade : cidades) {
                if (pais.alfa2 == cidade.alfa2){
                    pais.verificacidade = true;
                    pais.num_cidades ++;

                }
            }
        }*/


        return true;


    }

    public static ArrayList getObjects(TipoEntidade tipo) {
        if (tipo == TipoEntidade.PAIS){
            return paises;
        } else if (tipo == TipoEntidade.CIDADE){
            return cidades;
        } else {
            return invalidos;
        }
    }

    static Query interpretarComando(String comando){
        String[] partes = comando.split(" ",2); //dividir string comando em duas partes pelo " "

        if (partes.length != 2 && !comando.equals("QUIT")) {
            return null;
        }
        if (partes.length != 2 && !comando.equals("HELP")) {
            return null;
        }

        if (partes.length != 2){
            return null;
        }

        String nome_comando = partes[0];
        String argumentos_comando = partes.length == 2 ? partes[1] : "";

        Query query = new Query();
        query.name = nome_comando;



        switch (nome_comando){
            case "COUNT_CITIES": // <min_population>
                query.args = argumentos_comando.split(" ", 2);
                break;
            case "COUNT_REGIONS": // <min_population>
                query.args = argumentos_comando.split(",");
                break;

            case "GET_DENSITY_BELOW":
                query.args = argumentos_comando.split(" ", 2);
                break;

            case "GET_CITY_POPULATION_RANGE": // <min_population>
                query.args = argumentos_comando.split(" ", 3);
                break;
            case "GET_CITIES_BY_COUNTRY": // <num-results> <country-name>
                query.args = argumentos_comando.split(" ", 2);
                break;
            case "SUM_POPULATIONS": // <countries-list>
                query.args = argumentos_comando.split(",");
                break;
            case "GET_HISTORY": // <year-start> <year-end> <country-name>
                query.args = argumentos_comando.split(" ",3);
                break;

            case "GET_MISSING_HISTORY": // <year-start> <year-end>
                query.args = argumentos_comando.split(" ", 2);
                break;
            case "GET_MOST_POPULOUS": // <num-results>
                query.args = argumentos_comando.split(" ", 2);
                break;
            case "GET_TOP_CITIES_BY_COUNTRY": // <num-results> <country-name>
                query.args = argumentos_comando.split(" ", 2);
                break;
            case "GET_DUPLICATE_CITIES": // <min_population>
                query.args = argumentos_comando.split(" ", 2);
                break;
            case "GET_COUNTRIES_GENDER_GAP": // <min-gender-gap>
                query.args = argumentos_comando.split(" ", 2);
                break;
            case "GET_TOP_POPULATION_INCREASE": // <year-start> <year-end>
                query.args = argumentos_comando.split(" ", 2);
                break;
            case "GET_DUPLICATE_CITIES_DIFFERENT_COUNTRIES": // <min_population>
                query.args = argumentos_comando.split(" ", 2);
                break;
            case "GET_CITIES_AT_DISTANCE": // <distance> <country-name>
                query.args = argumentos_comando.split(" ", 2);
                break;
            case "GET_CITIES_AT_DISTANCE2": // <distance> <country-name>
                query.args = argumentos_comando.split(" ", 2);
                break;
            case "INSERT_CITY": // <alfa2> <city-name> <region> <population>
                query.args = argumentos_comando.split(" ",4);
                break;

            case "REMOVE_COUNTRY": // <country-name>
                query.args = argumentos_comando.split(" ");
                break;
            case "HELP":
                query.args = new String[0];
                break;

            case "QUIT":
                query.args = new String[0];
                break;





            default:
                return null;

        }
        return query;

    }

    static Result execute(String comando){
        Result queryResult = new Result();
        Query comandoNovo = interpretarComando(comando);

        if (comandoNovo != null){
            switch (comandoNovo.name){
                case "COUNT_CITIES": // OBG    //ESTA FEITO // <MIN_POPULATION>
                    int count = 0;
                    int ano = Integer.parseInt(comandoNovo.args[0]);
                    for (Cidade cidade : cidades) {
                        if(cidade.populacao >= ano){
                            count++;
                        }
                    }
                    queryResult.result = String.valueOf(count);
                    queryResult.success = true;
                    break;




                case "GET_CITIES_BY_COUNTRY": //OBG ta feito  // <num-results> <country-name>
                    int numero = Integer.parseInt(comandoNovo.args[0]);
                    String countryName = comandoNovo.args[1];
                    String result = "";

                    Pais pais_do_comando = null;
                    for (Pais pais : paises) {
                        if (pais.nome.equalsIgnoreCase(countryName)) {
                            pais_do_comando = pais;
                            break;
                        }
                    }

                    if (pais_do_comando != null) {
                        int citiesFound = 0;
                        ArrayList<Cidade> citiesList = new ArrayList<>();

                        for (Cidade cidade : cidades) {
                            if (cidade.alfa2.equalsIgnoreCase(pais_do_comando.alfa2)) {

                                citiesList.add(cidade);
                                citiesFound++;



                                if (citiesFound == numero) {
                                    break;
                                }
                            }
                        }

                        //citiesList.sort((c1, c2) -> Integer.compare(c2.populacao, c1.populacao)); // ordena por população decrescente

                        if (citiesList.isEmpty()) {
                            result = "Pais invalido: " + countryName; // aqui e a mensagem quando noa existem cidades na lista
                        } else {
                            for (Cidade cidade : citiesList) {
                                result += cidade.cidade + "\n";
                            }

                        }
                    } else {
                        result = "Pais invalido: " + countryName;
                    }

                    queryResult.result = result;
                    queryResult.success = true;
                    break;


                case "COUNT_REGIONS": //OBG    <LISTA PAISES SEPARADOS VIRGULA>
                    String[] countryNames = comandoNovo.args;
                    int total1 = 0;
                    boolean pelo_menos_1_pais_existe  = false;
                    String resultadoFinal = "";
                    //String pais_nao_existe = ""; variavel para guar nome paises que nao existem


                    for(String countryName1 : countryNames) {
                        Pais country = null;
                        for(Pais pais : paises) {
                            if(pais.nome.equalsIgnoreCase(countryName1)) {
                                country = pais;
                                break;
                            }
                        }

                        if(country != null) {
                            pelo_menos_1_pais_existe  = true;

                            HashSet<String> regioes_unicas = new HashSet<>(); // se puser o hashset no inicio regios repetidas ja nao entram no hashset
                            for(Cidade cidade : cidades) {
                                if(cidade.alfa2.equals(country.alfa2)) {
                                    regioes_unicas.add(cidade.regiao);
                                }
                            }

                            total1 += regioes_unicas.size();
                        }

                    }

                    if(pelo_menos_1_pais_existe ) {
                        resultadoFinal = String.valueOf(total1);
                    } else {
                        resultadoFinal = "-1";
                    }

                    queryResult.result = resultadoFinal;
                    queryResult.success = true;
                    break;




                case "GET_DENSITY_BELOW": //Obg  <densidade_maxima>  <pais>
                    float densidadeMaxima = Float.parseFloat(comandoNovo.args[0]);
                    String nome_pais = String.valueOf(comandoNovo.args[1]);
                    Pais pais_comando = null;
                    HashMap<Integer, Float> ano_densidade = new HashMap<>();
                    String result_final = "";



                    for (Pais paisNome : paises) {
                        if (paisNome.nome.equalsIgnoreCase(nome_pais)){
                            pais_comando = paisNome;

                        }
                    }
                    if (pais_comando != null){
                        for (Populacao pop : populacao) {
                            if (pais_comando.id == pop.id){
                                if (pop.densidade < densidadeMaxima){
                                    ano_densidade.put(pop.ano, pop.densidade);
                                }
                            }
                        }
                    }

                    ArrayList<Integer> anos_ordenados_decrescente = new ArrayList<>(ano_densidade.keySet()); //Cria uma nova lista ArrayList inicializada com as chaves (anos) do HashMap ano_densidade.
                    anos_ordenados_decrescente.sort(Comparator.reverseOrder()); //Ordena a lista anos_ordenados_decrescente em ordem decrescente
                    //anos_ordenados_decrescente.sort(Comparator.naturalOrder()); ordem crescente


                    if (anos_ordenados_decrescente.isEmpty()){
                        result_final = "Sem resultados";
                    } else {
                        for (Integer anoo : anos_ordenados_decrescente) {
                            Float densidade = ano_densidade.get(anoo);
                            result_final += anoo + " - " + densidade + "\n";
                        }

                    }

                    /*ArrayList<Float> anos_ordenados_decrescente = new ArrayList<>(ano_densidade.values());
                    anos_ordenados_decrescente.sort(Comparator.reverseOrder());

                    if (anos_ordenados_decrescente.isEmpty()){
                        result_final = "Sem resultados";                            isto e para ordenar nao pela key mas sim pelo
                    } else {                                                                    que corresponde
                        for (Float densidade : anos_ordenados_decrescente) {
                            for (Map.Entry<Integer, Float> entry : ano_densidade.entrySet()) {
                                if(entry.getValue() == densidade) {
                                    result_final += entry.getKey() + " - " + densidade + "\n";
                                    break;
                                }
                            }
                        }

                    }*/



                    queryResult.result = result_final;
                    queryResult.success = true;
                    break;


                case "SUM_POPULATIONS": //OBG
                    String[] paisesNomes = comandoNovo.args;

                    long totalPopulation = 0;
                    boolean todos_paises_encontrados1 = true;
                    String missingCountries = "";

                    for (String paisNome : paisesNomes) {
                        paisNome = paisNome.trim();

                        boolean paisEncontrado = false;

                        for (Pais pais : paises) {
                            if (pais.nome.equalsIgnoreCase(paisNome)) {
                                paisEncontrado = true;

                                for (Populacao pop : populacao) {
                                    if (pop.id == pais.id && pop.ano == 2024) {
                                        totalPopulation += pop.masculino + pop.feminino;
                                        break; // tirar este break se for para Somar Populações de Todos os Anos Disponíveis
                                    }
                                }

                                break;
                            }
                        }

                        if (!paisEncontrado) { // se o pais nao foi encontrado
                            todos_paises_encontrados1 = false;
                            if (!missingCountries.isEmpty()) {
                                missingCountries += ", ";
                            }
                            missingCountries += paisNome;
                        }
                    }

                    if (!todos_paises_encontrados1) { // se algum dos paises nao existe
                        queryResult.result = "Pais invalido: " + missingCountries;
                    } else { // se esta tudo bem faz a soma das pop
                        queryResult.result = String.valueOf(totalPopulation);
                    }


                    queryResult.success = true;

                    break;


                case "GET_HISTORY": //OBG  ta feito           //<year-start> <year-end> <country-name>
                    if (comandoNovo.args.length != 3) {
                        queryResult.success = false;
                        break;
                    }
                    int startYear = Integer.parseInt(comandoNovo.args[0]);
                    int endYear = Integer.parseInt(comandoNovo.args[1]);
                    String NomePais = comandoNovo.args[2];
                    String historyResult = "";
                    int countResults;

                    /*if (startYear > endYear) {
                        historyResult = "Intervalo inválido";
                    }*/


                    // Encontrar o país pelo nome
                    Pais targetCountry = null;
                    for (Pais pais : paises) {
                        if (pais.nome.equalsIgnoreCase(NomePais)) {
                            targetCountry = pais;
                            break;
                        }
                    }

                    if (targetCountry == null) {
                        queryResult.success = false;
                        queryResult.error = "País não encontrado: " + NomePais;
                        break;
                    }

                    /*countResults = 0;*/
                    for (Populacao pop : populacao) {

                        if (pop.id == targetCountry.id && pop.ano >= startYear && pop.ano <= endYear) {
                            historyResult += pop.ano + ":" + (pop.masculino / 1000) + "k:" + (pop.feminino / 1000) + "k\n";
                            /*countResults++;*/
                        }


                    }
                    /*if (countResults > 3){
                        historyResult = "demasiados resultados";
                    }*/


                    queryResult.success = true;
                    queryResult.result = historyResult;
                    queryResult.error = null;
                    break;

                case "GET_MISSING_HISTORY": //obg
                    int startYear2 = Integer.parseInt(comandoNovo.args[0]);
                    int endYear2 = Integer.parseInt(comandoNovo.args[1]);

                    StringBuilder missingYears = new StringBuilder();
                    HashSet<Pais> paises_adicionados = new HashSet<>();

                    // Conjunto para armazenar combinações de ano e id de país que têm dados de população
                    HashSet<String> populationEntries = new HashSet<>();

                    // Pre-processar os dados de população
                    for (Populacao pop : populacao) {
                        String entry = pop.ano + "-" + pop.id;
                        populationEntries.add(entry);
                    }

                    // Iterar sobre todos os anos no intervalo especificado
                    for (int year = startYear2; year <= endYear2; year++) {
                        ArrayList<Pais> countriesWithMissingEntries = new ArrayList<>();

                        // Iterar sobre todos os países
                        for (Pais pais : paises) {
                            // Verificar se existe uma entrada de população para este ano e país
                            String entry = year + "-" + pais.id;
                            if (!populationEntries.contains(entry)) {
                                countriesWithMissingEntries.add(pais);
                            }
                        }

                        // Adicionar os países faltantes ao resultado
                        for (Pais pais : countriesWithMissingEntries) {
                            if (!paises_adicionados.contains(pais)) {
                                missingYears.append(pais.alfa2).append(":").append(pais.nome).append("\n");
                                paises_adicionados.add(pais);
                            }
                        }
                    }

                    // Se não houver países com entradas em falta, defina o resultado como "Sem resultados"
                    if (missingYears.length() == 0) {
                        queryResult.result = "Sem resultados";
                    } else {
                        queryResult.result = missingYears.toString();
                    }

                    queryResult.success = true;
                    break;


                case "GET_MOST_POPULOUS": //ta feito // nao Obg  // <num-results>

                    if (comandoNovo.args.length != 1) {
                        queryResult.success = false;
                        queryResult.error = "Número incorreto de argumentos para este comando";
                        break;
                    }
                    int maxResults = Integer.parseInt(comandoNovo.args[0]);

                    HashMap<String, Cidade> maxPopCityByCountry = new HashMap<>();
                    for (Cidade cidade : cidades) {
                        String alfa2 = cidade.alfa2;
                        maxPopCityByCountry.computeIfAbsent(alfa2, k -> cidade);
                        if (cidade.populacao > maxPopCityByCountry.get(alfa2).populacao) {
                            maxPopCityByCountry.put(alfa2, cidade);
                        }
                    }


                    ArrayList<Cidade> sortedCities = new ArrayList<>(maxPopCityByCountry.values());
                    sortedCities.sort((c1, c2) -> Integer.compare(c2.populacao, c1.populacao));


                    HashMap<String, Pais> paisesMap = new HashMap<>();
                    for (Pais p : paises) {
                        paisesMap.put(p.alfa2, p);
                    }


                    StringBuilder mostPopulousResult = new StringBuilder();
                    int counttt = 0;
                    for (Cidade sortedCity : sortedCities) {
                        if (counttt == maxResults) {
                            break;
                        }
                        Pais pais = paisesMap.get(sortedCity.alfa2);
                        if (pais != null) {
                            mostPopulousResult.append(pais.nome).append(":").append(sortedCity.cidade).append(":").append(sortedCity.populacao).append("\n");
                        }
                        counttt++;
                    }

                    queryResult.result = mostPopulousResult.toString();
                    queryResult.success = true;
                    break;

                case "GET_TOP_CITIES_BY_COUNTRY": //<num-results> <country-name>
                    // Verifica se há argumentos válidos para a query
                    if (comandoNovo.args.length < 2 || comandoNovo.args[0].isEmpty()) {
                        queryResult.success = false;
                        queryResult.error = "Argumentos inválidos para a query.";
                        break;
                    }


                    int ano_INT = Integer.parseInt(comandoNovo.args[0]);
                    String nomoPAIS = comandoNovo.args[1];
                    Pais paisEncontrado = null;
                    ArrayList<Cidade> cidades_Top = new ArrayList<>();

                    // Itera sobre a lista de países
                    for (Pais pais : paises) {
                        // Verifica se o nome do país corresponde ao nome fornecido na query
                        if (pais.nome.equalsIgnoreCase(nomoPAIS)){
                            // Se corresponder, atribui o país encontrado à variável paisEncontrado e interrompe o loop
                            paisEncontrado = pais;
                            break;
                        }
                    }
                    // Se não foi encontrado um país correspondente, marca a query como não bem-sucedida
                    if (paisEncontrado == null) {
                        queryResult.success = false;
                        break;
                    }

                    // Itera sobre a lista de cidades
                    for (Cidade cidade : cidades) {
                        // Verifica se a população da cidade é maior ou igual a 10000
                        if (cidade.populacao >= 10000){
                            // Verifica se o código alfa-2 da cidade corresponde ao código do país encontrado
                            if (cidade.alfa2.equalsIgnoreCase(paisEncontrado.alfa2)){
                                // Se corresponder, adiciona a cidade à lista de cidades principais
                                cidades_Top.add(cidade);
                            }
                        }
                    }

                    // Ordena a lista de cidades principais com base na população e no nome da cidade
                    cidades_Top.sort((c1, c2) -> {
                        if (c1.populacao != c2.populacao) {
                            return c2.populacao - c1.populacao; // Ordena por população decrescente
                        } else {
                            return c1.cidade.compareToIgnoreCase(c2.cidade); // Se a população for igual, ordena por nome de cidade
                        }
                    });

                    // Inicializa uma string para armazenar o resultado da query
                    String resultado = "";
                    int contagem = 0;

                    // Verifica se o ano fornecido é -1 (sem limite de cidades) ou não
                    if (ano_INT == -1){
                        // Se for -1, adiciona todas as cidades principais à string de resultado
                        for (Cidade cidade : cidades_Top) {
                            resultado += cidade.cidade + ":" + cidade.populacao/1000 + "K" + "\n";
                        }
                    } else {
                        // Se não for -1, adiciona as cidades principais até o número fornecido pelo usuário
                        for(Cidade cidade : cidades_Top){
                            if(contagem < ano_INT){
                                resultado += cidade.cidade + ":" + cidade.populacao/1000 + "K" + "\n";
                                contagem++;
                            } else {
                                break; // Interrompe o loop quando o número de cidades especificado é alcançado
                            }
                        }
                    }

                    // Define o resultado da query e marca como bem-sucedida
                    queryResult.result = resultado;
                    queryResult.success = true;

                    break;



                case "GET_DUPLICATE_CITIES": //<min-population>
                    int populationThreshold = Integer.parseInt(comandoNovo.args[0]);
                    HashMap<String, ArrayList<Cidade>> duplicateCities = new HashMap<>();

                    // Percorra todas as cidades para encontrar duplicatas
                    for (Cidade cidade : cidades) {
                        // Verifique se a população é maior que o limiar especificado
                        if (cidade.populacao >= populationThreshold) {
                            // Se a cidade já estiver na lista de duplicatas, adicione-a
                            if (duplicateCities.containsKey(cidade.cidade)) {
                                duplicateCities.get(cidade.cidade).add(cidade);
                            } else {
                                // Se não, crie uma nova entrada na lista de duplicatas
                                ArrayList<Cidade> duplicates = new ArrayList<>();
                                duplicates.add(cidade);
                                duplicateCities.put(cidade.cidade, duplicates);
                            }
                        }
                    }

                    String resultt = "";
                    for (ArrayList<Cidade> duplicates : duplicateCities.values()) {
                        if (duplicates.size() > 1) { // Verifica se existem duplicatas
                            for (int i = 1; i < duplicates.size(); i++) { // Começa de 1 para evitar a cidade original
                                Cidade city = duplicates.get(i);
                                // Encontre o nome do país correspondente ao código alfa2
                                String countryName1 = "";
                                for (Pais pais : paises) {
                                    if (pais.alfa2.equals(city.alfa2)) {
                                        countryName1 = pais.nome;
                                        break;
                                    }
                                }
                                // Adicione o nome da cidade, o país e a região à saída
                                resultt += city.cidade + " (" + countryName1 + "," + city.regiao + ")\n";
                            }
                        }
                    }

                    // Defina o resultado e marque como sucesso
                    queryResult.result = resultt;
                    queryResult.success = true;
                    break;




                case "GET_COUNTRIES_GENDER_GAP": //nao obg
                    double numeroo = Double.parseDouble(comandoNovo.args[0]);
                    ArrayList<String> resultList = new ArrayList<>();
                    HashMap<Integer, Populacao> populacao2024Map = new HashMap<>();


                    for (Populacao pop : populacao) {
                        if (pop.ano == 2024) {
                            populacao2024Map.put(pop.id, pop);
                        }
                    }

                    // Iterando sobre a lista de países e verificando o gender gap
                    for (Pais pais : paises) {
                        Populacao pop2024 = populacao2024Map.get(pais.id);
                        if (pop2024 != null) {
                            int totalPopulation1 = pop2024.masculino + pop2024.feminino;
                            if (totalPopulation1 > 0) {
                                double genderGap = Math.abs((double)(pop2024.masculino - pop2024.feminino) / totalPopulation1) * 100;
                                if (genderGap >= numeroo) {
                                    String formattedGap = String.format("%.2f", genderGap).replace(',', '.');
                                    resultList.add(pais.nome + ":" + formattedGap);
                                }
                            }
                        }
                    }

                    if (resultList.isEmpty()) {
                        queryResult.result = "Sem resultados";
                    } else {
                        queryResult.result = String.join("\n", resultList);
                    }

                    queryResult.success = true;
                    break;



                case "GET_TOP_POPULATION_INCREASE":
                    int startYear1 = Integer.parseInt(comandoNovo.args[0]);
                    int endYear1 = Integer.parseInt(comandoNovo.args[1]);

                    HashMap<String, Integer> populacaoMap = new HashMap<>();
                    for (Populacao pop : populacao) {
                        populacaoMap.put(pop.id + ":" + pop.ano, pop.masculino + pop.feminino);
                    }

                    PriorityQueue<Map.Entry<String, Double>> topAumentos = new PriorityQueue<>(5, Comparator.comparingDouble(Map.Entry::getValue));

                    for (Pais pais : paises) {
                        for (int anoInicio = startYear1; anoInicio <= endYear1; anoInicio++) {
                            String keyInicio = pais.id + ":" + anoInicio;
                            if (!populacaoMap.containsKey(keyInicio)){
                                continue;
                            }

                            int populacaoInicio = populacaoMap.get(keyInicio);

                            for (int anoFim = anoInicio + 1; anoFim <= endYear1; anoFim++) {
                                String keyFim = pais.id + ":" + anoFim;
                                if (!populacaoMap.containsKey(keyFim)){
                                    continue;
                                }

                                int populacaoFim = populacaoMap.get(keyFim);
                                if (populacaoInicio == 0 || populacaoFim == 0){
                                    continue;
                                }

                                double aumentoPopulacional = ((double) (populacaoFim - populacaoInicio) / populacaoFim) * 100;
                                if (aumentoPopulacional > 0) {
                                    String intervaloAnos = anoInicio + "-" + anoFim;
                                    String paisIntervaloAnos = pais.nome + ":" + intervaloAnos;

                                    if (topAumentos.size() < 5) {
                                        topAumentos.add(new AbstractMap.SimpleEntry<>(paisIntervaloAnos, aumentoPopulacional));
                                    } else if (aumentoPopulacional > topAumentos.peek().getValue()) {
                                        topAumentos.poll();
                                        topAumentos.add(new AbstractMap.SimpleEntry<>(paisIntervaloAnos, aumentoPopulacional));
                                    }
                                }
                            }
                        }
                    }

                    List<Map.Entry<String, Double>> sortedResults = new ArrayList<>(topAumentos);
                    sortedResults.sort((a, b) -> Double.compare(b.getValue(), a.getValue()));

                    StringBuilder resultBuilder = new StringBuilder();
                    for (Map.Entry<String, Double> entry : sortedResults) {
                        String paisIntervaloAnos = entry.getKey();
                        double aumentoPopulacional = entry.getValue();
                        String porcentagemFormatada = String.format("%.2f", aumentoPopulacional).replace(",", ".");
                        resultBuilder.append(paisIntervaloAnos).append(":").append(porcentagemFormatada).append("%\n");
                    }

                    queryResult.result = resultBuilder.toString();
                    queryResult.success = true;
                    break;






                case "GET_DUPLICATE_CITIES_DIFFERENT_COUNTRIES":
                    int populacao_min = Integer.parseInt(comandoNovo.args[0]);
                    HashMap<String, ArrayList<String>> cidades_duplicadas = new HashMap<>();

                    // Percorra todas as cidades para encontrar duplicatas
                    for (Cidade cidade : cidades) {
                        // Verifique se a população é maior que o limiar especificado
                        if (cidade.populacao >= populacao_min) {
                            // Se a cidade já estiver na lista de duplicatas, adicione-a
                            if (cidades_duplicadas.containsKey(cidade.cidade)) {
                                ArrayList<String> countries = cidades_duplicadas.get(cidade.cidade);
                                String nome_pais1 = "";
                                for (Pais pais : paises) {
                                    if (pais.alfa2.equals(cidade.alfa2)) {
                                        nome_pais1 = pais.nome;
                                        break;
                                    }
                                }
                                if (!countries.contains(nome_pais1)) {
                                    countries.add(nome_pais1);
                                    // Ordena os países alfabeticamente
                                    Collections.sort(countries);
                                }
                            } else {
                                // Se não, crie uma nova entrada na lista de duplicatas
                                ArrayList<String> countries = new ArrayList<>();
                                String nome_pais2 = "";
                                for (Pais pais : paises) {
                                    if (pais.alfa2.equals(cidade.alfa2)) {
                                        nome_pais2 = pais.nome;
                                        break;
                                    }
                                }
                                countries.add(nome_pais2);
                                cidades_duplicadas.put(cidade.cidade, countries);
                            }
                        }
                    }

                    StringBuilder resultBuilder1 = new StringBuilder();
                    for (String city : cidades_duplicadas.keySet()) {
                        ArrayList<String> countries = cidades_duplicadas.get(city);
                        if (countries.size() > 1) { // Verifica se existem duplicatas em países diferentes
                            resultBuilder1.append(city).append(": ");
                            for (int i = 0; i < countries.size(); i++) {
                                resultBuilder1.append(countries.get(i));
                                if (i < countries.size() - 1) {
                                    resultBuilder1.append(",");
                                }
                            }
                            resultBuilder1.append("\n");
                        }
                    }

                    // Defina o resultado e marque como sucesso
                    queryResult.result = resultBuilder1.toString();
                    queryResult.success = true;
                    break;







                case "INSERT_CITY": // INSERT_CITY pt Lisboa 01 1000000
                    String alfa2 = comandoNovo.args[0];
                    String cidade = comandoNovo.args[1];
                    String regiao = comandoNovo.args[2];
                    int populacao = Integer.parseInt(comandoNovo.args[3]);
                    String resultado_final_Insert_City = "";

                    boolean paisExiste = false;
                    for (Pais pais : paises) {
                        if (pais.alfa2.equalsIgnoreCase(alfa2)) {
                            paisExiste = true;
                            break;
                        }
                    }

                    Cidade novaCidade = new Cidade(alfa2, cidade, regiao, populacao, 0, 0); // coordenadas nao tem
                    cidades.add(novaCidade);

                    if (!paisExiste) {
                        resultado_final_Insert_City = "Pais invalido";
                    } else {
                        resultado_final_Insert_City = "Inserido com sucesso";
                    }

                    queryResult.result = resultado_final_Insert_City;
                    queryResult.success = true;
                    break;




                case "REMOVE_COUNTRY":
                    String countryToRemove = comandoNovo.args[0];
                    Pais country1 = null;
                    String resultado1 = "";



                    for (Pais pais : paises) {
                        if (pais.nome.trim().equalsIgnoreCase(countryToRemove)) {
                            country1 = pais;
                            break;
                        }
                    }

                    if (country1 == null){
                        queryResult.result = "Pais invalido";
                        queryResult.success = true;
                        break;
                    }


                    ArrayList<Cidade> cidades_remover = new ArrayList<>();

                    for (Cidade cidade2 : cidades) {
                        if (cidade2.alfa2.equalsIgnoreCase(country1.alfa2)){
                            cidades_remover.add(cidade2);
                        }
                    }

                    cidades.removeAll(cidades_remover);
                    paises.remove(country1);
                    resultado1 = "Removido com sucesso";


                    queryResult.result = resultado1;
                    queryResult.success = true;
                    break;


                case "GET_CITIES_AT_DISTANCE":
                    int distance = Integer.parseInt(comandoNovo.args[0]);
                    String nome_paiss = comandoNovo.args[1];

                    Pais pais = null;
                    for (Pais p : paises) {
                        if (p.nome.equalsIgnoreCase(nome_paiss)) {
                            pais = p;
                            break;
                        }
                    }

                    if (pais == null) {
                        queryResult.result = "Pais invalido: " + nome_paiss;
                        queryResult.success = false;
                        break;
                    }


                    ArrayList<Cidade> countryCities = new ArrayList<>();
                    for (Cidade cidadee : cidades) {
                        if (cidadee.alfa2.equalsIgnoreCase(pais.alfa2)) {
                            countryCities.add(cidadee);
                        }
                    }


                    ArrayList<String> resultPairs = new ArrayList<>();
                    for (int i = 0; i < countryCities.size(); i++) {
                        for (int j = i + 1; j < countryCities.size(); j++) {
                            Cidade city1 = countryCities.get(i);
                            Cidade city2 = countryCities.get(j);
                            double calculatedDistance = calculateDistance(city1.latitude, city1.longitude, city2.latitude, city2.longitude);

                            if (Math.abs(calculatedDistance - distance) <= 1) {
                                String cityPair = city1.cidade.compareTo(city2.cidade) < 0
                                        ? city1.cidade + "->" + city2.cidade
                                        : city2.cidade + "->" + city1.cidade;
                                resultPairs.add(cityPair);
                            }
                        }
                    }

                    if (resultPairs.isEmpty()) {
                        queryResult.result = "Sem resultados";
                    } else {
                        queryResult.result = String.join("\n", resultPairs) + "\n";
                    }

                    queryResult.success = true;
                    break;


                case "GET_CITIES_AT_DISTANCE2":
                    int distance2 = Integer.parseInt(comandoNovo.args[0]);
                    String nomepais2 = comandoNovo.args[1];

                    // Encontre o país de origem especificado
                    Pais paisOrigem = null;
                    for (Pais p : paises) {
                        if (p.nome.equalsIgnoreCase(nomepais2)) {
                            paisOrigem = p;
                            break;
                        }
                    }

                    if (paisOrigem == null) {
                        queryResult.result = "Pais invalido: " + nomepais2;
                        queryResult.success = false;
                        break;
                    }

                    // Colete as cidades do país de origem e as cidades dos outros países
                    List<Cidade> cidadesPaisOrigem = new ArrayList<>();
                    List<Cidade> cidadesOutrosPaises = new ArrayList<>();
                    for (Cidade cidadee : cidades) {
                        if (cidadee.alfa2.equalsIgnoreCase(paisOrigem.alfa2)) {
                            cidadesPaisOrigem.add(cidadee);
                        } else {
                            cidadesOutrosPaises.add(cidadee);
                        }
                    }

                    // Use TreeSet to keep elements sorted
                    Set<String> resultPairs2 = new TreeSet<>();

                    // Calcular a distância apenas quando necessário
                    for (Cidade city1 : cidadesPaisOrigem) {
                        for (Cidade city2 : cidadesOutrosPaises) {
                            double calculatedDistance = calculateDistance(city1.latitude, city1.longitude, city2.latitude, city2.longitude);

                            if (Math.abs(calculatedDistance - distance2) <= 1) {
                                String cityPair = city1.cidade.compareTo(city2.cidade) < 0
                                        ? city1.cidade + "->" + city2.cidade
                                        : city2.cidade + "->" + city1.cidade;
                                resultPairs2.add(cityPair);
                            }
                        }
                    }

                    // Use a List to collect the results and then sort them
                    List<String> sortedPairs = new ArrayList<>(resultPairs2);
                    Collections.sort(sortedPairs);

                    if (sortedPairs.isEmpty()) {
                        queryResult.result = "Sem resultados";
                    } else {
                        queryResult.result = String.join("\n", sortedPairs) + "\n";
                    }

                    queryResult.success = true;
                    break;







                case "GET_CITY_POPULATION_RANGE": //criatividade
                    if (comandoNovo.args.length != 3) {
                        queryResult.success = false;
                        queryResult.error = "Número incorreto de argumentos para este comando";
                        break;
                    }

                    String countryNameRange = comandoNovo.args[0];
                    int minPopulation = Integer.parseInt(comandoNovo.args[1]);
                    int maxPopulation = Integer.parseInt(comandoNovo.args[2]);
                    Pais targetCountryRange = null;
                    ArrayList<Cidade> citiesInRange = new ArrayList<>();
                    int totalCitiesInRange = 0;
                    long totalPopulationInRange = 0;


                    for (Pais paiss : paises) {
                        if (paiss.nome.equalsIgnoreCase(countryNameRange)) {
                            targetCountryRange = paiss;
                            break;
                        }
                    }

                    if (targetCountryRange == null) {
                        queryResult.success = false;
                        queryResult.error = "País não encontrado: " + countryNameRange;
                        break;
                    }


                    for (Cidade city : cidades) {
                        if (city.alfa2.equalsIgnoreCase(targetCountryRange.alfa2) &&
                                city.populacao >= minPopulation && city.populacao <= maxPopulation) {
                            citiesInRange.add(city);
                            totalCitiesInRange++;
                            totalPopulationInRange += city.populacao;
                        }
                    }

                    if (totalCitiesInRange == 0) {
                        queryResult.result = "Nenhuma cidade encontrada no intervalo de população especificado.";
                    } else {
                        // Ordena as cidades por populacao (do maior para o menor)
                        citiesInRange.sort((c1, c2) -> Long.compare(c2.populacao, c1.populacao));

                        StringBuilder result_Builder = new StringBuilder();
                        result_Builder.append("Cidades em ").append(countryNameRange).append(" com população entre ")
                                .append(minPopulation).append(" e ").append(maxPopulation).append(" habitantes:\n");

                        int rank = 1;
                        for (Cidade city : citiesInRange) {
                            result_Builder.append(rank).append(". ").append(city.cidade).append(" (").append(city.populacao).append(" habitantes)\n");
                            rank++;
                        }

                        result_Builder.append("\nEstatísticas:\n");
                        result_Builder.append("Número total de cidades no intervalo: ").append(totalCitiesInRange).append("\n");
                        result_Builder.append("População total no intervalo: ").append(totalPopulationInRange).append("\n");

                        queryResult.result = result_Builder.toString();
                    }

                    queryResult.success = true;
                    break;







                case "HELP": // TENHO DE ESCREVER HELP (argumento a escolha á frente)
                    String help = """
                        -------------------------
                        Commands available:
                        COUNT_CITIES <nin_population>
                        GET_CITIES_BY_COUNTRY <num-results> <country-name>
                        SUM_POPULATIONS <countries-list>
                        GET_HISTORY <year-start> <year-end> <country_name>
                        GET_MISSING_HISTORY <year-start> <year-end>
                        GET_MOST_POPULOUS <num-results>
                        GET_TOP_CITIES_BY_COUNTRY <num-results> <country-name>
                        GET_DUPLICATE_CITIES <min-population>
                        GET_COUNTRIES_GENDER_GAP <min-gender-gap>
                        GET_TOP_POPULATION_INCREASE <year-start> <year-end>
                        GET_DUPLICATE_CITIES_DIFFERENT_COUNTRIES <min-population>
                        GET_CITIES_AT_DISTANCE <distance> <country-name>
                        INSERT_CITY <calfa2> <city-name> <cregion> <population>
                        REMOVE_COUNTRY <country-name>
                        HELP
                        QUIT
                        -------------------------
                        """;

                    queryResult.success = true;
                    queryResult.result = help;
                    queryResult.error = null;
                    break;

                case "QUIT":
                    queryResult.success = false;
                    queryResult.result = "Fim do Programa";
                    return queryResult;



            }

        }

        return queryResult;

    }

    public static double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        // Converter latitudes e longitudes de graus para radianos
        double lat1Rad = Math.toRadians(lat1);
        double lon1Rad = Math.toRadians(lon1);
        double lat2Rad = Math.toRadians(lat2);
        double lon2Rad = Math.toRadians(lon2);

        // Calcular as diferenças
        double dLat = lat2Rad - lat1Rad;
        double dLon = lon2Rad - lon1Rad;

        // Aplicar a fórmula de Haversine
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(lat1Rad) * Math.cos(lat2Rad) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.asin(Math.sqrt(a));

        // Calcular a distância final
        return EARTH_RADIUS * c;
    }

    /*static ArrayList getCities(int minPopulation, int maxPopulation){
        ArrayList<Cidade> cidades = new ArrayList<>();

        if (minPopulation == -1 && maxPopulation == -1){
            return cidades;
        }

        for (Cidade cidade : cidades) {
            if (cidade.populacao >= minPopulation && cidade.populacao <= maxPopulation){
                cidades.add(cidade);
            }
        }

        return cidades;

    }*/

    //Alteraçoes Possiveis GET_DENSITY_BELOW
    /*


                case "GET_DENSITY_BELOW":
    float densidadeMaxima = Float.parseFloat(comandoNovo.args[0]);
    String nome_pais = String.valueOf(comandoNovo.args[1]);
    Pais pais_comando = null;
    HashMap<Integer, Float> ano_densidade = new HashMap<>();
    String result_final = "";

    for (Pais paisNome : paises) {
        if (paisNome.nome.equalsIgnoreCase(nome_pais)){
            pais_comando = paisNome;
        }
    }

    if (pais_comando != null){
        for (Populacao pop : populacao) {
            if (pais_comando.id == pop.id && pop.ano > 2080){
                if (pop.densidade < densidadeMaxima){
                    ano_densidade.put(pop.ano, pop.densidade);
                }
            }
        }
    }

    ArrayList<Integer> anos_ordenados_decrescente = new ArrayList<>(ano_densidade.keySet());

    // Ordenação por ano em ordem decrescente
    anos_ordenados_decrescente.sort(Comparator.reverseOrder());

    // Ordenação adicional por densidade em ordem decrescente, caso anos sejam iguais
    anos_ordenados_decrescente.sort((ano1, ano2) -> {
        Float densidade1 = ano_densidade.get(ano1);
        Float densidade2 = ano_densidade.get(ano2);
        int comparacaoDensidade = densidade2.compareTo(densidade1);
        if (comparacaoDensidade != 0) {
            return comparacaoDensidade;
        } else {
            // Se densidades são iguais, mantém a ordem decrescente por ano
            return ano2.compareTo(ano1);
        }
    });

    if (anos_ordenados_decrescente.isEmpty()){
        result_final = "Sem resultados";
    } else {
        for (Integer ano : anos_ordenados_decrescente) {
            Float densidade = ano_densidade.get(ano);
            result_final += ano + " - " + densidade + "\n";
        }
    }

    queryResult.result = result_final;
    queryResult.success = true;
    break;



     */ // Ordenação adicional por densidade em ordem decrescente, caso anos sejam iguais
    /*
        case "GET_DENSITY_BELOW":
    float densidadeMinima = Float.parseFloat(comandoNovo.args[0]);
    float densidadeMaxima = Float.parseFloat(comandoNovo.args[1]);
    String nome_pais = String.valueOf(comandoNovo.args[2]);
    Pais pais_comando = null;
    HashMap<Integer, Float> ano_densidade = new HashMap<>();
    String result_final = "";

    // Encontra o país pelo nome fornecido
    for (Pais paisNome : paises) {
        if (paisNome.nome.equalsIgnoreCase(nome_pais)){
            pais_comando = paisNome;
        }
    }

    // Se o país existir, continua o processamento
    if (pais_comando != null){
        for (Populacao pop : populacao) {
            if (pais_comando.id == pop.id && pop.ano > 2080){
                if (pop.densidade >= densidadeMinima && pop.densidade <= densidadeMaxima){
                    ano_densidade.put(pop.ano, pop.densidade);
                }
            }
        }
    }

    // Lista para armazenar anos ordenados por ordem decrescente
    ArrayList<Integer> anos_ordenados_decrescente = new ArrayList<>(ano_densidade.keySet());

    // Ordenação por ano em ordem decrescente
    anos_ordenados_decrescente.sort(Comparator.reverseOrder());

    // Constrói a string final de resultados
    if (anos_ordenados_decrescente.isEmpty()){
        result_final = "Sem resultados";
    } else {
        for (Integer ano : anos_ordenados_decrescente) {
            Float densidade = ano_densidade.get(ano);
            result_final += ano + " - " + densidade + "\n";
        }
    }

    // Define o resultado da consulta
    queryResult.result = result_final;
    queryResult.success = true;
    break;


     */ // Filtro por Faixa de Densidade adicionado outro argumento no comando

    //Alteraçoes Possiveis COUNT_REGIONS
    /*
        case "COUNT_REGIONS": // OBG <LISTA PAISES SEPARADOS VIRGULA>
    if (comandoNovo.args.length == 0) {
        queryResult.result = "-1";
        queryResult.success = true;
        break;
    }

    String[] countryNames = comandoNovo.args;
    int total1 = 0;
    boolean pelo_menos_1_pais_existe = false;
    String resultadoFinal = "";

    for (String countryName1 : countryNames) {
        Pais country = null;
        for (Pais pais : paises) {
            if (pais.nome.equalsIgnoreCase(countryName1)) {
                country = pais;
                break;
            }
        }

        if (country != null) {
            pelo_menos_1_pais_existe = true;

            HashSet<String> regioes_unicas = new HashSet<>();
            for (Cidade cidade : cidades) {
                if (cidade.alfa2.equals(country.alfa2)) {
                    regioes_unicas.add(cidade.regiao);
                }
            }

            total1 += regioes_unicas.size();
        }
    }

    if (pelo_menos_1_pais_existe) {
        resultadoFinal = String.valueOf(total1);
    } else {
        resultadoFinal = "-1";
    }

    queryResult.result = resultadoFinal;
    queryResult.success = true;
    break;
     */ // Verificacao entrada vazia
    /*

    case "COUNT_REGIONS": // OBG <LISTA PAISES SEPARADOS VIRGULA>
    if (comandoNovo.args.length == 0) {
        queryResult.result = "-1";
        queryResult.success = true;
        break;
    }

    String[] countryNames = comandoNovo.args;
    int total1 = 0;
    boolean pelo_menos_1_pais_existe = false;
    StringBuilder resultadoFinal = new StringBuilder();
    List<String> invalidCountries = new ArrayList<>();

    for (String countryName1 : countryNames) {
        Pais country = null;
        for (Pais pais : paises) {
            if (pais.nome.equalsIgnoreCase(countryName1)) {
                country = pais;
                break;
            }
        }

        if (country != null) {
            pelo_menos_1_pais_existe = true;

            HashSet<String> regioes_unicas = new HashSet<>();
            for (Cidade cidade : cidades) {
                if (cidade.alfa2.equals(country.alfa2)) {
                    regioes_unicas.add(cidade.regiao);
                }
            }

            total1 += regioes_unicas.size();
        } else {
            invalidCountries.add(countryName1);
        }
    }

    if (pelo_menos_1_pais_existe) {
        resultadoFinal.append(total1);
    } else {
        resultadoFinal.append("-1");
    }

    if (!invalidCountries.isEmpty()) {
        resultadoFinal.append(" - Países inválidos: ").append(String.join(", ", invalidCountries));
    }

    queryResult.result = resultadoFinal.toString();
    queryResult.success = true;
    break;

     */ // retornar paises invalidos
    /*

    case "COUNT_REGIONS":
    String[] countryNames = comandoNovo.args;
    int total1 = 0;
    boolean pelo_menos_1_pais_existe = false;
    String resultadoFinal = "";
    ArrayList<String> paisesNaoEncontrados = new ArrayList<>();

    for (String countryName1 : countryNames) {
        Pais country = null;
        for (Pais pais : paises) {
            if (pais.nome.equalsIgnoreCase(countryName1)) {
                country = pais;
                break;
            }
        }

        if (country != null) {
            pelo_menos_1_pais_existe = true;

            HashSet<String> regioes_unicas = new HashSet<>();
            for (Cidade cidade : cidades) {
                if (cidade.alfa2.equals(country.alfa2)) {
                    regioes_unicas.add(cidade.regiao);
                }
            }

            total1 += regioes_unicas.size();
        } else {
            // Se o país não foi encontrado, adicione à lista de países não encontrados
            paisesNaoEncontrados.add(countryName1);
        }
    }

    if (pelo_menos_1_pais_existe) {
        if (!paisesNaoEncontrados.isEmpty()) {
            // Se houver países não encontrados, monte a mensagem de erro
            StringBuilder mensagemErro = new StringBuilder("Pais nao encontrado na lista: ");
            for (String pais : paisesNaoEncontrados) {
                mensagemErro.append(pais).append(", ");
            }
            // Remover a última vírgula e espaço
            mensagemErro.setLength(mensagemErro.length() - 2);
            resultadoFinal = mensagemErro.toString();
        } else {
            resultadoFinal = String.valueOf(total1);
        }
    } else {
        resultadoFinal = "-1";
    }

    queryResult.result = resultadoFinal;
    queryResult.success = true;
    break;

     */ // mensagem erro quando se um dos paises nao for encontrado na lista
    /*
        case "COUNT_REGIONS":
    String[] countryNames = comandoNovo.args;
    int total1 = 0;
    boolean pelo_menos_1_pais_existe = false;
    StringBuilder paisesNaoEncontrados = new StringBuilder();
    String resultadoFinal = "";

    for (String countryName : countryNames) {
        Pais country = null;
        for (Pais pais : paises) {
            if (pais.nome.equalsIgnoreCase(countryName)) {
                country = pais;
                break;
            }
        }

        if (country != null) {
            pelo_menos_1_pais_existe = true;

            HashSet<String> regioes_unicas = new HashSet<>();
            for (Cidade cidade : cidades) {
                if (cidade.alfa2.equals(country.alfa2)) {
                    regioes_unicas.add(cidade.regiao);
                }
            }

            total1 += regioes_unicas.size();
        } else {
            // Se o país não foi encontrado, adiciona à lista de não encontrados
            if (paisesNaoEncontrados.length() > 0) {
                paisesNaoEncontrados.append(", ");
            }
            paisesNaoEncontrados.append(countryName);
        }
    }

    if (pelo_menos_1_pais_existe && paisesNaoEncontrados.length() == 0) {
        // Se pelo menos um país foi encontrado e nenhum país não foi encontrado
        resultadoFinal = String.valueOf(total1);
    } else {
        // Se nenhum país foi encontrado ou há países não encontrados
        resultadoFinal = "Pais nao encontrado na lista: " + paisesNaoEncontrados.toString();
    }

    queryResult.result = resultadoFinal;
    queryResult.success = true;
    break;


     */ // mensagem erro quando 1 ou mais paises nao for encontrado na lista

    //Alteraçoes Possiveis REMOVE_COUNTRY
    /*
        case "REMOVE_COUNTRY":
    String countryToRemove = comandoNovo.args[0];
    Pais country1 = null;

    // Encontra o país pelo nome fornecido
    for (Pais pais : paises) {
        if (pais.nome.trim().equalsIgnoreCase(countryToRemove)) {
            country1 = pais;
            break;
        }
    }

    // Se o país não for encontrado, retorna "Pais invalido"
    if (country1 == null) {
        queryResult.result = "Pais invalido";
        queryResult.success = true;
        break;
    }

    // Coleta as cidades associadas ao país a ser removido
    ArrayList<Cidade> cidades_remover = new ArrayList<>();
    for (Cidade cidade2 : cidades) {
        if (cidade2.alfa2.equalsIgnoreCase(country1.alfa2)) {
            cidades_remover.add(cidade2);
        }
    }

    // Remove as cidades e o país
    cidades.removeAll(cidades_remover);
    paises.remove(country1);
    queryResult.result = "Removido com sucesso. Cidades removidas: " + cidades_remover.size();
    queryResult.success = true;
    break;
     */

    //Alteraçoes Possiveis INSERT_CITY
    /*
        case "INSERT_CITY": // INSERT_CITY pt Lisboa 01 1000000
    String alfa2 = comandoNovo.args[0];
    String cidade = comandoNovo.args[1];
    String regiao = comandoNovo.args[2];
    int populacao = Integer.parseInt(comandoNovo.args[3]);
    String resultado_final_Insert_City = "";

    boolean paisExiste = false;
    boolean cidadeExiste = false;

    for (Pais pais : paises) {
        if (pais.alfa2.equalsIgnoreCase(alfa2)) {
            paisExiste = true;
            break;
        }
    }

    for (Cidade cidadeExistente : cidades) {
        if (cidadeExistente.nome.equalsIgnoreCase(cidade) && cidadeExistente.alfa2.equalsIgnoreCase(alfa2)) {
            cidadeExiste = true;
            break;
        }
    }

    if (!paisExiste) {
        resultado_final_Insert_City = "Pais invalido";
    } else if (cidadeExiste) {
        resultado_final_Insert_City = "Cidade ja existe";
    } else {
        Cidade novaCidade = new Cidade(alfa2, cidade, regiao, populacao, 0, 0); // coordenadas nao tem
        cidades.add(novaCidade);
        resultado_final_Insert_City = "Inserido com sucesso";
    }

    queryResult.result = resultado_final_Insert_City;
    queryResult.success = true;
    break;


     */ // Evitar Inserção de Cidades Duplicadas
    /*
        case "INSERT_CITY": // INSERT_CITY pt Lisboa 01 1000000 [opcional: latitude longitude]
    String alfa2 = comandoNovo.args[0];
    String cidade = comandoNovo.args[1];
    String regiao = comandoNovo.args[2];
    int populacao = Integer.parseInt(comandoNovo.args[3]);
    float latitude = comandoNovo.args.length > 4 ? Float.parseFloat(comandoNovo.args[4]) : 0;
    float longitude = comandoNovo.args.length > 5 ? Float.parseFloat(comandoNovo.args[5]) : 0;
    String resultado_final_Insert_City = "";

    if (populacao <= 0) {
        queryResult.result = "Populacao invalida";
        queryResult.success = true;
        break;
    }

    boolean paisExiste = false;
    boolean cidadeExiste = false;

    for (Pais pais : paises) {
        if (pais.alfa2.equalsIgnoreCase(alfa2)) {
            paisExiste = true;
            break;
        }
    }

    for (Cidade cidadeExistente : cidades) {
        if (cidadeExistente.nome.equalsIgnoreCase(cidade) && cidadeExistente.alfa2.equalsIgnoreCase(alfa2)) {
            cidadeExiste = true;
            break;
        }
    }

    if (!paisExiste) {
        resultado_final_Insert_City = "Pais invalido";
    } else if (cidadeExiste) {
        resultado_final_Insert_City = "Cidade ja existe";
    } else {
        Cidade novaCidade = new Cidade(alfa2, cidade, regiao, populacao, latitude, longitude);
        cidades.add(novaCidade);
        resultado_final_Insert_City = "Inserido com sucesso";
    }

    queryResult.result = resultado_final_Insert_City;
    queryResult.success = true;
    break;


     */ // Adicionar Coordenadas Opcionalmente

    //Alteraçoes Possiveis SUM_Populations
    /*
        case "SUM_POPULATIONS": // OBG <MIN_POPULACAO>
    String[] paisesNomes = Arrays.copyOfRange(comandoNovo.args, 0, comandoNovo.args.length - 1);
    long minPopulacao = Long.parseLong(comandoNovo.args[comandoNovo.args.length - 1]);

    long totalPopulation = 0;
    boolean todos_paises_encontrados1 = true;
    String missingCountries = "";

    for (String paisNome : paisesNomes) {
        paisNome = paisNome.trim();

        boolean paisEncontrado = false;

        for (Pais pais : paises) {
            if (pais.nome.equalsIgnoreCase(paisNome)) {
                paisEncontrado = true;

                for (Populacao pop : populacao) {
                    if (pop.id == pais.id && pop.ano == 2024) {
                        long popTotal = pop.masculino + pop.feminino;
                        if (popTotal >= minPopulacao) {
                            totalPopulation += popTotal;
                        }
                        break;
                    }
                }

                break;
            }
        }

        if (!paisEncontrado) { // se o pais nao foi encontrado
            todos_paises_encontrados1 = false;
            if (!missingCountries.isEmpty()) {
                missingCountries += ", ";
            }
            missingCountries += paisNome;
        }
    }

    if (!todos_paises_encontrados1) { // se algum dos paises nao existe
        queryResult.result = "Pais invalido: " + missingCountries;
    } else { // se esta tudo bem faz a soma das pop
        queryResult.result = String.valueOf(totalPopulation);
    }

    queryResult.success = true;

    break;


     */ // Considerar populaçoes acima de valor minimo no argumento do comando
    /*
    case "SUM_POPULATIONS": // OBG
    String[] paisesNomes = comandoNovo.args;

    long totalPopulation = 0;
    boolean todos_paises_encontrados1 = true;
    String missingCountries = "";
    String noPopulationData = "";

    for (String paisNome : paisesNomes) {
        paisNome = paisNome.trim();

        boolean paisEncontrado = false;
        boolean populacaoEncontrada = false;

        for (Pais pais : paises) {
            if (pais.nome.equalsIgnoreCase(paisNome)) {
                paisEncontrado = true;

                for (Populacao pop : populacao) {
                    if (pop.id == pais.id && pop.ano == 2024) {
                        totalPopulation += pop.masculino + pop.feminino;
                        populacaoEncontrada = true;
                        break;
                    }
                }

                if (!populacaoEncontrada) {
                    if (!noPopulationData.isEmpty()) {
                        noPopulationData += ", ";
                    }
                    noPopulationData += paisNome;
                }

                break;
            }
        }

        if (!paisEncontrado) { // se o pais nao foi encontrado
            todos_paises_encontrados1 = false;
            if (!missingCountries.isEmpty()) {
                missingCountries += ", ";
            }
            missingCountries += paisNome;
        }
    }

    if (!todos_paises_encontrados1) { // se algum dos paises nao existe
        queryResult.result = "Pais invalido: " + missingCountries;
    } else if (!noPopulationData.isEmpty()) { // se algum pais nao tem dados de populacao
        queryResult.result = "Sem dados de populacao para: " + noPopulationData;
    } else { // se esta tudo bem faz a soma das pop
        queryResult.result = String.valueOf(totalPopulation);
    }

    queryResult.success = true;

    break;


     */ // Tratar casos onde nao tem dados da populaçao
    /*
    case "SUM_POPULATIONS": // OBG <MIN_POPULACAO> <MAX_POPULACAO>
    String[] paisesNomes = Arrays.copyOfRange(comandoNovo.args, 0, comandoNovo.args.length - 2);
    long minPopulacao = Long.parseLong(comandoNovo.args[comandoNovo.args.length - 2]);
    long maxPopulacao = Long.parseLong(comandoNovo.args[comandoNovo.args.length - 1]);

    long totalPopulation = 0;
    boolean todos_paises_encontrados1 = true;
    String missingCountries = "";

    for (String paisNome : paisesNomes) {
        paisNome = paisNome.trim();

        boolean paisEncontrado = false;

        for (Pais pais : paises) {
            if (pais.nome.equalsIgnoreCase(paisNome)) {
                paisEncontrado = true;

                for (Populacao pop : populacao) {
                    if (pop.id == pais.id && pop.ano == 2024) {
                        long popTotal = pop.masculino + pop.feminino;
                        if (popTotal >= minPopulacao && popTotal <= maxPopulacao) {
                            totalPopulation += popTotal;
                        }
                        break;
                    }
                }

                break;
            }
        }

        if (!paisEncontrado) { // se o pais nao foi encontrado
            todos_paises_encontrados1 = false;
            if (!missingCountries.isEmpty()) {
                missingCountries += ", ";
            }
            missingCountries += paisNome;
        }
    }

    if (!todos_paises_encontrados1) { // se algum dos paises nao existe
        queryResult.result = "Pais invalido: "


     */ // Filtro por populacao minima e maxima
    /*

    case "SUM_POPULATIONS": // OBG
    String[] paisesNomes = comandoNovo.args;

    long totalPopulation = 0;
    boolean todos_paises_encontrados1 = true;
    String missingCountries = "";
    String noDataCountries = "";

    for (String paisNome : paisesNomes) {
        paisNome = paisNome.trim();

        boolean paisEncontrado = false;
        boolean populacaoEncontrada = false;

        for (Pais pais : paises) {
            if (pais.nome.equalsIgnoreCase(paisNome)) {
                paisEncontrado = true;

                for (Populacao pop : populacao) {
                    if (pop.id == pais.id && pop.ano == 2024) {
                        totalPopulation += pop.masculino + pop.feminino;
                        populacaoEncontrada = true;
                        break;
                    }
                }

                if (!populacaoEncontrada) {
                    if (!noDataCountries.isEmpty()) {
                        noDataCountries += ", ";
                    }
                    noDataCountries += paisNome;
                }

                break;
            }
        }

        if (!paisEncontrado) {
            todos_paises_encontrados1 = false;
            if (!missingCountries.isEmpty()) {
                missingCountries += ", ";
            }
            missingCountries += paisNome;
        }
    }

    if (!todos_paises_encontrados1) {
        queryResult.result = "Pais invalido: " + missingCountries;
    } else if (!noDataCountries.isEmpty()) {
        queryResult.result = "Sem dados de populacao para: " + noDataCountries;
    } else {
        queryResult.result = String.valueOf(totalPopulation);
    }

    queryResult.success = true;

    break;

     */ // mensagem erro se algum pais tiver populacao nao encontrada
    /*
        case "SUM_POPULATIONS": // OBG
    String[] paisesNomes = comandoNovo.args;

    Map<String, Long> populacaoPorPais = new HashMap<>();
    boolean todos_paises_encontrados1 = true;
    String missingCountries = "";

    for (String paisNome : paisesNomes) {
        paisNome = paisNome.trim();

        boolean paisEncontrado = false;

        for (Pais pais : paises) {
            if (pais.nome.equalsIgnoreCase(paisNome)) {
                paisEncontrado = true;

                long totalPopulation = 0;
                for (Populacao pop : populacao) {
                    if (pop.id == pais.id) {
                        totalPopulation += pop.masculino + pop.feminino;
                    }
                }
                populacaoPorPais.put(paisNome, totalPopulation);

                break;
            }
        }

        if (!paisEncontrado) {
            todos_paises_encontrados1 = false;
            if (!missingCountries.isEmpty()) {
                missingCountries += ", ";
            }
            missingCountries += paisNome;
        }
    }

    if (!todos_paises_encontrados1) {
        queryResult.result = "Pais invalido: " + missingCountries;
    } else {
        StringBuilder resultado = new StringBuilder();
        for (Map.Entry<String, Long> entry : populacaoPorPais.entrySet()) {
            resultado.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
        }
        queryResult.result = resultado.toString().trim();
    }

    queryResult.success = true;

    break;


     */  // Somar Populações de Todos os Anos Disponíveis e Agrupar por País. Exemplo:  Brasil: 102000000 Argentina: 41000000 Chile: 0
    /*
        case "SUM_POPULATIONS": // OBG
    String[] paisesNomes = comandoNovo.args;

    Map<String, Double> mediaPopulacaoPorPais = new HashMap<>();
    boolean todos_paises_encontrados1 = true;
    String missingCountries = "";

    for (String paisNome : paisesNomes) {
        paisNome = paisNome.trim();

        boolean paisEncontrado = false;

        for (Pais pais : paises) {
            if (pais.nome.equalsIgnoreCase(paisNome)) {
                paisEncontrado = true;

                long totalPopulation = 0;
                int numAnos = 0;
                for (Populacao pop : populacao) {
                    if (pop.id == pais.id) {

                totalPopulation += pop.masculino + pop.feminino;
                    numAnos++;
                }

                if (numAnos > 0) {
                    double media = (double) totalPopulation / numAnos;
                    mediaPopulacaoPorPais.put(paisNome, media);
                } else {
                    mediaPopulacaoPorPais.put(paisNome, 0.0); // Caso não haja dados de população para o país
                }

                break;
            }
        }

        if (!paisEncontrado) {
            todos_paises_encontrados1 = false;
            if (!missingCountries.isEmpty()) {
                missingCountries += ", ";
            }
            missingCountries += paisNome;
        }
    }

    if (!todos_paises_encontrados1) {
        queryResult.result = "Pais invalido: " + missingCountries;
    } else {
        StringBuilder resultado = new StringBuilder();
        for (Map.Entry<String, Double> entry : mediaPopulacaoPorPais.entrySet()) {
            resultado.append(entry.getKey()).append(": ").append(String.format("%.2f", entry.getValue())).append("\n");
        }
        queryResult.result = resultado.toString().trim();
    }

    queryResult.success = true;

    break;

     */     // Retornar Média de População por País

    //Alteraçoes Possiveis COUNT_CITIES
    /*
        case "COUNT_CITIES":
    int count = 0;
    int ano = Integer.parseInt(comandoNovo.args[0]);
    String listaPaises = comandoNovo.args[1];

    // Separar a lista de países por vírgula
    String[] paisesArray = listaPaises.split(",");

    for (Cidade cidade : cidades) {
        // Verificar se a cidade pertence a algum dos países da lista
        for (String paisNome : paisesArray) {
            Pais pais_do_comando = null;
            for (Pais pais : paises) {
                if (pais.nome.equalsIgnoreCase(paisNome.trim())) {
                    pais_do_comando = pais;
                    break;
                }
            }

            if (pais_do_comando != null && cidade.alfa2.equalsIgnoreCase(pais_do_comando.alfa2)) {
                if (cidade.populacao >= ano) {
                    count++;
                }
                break; // Não precisamos verificar para mais países se já encontramos um correspondente
            }
        }
    }

    queryResult.result = String.valueOf(count);
    queryResult.success = true;
    break;

     */ // recebe um argumento a mais que e uma lista de paises e calcula as cidades dos paises da lista
    /*
        case "COUNT_CITIES":
                    int count = 0;
                    int ano = Integer.parseInt(comandoNovo.args[0]);
                    String paisNome1 = comandoNovo.args[1]; // Nome do país específico

                    Pais pais_do_comando1 = null;

                    // Encontrar o objeto Pais correspondente ao nome fornecido
                    for (Pais pais : paises) {
                        if (pais.nome.equalsIgnoreCase(paisNome1.trim())) {
                            pais_do_comando1 = pais;
                            break;
                        }
                    }

                    if (pais_do_comando1 != null) {
                        // Contar as cidades do país específico que têm população >= ano
                        for (Cidade cidade : cidades) {
                            if (cidade.alfa2.equalsIgnoreCase(pais_do_comando1.alfa2) && cidade.populacao >= ano) {
                                count++;
                            }
                        }

                        queryResult.result = String.valueOf(count);
                    } else {
                        queryResult.result = "País inválido: " + paisNome1;
                    }

                    queryResult.success = true;
                    break;

     */ // recebe 1 argumento a mais que é o pais do comando, basicamente conta as cidades desse pais com o minpopulatioin do comadno









}






































