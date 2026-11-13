package br.com.ecolog.controller;

import br.com.ecolog.model.LoteAlimento;
import br.com.ecolog.model.OrdemTransporte;
import br.com.ecolog.service.OrdemTransporteService;
import br.com.ecolog.view.EntradaConsole;

import java.util.List;

class LotesOrdemController extends MenuController {

    private final OrdemTransporteService service;
    private final LeitorLote leitorLote;
    private final Long idOrdem;

    LotesOrdemController(EntradaConsole console, OrdemTransporteService service, Long idOrdem) {
        super(console);
        this.service = service;
        this.leitorLote = new LeitorLote(console);
        this.idOrdem = idOrdem;
    }

    @Override
    protected String getTitulo() {
        return "Lotes da Ordem #" + idOrdem;
    }

    @Override
    protected List<String> getOpcoes() {
        return List.of("Listar lotes", "Adicionar lote", "Alterar lote", "Remover lote");
    }

    @Override
    protected void executarOpcao(int opcao) {
        switch (opcao) {
            case 1 -> listar();
            case 2 -> adicionar();
            case 3 -> alterar();
            case 4 -> remover();
            default -> throw new IllegalArgumentException("Opção inexistente: " + opcao);
        }
    }

    private void listar() {
        OrdemTransporte ordem = service.buscar(idOrdem);
        ordem.getLotesPorPrioridade().forEach(lote -> console.escrever("  " + lote));
    }

    private void adicionar() {
        service.buscarEditavel(idOrdem);
        LoteAlimento lote = leitorLote.lerNovo();
        service.adicionarLote(idOrdem, lote);
        console.sucesso("Lote #" + lote.getId() + " adicionado à ordem.");
    }

    private void alterar() {
        OrdemTransporte ordem = service.buscarEditavel(idOrdem);
        listarLotes(ordem);
        Long idLote = console.lerId("Código do lote");
        LoteAlimento atual = ordem.getLotes().stream()
                .filter(lote -> idLote.equals(lote.getId()))
                .findFirst()
                .orElse(null);
        if (atual == null) {
            console.erro("O lote #" + idLote + " não pertence a esta ordem.");
            return;
        }
        service.atualizarLote(idOrdem, leitorLote.lerAlteracao(atual));
        console.sucesso("Lote #" + idLote + " atualizado.");
    }

    private void remover() {
        OrdemTransporte ordem = service.buscarEditavel(idOrdem);
        listarLotes(ordem);
        Long idLote = console.lerId("Código do lote");
        if (console.confirmar("Confirma a remoção do lote #" + idLote + "?")) {
            service.removerLote(idOrdem, idLote);
            console.sucesso("Lote removido.");
        }
    }

    private void listarLotes(OrdemTransporte ordem) {
        ordem.getLotes().forEach(lote -> console.escrever("  " + lote));
    }
}
