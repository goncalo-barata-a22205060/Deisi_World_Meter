package pt.ulusofona.aed.deisiworldmeter;

public class Cidade {
    String alfa2;
    String cidade;
    String regiao;
    int populacao;
    double latitude;
    double longitude;

    public Cidade(String alfa2, String cidade, String regiao, int populacao, double latitude, double longitude) {
        this.alfa2 = alfa2;
        this.cidade = cidade;
        this.regiao = regiao;
        this.populacao = populacao;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    @Override
    public String toString() {
        String coordenadas = "(" + latitude + "," + longitude + ")";

        return cidade + " | " + alfa2.toUpperCase() + " | " + regiao + " | " + populacao + " | " + coordenadas;


    }
}
