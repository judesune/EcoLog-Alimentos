package br.com.ecolog;

import br.com.ecolog.config.ConexaoBanco;
import br.com.ecolog.controller.InstituicaoReceptoraController;
import br.com.ecolog.controller.MenuPrincipalController;
import br.com.ecolog.controller.OrdemTransporteController;
import br.com.ecolog.controller.PontoColetaController;
import br.com.ecolog.repository.InstituicaoReceptoraRepository;
import br.com.ecolog.repository.OrdemTransporteRepository;
import br.com.ecolog.repository.PontoColetaRepository;
import br.com.ecolog.service.InstituicaoReceptoraService;
import br.com.ecolog.service.OrdemTransporteService;
import br.com.ecolog.service.PontoColetaService;
import br.com.ecolog.view.EntradaConsole;
import br.com.ecolog.view.EntradaEncerradaException;

import java.io.Console;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.sql.SQLException;
import java.util.Scanner;

/** Ponto de entrada: monta as camadas (Repository -> Service -> Controller) e abre o menu. */
public final class App {

    private App() {
    }

    public static void main(String[] args) {
        Charset charset = charsetDoTerminal();
        PrintStream saida = new PrintStream(new FileOutputStream(FileDescriptor.out), true, charset);
        EntradaConsole console = new EntradaConsole(new Scanner(System.in, charset), saida);

        ConexaoBanco conexaoBanco;
        try {
            conexaoBanco = conectar(console);
        } catch (EntradaEncerradaException e) {
            conexaoBanco = null;
        }
        if (conexaoBanco == null) {
            console.escrever("Encerrando: sem conexão com o banco de dados.");
            System.exit(1);
            return;
        }

        PontoColetaRepository pontoColetaRepository = new PontoColetaRepository(conexaoBanco);
        InstituicaoReceptoraRepository instituicaoRepository = new InstituicaoReceptoraRepository(conexaoBanco);
        OrdemTransporteRepository ordemRepository = new OrdemTransporteRepository(conexaoBanco);

        PontoColetaService pontoColetaService = new PontoColetaService(pontoColetaRepository, ordemRepository);
        InstituicaoReceptoraService instituicaoService =
                new InstituicaoReceptoraService(instituicaoRepository, ordemRepository);
        OrdemTransporteService ordemService =
                new OrdemTransporteService(ordemRepository, pontoColetaRepository, instituicaoRepository);

        MenuPrincipalController menuPrincipal = new MenuPrincipalController(console,
                new PontoColetaController(console, pontoColetaService),
                new InstituicaoReceptoraController(console, instituicaoService),
                new OrdemTransporteController(console, ordemService, pontoColetaService, instituicaoService));

        try {
            menuPrincipal.exibir();
        } catch (EntradaEncerradaException e) {
            console.escrever("");
        }
        console.escrever("Até logo!");
    }

    /**
     * Tenta conectar com a configuração padrão (database.properties / variáveis
     * de ambiente). Se falhar, pede usuário e senha do MySQL no terminal e tenta
     * de novo, até conectar ou o usuário desistir.
     *
     * @return conexão testada, ou {@code null} se o usuário desistiu
     */
    private static ConexaoBanco conectar(EntradaConsole console) {
        ConexaoBanco conexaoBanco = new ConexaoBanco();
        String usuario = "root";
        while (true) {
            try {
                conexaoBanco.testarConexao();
                return conexaoBanco;
            } catch (SQLException e) {
                console.erro("Não foi possível conectar ao MySQL em " + conexaoBanco.getUrl());
                console.erro(e.getMessage());
                console.escrever("Verifique se o MySQL está ativo e se o script database/schema.sql foi executado.");
            }
            if (!console.confirmar("Deseja informar o usuário e a senha do MySQL?")) {
                return null;
            }
            usuario = console.lerTextoOpcional("Usuário do MySQL", usuario);
            String senha = console.lerSenha("Senha do MySQL (Enter se não tiver)");
            conexaoBanco = new ConexaoBanco(conexaoBanco.getUrl(), usuario, senha);
        }
    }

    /** Usa a codificação do terminal (ex.: CP850 no Windows) para exibir acentos corretamente. */
    private static Charset charsetDoTerminal() {
        Console terminal = System.console();
        return terminal != null ? terminal.charset() : Charset.defaultCharset();
    }
}
