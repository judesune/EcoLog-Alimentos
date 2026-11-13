package br.com.ecolog.service;

import br.com.ecolog.model.InstituicaoReceptora;
import br.com.ecolog.repository.InstituicaoReceptoraRepository;
import br.com.ecolog.repository.OrdemTransporteRepository;

import java.util.ArrayList;
import java.util.List;

/** Regras de negócio do cadastro de instituições receptoras (RF2). */
public class InstituicaoReceptoraService {

    private final InstituicaoReceptoraRepository repositorio;
    private final OrdemTransporteRepository ordemRepositorio;

    public InstituicaoReceptoraService(InstituicaoReceptoraRepository repositorio,
                                       OrdemTransporteRepository ordemRepositorio) {
        this.repositorio = repositorio;
        this.ordemRepositorio = ordemRepositorio;
    }

    public InstituicaoReceptora cadastrar(InstituicaoReceptora instituicao) {
        validar(instituicao);
        return repositorio.salvar(instituicao);
    }

    public List<InstituicaoReceptora> listar() {
        return repositorio.listarTodos();
    }

    public InstituicaoReceptora buscar(Long id) {
        return repositorio.buscarPorId(id)
                .orElseThrow(() -> new RegraNegocioException("Instituição receptora #" + id + " não encontrada."));
    }

    public void atualizar(InstituicaoReceptora instituicao) {
        buscar(instituicao.getId());
        validar(instituicao);
        repositorio.atualizar(instituicao);
    }

    public void excluir(Long id) {
        buscar(id);
        long ordens = ordemRepositorio.contarPorInstituicao(id);
        if (ordens > 0) {
            throw new RegraNegocioException(
                    "Não é possível excluir: a instituição é destino de " + ordens + " ordem(ns) de transporte.");
        }
        repositorio.excluir(id);
    }

    private void validar(InstituicaoReceptora instituicao) {
        List<String> erros = new ArrayList<>();
        Validacoes.texto(erros, instituicao.getRazaoSocial(), "a razão social", 150);
        Validacoes.cnpj(erros, instituicao.getCnpj());
        Validacoes.texto(erros, instituicao.getEndereco(), "o endereço", 255);
        Validacoes.telefone(erros, instituicao.getTelefone());
        Double capacidade = instituicao.getCapacidadeRefrigeradaKg();
        if (capacidade == null || capacidade < 0) {
            erros.add("A capacidade refrigerada não pode ser negativa.");
        } else if (capacidade >= 100_000_000) {
            erros.add("A capacidade refrigerada informada é grande demais.");
        }
        RegraNegocioException.lancarSeHouver(erros);

        instituicao.setRazaoSocial(instituicao.getRazaoSocial().trim());
        instituicao.setCnpj(Validacoes.formatarCnpj(instituicao.getCnpj()));
        repositorio.buscarPorCnpj(instituicao.getCnpj())
                .filter(existente -> !existente.getId().equals(instituicao.getId()))
                .ifPresent(existente -> {
                    throw new RegraNegocioException("Já existe uma instituição com o CNPJ " + instituicao.getCnpj()
                            + " (#" + existente.getId() + ").");
                });
    }
}
