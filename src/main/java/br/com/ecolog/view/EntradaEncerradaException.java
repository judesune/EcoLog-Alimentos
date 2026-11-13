package br.com.ecolog.view;

/** Sinaliza que a entrada padrão foi fechada (Ctrl+D / Ctrl+Z ou fim de arquivo). */
public class EntradaEncerradaException extends RuntimeException {

    public EntradaEncerradaException() {
        super("Entrada encerrada.");
    }
}
