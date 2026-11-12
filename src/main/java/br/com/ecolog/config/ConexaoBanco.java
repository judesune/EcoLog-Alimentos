package br.com.ecolog.config;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Fornece conexões JDBC com o MySQL.
 * <p>
 * Lê a configuração padrão de {@code database.properties} (classpath) e permite
 * sobrescrevê-la com as variáveis de ambiente {@code ECOLOG_DB_URL},
 * {@code ECOLOG_DB_USUARIO} e {@code ECOLOG_DB_SENHA}.
 */
public final class ConexaoBanco {

    private static final String ARQUIVO_CONFIGURACAO = "database.properties";

    private final String url;
    private final String usuario;
    private final String senha;

    public ConexaoBanco() {
        Properties propriedades = carregarPropriedades();
        this.url = valorConfigurado("ECOLOG_DB_URL", propriedades.getProperty("db.url"));
        this.usuario = valorConfigurado("ECOLOG_DB_USUARIO", propriedades.getProperty("db.usuario"));
        this.senha = valorConfigurado("ECOLOG_DB_SENHA", propriedades.getProperty("db.senha", ""));
    }

    public ConexaoBanco(String url, String usuario, String senha) {
        this.url = url;
        this.usuario = usuario;
        this.senha = senha;
    }

    /** Abre uma nova conexão; quem chama deve fechá-la (try-with-resources). */
    public Connection abrirConexao() throws SQLException {
        return DriverManager.getConnection(url, usuario, senha);
    }

    /** Verifica se o banco está acessível, lançando a falha original caso não esteja. */
    public void testarConexao() throws SQLException {
        try (Connection conexao = abrirConexao()) {
            if (!conexao.isValid(5)) {
                throw new SQLException("A conexão com o banco não respondeu a tempo.");
            }
        }
    }

    public String getUrl() {
        return url;
    }

    private static Properties carregarPropriedades() {
        Properties propriedades = new Properties();
        try (InputStream entrada = ConexaoBanco.class.getClassLoader().getResourceAsStream(ARQUIVO_CONFIGURACAO)) {
            if (entrada != null) {
                propriedades.load(entrada);
            }
        } catch (IOException e) {
            throw new IllegalStateException("Não foi possível ler " + ARQUIVO_CONFIGURACAO, e);
        }
        return propriedades;
    }

    private static String valorConfigurado(String variavelAmbiente, String valorPadrao) {
        String valor = System.getenv(variavelAmbiente);
        return (valor != null && !valor.isBlank()) ? valor : valorPadrao;
    }
}
