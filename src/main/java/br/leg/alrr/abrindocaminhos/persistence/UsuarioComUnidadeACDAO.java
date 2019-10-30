package br.leg.alrr.abrindocaminhos.persistence;

import br.leg.alrr.abrindocaminhos.model.UsuarioComUnidadeAC;
import br.leg.alrr.cursos.model.Unidade;
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
public class UsuarioComUnidadeACDAO{

    @PersistenceContext
    protected EntityManager em;

    public void salvar(UsuarioComUnidadeAC o) throws DAOException{
        try {
            em.persist(o);
        } catch (Exception e) {
            throw new DAOException("Erro ao salvar usuário.", e);
        }
    }

    public void atualizar(UsuarioComUnidadeAC o) throws DAOException{
        try {
            em.merge(o);
        } catch (Exception e) {
            throw new DAOException("Erro ao atualizar usuário.", e);
        }
    }

    public List listarTodos() throws DAOException{
        try {
            return em.createQuery("select o from UsuarioComUnidadeAC o order by o.login asc").getResultList();
        } catch (Exception e) {
            throw new DAOException("Erro ao listar usuários.", e);
        }
    }
    
    public List listarTodosPorUnidade(Unidade u) throws DAOException{
        try {
            return em.createQuery("select o from UsuarioComUnidadeAC o where o.unidade.id = :idUnidade order by o.login asc")
                    .setParameter("idUnidade", u.getId())
                    .getResultList();
        } catch (Exception e) {
            throw new DAOException("Erro ao listar usuários por unidade.", e);
        }
    }
    
    public List listarTodosPorUnidadeSemOSuperAdmin(Unidade u) throws DAOException{
        try {
            return em.createQuery("select o from UsuarioComUnidadeAC o where o.unidade.id = :idUnidade and o.tipo != 'SUPER_ADMIN' order by o.login asc")
                    .setParameter("idUnidade", u.getId())
                    .getResultList();
        } catch (Exception e) {
            throw new DAOException("Erro ao listar usuários por unidade.", e);
        }
    }

    public void remover(UsuarioComUnidadeAC o) throws DAOException{
        try {
            o = em.merge(o);
            em.remove(o);
        } catch (Exception e) {
            throw new DAOException("Erro ao remover usuário.", e);
        }
    }

    public UsuarioComUnidadeAC pesquisarPorLogin(String login) throws DAOException{
        try {
            return (UsuarioComUnidadeAC) em.createQuery("select u from UsuarioComUnidadeAC u where u.login =:login")
                    .setParameter("login", login)
                    .getSingleResult();
        } catch (Exception e) {
            throw new DAOException("Erro ao pesquisar usuário por login.", e);
        }
    }

    public UsuarioComUnidadeAC pesquisarPorLoginESenha(String login, String senha) throws DAOException{
        try {
            return (UsuarioComUnidadeAC) em.createQuery("select u from UsuarioComUnidadeAC u where u.login =:login and u.senha =:senha")
                    .setParameter("login", login)
                    .setParameter("senha", senha)
                    .getSingleResult();
        } catch (Exception e) {
            throw new DAOException("Erro ao pesquisar usuário por login e senha.", e);
        }
    }
    
    public boolean haUsuarioComUnidadeComEsteLogin(String login) throws DAOException{
        try {
            return em.createQuery("select u from UsuarioComUnidadeAC u where u.login =:login")
                    .setParameter("login", login)
                    .getResultList().size() >= 1;
        } catch (Exception e) {
            throw new DAOException("Erro ao pesquisar usuário por login.", e);
        }
    }
    
    public int salvarUnidadeParaUsuario(Long idUsuario, Long idUnidade) throws DAOException{
        try {
            return  em.createNativeQuery("INSERT INTO abrindo_caminhos.usuariocomunidade(id, unidade_id) VALUES (:idUsuario, :idUnidade)")
                    .setParameter("idUsuario", idUsuario)
                    .setParameter("idUnidade", idUnidade)
                    .executeUpdate();
        } catch (Exception e) {
            throw new DAOException("Erro ao salvar unidade para o usuário.", e);
        }
    }
    
    
}
