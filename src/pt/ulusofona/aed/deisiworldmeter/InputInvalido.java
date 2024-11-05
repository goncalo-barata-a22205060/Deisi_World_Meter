package pt.ulusofona.aed.deisiworldmeter;

public class InputInvalido {
    String nomeFicheiro;
    int linhasOk;
    int linhasNok;
    int primeiraLinhaNok;

    public InputInvalido(String nomeFicheiro, int linhasOk, int linhasNok, int primeiraLinhaNok) {
        this.nomeFicheiro = nomeFicheiro;
        this.linhasOk = linhasOk;
        this.linhasNok = linhasNok;
        this.primeiraLinhaNok = primeiraLinhaNok;
    }

    @Override
    public String toString() {
        return nomeFicheiro + " | " + linhasOk + " | " + linhasNok + " | " + primeiraLinhaNok;
    }
}
