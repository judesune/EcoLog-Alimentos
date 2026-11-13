package br.com.ecolog.service;

import java.util.List;

/** Violação de uma regra de negócio; a mensagem é exibida diretamente ao usuário. */
public class RegraNegocioException extends RuntimeException {

    public RegraNegocioException(String mensagem) {
        super(mensagem);
    }

    /** Lança a exceção se houver erros, juntando todas as mensagens. */
    public static void lancarSeHouver(List<String> erros) {
        if (!erros.isEmpty()) {
            throw new RegraNegocioException(String.join(System.lineSeparator(), erros));
        }
    }
}
