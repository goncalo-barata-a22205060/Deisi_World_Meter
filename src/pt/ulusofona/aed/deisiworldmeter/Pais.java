package pt.ulusofona.aed.deisiworldmeter;

public class Pais {
    int id;
    String alfa2;
    String alfa3;
    String nome;
    int ocorrencias;

    boolean verificacidade = false;

    int num_cidades;



    public Pais(int id, String alfa2, String alfa3, String nome) {
        this.id = id;
        this.alfa2 = alfa2;
        this.alfa3 = alfa3;
        this.nome = nome;
    }



    @Override
    public String toString() {
        if (ocorrencias > 0){
            return nome + " | " + id + " | " + alfa2.toUpperCase() + " | " + alfa3.toUpperCase() + " | " + ocorrencias;
        }
            return nome + " | " + id + " | " + alfa2.toUpperCase() + " | " + alfa3.toUpperCase();

    }

    /*@Override
    public String toString() {

        if (verificacidade){
            return nome + " | " + id + " | cidades:" + num_cidades;
        }
        if (id > 600 && id < 700){
            return nome + " | " + id + " | cidades:" + num_cidades;
        }

        if (ocorrencias > 0){
            return nome + " | " + id + " | " + alfa2.toUpperCase() + " | " + alfa3.toUpperCase() + " | " + ocorrencias;
        }
        return nome + " | " + id + " | " + alfa2.toUpperCase() + " | " + alfa3.toUpperCase();
    }*/



}
