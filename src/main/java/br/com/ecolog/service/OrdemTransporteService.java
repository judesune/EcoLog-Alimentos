package br.com.ecolog.service;

import br.com.ecolog.model.InstituicaoReceptora;
import br.com.ecolog.model.LoteAlimento;
import br.com.ecolog.model.LotePerecivel;
import br.com.ecolog.model.OrdemTransporte;
import br.com.ecolog.model.PontoColeta;
import br.com.ecolog.repository.InstituicaoReceptoraRepository;
import br.com.ecolog.repository.OrdemTransporteRepository;
import br.com.ecolog.repository.PontoColetaRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Regras de negócio da entidade principal: criação (RF5), consulta e
 * atualização de status (RF6), cancelamento (RF7) e gestão dos lotes (RF4).
 */
public class OrdemTransporteService {

    private final OrdemTransporteRepository repositorio;
    private final PontoColetaRepository pontoColetaRepositorio;
    private final InstituicaoReceptoraRepository instituicaoRepositorio;

    public OrdemTransporteService(OrdemTransporteRepository repositorio,
                                  PontoColetaRepository pontoColetaRepositorio,
                                  InstituicaoReceptoraRepository instituicaoRepositorio) {
        this.repositorio = repositorio;
        this.pontoColetaRepositorio = pontoColetaRepositorio;
        this.instituicaoRepositorio = instituicaoRepositorio;
    }

    // ------------------------------------------------------------------
    // CREATE
    // ------------------------------------------------------------------

    public OrdemTransporte criar(LocalDate dataAgendada, Long idOrigem, Long idDestino, List<LoteAlimento> lotes) {
        validarDataAgendada(dataAgendada);
        OrdemTransporte ordem = new OrdemTransporte(null, dataAgendada, OrdemTransporte.STATUS_PENDENTE,
                buscarOrigem(idOrigem), buscarDestino(idDestino));

        if (lotes == null || lotes.isEmpty()) {
            throw new RegraNegocioException("A ordem precisa ter pelo menos um lote de alimento.");
        }
        for (LoteAlimento lote : lotes) {
            validarLote(lote, dataAgendada);
            ordem.adicionarLote(lote);
        }
        validarCapacidadeRefrigerada(ordem);
        return repositorio.salvar(ordem);
    }

    // ------------------------------------------------------------------
    // READ
    // ------------------------------------------------------------------

    public List<OrdemTransporte> listar() {
        return repositorio.listarTodos();
    }

    /** Ordens pendentes da mais urgente para a menos urgente (priorização automática). */
    public List<OrdemTransporte> listarPendentesPorPrioridade() {
        return repositorio.listarTodos().stream()
                .filter(OrdemTransporte::isEditavel)
                .sorted(Comparator.comparingInt(OrdemTransporte::calcularPrioridadeGeral).reversed()
                        .thenComparing(OrdemTransporte::getDataAgendada))
                .collect(Collectors.toList());
    }

    public OrdemTransporte buscar(Long id) {
        return repositorio.buscarPorId(id)
                .orElseThrow(() -> new RegraNegocioException("Ordem de transporte #" + id + " não encontrada."));
    }

    // ------------------------------------------------------------------
    // UPDATE
    // ------------------------------------------------------------------

    /** Altera data, origem e destino de uma ordem ainda pendente. */
    public OrdemTransporte atualizarDados(Long id, LocalDate dataAgendada, Long idOrigem, Long idDestino) {
        OrdemTransporte ordem = buscarEditavel(id);
        validarDataAgendada(dataAgendada);
        ordem.setDataAgendada(dataAgendada);
        ordem.setOrigem(buscarOrigem(idOrigem));
        ordem.setDestino(buscarDestino(idDestino));

        List<String> erros = new ArrayList<>();
        for (LoteAlimento lote : ordem.getLotes()) {
            if (lote.getDataValidade().isBefore(dataAgendada)) {
                erros.add("O lote #" + lote.getId() + " vence antes da nova data de coleta.");
            }
        }
        RegraNegocioException.lancarSeHouver(erros);
        validarCapacidadeRefrigerada(ordem);
        repositorio.atualizar(ordem);
        return ordem;
    }

    public OrdemTransporte adicionarLote(Long idOrdem, LoteAlimento lote) {
        OrdemTransporte ordem = buscarEditavel(idOrdem);
        validarLote(lote, ordem.getDataAgendada());
        ordem.adicionarLote(lote);
        validarCapacidadeRefrigerada(ordem);
        repositorio.atualizar(ordem);
        return ordem;
    }

    /** Substitui os dados de um lote existente (pode inclusive trocar o tipo do lote). */
    public OrdemTransporte atualizarLote(Long idOrdem, LoteAlimento loteAtualizado) {
        OrdemTransporte ordem = buscarEditavel(idOrdem);
        exigirLote(ordem, loteAtualizado.getId());
        validarLote(loteAtualizado, ordem.getDataAgendada());
        ordem.removerLote(loteAtualizado.getId());
        ordem.adicionarLote(loteAtualizado);
        validarCapacidadeRefrigerada(ordem);
        repositorio.atualizar(ordem);
        return ordem;
    }

    public OrdemTransporte removerLote(Long idOrdem, Long idLote) {
        OrdemTransporte ordem = buscarEditavel(idOrdem);
        exigirLote(ordem, idLote);
        if (ordem.getLotes().size() == 1) {
            throw new RegraNegocioException(
                    "A ordem precisa manter pelo menos um lote. Para desfazer a carga, cancele ou exclua a ordem.");
        }
        ordem.removerLote(idLote);
        repositorio.atualizar(ordem);
        return ordem;
    }

    /** Pendente -> Em Trânsito -> Concluída; Pendente -> Cancelada. */
    public OrdemTransporte atualizarStatus(Long id, String novoStatus) {
        OrdemTransporte ordem = buscar(id);
        try {
            ordem.atualizarStatus(novoStatus);
        } catch (IllegalArgumentException | IllegalStateException e) {
            throw new RegraNegocioException(e.getMessage());
        }
        repositorio.atualizar(ordem);
        return ordem;
    }

    public OrdemTransporte cancelar(Long id) {
        OrdemTransporte ordem = buscar(id);
        if (!ordem.isEditavel()) {
            throw new RegraNegocioException("Apenas ordens pendentes podem ser canceladas. Status atual: "
                    + OrdemTransporte.descreverStatus(ordem.getStatus()) + ".");
        }
        return atualizarStatus(id, OrdemTransporte.STATUS_CANCELADA);
    }

    // ------------------------------------------------------------------
    // DELETE
    // ------------------------------------------------------------------

    public void excluir(Long id) {
        OrdemTransporte ordem = buscar(id);
        if (OrdemTransporte.STATUS_EM_TRANSITO.equals(ordem.getStatus())) {
            throw new RegraNegocioException("Não é possível excluir uma ordem em trânsito; conclua-a primeiro.");
        }
        repositorio.excluir(id);
    }

    // ------------------------------------------------------------------
    // Regras auxiliares
    // ------------------------------------------------------------------

    /** Busca a ordem garantindo que ainda pode ser alterada (status Pendente). */
    public OrdemTransporte buscarEditavel(Long id) {
        OrdemTransporte ordem = buscar(id);
        if (!ordem.isEditavel()) {
            throw new RegraNegocioException("A ordem #" + id + " está "
                    + OrdemTransporte.descreverStatus(ordem.getStatus()) + " e não pode mais ser alterada.");
        }
        return ordem;
    }

    private static void exigirLote(OrdemTransporte ordem, Long idLote) {
        try {
            ordem.buscarLote(idLote);
        } catch (IllegalArgumentException e) {
            throw new RegraNegocioException(e.getMessage());
        }
    }

    private PontoColeta buscarOrigem(Long idOrigem) {
        return pontoColetaRepositorio.buscarPorId(idOrigem)
                .orElseThrow(() -> new RegraNegocioException("Ponto de coleta #" + idOrigem + " não encontrado."));
    }

    private InstituicaoReceptora buscarDestino(Long idDestino) {
        return instituicaoRepositorio.buscarPorId(idDestino)
                .orElseThrow(() -> new RegraNegocioException(
                        "Instituição receptora #" + idDestino + " não encontrada."));
    }

    private static void validarDataAgendada(LocalDate dataAgendada) {
        if (dataAgendada == null) {
            throw new RegraNegocioException("Informe a data agendada da coleta.");
        }
        if (dataAgendada.isBefore(LocalDate.now())) {
            throw new RegraNegocioException("A data agendada não pode estar no passado.");
        }
    }

    /** Usa a validação polimórfica de cada tipo de lote e confere a validade com a data da coleta. */
    private static void validarLote(LoteAlimento lote, LocalDate dataAgendada) {
        if (lote == null) {
            throw new RegraNegocioException("Informe os dados do lote.");
        }
        List<String> erros = lote.validar();
        if (erros.isEmpty() && lote.getDataValidade().isBefore(dataAgendada)) {
            erros.add("O lote \"" + lote.getDescricao() + "\" vence antes da data agendada da coleta.");
        }
        RegraNegocioException.lancarSeHouver(erros);
    }

    /** Perecíveis refrigerados/congelados não podem exceder a câmara fria do destino. */
    private static void validarCapacidadeRefrigerada(OrdemTransporte ordem) {
        double pesoRefrigerado = ordem.getLotes().stream()
                .filter(lote -> lote instanceof LotePerecivel && ((LotePerecivel) lote).isRefrigerado())
                .mapToDouble(LoteAlimento::getQuantidadeKg)
                .sum();
        double capacidade = ordem.getDestino().getCapacidadeRefrigeradaKg();
        if (pesoRefrigerado > capacidade) {
            throw new RegraNegocioException(String.format(
                    "A carga refrigerada (%.2f kg) excede a capacidade refrigerada de %s (%.2f kg).",
                    pesoRefrigerado, ordem.getDestino().getRazaoSocial(), capacidade));
        }
    }
}
