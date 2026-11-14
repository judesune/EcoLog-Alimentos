package br.com.ecolog.repository;

import br.com.ecolog.config.ConexaoBanco;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class RepositorioJdbc<T> implements Repositorio<T> {

    protected final ConexaoBanco conexaoBanco;

    protected RepositorioJdbc(ConexaoBanco conexaoBanco) {
        this.conexaoBanco = conexaoBanco;
    }

    protected abstract String sqlBuscarPorId();

    protected abstract String sqlListarTodos();

    protected abstract String sqlExcluir();

    protected abstract T mapearLinha(ResultSet resultado) throws SQLException;

    @Override
    public Optional<T> buscarPorId(Long id) {
        try (Connection conexao = conexaoBanco.abrirConexao();
             PreparedStatement comando = conexao.prepareStatement(sqlBuscarPorId())) {
            comando.setLong(1, id);
            try (ResultSet resultado = comando.executeQuery()) {
                return resultado.next() ? Optional.of(mapearLinha(resultado)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new PersistenciaException("Erro ao buscar registro #" + id, e);
        }
    }

    @Override
    public List<T> listarTodos() {
        try (Connection conexao = conexaoBanco.abrirConexao();
             PreparedStatement comando = conexao.prepareStatement(sqlListarTodos());
             ResultSet resultado = comando.executeQuery()) {
            List<T> entidades = new ArrayList<>();
            while (resultado.next()) {
                entidades.add(mapearLinha(resultado));
            }
            return entidades;
        } catch (SQLException e) {
            throw new PersistenciaException("Erro ao listar registros", e);
        }
    }

    @Override
    public boolean excluir(Long id) {
        try (Connection conexao = conexaoBanco.abrirConexao();
             PreparedStatement comando = conexao.prepareStatement(sqlExcluir())) {
            comando.setLong(1, id);
            return comando.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new PersistenciaException("Erro ao excluir registro #" + id, e);
        }
    }

    protected static Long obterIdGerado(Statement comando) throws SQLException {
        try (ResultSet chaves = comando.getGeneratedKeys()) {
            if (!chaves.next()) {
                throw new SQLException("O banco não retornou o id gerado.");
            }
            return chaves.getLong(1);
        }
    }

    protected long contar(String sql, Long parametro) {
        try (Connection conexao = conexaoBanco.abrirConexao();
             PreparedStatement comando = conexao.prepareStatement(sql)) {
            comando.setLong(1, parametro);
            try (ResultSet resultado = comando.executeQuery()) {
                resultado.next();
                return resultado.getLong(1);
            }
        } catch (SQLException e) {
            throw new PersistenciaException("Erro ao contar registros", e);
        }
    }
}
