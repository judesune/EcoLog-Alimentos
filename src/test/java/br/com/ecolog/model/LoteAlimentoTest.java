package br.com.ecolog.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LoteAlimentoTest {

    private static LocalDate daquiA(int dias) {
        return LocalDate.now().plusDays(dias);
    }

    @Test
    void perecivelRefrigeradoQueVenceAmanhaTemPrioridadeAlta() {
        LoteAlimento iogurte = new LotePerecivel(1L, "Iogurte", "Laticínios", 40.0, daquiA(1), 2.0, 6.0);
        assertEquals(80, iogurte.calcularPrioridadeTransporte());
    }

    @Test
    void perecivelCongeladoQueVenceHojeRecebePrioridadeMaxima() {
        LoteAlimento frango = new LotePerecivel(1L, "Frango", "Carnes", 80.0, daquiA(0), -18.0, -12.0);
        assertEquals(LoteAlimento.PRIORIDADE_MAXIMA, frango.calcularPrioridadeTransporte());
    }

    @Test
    void perecivelEmTemperaturaAmbienteComPrazoLongo() {
        LoteAlimento banana = new LotePerecivel(1L, "Banana", "Hortifrúti", 25.0, daquiA(10), 12.0, 25.0);
        assertEquals(20, banana.calcularPrioridadeTransporte());
    }

    @Test
    void naoPerecivelComPrazoLongoTemPrioridadeBaixa() {
        LoteAlimento arroz = new LoteNaoPerecivel(1L, "Arroz", "Grãos", 50.0, daquiA(120), "Saco plástico");
        assertEquals(10, arroz.calcularPrioridadeTransporte());
    }

    @Test
    void naoPerecivelConsideraPrazoVolumeEEmbalagemFragil() {
        LoteAlimento palmito = new LoteNaoPerecivel(1L, "Palmito", "Conservas", 600.0, daquiA(5), "Pote de vidro");
        assertEquals(50, palmito.calcularPrioridadeTransporte());
    }

    @Test
    void loteVencidoNaoDeveSerPriorizado() {
        LoteAlimento leite = new LotePerecivel(1L, "Leite", "Laticínios", 10.0, daquiA(-1), 2.0, 6.0);
        LoteAlimento feijao = new LoteNaoPerecivel(2L, "Feijão", "Grãos", 10.0, daquiA(-3), "Saco");
        assertEquals(0, leite.calcularPrioridadeTransporte());
        assertEquals(0, feijao.calcularPrioridadeTransporte());
    }

    @Test
    void mesmaChamadaProduzResultadosDiferentesPorPolimorfismo() {
        List<LoteAlimento> lotes = List.of(
                new LotePerecivel(1L, "Queijo", "Laticínios", 20.0, daquiA(3), 2.0, 8.0),
                new LoteNaoPerecivel(2L, "Macarrão", "Massas", 20.0, daquiA(3), "Pacote"));

        assertEquals(65, lotes.get(0).calcularPrioridadeTransporte());
        assertEquals(35, lotes.get(1).calcularPrioridadeTransporte());
        assertEquals("PERECIVEL", lotes.get(0).getTipoLote());
        assertEquals("NAO_PERECIVEL", lotes.get(1).getTipoLote());
    }

    @Test
    void validacaoDoPerecivelExigeFaixaDeTemperaturaCoerente() {
        LoteAlimento semTemperatura = new LotePerecivel(null, "Carne", "Carnes", 5.0, daquiA(2), null, null);
        LoteAlimento faixaInvertida = new LotePerecivel(null, "Carne", "Carnes", 5.0, daquiA(2), 8.0, 2.0);

        assertTrue(semTemperatura.validar().stream().anyMatch(erro -> erro.contains("faixa de temperatura")));
        assertTrue(faixaInvertida.validar().stream().anyMatch(erro -> erro.contains("mínima não pode ser maior")));
    }

    @Test
    void validacaoDoNaoPerecivelExigeEmbalagemEDadosComuns() {
        LoteAlimento invalido = new LoteNaoPerecivel(null, "", "Grãos", 0.0, daquiA(-1), " ");
        List<String> erros = invalido.validar();

        assertTrue(erros.contains("Informe a descrição do lote."));
        assertTrue(erros.contains("A quantidade deve ser maior que zero."));
        assertTrue(erros.contains("Não é possível resgatar um lote já vencido."));
        assertTrue(erros.contains("Informe o tipo de embalagem do não perecível."));
    }

    @Test
    void loteValidoNaoTemErros() {
        LoteAlimento lote = new LotePerecivel(null, "Alface", "Hortifrúti", 12.5, daquiA(2), 4.0, 10.0);
        assertTrue(lote.validar().isEmpty());
    }
}
