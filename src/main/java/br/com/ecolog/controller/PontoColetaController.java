package br.com.ecolog.controller;

import br.com.ecolog.model.PontoColeta;
import br.com.ecolog.service.PontoColetaService;
import br.com.ecolog.view.EntradaConsole;

import java.util.List;

public class PontoColetaController extends MenuController {

    private final PontoColetaService service;

    public PontoColetaController(EntradaConsole console, PontoColetaService service) {
        super(console);
        this.service = service;
    }

    @Override
    protected String getTitulo() {
        return "Pontos de Coleta";
    }

    @Override
    protected List<String> getOpcoes() {
        return List.of("Cadastrar", "Listar", "Consultar por código", "Alterar", "Excluir");
    }

    @Override
    protected void executarOpcao(int opcao) {
        switch (opcao) {
            case 1 -> cadastrar();
            case 2 -> listar();
            case 3 -> consultar();
            case 4 -> alterar();
            case 5 -> excluir();
            default -> throw new IllegalArgumentException("Opção inexistente: " + opcao);
        }
    }

    private void cadastrar() {
        PontoColeta ponto = new PontoColeta();
        ponto.setRazaoSocial(console.lerTexto("Razão social"));
        ponto.setCnpj(console.lerTexto("CNPJ"));
        ponto.setEndereco(console.lerTexto("Endereço completo de coleta"));
        ponto.setTelefone(console.lerTexto("Telefone"));
        ponto.setEmail(console.lerTexto("E-mail"));
        service.cadastrar(ponto);
        console.sucesso("Ponto de coleta cadastrado com o código #" + ponto.getId() + ".");
    }

    private void listar() {
        List<PontoColeta> pontos = service.listar();
        if (pontos.isEmpty()) {
            console.escrever("Nenhum ponto de coleta cadastrado.");
            return;
        }
        pontos.forEach(ponto -> console.escrever(ponto.toString()));
    }

    private void consultar() {
        console.escrever(service.buscar(console.lerId("Código do ponto de coleta")).toString());
    }

    private void alterar() {
        PontoColeta ponto = service.buscar(console.lerId("Código do ponto de coleta"));
        console.escrever("Enter mantém o valor atual.");
        ponto.setRazaoSocial(console.lerTextoOpcional("Razão social", ponto.getRazaoSocial()));
        ponto.setCnpj(console.lerTextoOpcional("CNPJ", ponto.getCnpj()));
        ponto.setEndereco(console.lerTextoOpcional("Endereço", ponto.getEndereco()));
        ponto.setTelefone(console.lerTextoOpcional("Telefone", ponto.getTelefone()));
        ponto.setEmail(console.lerTextoOpcional("E-mail", ponto.getEmail()));
        service.atualizar(ponto);
        console.sucesso("Ponto de coleta #" + ponto.getId() + " atualizado.");
    }

    private void excluir() {
        PontoColeta ponto = service.buscar(console.lerId("Código do ponto de coleta"));
        console.escrever(ponto.toString());
        if (console.confirmar("Confirma a exclusão?")) {
            service.excluir(ponto.getId());
            console.sucesso("Ponto de coleta excluído.");
        }
    }
}
