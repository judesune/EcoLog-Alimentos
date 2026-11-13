package br.com.ecolog.controller;

import br.com.ecolog.repository.PersistenciaException;
import br.com.ecolog.service.RegraNegocioException;
import br.com.ecolog.view.EntradaConsole;

import java.util.List;

public abstract class MenuController {

    protected final EntradaConsole console;

    protected MenuController(EntradaConsole console) {
        this.console = console;
    }

    protected abstract String getTitulo();

    protected abstract List<String> getOpcoes();

    protected abstract void executarOpcao(int opcao);

    protected String getRotuloSaida() {
        return "Voltar";
    }

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
