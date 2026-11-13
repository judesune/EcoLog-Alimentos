package br.com.ecolog.controller;

import br.com.ecolog.model.LoteAlimento;
import br.com.ecolog.model.LoteNaoPerecivel;
import br.com.ecolog.model.LotePerecivel;
import br.com.ecolog.view.EntradaConsole;

import java.time.LocalDate;

/** Monta um lote (perecível ou não perecível) a partir do que o usuário digita. */
class LeitorLote {

    private static final String PERECIVEL = "1";
    private static final String NAO_PERECIVEL = "2";

    private final EntradaConsole console;

    LeitorLote(EntradaConsole console) {
        this.console = console;
    }

    /** Lê um lote novo (RF4). */
    LoteAlimento lerNovo() {
        String tipo = console.lerInteiro("Tipo do lote (1 - Perecível | 2 - Não perecível)", 1, 2) == 1
                ? PERECIVEL : NAO_PERECIVEL;
        String descricao = console.lerTexto("Descrição do produto");
        String categoria = console.lerTexto("Categoria (ex.: Laticínios, Grãos, Hortifrúti)");
        Double quantidade = console.lerDecimal("Quantidade (kg)");
        LocalDate validade = console.lerData("Data de validade");

        if (PERECIVEL.equals(tipo)) {
            Double minima = console.lerDecimal("Temperatura mínima de conservação (°C)");
            Double maxima = console.lerDecimal("Temperatura máxima de conservação (°C)");
            return new LotePerecivel(null, descricao, categoria, quantidade, validade, minima, maxima);
        }
        String embalagem = console.lerTexto("Tipo de embalagem (ex.: Saco plástico, Lata, Vidro)");
        return new LoteNaoPerecivel(null, descricao, categoria, quantidade, validade, embalagem);
    }

    /** Lê as alterações de um lote existente; Enter mantém cada valor atual. */
    LoteAlimento lerAlteracao(LoteAlimento atual) {
        console.escrever("Enter mantém o valor atual.");
        String tipoAtual = atual instanceof LotePerecivel ? PERECIVEL : NAO_PERECIVEL;
        String tipo = lerTipo(tipoAtual);
        String descricao = console.lerTextoOpcional("Descrição do produto", atual.getDescricao());
        String categoria = console.lerTextoOpcional("Categoria", atual.getCategoria());
        Double quantidade = console.lerDecimalOpcional("Quantidade (kg)", atual.getQuantidadeKg());
        LocalDate validade = console.lerDataOpcional("Data de validade", atual.getDataValidade());

        if (PERECIVEL.equals(tipo)) {
            LotePerecivel anterior = atual instanceof LotePerecivel p ? p : null;
            Double minima = anterior == null
                    ? console.lerDecimal("Temperatura mínima de conservação (°C)")
                    : console.lerDecimalOpcional("Temperatura mínima (°C)", anterior.getTemperaturaMinima());
            Double maxima = anterior == null
                    ? console.lerDecimal("Temperatura máxima de conservação (°C)")
                    : console.lerDecimalOpcional("Temperatura máxima (°C)", anterior.getTemperaturaMaxima());
            return new LotePerecivel(atual.getId(), descricao, categoria, quantidade, validade, minima, maxima);
        }
        LoteNaoPerecivel anterior = atual instanceof LoteNaoPerecivel n ? n : null;
        String embalagem = anterior == null
                ? console.lerTexto("Tipo de embalagem")
                : console.lerTextoOpcional("Tipo de embalagem", anterior.getTipoEmbalagem());
        return new LoteNaoPerecivel(atual.getId(), descricao, categoria, quantidade, validade, embalagem);
    }

    private String lerTipo(String atual) {
        while (true) {
            String valor = console.lerTextoOpcional("Tipo do lote (1 - Perecível | 2 - Não perecível)", atual);
            if (PERECIVEL.equals(valor) || NAO_PERECIVEL.equals(valor)) {
                return valor;
            }
            console.erro("Digite 1 ou 2.");
        }
    }
}
