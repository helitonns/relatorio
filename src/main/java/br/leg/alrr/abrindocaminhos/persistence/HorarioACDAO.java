package br.leg.alrr.abrindocaminhos.persistence;

import br.leg.alrr.abrindocaminhos.model.HorarioAC;
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
public class HorarioACDAO{

    @PersistenceContext
    protected EntityManager em;
    
    public void salvar(HorarioAC o) throws DAOException{
        try {
            em.persist(o);
        } catch (Exception e) {
             throw new DAOException("Erro ao salvar horário.", e);
        }
    }

    public void atualizar(HorarioAC o) throws DAOException{
        try {
            em.merge(o);
        } catch (Exception e) {
            throw new DAOException("Erro ao atualizar horário.", e);
        }
    }

    public List listarTodos() throws DAOException{
        try {
            return em.createQuery("select o from Horario o order by o.descricao asc").getResultList();
        } catch (Exception e) {
            throw new DAOException("Erro ao listar horários.", e);
        }
    }
    
    public List listarAtivos() throws DAOException{
        try {
            return em.createQuery("select o from Horario o where o.status = true order by o.descricao asc").getResultList();
        } catch (Exception e) {
            throw new DAOException("Erro ao listar horários ativos.", e);
        }
    }

    public void remover(HorarioAC o) throws DAOException{
        try {
            o = em.merge(o);
            em.remove(o);
        } catch (Exception e) {
            throw new DAOException("Erro ao remover horário.", e);
        }
    }

    public HorarioAC buscarPorID(Long id) throws DAOException{
        try {
            return em.find(HorarioAC.class, id);
        } catch (Exception e) {
            throw new DAOException("Erro ao buscar horário por ID.", e);
        }
    }
    
    public boolean horarioNaoCadastrado(String horario) throws DAOException{
        try {
            List l =  em.createQuery("select o from Horario o where o.descricao = :descricao")
                    .setParameter("descricao", horario )
                    .getResultList();
            return l.size() <= 0;
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw new DAOException("Erro ao verificar se a disciplina já foi cadastrada.", e);
        }
    }
}
