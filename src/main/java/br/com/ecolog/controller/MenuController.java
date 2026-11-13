package br.com.ecolog.controller;

import br.com.ecolog.repository.PersistenciaException;
import br.com.ecolog.service.RegraNegocioException;
import br.com.ecolog.view.EntradaConsole;

import java.util.List;

/**
 * Base abstrata de todos os menus do terminal (Template Method): o laço de
 * exibição e o tratamento de erros ficam aqui, e cada controller concreto
 * sobrescreve título, opções e a ação de cada opção.
 */
public abstract class MenuController {

    protected final EntradaConsole console;

    protected MenuController(EntradaConsole console) {
        this.console = console;
    }

    /** Título exibido no topo do menu. */
    protected abstract String getTitulo();

    /** Opções numeradas a partir de 1, na ordem exibida. */
    protected abstract List<String> getOpcoes();

    /** Executa a opção escolhida (1..n). */
    protected abstract void executarOpcao(int opcao);

    /** Texto da opção 0. */
    protected String getRotuloSaida() {
        return "Voltar";
    }

    /** Mostra o menu até o usuário escolher 0. */
    public final void exibir() {
        while (true) {
            console.titulo(getTitulo());
            List<String> opcoes = getOpcoes();
            for (int i = 0; i < opcoes.size(); i++) {
                console.escrever(String.format("  %d - %s", i + 1, opcoes.get(i)));
            }
            console.escrever("  0 - " + getRotuloSaida());

            int opcao = console.lerInteiro("Escolha uma opção", 0, opcoes.size());
            if (opcao == 0) {
                return;
            }
            try {
                executarOpcao(opcao);
            } catch (RegraNegocioException e) {
                console.erro(e.getMessage());
            } catch (PersistenciaException e) {
                console.erro(e.getMessage() + ": " + causaRaiz(e).getMessage());
            }
        }
    }

    private static Throwable causaRaiz(Throwable erro) {
        Throwable atual = erro;
        while (atual.getCause() != null) {
            atual = atual.getCause();
        }
        return atual;
    }
}
