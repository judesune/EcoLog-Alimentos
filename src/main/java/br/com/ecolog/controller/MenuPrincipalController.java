package br.com.ecolog.controller;

import br.com.ecolog.view.EntradaConsole;

import java.util.List;

public class MenuPrincipalController extends MenuController {

    private final List<MenuController> submenus;

    public MenuPrincipalController(EntradaConsole console, PontoColetaController pontoColeta,
                                   InstituicaoReceptoraController instituicao, OrdemTransporteController ordem) {
        super(console);
        this.submenus = List.of(pontoColeta, instituicao, ordem);
    }

    @Override
    protected String getTitulo() {
        return "EcoLog Alimentos - Resgate e Redistribuição de Alimentos";
    }

    @Override
    protected List<String> getOpcoes() {
        return submenus.stream().map(MenuController::getTitulo).toList();
    }

    @Override
    protected void executarOpcao(int opcao) {
        submenus.get(opcao - 1).exibir();
    }

    @Override
    protected String getRotuloSaida() {
        return "Sair";
    }
}
