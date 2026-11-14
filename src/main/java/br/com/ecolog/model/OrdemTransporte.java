package br.com.ecolog.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class OrdemTransporte {

    public static final String STATUS_PENDENTE = "PENDENTE";
    public static final String STATUS_EM_TRANSITO = "EM_TRANSITO";
    public static final String STATUS_CONCLUIDA = "CONCLUIDA";
    public static final String STATUS_CANCELADA = "CANCELADA";

    private Long id;
    private LocalDate dataAgendada;
    private String status;
    private PontoColeta origem;
    private InstituicaoReceptora destino;
    private final List<LoteAlimento> lotes = new ArrayList<>();

    public OrdemTransporte() {
        this.status = STATUS_PENDENTE;
    }

    public OrdemTransporte(Long id, LocalDate dataAgendada, String status,
                           PontoColeta origem, InstituicaoReceptora destino) {
        this.id = id;
        this.dataAgendada = dataAgendada;
        this.status = status == null ? STATUS_PENDENTE : status;
        this.origem = origem;
        this.destino = destino;
    }

    public void adicionarLote(LoteAlimento lote) {
        Objects.requireNonNull(lote, "O lote não pode ser nulo.");
        lotes.add(lote);
    }

    public void removerLote(Long idLote) {
        boolean removido = lotes.removeIf(lote -> lote.getId() != null && lote.getId().equals(idLote));
        if (!removido) {
            throw new IllegalArgumentException("O lote #" + idLote + " não pertence à ordem #" + id + ".");
        }
    }

    public LoteAlimento buscarLote(Long idLote) {
        return lotes.stream()
                .filter(lote -> lote.getId() != null && lote.getId().equals(idLote))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "O lote #" + idLote + " não pertence à ordem #" + id + "."));
    }

    public List<LoteAlimento> getLotes() {
        return Collections.unmodifiableList(lotes);
    }

    public List<LoteAlimento> getLotesPorPrioridade() {
        List<LoteAlimento> ordenados = new ArrayList<>(lotes);
        ordenados.sort(Comparator.comparingInt(LoteAlimento::calcularPrioridadeTransporte).reversed());
        return ordenados;
    }

    public int calcularPrioridadeGeral() {
        return lotes.stream()
                .mapToInt(LoteAlimento::calcularPrioridadeTransporte)
                .max()
                .orElse(LoteAlimento.PRIORIDADE_MINIMA);
    }

    public double getPesoTotalKg() {
        return lotes.stream()
                .mapToDouble(lote -> lote.getQuantidadeKg() == null ? 0 : lote.getQuantidadeKg())
                .sum();
    }

    public void atualizarStatus(String novoStatus) {
        if (!isStatusValido(novoStatus)) {
            throw new IllegalArgumentException("Status inválido: " + novoStatus);
        }
        if (!podeMudarPara(novoStatus)) {
            throw new IllegalStateException(String.format(
                    "Não é permitido mudar a ordem de %s para %s.", descreverStatus(status), descreverStatus(novoStatus)));
        }
        this.status = novoStatus;
    }

    public boolean podeMudarPara(String novoStatus) {
        switch (status) {
            case STATUS_PENDENTE:
                return STATUS_EM_TRANSITO.equals(novoStatus) || STATUS_CANCELADA.equals(novoStatus);
            case STATUS_EM_TRANSITO:
                return STATUS_CONCLUIDA.equals(novoStatus);
            default:
                return false; // Concluída e Cancelada são estados finais
        }
    }

    public boolean isEditavel() {
        return STATUS_PENDENTE.equals(status);
    }

    public static boolean isStatusValido(String status) {
        return STATUS_PENDENTE.equals(status) || STATUS_EM_TRANSITO.equals(status)
                || STATUS_CONCLUIDA.equals(status) || STATUS_CANCELADA.equals(status);
    }

    public static String descreverStatus(String status) {
        if (status == null) {
            return "-";
        }
        switch (status) {
            case STATUS_PENDENTE:
                return "Pendente";
            case STATUS_EM_TRANSITO:
                return "Em Trânsito";
            case STATUS_CONCLUIDA:
                return "Concluída";
            case STATUS_CANCELADA:
                return "Cancelada";
            default:
                return status;
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDataAgendada() {
        return dataAgendada;
    }

    public void setDataAgendada(LocalDate dataAgendada) {
        this.dataAgendada = dataAgendada;
    }

    public String getStatus() {
        return status;
    }

    public PontoColeta getOrigem() {
        return origem;
    }

    public void setOrigem(PontoColeta origem) {
        this.origem = origem;
    }

    public InstituicaoReceptora getDestino() {
        return destino;
    }

    public void setDestino(InstituicaoReceptora destino) {
        this.destino = destino;
    }

    @Override
    public String toString() {
        return String.format("Ordem #%s | %s | agendada para %s | %s -> %s | %d lote(s), %.2f kg | prioridade %d",
                id == null ? "nova" : id,
                descreverStatus(status),
                dataAgendada == null ? "-" : dataAgendada.format(LoteAlimento.FORMATO_DATA),
                origem == null ? "-" : origem.getRazaoSocial(),
                destino == null ? "-" : destino.getRazaoSocial(),
                lotes.size(),
                getPesoTotalKg(),
                calcularPrioridadeGeral());
    }
}
