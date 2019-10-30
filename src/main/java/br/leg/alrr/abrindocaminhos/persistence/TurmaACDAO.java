package br.leg.alrr.abrindocaminhos.persistence;

import br.leg.alrr.abrindocaminhos.model.AlunoAC;
import br.leg.alrr.abrindocaminhos.model.TurmaAC;
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
public class TurmaACDAO {

    @PersistenceContext
    protected EntityManager em;

    public void salvar(TurmaAC o) throws DAOException {
        try {
            em.persist(o);
        } catch (Exception e) {
            throw new DAOException("Erro ao salvar turma: " + e.getMessage(), e);
        }
    }

    public void atualizar(TurmaAC o) throws DAOException {
        try {
            em.merge(o);
        } catch (Exception e) {
            throw new DAOException("Erro ao atualizar turma.", e);
        }
    }

    public TurmaAC buscarReferencia(TurmaAC o) throws DAOException {
        try {
            return em.getReference(TurmaAC.class, o.getId());
        } catch (Exception e) {
            throw new DAOException("Erro ao buscar referência do turma.", e);
        }
    }

    public List listarTodos() throws DAOException {
        try {
            return em.createQuery("select o from TurmaAC o order by o.nome asc").getResultList();
        } catch (Exception e) {
            throw new DAOException("Erro ao listar turmas.", e);
        }
    }

    public List listarTurmasAtivas() throws DAOException {
        try {
            return em.createQuery("select o from TurmaAC o where o.status = true order by o.nome asc").getResultList();
        } catch (Exception e) {
            throw new DAOException("Erro ao listar turmas ativas.", e);
        }
    }

    public List listarTurmasIniciadas() throws DAOException {
        try {
            return em.createQuery("select o from TurmaAC o where o.iniciada = true order by o.nome asc").getResultList();
        } catch (Exception e) {
            throw new DAOException("Erro ao listar turmas ativas.", e);
        }
    }

    public List listarTurmasIniciadasPorUnidade(UnidadeAC u) throws DAOException {
        try {
            return em.createQuery("select o from TurmaAC o where o.status = TRUE AND o.iniciada = true and o.unidade.id = :idUnidade order by o.atividade.descricao,o.nome")
                    .setParameter("idUnidade", u.getId())
                    .getResultList();
        } catch (Exception e) {
            throw new DAOException("Erro ao listar turmas por unidade.", e);
        }
    }

    public List listarTurmasPorUnidadeESeIniciadas(UnidadeAC u, String statusTurma) throws DAOException {
        try {
            StringBuilder sb = new StringBuilder();

            if (statusTurma.equals("ambas")) {
                sb.append("select o from TurmaAC o where o.status = TRUE ");

                //VERIFICA SE A UNIDADE FOI PASSADA
                if (u.getId() <= 0) {
                    sb.append("order by o.atividade.descricao, o.nome");
                    return em.createQuery(sb.toString())
                            .getResultList();
                } else {
                    sb.append("AND o.unidade.id = :idUnidade order by o.atividade.descricao, o.nome");
                    return em.createQuery(sb.toString())
                            .setParameter("idUnidade", u.getId())
                            .getResultList();
                }
            } else {
                boolean b = (statusTurma.equals("ativas"));

                sb.append("select o from TurmaAC o where o.status = TRUE ");

                //VERIFICA SE A UNIDADE FOI PASSADA
                if (u.getId() <= 0) {
                    sb.append("AND o.iniciada =:statusTurma order by o.atividade.descricao, o.nome");
                    return em.createQuery(sb.toString())
                            .setParameter("statusTurma", b)
                            .getResultList();
                } else {
                    sb.append("AND o.unidade.id = :idUnidade AND o.iniciada =:statusTurma order by o.atividade.descricao, o.nome");
                    return em.createQuery(sb.toString())
                            .setParameter("idUnidade", u.getId())
                            .setParameter("statusTurma", b)
                            .getResultList();
                }
            }
        } catch (Exception e) {
            throw new DAOException("Erro ao listar turmas por unidade.", e);
        }
    }

    public List listarTurmasPorAluno(AlunoAC a) throws DAOException {
        try {
            return em.createQuery("select o from MatriculaAC o where o.aluno.id = :idAluno order by o.turma.nome asc")
                    .setParameter("idAluno", a.getId())
                    .getResultList();
        } catch (Exception e) {
            throw new DAOException("Erro ao listar turmas por aluno.", e);
        }
    }

    public void remover(TurmaAC o) throws DAOException {
        try {
            o = em.merge(o);
            em.remove(o);
        } catch (Exception e) {
            throw new DAOException("Erro ao remover turma.", e);
        }
    }

    public TurmaAC buscarPorID(Long id) throws DAOException {
        try {
            return em.find(TurmaAC.class, id);
        } catch (Exception e) {
            throw new DAOException("Erro ao buscar turma por ID.", e);
        }
    }

    public boolean verificarSeAlunoJaEstaMatriculadoNaTurma(AlunoAC a, TurmaAC t) throws DAOException {
        try {
            List l = em.createQuery("select o from MatriculaAC o where o.aluno.id = :idAluno and o.turma.id = :idTurma")
                    .setParameter("idAluno", a.getId())
                    .setParameter("idTurma", t.getId())
                    .getResultList();
            return l.size() > 0;
        } catch (Exception e) {
            throw new DAOException("Erro ao listar turmas por aluno.", e);
        }
    }

}
