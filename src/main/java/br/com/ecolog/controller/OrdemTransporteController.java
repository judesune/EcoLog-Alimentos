package br.com.ecolog.controller;

import br.com.ecolog.model.InstituicaoReceptora;
import br.com.ecolog.model.LoteAlimento;
import br.com.ecolog.model.OrdemTransporte;
import br.com.ecolog.model.PontoColeta;
import br.com.ecolog.service.InstituicaoReceptoraService;
import br.com.ecolog.service.OrdemTransporteService;
import br.com.ecolog.service.PontoColetaService;
import br.com.ecolog.service.RegraNegocioException;
import br.com.ecolog.view.EntradaConsole;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/** Menu de CRUD da entidade principal, OrdemTransporte (RF4 a RF7). */
public class OrdemTransporteController extends MenuController {

    private static final List<String> STATUS_POSSIVEIS = List.of(
            OrdemTransporte.STATUS_PENDENTE,
            OrdemTransporte.STATUS_EM_TRANSITO,
            OrdemTransporte.STATUS_CONCLUIDA,
            OrdemTransporte.STATUS_CANCELADA);

    private final OrdemTransporteService service;
    private final PontoColetaService pontoColetaService;
    private final InstituicaoReceptoraService instituicaoService;
    private final LeitorLote leitorLote;

    public OrdemTransporteController(EntradaConsole console, OrdemTransporteService service,
                                     PontoColetaService pontoColetaService,
                                     InstituicaoReceptoraService instituicaoService) {
        super(console);
        this.service = service;
        this.pontoColetaService = pontoColetaService;
        this.instituicaoService = instituicaoService;
        this.leitorLote = new LeitorLote(console);
    }

    @Override
    protected String getTitulo() {
        return "Ordens de Transporte";
    }

    @Override
    protected List<String> getOpcoes() {
        return List.of(
                "Criar ordem de transporte",
                "Listar todas as ordens",
                "Fila de prioridade (ordens pendentes)",
                "Consultar ordem",
                "Alterar data, origem ou destino",
                "Gerenciar lotes da ordem",
                "Atualizar status",
                "Cancelar ordem pendente",
                "Excluir ordem");
    }

    @Override
    protected void executarOpcao(int opcao) {
        switch (opcao) {
            case 1 -> criar();
            case 2 -> listar(service.listar(), "Nenhuma ordem cadastrada.");
            case 3 -> listar(service.listarPendentesPorPrioridade(), "Nenhuma ordem pendente.");
            case 4 -> consultar();
            case 5 -> alterarDados();
            case 6 -> gerenciarLotes();
            case 7 -> atualizarStatus();
            case 8 -> cancelar();
            case 9 -> excluir();
            default -> throw new IllegalArgumentException("Opção inexistente: " + opcao);
        }
    }

    // ------------------------------------------------------------------
    // CREATE
    // ------------------------------------------------------------------

    private void criar() {
        exigirCadastrosBase();
        LocalDate data = console.lerData("Data agendada da coleta");
        mostrarPontosColeta();
        Long idOrigem = console.lerId("Código do ponto de coleta (origem)");
        mostrarInstituicoes();
        Long idDestino = console.lerId("Código da instituição receptora (destino)");

        List<LoteAlimento> lotes = new ArrayList<>();
        do {
            console.escrever(String.format("%n-- Lote %d --", lotes.size() + 1));
            lotes.add(leitorLote.lerNovo());
        } while (console.confirmar("Adicionar outro lote?"));

        OrdemTransporte ordem = service.criar(data, idOrigem, idDestino, lotes);
        console.sucesso("Ordem #" + ordem.getId() + " criada com status Pendente e prioridade "
                + ordem.calcularPrioridadeGeral() + ".");
    }

    // ------------------------------------------------------------------
    // READ
    // ------------------------------------------------------------------

    private void listar(List<OrdemTransporte> ordens, String mensagemVazia) {
        if (ordens.isEmpty()) {
            console.escrever(mensagemVazia);
            return;
        }
        ordens.forEach(ordem -> console.escrever(ordem.toString()));
    }

    private void consultar() {
        OrdemTransporte ordem = service.buscar(console.lerId("Código da ordem"));
        console.escrever(ordem.toString());
        console.escrever("  Origem : " + ordem.getOrigem());
        console.escrever("  Destino: " + ordem.getDestino());
        console.escrever("  Lotes (do mais urgente para o menos urgente):");
        ordem.getLotesPorPrioridade().forEach(lote -> console.escrever("    " + lote));
    }

    // ------------------------------------------------------------------
    // UPDATE
    // ------------------------------------------------------------------

    private void alterarDados() {
        OrdemTransporte ordem = service.buscarEditavel(console.lerId("Código da ordem"));
        console.escrever(ordem.toString());
        console.escrever("Enter mantém o valor atual.");
        LocalDate data = console.lerDataOpcional("Data agendada", ordem.getDataAgendada());
        mostrarPontosColeta();
        Long idOrigem = console.lerIdOpcional("Código da origem", ordem.getOrigem().getId());
        mostrarInstituicoes();
        Long idDestino = console.lerIdOpcional("Código do destino", ordem.getDestino().getId());
        service.atualizarDados(ordem.getId(), data, idOrigem, idDestino);
        console.sucesso("Ordem #" + ordem.getId() + " atualizada.");
    }

    private void gerenciarLotes() {
        Long idOrdem = service.buscar(console.lerId("Código da ordem")).getId();
        new LotesOrdemController(console, service, idOrdem).exibir();
    }

    private void atualizarStatus() {
        OrdemTransporte ordem = service.buscar(console.lerId("Código da ordem"));
        console.escrever("Status atual: " + OrdemTransporte.descreverStatus(ordem.getStatus()));

        List<String> permitidos = STATUS_POSSIVEIS.stream().filter(ordem::podeMudarPara).toList();
        if (permitidos.isEmpty()) {
            throw new RegraNegocioException("A ordem está finalizada; o status não pode mais ser alterado.");
        }
        for (int i = 0; i < permitidos.size(); i++) {
            console.escrever(String.format("  %d - %s", i + 1, OrdemTransporte.descreverStatus(permitidos.get(i))));
        }
        int escolha = console.lerInteiro("Novo status", 1, permitidos.size());
        String novoStatus = permitidos.get(escolha - 1);
        if (OrdemTransporte.STATUS_CANCELADA.equals(novoStatus)
                && !console.confirmar("Confirma o cancelamento da ordem?")) {
            return;
        }
        service.atualizarStatus(ordem.getId(), novoStatus);
        console.sucesso("Ordem #" + ordem.getId() + " agora está " + OrdemTransporte.descreverStatus(novoStatus) + ".");
    }

    private void cancelar() {
        OrdemTransporte ordem = service.buscar(console.lerId("Código da ordem"));
        if (!ordem.podeMudarPara(OrdemTransporte.STATUS_CANCELADA)) {
            throw new RegraNegocioException("Apenas ordens pendentes podem ser canceladas. Status atual: "
                    + OrdemTransporte.descreverStatus(ordem.getStatus()) + ".");
        }
        console.escrever(ordem.toString());
        if (console.confirmar("Confirma o cancelamento da ordem?")) {
            service.cancelar(ordem.getId());
            console.sucesso("Ordem #" + ordem.getId() + " cancelada.");
        }
    }

    // ------------------------------------------------------------------
    // DELETE
    // ------------------------------------------------------------------

    private void excluir() {
        OrdemTransporte ordem = service.buscar(console.lerId("Código da ordem"));
        console.escrever(ordem.toString());
        if (console.confirmar("A exclusão apaga a ordem e todos os seus lotes. Confirma?")) {
            service.excluir(ordem.getId());
            console.sucesso("Ordem #" + ordem.getId() + " e seus lotes foram excluídos.");
        }
    }

    // ------------------------------------------------------------------
    // Apoio
    // ------------------------------------------------------------------

    private void exigirCadastrosBase() {
        if (pontoColetaService.listar().isEmpty() || instituicaoService.listar().isEmpty()) {
            throw new RegraNegocioException(
                    "Cadastre ao menos um ponto de coleta e uma instituição receptora antes de criar ordens.");
        }
    }

    private void mostrarPontosColeta() {
        console.escrever("Pontos de coleta:");
        for (PontoColeta ponto : pontoColetaService.listar()) {
            console.escrever(String.format("  #%d - %s", ponto.getId(), ponto.getRazaoSocial()));
        }
    }

    private void mostrarInstituicoes() {
        console.escrever("Instituições receptoras:");
        for (InstituicaoReceptora instituicao : instituicaoService.listar()) {
            console.escrever(String.format("  #%d - %s (refrigerado: %s kg)", instituicao.getId(),
                    instituicao.getRazaoSocial(),
                    EntradaConsole.formatarDecimal(instituicao.getCapacidadeRefrigeradaKg())));
        }
    }
}
