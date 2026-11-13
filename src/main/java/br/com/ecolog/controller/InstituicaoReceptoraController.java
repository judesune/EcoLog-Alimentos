package br.com.ecolog.controller;

import br.com.ecolog.model.InstituicaoReceptora;
import br.com.ecolog.service.InstituicaoReceptoraService;
import br.com.ecolog.view.EntradaConsole;

import java.util.List;

public class InstituicaoReceptoraController extends MenuController {

    private final InstituicaoReceptoraService service;

    public InstituicaoReceptoraController(EntradaConsole console, InstituicaoReceptoraService service) {
        super(console);
        this.service = service;
    }

    @Override
    protected String getTitulo() {
        return "Instituições Receptoras";
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
        InstituicaoReceptora instituicao = new InstituicaoReceptora();
        instituicao.setRazaoSocial(console.lerTexto("Razão social"));
        instituicao.setCnpj(console.lerTexto("CNPJ"));
        instituicao.setEndereco(console.lerTexto("Endereço de entrega"));
        instituicao.setCapacidadeRefrigeradaKg(console.lerDecimal("Capacidade refrigerada (kg)"));
        instituicao.setTelefone(console.lerTexto("Telefone"));
        service.cadastrar(instituicao);
        console.sucesso("Instituição cadastrada com o código #" + instituicao.getId() + ".");
    }

    private void listar() {
        List<InstituicaoReceptora> instituicoes = service.listar();
        if (instituicoes.isEmpty()) {
            console.escrever("Nenhuma instituição receptora cadastrada.");
            return;
        }
        instituicoes.forEach(instituicao -> console.escrever(instituicao.toString()));
    }

    private void consultar() {
        console.escrever(service.buscar(console.lerId("Código da instituição")).toString());
    }

    private void alterar() {
        InstituicaoReceptora instituicao = service.buscar(console.lerId("Código da instituição"));
        console.escrever("Enter mantém o valor atual.");
        instituicao.setRazaoSocial(console.lerTextoOpcional("Razão social", instituicao.getRazaoSocial()));
        instituicao.setCnpj(console.lerTextoOpcional("CNPJ", instituicao.getCnpj()));
        instituicao.setEndereco(console.lerTextoOpcional("Endereço", instituicao.getEndereco()));
        instituicao.setCapacidadeRefrigeradaKg(console.lerDecimalOpcional(
                "Capacidade refrigerada (kg)", instituicao.getCapacidadeRefrigeradaKg()));
        instituicao.setTelefone(console.lerTextoOpcional("Telefone", instituicao.getTelefone()));
        service.atualizar(instituicao);
        console.sucesso("Instituição #" + instituicao.getId() + " atualizada.");
    }

    private void excluir() {
        InstituicaoReceptora instituicao = service.buscar(console.lerId("Código da instituição"));
        console.escrever(instituicao.toString());
        if (console.confirmar("Confirma a exclusão?")) {
            service.excluir(instituicao.getId());
            console.sucesso("Instituição excluída.");
        }
    }
}
