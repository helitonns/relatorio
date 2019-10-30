package br.leg.alrr.abrindocaminhos.persistence;

import br.leg.alrr.abrindocaminhos.model.UnidadeAC;
import br.leg.alrr.relatorio.util.DAOException;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author heliton
 */
@Stateless
public class UnidadeACDAO{

    @PersistenceContext
    protected EntityManager em;

     public void salvar(UnidadeAC o) throws DAOException{
        try {
            em.persist(o);
        } catch (Exception e) {
            throw new DAOException("Erro ao salvar unidade.", e);
        }
    }

    public void atualizar(UnidadeAC o) throws DAOException{
        try {
            em.merge(o);
        } catch (Exception e) {
            throw new DAOException("Erro ao atualizar unidade.", e);
        }
    }
    
    public UnidadeAC buscarReferencia(UnidadeAC o) throws DAOException{
        try {
            return em.merge(o);
        } catch (Exception e) {
            throw new DAOException("Erro ao buscar unidade.", e);
        }
    }

    public List listarTodos() throws DAOException{
        try {
            return em.createQuery("select o from UnidadeAC o order by o.nome asc").getResultList();
        } catch (Exception e) {
            throw new DAOException("Erro ao listar unidades.", e);
        }
    }

    public void remover(UnidadeAC o) throws DAOException{
        try {
            o = em.merge(o);
            em.remove(o);
        } catch (Exception e) {
            throw new DAOException("Erro ao remover unidade.", e);
        }
    }
    
    public UnidadeAC pesquisarPorID(Long id) throws DAOException{
        try {
            return (UnidadeAC) em.createQuery("select o from UnidadeAC o where o.id =:id")
                    .setParameter("id", id)
                    .getSingleResult();
        } catch (Exception e) {
            throw new DAOException("Erro ao buscar unidade por ID.", e);
        }
    }
}
