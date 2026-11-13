package br.com.ecolog.repository;

/** Falha de acesso ao banco de dados, sem obrigar as camadas de cima a tratar SQLException. */
public class PersistenciaException extends RuntimeException {

    public PersistenciaException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
