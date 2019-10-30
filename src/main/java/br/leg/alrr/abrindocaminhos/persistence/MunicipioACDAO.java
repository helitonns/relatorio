package br.leg.alrr.abrindocaminhos.persistence;

import br.leg.alrr.abrindocaminhos.model.MunicipioAC;
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
public class MunicipioACDAO{

    @PersistenceContext
    protected EntityManager em;
    
    public void salvar(MunicipioAC o) throws DAOException{
        try {
            em.persist(o);
        } catch (Exception e) {
            throw new DAOException("Erro ao salvar município.", e);
        }
    }

    public void atualizar(MunicipioAC o) throws DAOException{
        try {
            em.merge(o);
        } catch (Exception e) {
            throw new DAOException("Erro ao atualiza município.", e);
        }
    }

    public List listarTodos() throws DAOException{
        try {
            return em.createQuery("select o from MunicipioAC o order by o.nome asc").getResultList();
        } catch (Exception e) {
            throw new DAOException("Erro ao listar municípios.", e);
        }
    }

    public void remover(MunicipioAC o) throws DAOException{
        try {
            o = em.merge(o);
            em.remove(o);
        } catch (Exception e) {
            throw new DAOException("Erro ao remover município.", e);
        }
    }

    public MunicipioAC buscarPorID(Long id) throws DAOException{
        try {
            return em.find(MunicipioAC.class, id);
        } catch (Exception e) {
            throw new DAOException("Erro ao buscar município por ID.", e);
        }
    }
}
