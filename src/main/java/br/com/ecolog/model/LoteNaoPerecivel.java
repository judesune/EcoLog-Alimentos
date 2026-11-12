package br.com.ecolog.model;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

/**
 * Lote de alimento não perecível (grãos, enlatados, massas...), que não
 * depende de cadeia refrigerada.
 */
public class LoteNaoPerecivel extends LoteAlimento {

    public static final String TIPO = "NAO_PERECIVEL";

    private String tipoEmbalagem;

    public LoteNaoPerecivel() {
    }

    public LoteNaoPerecivel(Long id, String descricao, String categoria, Double quantidadeKg, LocalDate dataValidade,
                            String tipoEmbalagem) {
        super(id, descricao, categoria, quantidadeKg, dataValidade);
        this.tipoEmbalagem = tipoEmbalagem;
    }

    /**
     * Não perecíveis têm prioridade padrão mais baixa, orientada pelo prazo de
     * validade, pelo volume (cargas grandes ocupam o veículo e precisam ser
     * planejadas) e pelo tipo de embalagem (embalagens frágeis exigem cuidado).
     */
    @Override
    public int calcularPrioridadeTransporte() {
        long dias = getDiasAteVencimento();
        if (dias < 0) {
            return PRIORIDADE_MINIMA;
        }

        int prioridade;
        if (dias <= 7) {
            prioridade = 35;
        } else if (dias <= 30) {
            prioridade = 20;
        } else {
            prioridade = 10;
        }

        double quantidade = getQuantidadeKg() == null ? 0 : getQuantidadeKg();
        if (quantidade >= 500) {
            prioridade += 10;
        } else if (quantidade >= 100) {
            prioridade += 5;
        }

        if (isEmbalagemFragil()) {
            prioridade += 5;
        }
        return limitarPrioridade(prioridade);
    }

    @Override
    public List<String> validar() {
        List<String> erros = super.validar();
        if (tipoEmbalagem == null || tipoEmbalagem.isBlank()) {
            erros.add("Informe o tipo de embalagem do não perecível.");
        } else if (tipoEmbalagem.length() > 50) {
            erros.add("O tipo de embalagem deve ter no máximo 50 caracteres.");
        }
        return erros;
    }

    @Override
    public String getTipoLote() {
        return TIPO;
    }

    @Override
    public String getDescricaoTipo() {
        return "Não perecível";
    }

    public boolean isEmbalagemFragil() {
        return tipoEmbalagem != null && tipoEmbalagem.toLowerCase(Locale.ROOT).contains("vidro");
    }

    public String getTipoEmbalagem() {
        return tipoEmbalagem;
    }

    public void setTipoEmbalagem(String tipoEmbalagem) {
        this.tipoEmbalagem = tipoEmbalagem;
    }

    @Override
    public String toString() {
        return super.toString() + " | embalagem: " + tipoEmbalagem;
    }
}
