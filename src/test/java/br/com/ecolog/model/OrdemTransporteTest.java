package br.com.ecolog.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OrdemTransporteTest {

    private OrdemTransporte ordem;

    @BeforeEach
    void criarOrdem() {
        PontoColeta origem = new PontoColeta(1L, "Mercado", "11.222.333/0001-81", "Rua A, 1", "(44) 3000-0000",
                "a@b.com");
        InstituicaoReceptora destino = new InstituicaoReceptora(1L, "ONG", "45.723.174/0001-10", "Rua B, 2",
                100.0, "(44) 3000-0001");
        ordem = new OrdemTransporte(10L, LocalDate.now().plusDays(1), null, origem, destino);
        ordem.adicionarLote(new LoteNaoPerecivel(1L, "Arroz", "Grãos", 100.0, LocalDate.now().plusDays(60), "Saco"));
        ordem.adicionarLote(new LotePerecivel(2L, "Iogurte", "Laticínios", 30.0, LocalDate.now().plusDays(1), 2.0, 6.0));
    }

    @Test
    void novaOrdemComecaPendente() {
        assertEquals(OrdemTransporte.STATUS_PENDENTE, new OrdemTransporte().getStatus());
        assertEquals(OrdemTransporte.STATUS_PENDENTE, ordem.getStatus());
    }

    @Test
    void prioridadeGeralEhADoLoteMaisUrgente() {
        assertEquals(80, ordem.calcularPrioridadeGeral());
        assertEquals(2L, ordem.getLotesPorPrioridade().get(0).getId());
    }

    @Test
    void pesoTotalSomaTodosOsLotes() {
        assertEquals(130.0, ordem.getPesoTotalKg(), 0.001);
    }

    @Test
    void fluxoCompletoPendenteEmTransitoConcluida() {
        ordem.atualizarStatus(OrdemTransporte.STATUS_EM_TRANSITO);
        assertFalse(ordem.isEditavel());
        ordem.atualizarStatus(OrdemTransporte.STATUS_CONCLUIDA);
        assertEquals(OrdemTransporte.STATUS_CONCLUIDA, ordem.getStatus());
    }

    @Test
    void soOrdemPendentePodeSerCancelada() {
        ordem.atualizarStatus(OrdemTransporte.STATUS_EM_TRANSITO);
        assertThrows(IllegalStateException.class, () -> ordem.atualizarStatus(OrdemTransporte.STATUS_CANCELADA));
    }

    @Test
    void ordemCanceladaEhEstadoFinal() {
        ordem.atualizarStatus(OrdemTransporte.STATUS_CANCELADA);
        assertThrows(IllegalStateException.class, () -> ordem.atualizarStatus(OrdemTransporte.STATUS_EM_TRANSITO));
    }

    @Test
    void statusDesconhecidoEhRejeitado() {
        assertThrows(IllegalArgumentException.class, () -> ordem.atualizarStatus("PERDIDA"));
    }

    @Test
    void removerLoteRetiraApenasOLoteInformado() {
        ordem.removerLote(1L);
        assertEquals(1, ordem.getLotes().size());
        assertEquals(2L, ordem.getLotes().get(0).getId());
    }

    @Test
    void removerLoteDeOutraOrdemFalha() {
        assertThrows(IllegalArgumentException.class, () -> ordem.removerLote(99L));
    }

    @Test
    void listaDeLotesNaoPodeSerAlteradaPorFora() {
        assertThrows(UnsupportedOperationException.class, () -> ordem.getLotes().clear());
        assertTrue(ordem.getLotes().size() == 2);
    }
}
