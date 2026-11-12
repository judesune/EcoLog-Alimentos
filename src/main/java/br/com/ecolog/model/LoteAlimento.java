package br.com.ecolog.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe abstrata que reúne o que é comum a todo lote de alimento resgatado.
 * <p>
 * Cada tipo concreto de lote ({@link LotePerecivel} e {@link LoteNaoPerecivel})
 * define a própria regra de prioridade sobrescrevendo
 * {@link #calcularPrioridadeTransporte()} (polimorfismo).
 */
public abstract class LoteAlimento {

    /** Escala de prioridade usada por todos os lotes: 0 (baixa) a 100 (máxima). */
    public static final int PRIORIDADE_MINIMA = 0;
    public static final int PRIORIDADE_MAXIMA = 100;

    /** Formato de data exibido nas descrições (dd/mm/aaaa). */
    public static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private Long id;
    private String descricao;
    private String categoria;
    private Double quantidadeKg;
    private LocalDate dataValidade;

    protected LoteAlimento() {
    }

    protected LoteAlimento(Long id, String descricao, String categoria, Double quantidadeKg, LocalDate dataValidade) {
        this.id = id;
        this.descricao = descricao;
        this.categoria = categoria;
        this.quantidadeKg = quantidadeKg;
        this.dataValidade = dataValidade;
    }

    /**
     * Calcula a prioridade de transporte deste lote (0 a 100).
     * Quanto maior o valor, mais cedo o lote deve ser despachado.
     */
    public abstract int calcularPrioridadeTransporte();

    /** Identificador do tipo, gravado na coluna {@code tipo_lote}. */
    public abstract String getTipoLote();

    /** Descrição legível do tipo, usada nas telas. */
    public abstract String getDescricaoTipo();

    /**
     * Valida os dados comuns do lote. Subclasses sobrescrevem para acrescentar
     * as próprias regras, chamando {@code super.validar()} primeiro.
     *
     * @return lista de problemas encontrados (vazia se o lote é válido)
     */
    public List<String> validar() {
        List<String> erros = new ArrayList<>();
        if (descricao == null || descricao.isBlank()) {
            erros.add("Informe a descrição do lote.");
        } else if (descricao.length() > 120) {
            erros.add("A descrição do lote deve ter no máximo 120 caracteres.");
        }
        if (categoria == null || categoria.isBlank()) {
            erros.add("Informe a categoria do lote.");
        } else if (categoria.length() > 50) {
            erros.add("A categoria deve ter no máximo 50 caracteres.");
        }
        if (quantidadeKg == null || quantidadeKg <= 0) {
            erros.add("A quantidade deve ser maior que zero.");
        } else if (quantidadeKg >= 100_000_000) {
            erros.add("A quantidade informada é grande demais.");
        }
        if (dataValidade == null) {
            erros.add("Informe a data de validade.");
        } else if (isVencido()) {
            erros.add("Não é possível resgatar um lote já vencido.");
        }
        return erros;
    }

    /** Dias que faltam até o vencimento (negativo se já venceu). */
    public long getDiasAteVencimento() {
        return ChronoUnit.DAYS.between(LocalDate.now(), dataValidade);
    }

    public boolean isVencido() {
        return getDiasAteVencimento() < 0;
    }

    /** Limita qualquer cálculo de prioridade à escala 0..100. */
    protected static int limitarPrioridade(int prioridade) {
        return Math.max(PRIORIDADE_MINIMA, Math.min(PRIORIDADE_MAXIMA, prioridade));
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public Double getQuantidadeKg() {
        return quantidadeKg;
    }

    public void setQuantidadeKg(Double quantidadeKg) {
        this.quantidadeKg = quantidadeKg;
    }

    public LocalDate getDataValidade() {
        return dataValidade;
    }

    public void setDataValidade(LocalDate dataValidade) {
        this.dataValidade = dataValidade;
    }

    @Override
    public String toString() {
        return String.format("Lote #%s | %s | %s | %s | %.2f kg | validade %s (%d dia(s)) | prioridade %d",
                id == null ? "novo" : id, getDescricaoTipo(), descricao, categoria, quantidadeKg,
                dataValidade == null ? "-" : dataValidade.format(FORMATO_DATA), getDiasAteVencimento(),
                calcularPrioridadeTransporte());
    }
}
