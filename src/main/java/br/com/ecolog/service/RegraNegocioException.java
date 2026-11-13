package br.com.ecolog.service;

import java.util.List;

public class RegraNegocioException extends RuntimeException {

    public RegraNegocioException(String mensagem) {
        super(mensagem);
    }

    public static void lancarSeHouver(List<String> erros) {
        if (!erros.isEmpty()) {
            throw new RegraNegocioException(String.join(System.lineSeparator(), erros));
        }
    }
}
