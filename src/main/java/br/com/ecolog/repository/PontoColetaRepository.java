package br.com.ecolog.repository;

import br.com.ecolog.config.ConexaoBanco;
import br.com.ecolog.model.PontoColeta;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

/** Persistência de {@link PontoColeta} na tabela {@code tb_ponto_coleta}. */
public class PontoColetaRepository extends RepositorioJdbc<PontoColeta> {

    private static final String COLUNAS = "id_coleta, razao_social, cnpj, endereco, telefone, email";

    public PontoColetaRepository(ConexaoBanco conexaoBanco) {
        super(conexaoBanco);
    }

    @Override
    public PontoColeta salvar(PontoColeta ponto) {
        String sql = "INSERT INTO tb_ponto_coleta (razao_social, cnpj, endereco, telefone, email) VALUES (?, ?, ?, ?, ?)";
        try (Connection conexao = conexaoBanco.abrirConexao();
             PreparedStatement comando = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            preencherCampos(comando, ponto);
            comando.executeUpdate();
            ponto.setId(obterIdGerado(comando));
            return ponto;
        } catch (SQLException e) {
            throw new PersistenciaException("Erro ao cadastrar ponto de coleta", e);
        }
    }

    @Override
    public void atualizar(PontoColeta ponto) {
        String sql = "UPDATE tb_ponto_coleta SET razao_social = ?, cnpj = ?, endereco = ?, telefone = ?, email = ? "
                + "WHERE id_coleta = ?";
        try (Connection conexao = conexaoBanco.abrirConexao();
             PreparedStatement comando = conexao.prepareStatement(sql)) {
            preencherCampos(comando, ponto);
            comando.setLong(6, ponto.getId());
            comando.executeUpdate();
        } catch (SQLException e) {
            throw new PersistenciaException("Erro ao atualizar ponto de coleta #" + ponto.getId(), e);
        }
    }

    public Optional<PontoColeta> buscarPorCnpj(String cnpj) {
        String sql = "SELECT " + COLUNAS + " FROM tb_ponto_coleta WHERE cnpj = ?";
        try (Connection conexao = conexaoBanco.abrirConexao();
             PreparedStatement comando = conexao.prepareStatement(sql)) {
            comando.setString(1, cnpj);
            try (ResultSet resultado = comando.executeQuery()) {
                return resultado.next() ? Optional.of(mapearLinha(resultado)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new PersistenciaException("Erro ao buscar ponto de coleta por CNPJ", e);
        }
    }

    @Override
    protected String sqlBuscarPorId() {
        return "SELECT " + COLUNAS + " FROM tb_ponto_coleta WHERE id_coleta = ?";
    }

    @Override
    protected String sqlListarTodos() {
        return "SELECT " + COLUNAS + " FROM tb_ponto_coleta ORDER BY razao_social";
    }

    @Override
    protected String sqlExcluir() {
        return "DELETE FROM tb_ponto_coleta WHERE id_coleta = ?";
    }

    @Override
    protected PontoColeta mapearLinha(ResultSet resultado) throws SQLException {
        return new PontoColeta(
                resultado.getLong("id_coleta"),
                resultado.getString("razao_social"),
                resultado.getString("cnpj"),
                resultado.getString("endereco"),
                resultado.getString("telefone"),
                resultado.getString("email"));
    }

    private static void preencherCampos(PreparedStatement comando, PontoColeta ponto) throws SQLException {
        comando.setString(1, ponto.getRazaoSocial());
        comando.setString(2, ponto.getCnpj());
        comando.setString(3, ponto.getEndereco());
        comando.setString(4, ponto.getTelefone());
        comando.setString(5, ponto.getEmail());
    }
}
