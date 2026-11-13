package br.com.ecolog.service;

import br.com.ecolog.model.PontoColeta;
import br.com.ecolog.repository.OrdemTransporteRepository;
import br.com.ecolog.repository.PontoColetaRepository;

import java.util.ArrayList;
import java.util.List;

/** Regras de negócio do cadastro de pontos de coleta (RF1). */
public class PontoColetaService {

    private final PontoColetaRepository repositorio;
    private final OrdemTransporteRepository ordemRepositorio;

    public PontoColetaService(PontoColetaRepository repositorio, OrdemTransporteRepository ordemRepositorio) {
        this.repositorio = repositorio;
        this.ordemRepositorio = ordemRepositorio;
    }

    public PontoColeta cadastrar(PontoColeta ponto) {
        validar(ponto);
        return repositorio.salvar(ponto);
    }

    public List<PontoColeta> listar() {
        return repositorio.listarTodos();
    }

    public PontoColeta buscar(Long id) {
        return repositorio.buscarPorId(id)
                .orElseThrow(() -> new RegraNegocioException("Ponto de coleta #" + id + " não encontrado."));
    }

    public void atualizar(PontoColeta ponto) {
        buscar(ponto.getId());
        validar(ponto);
        repositorio.atualizar(ponto);
    }

    public void excluir(Long id) {
        buscar(id);
        long ordens = ordemRepositorio.contarPorPontoColeta(id);
        if (ordens > 0) {
            throw new RegraNegocioException(
                    "Não é possível excluir: o ponto de coleta é origem de " + ordens + " ordem(ns) de transporte.");
        }
        repositorio.excluir(id);
    }

    private void validar(PontoColeta ponto) {
        List<String> erros = new ArrayList<>();
        Validacoes.texto(erros, ponto.getRazaoSocial(), "a razão social", 150);
        Validacoes.cnpj(erros, ponto.getCnpj());
        Validacoes.texto(erros, ponto.getEndereco(), "o endereço", 255);
        Validacoes.telefone(erros, ponto.getTelefone());
        Validacoes.email(erros, ponto.getEmail());
        RegraNegocioException.lancarSeHouver(erros);

        ponto.setRazaoSocial(ponto.getRazaoSocial().trim());
        ponto.setCnpj(Validacoes.formatarCnpj(ponto.getCnpj()));
        repositorio.buscarPorCnpj(ponto.getCnpj())
                .filter(existente -> !existente.getId().equals(ponto.getId()))
                .ifPresent(existente -> {
                    throw new RegraNegocioException("Já existe um ponto de coleta com o CNPJ " + ponto.getCnpj()
                            + " (#" + existente.getId() + ").");
                });
    }
}
