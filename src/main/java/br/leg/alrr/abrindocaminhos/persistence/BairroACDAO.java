package br.leg.alrr.abrindocaminhos.persistence;

import br.leg.alrr.abrindocaminhos.model.BairroAC;
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
public class BairroACDAO{

    @PersistenceContext
    protected EntityManager em;
    
    public void salvar(BairroAC o) throws DAOException{
        try {
            em.persist(o);
        } catch (Exception e) {
            throw new DAOException("Erro ao salvar bairro.", e);
        }
    }

    public void atualizar(BairroAC o) throws DAOException{
        try {
            em.merge(o);
        } catch (Exception e) {
            throw new DAOException("Erro ao atualizar bairro.", e);
        }
    }

    public List listarTodos() throws DAOException{
        try {
            return em.createQuery("select o from BairroAC o order by o.nome asc").getResultList();
        } catch (Exception e) {
            throw new DAOException("Erro ao listar bairros.", e);
        }
    }

    public void remover(BairroAC o) throws DAOException{
        try {
            o = em.merge(o);
            em.remove(o);
        } catch (Exception e) {
            throw new DAOException("Erro ao remover bairro.", e);
        }
    }
    
    public BairroAC buscarPorID(Long id) throws DAOException{
        try {
            return em.find(BairroAC.class, id);
        } catch (Exception e) {
            throw new DAOException("Erro ao buscar bairro por ID.", e);
        }
    }
    
    public List<BairroAC> listarBairroPorMunicipio(MunicipioAC m) throws DAOException{
        try {
            return em.createQuery("select o from BairroAC o where o.municipio =:m order by o.nome asc")
                    .setParameter("m", m)
                    .getResultList();
        } catch (Exception e) {
            throw new DAOException("Erro ao buscar bairro por município.", e);
        }
    }

}
