package br.com.ecolog.model;

import java.time.LocalDate;
import java.util.List;

public class LotePerecivel extends LoteAlimento {

    public static final String TIPO = "PERECIVEL";

    static final double LIMITE_CONGELADO = 0.0;

    static final double LIMITE_REFRIGERADO = 8.0;

    private Double temperaturaMinima;
    private Double temperaturaMaxima;

    public LotePerecivel() {
    }

    public LotePerecivel(Long id, String descricao, String categoria, Double quantidadeKg, LocalDate dataValidade,
                         Double temperaturaMinima, Double temperaturaMaxima) {
        super(id, descricao, categoria, quantidadeKg, dataValidade);
        this.temperaturaMinima = temperaturaMinima;
        this.temperaturaMaxima = temperaturaMaxima;
    }

    @Override
    public int calcularPrioridadeTransporte() {
        long dias = getDiasAteVencimento();
        if (dias < 0) {
            return PRIORIDADE_MINIMA;
        }

        int prioridade;
        if (dias == 0) {
            prioridade = 70;
        } else if (dias == 1) {
            prioridade = 60;
        } else if (dias <= 3) {
            prioridade = 45;
        } else if (dias <= 7) {
            prioridade = 30;
        } else {
            prioridade = 15;
        }

        if (isCongelado()) {
            prioridade += 30;
        } else if (isRefrigerado()) {
            prioridade += 20;
        } else {
            prioridade += 5;
        }
        return limitarPrioridade(prioridade);
    }

    @Override
    public List<String> validar() {
        List<String> erros = super.validar();
        if (temperaturaMinima == null || temperaturaMaxima == null) {
            erros.add("Informe a faixa de temperatura de conservação do perecível.");
        } else {
            if (temperaturaMinima > temperaturaMaxima) {
                erros.add("A temperatura mínima não pode ser maior que a máxima.");
            }
            if (temperaturaMinima < -40 || temperaturaMaxima > 40) {
                erros.add("A faixa de temperatura deve ficar entre -40°C e 40°C.");
            }
        }
        return erros;
    }

    @Override
    public String getTipoLote() {
        return TIPO;
    }

    @Override
    public String getDescricaoTipo() {
        return "Perecível";
    }

    public boolean isCongelado() {
        return temperaturaMaxima != null && temperaturaMaxima <= LIMITE_CONGELADO;
    }

    public boolean isRefrigerado() {
        return temperaturaMaxima != null && temperaturaMaxima <= LIMITE_REFRIGERADO;
    }

    public Double getTemperaturaMinima() {
        return temperaturaMinima;
    }

    public void setTemperaturaMinima(Double temperaturaMinima) {
        this.temperaturaMinima = temperaturaMinima;
    }

    public Double getTemperaturaMaxima() {
        return temperaturaMaxima;
    }

    public void setTemperaturaMaxima(Double temperaturaMaxima) {
        this.temperaturaMaxima = temperaturaMaxima;
    }

    @Override
    public String toString() {
        return super.toString() + String.format(" | faixa %.1f°C a %.1f°C", temperaturaMinima, temperaturaMaxima);
    }
}
