package br.com.ecolog.repository;

import java.util.List;
import java.util.Optional;

/**
 * Contrato de persistência (CRUD) comum a todas as entidades.
 *
 * @param <T> tipo da entidade persistida
 */
public interface Repositorio<T> {

    /** Create: insere a entidade e devolve-a com o id gerado pelo banco. */
    T salvar(T entidade);

    /** Read: busca uma entidade pelo identificador. */
    Optional<T> buscarPorId(Long id);

    /** Read: lista todas as entidades. */
    List<T> listarTodos();

    /** Update: grava as alterações de uma entidade existente. */
    void atualizar(T entidade);

    /** Delete: remove pelo identificador; devolve {@code false} se não existia. */
    boolean excluir(Long id);
}
