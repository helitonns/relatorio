package br.leg.alrr.abrindocaminhos.persistence;

import br.leg.alrr.abrindocaminhos.model.PaisAC;
import br.leg.alrr.relatorio.util.DAOException;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author Heliton
 */
@Stateless
public class PaisACDAO {

    @PersistenceContext
    protected EntityManager em;

    public void salvar(PaisAC o) throws DAOException {
        try {
            em.persist(o);
        } catch (Exception e) {
            throw new DAOException("Erro ao salvar país.", e);
        }
    }

    public void atualizar(PaisAC o) throws DAOException {
        try {
            em.merge(o);
        } catch (Exception e) {
            throw new DAOException("Erro ao atualiza país.", e);
        }
    }

    public List listarTodos() throws DAOException {
        try {
            return em.createQuery("select o from Pais o order by o.nome asc").getResultList();
        } catch (Exception e) {
            throw new DAOException("Erro ao listar paíss.", e);
        }
    }
    
    public List listarAtivos() throws DAOException {
        try {
            return em.createQuery("select o from Pais o where o.status = TRUE order by o.nome asc").getResultList();
        } catch (Exception e) {
            throw new DAOException("Erro ao listar paíss.", e);
        }
    }

    public void remover(PaisAC o) throws DAOException {
        try {
            o = em.merge(o);
            em.remove(o);
        } catch (Exception e) {
            throw new DAOException("Erro ao remover país.", e);
        }
    }

    public PaisAC buscarPorID(Long id) throws DAOException {
        try {
            return em.find(PaisAC.class, id);
        } catch (Exception e) {
            throw new DAOException("Erro ao buscar país por ID.", e);
        }
    }

}
