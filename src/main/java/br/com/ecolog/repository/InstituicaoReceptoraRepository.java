package br.com.ecolog.repository;

import br.com.ecolog.config.ConexaoBanco;
import br.com.ecolog.model.InstituicaoReceptora;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

/** Persistência de {@link InstituicaoReceptora} na tabela {@code tb_instituicao_receptora}. */
public class InstituicaoReceptoraRepository extends RepositorioJdbc<InstituicaoReceptora> {

    private static final String COLUNAS =
            "id_receptora, razao_social, cnpj, endereco, capacidade_refrigerada_kg, telefone";

    public InstituicaoReceptoraRepository(ConexaoBanco conexaoBanco) {
        super(conexaoBanco);
    }

    @Override
    public InstituicaoReceptora salvar(InstituicaoReceptora instituicao) {
        String sql = "INSERT INTO tb_instituicao_receptora "
                + "(razao_social, cnpj, endereco, capacidade_refrigerada_kg, telefone) VALUES (?, ?, ?, ?, ?)";
        try (Connection conexao = conexaoBanco.abrirConexao();
             PreparedStatement comando = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            preencherCampos(comando, instituicao);
            comando.executeUpdate();
            instituicao.setId(obterIdGerado(comando));
            return instituicao;
        } catch (SQLException e) {
            throw new PersistenciaException("Erro ao cadastrar instituição receptora", e);
        }
    }

    @Override
    public void atualizar(InstituicaoReceptora instituicao) {
        String sql = "UPDATE tb_instituicao_receptora SET razao_social = ?, cnpj = ?, endereco = ?, "
                + "capacidade_refrigerada_kg = ?, telefone = ? WHERE id_receptora = ?";
        try (Connection conexao = conexaoBanco.abrirConexao();
             PreparedStatement comando = conexao.prepareStatement(sql)) {
            preencherCampos(comando, instituicao);
            comando.setLong(6, instituicao.getId());
            comando.executeUpdate();
        } catch (SQLException e) {
            throw new PersistenciaException("Erro ao atualizar instituição receptora #" + instituicao.getId(), e);
        }
    }

    public Optional<InstituicaoReceptora> buscarPorCnpj(String cnpj) {
        String sql = "SELECT " + COLUNAS + " FROM tb_instituicao_receptora WHERE cnpj = ?";
        try (Connection conexao = conexaoBanco.abrirConexao();
             PreparedStatement comando = conexao.prepareStatement(sql)) {
            comando.setString(1, cnpj);
            try (ResultSet resultado = comando.executeQuery()) {
                return resultado.next() ? Optional.of(mapearLinha(resultado)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new PersistenciaException("Erro ao buscar instituição receptora por CNPJ", e);
        }
    }

    @Override
    protected String sqlBuscarPorId() {
        return "SELECT " + COLUNAS + " FROM tb_instituicao_receptora WHERE id_receptora = ?";
    }

    @Override
    protected String sqlListarTodos() {
        return "SELECT " + COLUNAS + " FROM tb_instituicao_receptora ORDER BY razao_social";
    }

    @Override
    protected String sqlExcluir() {
        return "DELETE FROM tb_instituicao_receptora WHERE id_receptora = ?";
    }

    @Override
    protected InstituicaoReceptora mapearLinha(ResultSet resultado) throws SQLException {
        return new InstituicaoReceptora(
                resultado.getLong("id_receptora"),
                resultado.getString("razao_social"),
                resultado.getString("cnpj"),
                resultado.getString("endereco"),
                resultado.getBigDecimal("capacidade_refrigerada_kg").doubleValue(),
                resultado.getString("telefone"));
    }

    private static void preencherCampos(PreparedStatement comando, InstituicaoReceptora instituicao)
            throws SQLException {
        comando.setString(1, instituicao.getRazaoSocial());
        comando.setString(2, instituicao.getCnpj());
        comando.setString(3, instituicao.getEndereco());
        comando.setBigDecimal(4, BigDecimal.valueOf(instituicao.getCapacidadeRefrigeradaKg()));
        comando.setString(5, instituicao.getTelefone());
    }
}
