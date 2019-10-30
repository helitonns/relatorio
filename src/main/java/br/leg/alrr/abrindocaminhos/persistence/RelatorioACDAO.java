package br.leg.alrr.abrindocaminhos.persistence;

import br.leg.alrr.abrindocaminhos.model.AlunoAC;
import br.leg.alrr.abrindocaminhos.model.MatriculaAC;
import br.leg.alrr.abrindocaminhos.model.TurmaAC;
import br.leg.alrr.relatorio.business.BlocoParametro;
import br.leg.alrr.relatorio.util.DAOException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author heliton
 */
@Stateless
public class RelatorioACDAO {

    @PersistenceContext
    protected EntityManager em;

    public List<AlunoAC> gerarRelatorioEspecificoPorAluno(String consulta, List<BlocoParametro> parametros) throws DAOException {
        try {

            Query q = em.createQuery(consulta);
            Calendar d = new GregorianCalendar();

            for (BlocoParametro b : parametros) {

                if (b.getParametro().equals("dataNascimento1")) {
                    d.setTime((Date) b.getValor());
                    q.setParameter("dia1", d.get(Calendar.DAY_OF_MONTH));
                    q.setParameter("mes1", d.get(Calendar.MONTH) + 1);
                } else if (b.getParametro().equals("dataNascimento2")) {
                    d.setTime((Date) b.getValor());
                    q.setParameter("dia2", d.get(Calendar.DAY_OF_MONTH));
                    q.setParameter("mes2", d.get(Calendar.MONTH) + 1);
                } else if (b.getParametro().equals("idUnidade") || b.getParametro().equals("idAtividade") || b.getParametro().equals("idTurma")
                        || b.getParametro().equals("idMunicipio") || b.getParametro().equals("idBairro")) {
                    q.setParameter(b.getParametro(), (Long) b.getValor());
                }
            }
            return q.getResultList();
        } catch (Exception e) {
            throw new DAOException("Erro ao gerar relatório.", e);
        }
    }

    public List<MatriculaAC> gerarRelatorioEspecificoPorMatricula(String consulta, List<BlocoParametro> parametros) throws DAOException {
        try {

            Query q = em.createQuery(consulta);
            Calendar d = new GregorianCalendar();

            for (BlocoParametro b : parametros) {

                if (b.getParametro().equals("dataNascimento1")) {
                    d.setTime((Date) b.getValor());
                    q.setParameter("dia1", d.get(Calendar.DAY_OF_MONTH));
                    q.setParameter("mes1", d.get(Calendar.MONTH) + 1);
                } else if (b.getParametro().equals("dataNascimento2")) {
                    d.setTime((Date) b.getValor());
                    q.setParameter("dia2", d.get(Calendar.DAY_OF_MONTH));
                    q.setParameter("mes2", d.get(Calendar.MONTH) + 1);
                } else if (b.getParametro().equals("idUnidade") || b.getParametro().equals("idAtividade") || b.getParametro().equals("idTurma")
                        || b.getParametro().equals("idMunicipio") || b.getParametro().equals("idBairro")) {
                    q.setParameter(b.getParametro(), (Long) b.getValor());
                }
            }
            return q.getResultList();
        } catch (Exception e) {
            throw new DAOException("Erro ao gerar relatório.", e);
        }
    }

    public List<TurmaAC> gerarRelatorioEspecificoPorTurma(String consulta, List<BlocoParametro> parametros) throws DAOException {
        try {

            Query q = em.createQuery(consulta);
            Calendar d = new GregorianCalendar();

            for (BlocoParametro b : parametros) {
                if (b.getParametro().equals("idUnidade") || b.getParametro().equals("idAtividade") || b.getParametro().equals("idTurma") || b.getParametro().equals("idMunicipio") || b.getParametro().equals("idBairro")) {
                    q.setParameter(b.getParametro(), (Long) b.getValor());
                }
            }
            return q.getResultList();
        } catch (Exception e) {
            throw new DAOException("Erro ao gerar relatório.", e);
        }
    }

    public Long gerarRelatorioPorQuantidade(String consulta, List<BlocoParametro> parametros) throws DAOException {

        Query q = em.createQuery(consulta);

        for (BlocoParametro b : parametros) {
            if (b.getParametro().equals("idUnidade") || b.getParametro().equals("idAtividade") || b.getParametro().equals("idTurma")
                    || b.getParametro().equals("idMunicipio") || b.getParametro().equals("idBairro")) {
                q.setParameter(b.getParametro(), (Long) b.getValor());
            }else if (b.getParametro().equals("status") ){
                q.setParameter(b.getParametro(), (boolean) b.getValor());
            }
        }
        return (Long) q.getSingleResult();
    }
}
