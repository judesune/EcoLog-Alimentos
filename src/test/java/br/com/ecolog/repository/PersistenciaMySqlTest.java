package br.com.ecolog.repository;

import br.com.ecolog.config.ConexaoBanco;
import br.com.ecolog.model.InstituicaoReceptora;
import br.com.ecolog.model.LoteAlimento;
import br.com.ecolog.model.LoteNaoPerecivel;
import br.com.ecolog.model.LotePerecivel;
import br.com.ecolog.model.OrdemTransporte;
import br.com.ecolog.model.PontoColeta;
import br.com.ecolog.service.InstituicaoReceptoraService;
import br.com.ecolog.service.OrdemTransporteService;
import br.com.ecolog.service.PontoColetaService;
import br.com.ecolog.service.RegraNegocioException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@EnabledIfEnvironmentVariable(named = "ECOLOG_DB_TESTE", matches = "true")
class PersistenciaMySqlTest {

    private final ConexaoBanco conexaoBanco = new ConexaoBanco();
    private final PontoColetaRepository pontoRepositorio = new PontoColetaRepository(conexaoBanco);
    private final InstituicaoReceptoraRepository instituicaoRepositorio =
            new InstituicaoReceptoraRepository(conexaoBanco);
    private final OrdemTransporteRepository ordemRepositorio = new OrdemTransporteRepository(conexaoBanco);

    private final PontoColetaService pontoService = new PontoColetaService(pontoRepositorio, ordemRepositorio);
    private final InstituicaoReceptoraService instituicaoService =
            new InstituicaoReceptoraService(instituicaoRepositorio, ordemRepositorio);
    private final OrdemTransporteService ordemService =
            new OrdemTransporteService(ordemRepositorio, pontoRepositorio, instituicaoRepositorio);

    @Test
    void crudCompletoDaOrdemDeTransporteComLotes() throws Exception {
        PontoColeta ponto = pontoService.cadastrar(new PontoColeta(null, "Mercado Teste", cnpjAleatorio(),
                "Rua Teste, 10", "(44) 99999-0000", "teste@mercado.com"));
        InstituicaoReceptora instituicao = instituicaoService.cadastrar(new InstituicaoReceptora(null,
                "ONG Teste", cnpjAleatorio(), "Rua ONG, 20", 50.0, "(44) 98888-0000"));
        try {
            OrdemTransporte ordem = ordemService.criar(LocalDate.now().plusDays(1), ponto.getId(),
                    instituicao.getId(), List.of(
                            new LotePerecivel(null, "Iogurte", "Laticínios", 20.0, LocalDate.now().plusDays(3),
                                    2.0, 6.0),
                            new LoteNaoPerecivel(null, "Arroz", "Grãos", 100.0, LocalDate.now().plusDays(90),
                                    "Saco plástico")));
            assertNotNull(ordem.getId());

            OrdemTransporte lida = ordemService.buscar(ordem.getId());
            assertEquals(2, lida.getLotes().size());
            assertInstanceOf(LotePerecivel.class, lida.getLotesPorPrioridade().get(0));
            assertInstanceOf(LoteNaoPerecivel.class, lida.getLotesPorPrioridade().get(1));
            assertEquals(ponto.getRazaoSocial(), lida.getOrigem().getRazaoSocial());

            assertThrows(RegraNegocioException.class, () -> ordemService.adicionarLote(ordem.getId(),
                    new LotePerecivel(null, "Carne", "Carnes", 40.0, LocalDate.now().plusDays(2), -2.0, 4.0)));

            ordemService.adicionarLote(ordem.getId(), new LoteNaoPerecivel(null, "Feijão", "Grãos", 30.0,
                    LocalDate.now().plusDays(40), "Saco plástico"));
            Long idArroz = lida.getLotesPorPrioridade().get(1).getId();
            ordemService.atualizarLote(ordem.getId(), new LoteNaoPerecivel(idArroz, "Arroz integral", "Grãos",
                    120.0, LocalDate.now().plusDays(90), "Saco plástico"));
            Long idIogurte = lida.getLotesPorPrioridade().get(0).getId();
            ordemService.removerLote(ordem.getId(), idIogurte);
            ordemService.atualizarDados(ordem.getId(), LocalDate.now().plusDays(2), ponto.getId(),
                    instituicao.getId());

            OrdemTransporte atualizada = ordemService.buscar(ordem.getId());
            assertEquals(2, atualizada.getLotes().size());
            assertEquals(LocalDate.now().plusDays(2), atualizada.getDataAgendada());
            assertTrue(atualizada.getLotes().stream().anyMatch(l -> l.getDescricao().equals("Arroz integral")));
            assertTrue(atualizada.getLotes().stream().noneMatch(l -> l.getId().equals(idIogurte)));

            ordemService.atualizarStatus(ordem.getId(), OrdemTransporte.STATUS_EM_TRANSITO);
            assertThrows(RegraNegocioException.class, () -> ordemService.cancelar(ordem.getId()));
            assertThrows(RegraNegocioException.class, () -> pontoService.excluir(ponto.getId()));
            ordemService.atualizarStatus(ordem.getId(), OrdemTransporte.STATUS_CONCLUIDA);
            assertEquals(OrdemTransporte.STATUS_CONCLUIDA, ordemService.buscar(ordem.getId()).getStatus());

            List<Long> idsLotes = atualizada.getLotes().stream().map(LoteAlimento::getId).toList();
            ordemService.excluir(ordem.getId());
            assertTrue(ordemRepositorio.buscarPorId(ordem.getId()).isEmpty());
            for (Long idLote : idsLotes) {
                assertEquals(0, contarLote(idLote));
            }
        } finally {
            pontoService.excluir(ponto.getId());
            instituicaoService.excluir(instituicao.getId());
        }
        assertTrue(pontoRepositorio.buscarPorId(ponto.getId()).isEmpty());
        assertTrue(instituicaoRepositorio.buscarPorId(instituicao.getId()).isEmpty());
    }

    private long contarLote(Long idLote) throws Exception {
        try (Connection conexao = conexaoBanco.abrirConexao();
             PreparedStatement comando = conexao.prepareStatement(
                     "SELECT COUNT(*) FROM tb_lote_alimento WHERE id_lote = ?")) {
            comando.setLong(1, idLote);
            try (ResultSet resultado = comando.executeQuery()) {
                resultado.next();
                return resultado.getLong(1);
            }
        }
    }

    private static String cnpjAleatorio() {
        StringBuilder digitos = new StringBuilder("9");
        for (int i = 0; i < 13; i++) {
            digitos.append(ThreadLocalRandom.current().nextInt(10));
        }
        return digitos.toString();
    }
}
