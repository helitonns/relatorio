package br.leg.alrr.abrindocaminhos.controller;

import br.leg.alrr.abrindocaminhos.model.AlunoAC;
import br.leg.alrr.abrindocaminhos.model.Atividade;
import br.leg.alrr.abrindocaminhos.model.BairroAC;
import br.leg.alrr.abrindocaminhos.model.HorarioAC;
import br.leg.alrr.abrindocaminhos.model.MatriculaAC;
import br.leg.alrr.abrindocaminhos.model.MunicipioAC;
import br.leg.alrr.abrindocaminhos.model.TurmaAC;
import br.leg.alrr.abrindocaminhos.model.UnidadeAC;
import br.leg.alrr.abrindocaminhos.persistence.AtividadeDAO;
import br.leg.alrr.abrindocaminhos.persistence.BairroACDAO;
import br.leg.alrr.abrindocaminhos.persistence.MunicipioACDAO;
import br.leg.alrr.abrindocaminhos.persistence.RelatorioACDAO;
import br.leg.alrr.abrindocaminhos.persistence.TurmaACDAO;
import br.leg.alrr.abrindocaminhos.persistence.UnidadeACDAO;
import br.leg.alrr.abrindocaminhos.util.Relatorio;
import br.leg.alrr.relatorio.business.BlocoParametro;
import br.leg.alrr.relatorio.business.RetornoConsultaQuantidade;
import br.leg.alrr.relatorio.util.DAOException;
import br.leg.alrr.relatorio.util.FacesUtils;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import javax.faces.event.ValueChangeEvent;

/**
 *
 * @author heliton
 */
@ViewScoped
@Named
public class RelatorioEspecificoMB implements Serializable {

    private static final long serialVersionUID = 1L;

    @EJB
    private RelatorioACDAO relatorioDAO;

    @EJB
    private UnidadeACDAO unidadeDAO;

    @EJB
    private TurmaACDAO turmaDAO;

    @EJB
    private AtividadeDAO atividadeDAO;

    @EJB
    private MunicipioACDAO municipioDAO;

    @EJB
    private BairroACDAO bairroDAO;

    private ArrayList<AlunoAC> alunos;
    private ArrayList<MatriculaAC> matriculas;
    private ArrayList<UnidadeAC> unidades;
    private ArrayList<TurmaAC> turmas;
    private ArrayList<Atividade> atividades;
    private ArrayList<MunicipioAC> municipios;
    private ArrayList<BairroAC> bairros;
    private ArrayList<BlocoParametro> blocosParametros;
    private ArrayList<RetornoConsultaQuantidade> retornoConsultaQuantidades;

    private Date data1;
    private Date data2;
    private String tipoRelatorio;
    private String statusTurma;
    private long idUnidade;
    private long idTurma;
    private long idAtividade;
    private long idMunicipio;
    private long idBairro;
    private long totalDeAlunos;

    private boolean exibirTabelaAluno;
    private boolean exibirTabelaMatricula;
//==========================================================================

    @PostConstruct
    public void init() {
        limparForm();

        listarUnidades();
        listarMunicipio();
    }

    private void listarMunicipio() {
        try {
            municipios = (ArrayList<MunicipioAC>) municipioDAO.listarTodos();
        } catch (DAOException e) {
            FacesUtils.addErrorMessage(e.getMessage());
        }
    }

    private void listarBairroPorMunicipio() {
        try {
            bairros = (ArrayList<BairroAC>) bairroDAO.listarBairroPorMunicipio(new MunicipioAC(idMunicipio));
        } catch (DAOException e) {
            FacesUtils.addErrorMessage(e.getMessage());
        }
    }

    public void recarregarBairros(ValueChangeEvent event) {
        idMunicipio = Long.parseLong(event.getNewValue().toString());
        listarBairroPorMunicipio();
    }

    private void listarAtividades(UnidadeAC u) {
        try {
            atividades = (ArrayList<Atividade>) atividadeDAO.listarAtivasPorUnidade(u);
        } catch (DAOException e) {
            FacesUtils.addErrorMessage(e.getMessage());
        }
    }

    public void recarregarAtividades(ValueChangeEvent event) {
        try {
            idUnidade = Long.parseLong(event.getNewValue().toString());
            listarAtividades(new UnidadeAC(idUnidade));
        } catch (NumberFormatException e) {
            FacesUtils.addInfoMessage(e.getMessage());
        }
    }

    private void listarUnidades() {
        try {
            unidades = (ArrayList<UnidadeAC>) unidadeDAO.listarTodos();
        } catch (DAOException e) {
            FacesUtils.addErrorMessage(e.getMessage());
        }
    }

    public void pegarIdDeUnidade(ValueChangeEvent event) {
        idUnidade = Long.parseLong(event.getNewValue().toString());
    }

    public void recarregarTurmas(ValueChangeEvent event) {
        try {
            statusTurma = event.getNewValue().toString();
            turmas = (ArrayList<TurmaAC>) turmaDAO.listarTurmasPorUnidadeESeIniciadas(new UnidadeAC(idUnidade), statusTurma);
        } catch (NumberFormatException | DAOException e) {
            FacesUtils.addErrorMessage(e.getMessage());
        }
    }

    public void pesquisarPorAniversariantes() {
        try {
            exibirTabelaAluno = false;
            exibirTabelaMatricula = false;
            blocosParametros = new ArrayList<>();
            StringBuilder query = new StringBuilder();

            if (tipoRelatorio.equals("geral")) {
                query.append("SELECT a FROM AlunoAC a WHERE ");

                if (idUnidade > 0) {
                    query.append("a.unidade.id=:idUnidade AND ");
                    blocosParametros.add(new BlocoParametro("idUnidade", idUnidade));
                }

                query.append("DAY(a.dataNascimento) >= :dia1 AND MONTH (a.dataNascimento) >= :mes1 AND DAY(a.dataNascimento) <= :dia2 AND MONTH (a.dataNascimento) <= :mes2 ORDER BY a.dataNascimento ASC, a.nome ASC");

                blocosParametros.add(new BlocoParametro("dataNascimento1", data1));
                blocosParametros.add(new BlocoParametro("dataNascimento2", data2));
                alunos = (ArrayList<AlunoAC>) relatorioDAO.gerarRelatorioEspecificoPorAluno(query.toString(), blocosParametros);
                exibirTabelaAluno = true;

            } else if (tipoRelatorio.equals("porMatriculaAtiva")) {
                query.append("SELECT m FROM MatriculaAC m WHERE ");

                if (idUnidade > 0) {
                    query.append("m.unidade.id=:idUnidade AND ");
                    blocosParametros.add(new BlocoParametro("idUnidade", idUnidade));
                }

                query.append("m.status = TRUE AND DAY(m.aluno.dataNascimento) >= :dia1 AND MONTH (m.aluno.dataNascimento) >= :mes1 AND DAY(m.aluno.dataNascimento) <= :dia2 AND MONTH (m.aluno.dataNascimento) <= :mes2 ORDER BY m.aluno.dataNascimento ASC, m.aluno.nome ASC");

                blocosParametros.add(new BlocoParametro("dataNascimento1", data1));
                blocosParametros.add(new BlocoParametro("dataNascimento2", data2));
                matriculas = (ArrayList<MatriculaAC>) relatorioDAO.gerarRelatorioEspecificoPorMatricula(query.toString(), blocosParametros);
                exibirTabelaMatricula = true;
            }
        } catch (DAOException e) {
            FacesUtils.addErrorMessage(e.getMessage());
        }

    }

    public void pesquisarPaisAniversariantes() {
        try {
            exibirTabelaAluno = false;
            exibirTabelaMatricula = false;
            blocosParametros = new ArrayList<>();
            StringBuilder query = new StringBuilder();

            if (tipoRelatorio.equals("geral")) {
                query.append("SELECT a FROM AlunoAC a WHERE ");

                if (idUnidade > 0) {
                    query.append("a.unidade.id=:idUnidade AND ");
                    blocosParametros.add(new BlocoParametro("idUnidade", idUnidade));
                }

                query.append("DAY(a.genitores.dataNascimentoMae) >= :dia1 AND MONTH (a.genitores.dataNascimentoMae) >= :mes1 AND DAY(a.genitores.dataNascimentoMae) <= :dia2 AND MONTH (a.genitores.dataNascimentoMae) <= :mes2 ORDER BY a.genitores.dataNascimentoMae ASC, a.genitores.nomeMae ASC");

                blocosParametros.add(new BlocoParametro("dataNascimento1", data1));
                blocosParametros.add(new BlocoParametro("dataNascimento2", data2));
                alunos = (ArrayList<AlunoAC>) relatorioDAO.gerarRelatorioEspecificoPorAluno(query.toString(), blocosParametros);
                exibirTabelaAluno = true;

            } else if (tipoRelatorio.equals("porMatriculaAtiva")) {
                query.append("SELECT m FROM MatriculaAC m WHERE ");

                if (idUnidade > 0) {
                    query.append("m.unidade.id=:idUnidade AND ");
                    blocosParametros.add(new BlocoParametro("idUnidade", idUnidade));
                }

                query.append("m.status = TRUE AND DAY(m.aluno.genitores.dataNascimentoMae) >= :dia1 AND MONTH (m.aluno.genitores.dataNascimentoMae) >= :mes1 AND DAY(m.aluno.genitores.dataNascimentoMae) <= :dia2 AND MONTH (m.aluno.genitores.dataNascimentoMae) <= :mes2 ORDER BY m.aluno.genitores.dataNascimentoMae ASC, m.aluno.genitores.nomeMae ASC");

                blocosParametros.add(new BlocoParametro("dataNascimento1", data1));
                blocosParametros.add(new BlocoParametro("dataNascimento2", data2));
                matriculas = (ArrayList<MatriculaAC>) relatorioDAO.gerarRelatorioEspecificoPorMatricula(query.toString(), blocosParametros);
                exibirTabelaMatricula = true;
            }
        } catch (DAOException e) {
            FacesUtils.addErrorMessage(e.getMessage());
            System.out.println(e.getCause());
        }

    }

    /* Relatório dos Aniversariantes do Mês */
    public void imprimirRelAniversariantesMes() {
        Relatorio<AlunoAC> relatorio = new Relatorio<>();
        Relatorio<MatriculaAC> relatorioMatriculados = new Relatorio<>();
        try {
            if ((matriculas.size() > 0) || (alunos.size() > 0)) {
                if (matriculas.size() == 0) {
                    relatorio.getRelAniversarianteMesGeral(alunos);
                } else {
                    relatorioMatriculados.getRelAniversarianteMesMatriculados(matriculas);
                }
            } else {
                FacesUtils.addInfoMessage("Não há registros!");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            FacesUtils.addErrorMessage("Não há registros!");
        }
    }

    public void pesquisarPorAtividades() {
        try {
            blocosParametros = new ArrayList<>();
            StringBuilder query = new StringBuilder();

            //PESQUISA ATIVIDADE EM UMA UNIDADE ESPECÍFICA
            if (idAtividade > 0) {
                query.append("SELECT m FROM MatriculaAC m WHERE m.turma.atividade.id =:idAtividade ");
                blocosParametros.add(new BlocoParametro("idAtividade", idAtividade));

                //VERIFICA SE PESQUISARÁ POR TURMAS ATIVAS OU FINALIZADAS, CASO NÃO NAS CONDIÇÕES PESQUISARÁ POR AMBAS
                if (statusTurma.equals("ativas")) {
                    query.append("AND m.turma.iniciada=TRUE ");
                } else if (statusTurma.equals("finalizadas")) {
                    query.append("AND m.turma.iniciada=FALSE ");
                }

                //SE ENTRAR NA CONDIÇÃO PESQUISARÁ POR UNIDADE ESPECÍFICA, CASO CONTRÁRIO PESQUISARÁ EM TODAS
                if (idUnidade > 0) {
                    query.append("AND m.unidade.id=:idUnidade ");
                    blocosParametros.add(new BlocoParametro("idUnidade", idUnidade));
                }

                query.append("ORDER BY m.unidade.nome, m.turma.atividade.descricao, m.turma.nome, m.aluno.nome");
                matriculas = (ArrayList<MatriculaAC>) relatorioDAO.gerarRelatorioEspecificoPorMatricula(query.toString(), blocosParametros);
            } //PESQUISA TODAS AS ATIVIDADES
            else {
                query.append("SELECT m FROM MatriculaAC m  ");

                //VERIFICA SE PESQUISARÁ POR TURMAS ATIVAS OU FINALIZADAS, CASO NÃO NAS CONDIÇÕES PESQUISARÁ POR AMBAS
                if (statusTurma.equals("ativas")) {
                    query.append("WHERE m.turma.iniciada=TRUE ");
                } else if (statusTurma.equals("finalizadas")) {
                    query.append("WHERE m.turma.iniciada=FALSE ");
                }

                //SE ENTRAR NA CONDIÇÃO PESQUISARÁ POR UNIDADE ESPECÍFICA, CASO CONTRÁRIO PESQUISARÁ EM TODAS
                if (idUnidade > 0) {
                    if (query.toString().contains("WHERE")) {
                        query.append("AND m.unidade.id=:idUnidade ");
                    } else {
                        query.append("WHERE m.unidade.id=:idUnidade ");
                    }
                    blocosParametros.add(new BlocoParametro("idUnidade", idUnidade));
                }

                query.append("ORDER BY m.unidade.nome, m.turma.atividade.descricao, m.turma.nome, m.aluno.nome");

                matriculas = (ArrayList<MatriculaAC>) relatorioDAO.gerarRelatorioEspecificoPorMatricula(query.toString(), blocosParametros);
            }
            exibirTabelaMatricula = true;
        } catch (DAOException e) {
            FacesUtils.addErrorMessage(e.getMessage());
        }
    }

    /* Relatório das Atividades */
    public void imprimirRelAtividadesAlunos() {
        Relatorio<MatriculaAC> relatorioAtividades = new Relatorio<>();
        try {
            if ((matriculas.size() > 0)) {
                relatorioAtividades.getRelAtividadesAlunos(matriculas);
            } else {
                FacesUtils.addInfoMessage("Não há registros!");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            FacesUtils.addErrorMessage("Não há registros!");
        }
    }

    public void pesquisarPorAtividadePorQuantidade() {
        try {
            atividades = new ArrayList<>();
            retornoConsultaQuantidades = new ArrayList<>();
            blocosParametros = new ArrayList<>();
            listarAtividades(new UnidadeAC(idUnidade));

            StringBuilder query = new StringBuilder();

            for (Atividade a : atividades) {
                query.append("SELECT COUNT(DISTINCT o.id) FROM MatriculaAC o WHERE o.turma.atividade.id=:idAtividade ");
                blocosParametros.add(new BlocoParametro("idAtividade", a.getId()));

                //VERIFICA SE PESQUISARÁ POR TURMAS ATIVAS OU FINALIZADAS, CASO NÃO NAS CONDIÇÕES PESQUISARÁ POR AMBAS
                if (statusTurma.equals("ativas")) {
                    query.append("AND o.turma.iniciada=TRUE ");
                } else if (statusTurma.equals("finalizadas")) {
                    query.append("AND o.turma.iniciada=FALSE ");
                }

                //SE ENTRAR NA CONDIÇÃO PESQUISARÁ POR UNIDADE ESPECÍFICA, CASO CONTRÁRIO PESQUISARÁ EM TODAS
                if (idUnidade > 0) {
                    query.append("AND o.unidade.id=:idUnidade ");
                    blocosParametros.add(new BlocoParametro("idUnidade", idUnidade));
                }

                RetornoConsultaQuantidade rcq = new RetornoConsultaQuantidade();
                rcq.setTexto(a.getDescricao());
                rcq.setQuantidade(relatorioDAO.gerarRelatorioPorQuantidade(query.toString(), blocosParametros));
                totalDeAlunos += rcq.getQuantidade();

                retornoConsultaQuantidades.add(rcq);
                query = new StringBuilder();
            }
            retornoConsultaQuantidades.add(new RetornoConsultaQuantidade("TOTAL DE ALUNOS", totalDeAlunos));
            exibirTabelaMatricula = true;
        } catch (DAOException e) {
            System.out.println(e.getCause());
            FacesUtils.addErrorMessage(e.getMessage());
        }
    }

    /* Relatório Qauntidade de alunos por Atividade */
    public void imprimirRelQuantidadeAlunosAtividade() {
        Relatorio<RetornoConsultaQuantidade> relatorioQtdAlunosAtividade = new Relatorio<>();
        try {
            if ((blocosParametros.size() > 0)) {
                relatorioQtdAlunosAtividade.getRelQuantidadeAlunoAtividade(retornoConsultaQuantidades);
            } else {
                FacesUtils.addInfoMessage("Não há registros!");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            FacesUtils.addErrorMessage("Não há registros!");
        }
    }

    public void pesquisarPorTurma() {
        try {
            blocosParametros = new ArrayList<>();
            StringBuilder query = new StringBuilder();

            //PESQUISA TURMAS EM UMA UNIDADE ESPECÍFICA
            if (idUnidade > 0) {
                query.append("SELECT m FROM MatriculaAC m WHERE m.unidade.id=:idUnidade ");
                blocosParametros.add(new BlocoParametro("idUnidade", idUnidade));

                //SE ENTRAR NA CONSIÇÃO, PESQUISARÁ POR UMA TURMA ESPECÍFICA
                if (idTurma > 0) {
                    query.append("AND m.turma.id=:idTurma ");
                    blocosParametros.add(new BlocoParametro("idTurma", idTurma));
                }

                //VERIFICA SE PESQUISARÁ POR TURMAS ATIVAS OU FINALIZADAS, CASO NÃO NAS CONDIÇÕES PESQUISARÁ POR AMBAS
                if (statusTurma.equals("ativas")) {
                    query.append("AND m.turma.iniciada=TRUE ");
                } else if (statusTurma.equals("finalizadas")) {
                    query.append("AND m.turma.iniciada=FALSE ");
                }

                query.append("ORDER BY m.turma.nome, m.aluno.nome");
                matriculas = (ArrayList<MatriculaAC>) relatorioDAO.gerarRelatorioEspecificoPorMatricula(query.toString(), blocosParametros);
            } //PESQUISA TURMAS EM TODAS AS UNIDADES
            else {
                query.append("SELECT m FROM MatriculaAC m ");
                query.append("ORDER BY m.turma.nome, m.aluno.nome");
                matriculas = (ArrayList<MatriculaAC>) relatorioDAO.gerarRelatorioEspecificoPorMatricula(query.toString(), blocosParametros);
            }
            exibirTabelaMatricula = true;
        } catch (DAOException e) {
            FacesUtils.addErrorMessage(e.getMessage());
        }

    }

    /* Relatório das TurmaAC */
    public void imprimirRelTurma() {
        Relatorio<MatriculaAC> relatorioTurma = new Relatorio<>();
        try {
            if ((matriculas.size() > 0)) {
                relatorioTurma.getRelTurma(matriculas);
            } else {
                FacesUtils.addInfoMessage("Não há registros!");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            FacesUtils.addErrorMessage("Não há registros!");
        }
    }

    /* Relatório de Alunos e Telefone por TurmaAC */
    public void imprimirRelTurmaTelefone() {
        Relatorio<MatriculaAC> relatorioTurmaTelefone = new Relatorio<>();
        try {
            if ((matriculas.size() > 0)) {
                relatorioTurmaTelefone.getRelTurmaTelefone(matriculas);
            } else {
                FacesUtils.addInfoMessage("Não há registros!");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            FacesUtils.addErrorMessage("Não há registros!");
        }
    }

    /* Relatório de Alunos e Telefone por TurmaAC */
    public void imprimirRelTurmaMae() {
        Relatorio<MatriculaAC> relatorioTurmaMae = new Relatorio<>();
        try {
            if ((matriculas.size() > 0)) {
                relatorioTurmaMae.getRelTurmaMae(matriculas);
            } else {
                FacesUtils.addInfoMessage("Não há registros!");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            FacesUtils.addErrorMessage("Não há registros2!");
        }
    }

    public void pesquisarPorBairros() {
        try {
            exibirTabelaAluno = false;
            exibirTabelaMatricula = false;
            blocosParametros = new ArrayList<>();
            StringBuilder query = new StringBuilder();

            if (tipoRelatorio.equals("geral")) {
                query.append("SELECT a FROM AlunoAC a WHERE a.endereco.bairro.municipio.id =:idMunicipio ");
                blocosParametros.add(new BlocoParametro("idMunicipio", idMunicipio));

                if (idBairro > 0) {
                    query.append("AND a.endereco.bairro.id=:idBairro ");
                    blocosParametros.add(new BlocoParametro("idBairro", idBairro));
                }
                query.append("ORDER BY a.endereco.bairro.nome, a.nome, a.genitores.nomeMae");

                alunos = (ArrayList<AlunoAC>) relatorioDAO.gerarRelatorioEspecificoPorAluno(query.toString(), blocosParametros);
                exibirTabelaAluno = true;

            } else {
                //PESQUISA AS MATRÍCULAS ATIVAS, MAS NÃO CONSEGUE EXCLUIR ALUNOS QUE POSSUA MAIS DE UMA MATRÍCULA
                /*query.append("SELECT m FROM MatriculaAC m WHERE m.status = true AND m.aluno.endereco.bairro.municipio.id =:idMunicipio ");
                blocosParametros.add(new BlocoParametro("idMunicipio", idMunicipio));

                if (idBairro > 0) {
                    query.append("AND m.aluno.endereco.bairro.id=:idBairro ");
                    blocosParametros.add(new BlocoParametro("idBairro", idBairro));
                }
                query.append("ORDER BY m.aluno.endereco.bairro.nome, m.aluno.nome, m.aluno.genitores.nomeMae");

                matriculas = (ArrayList<Matricula>) relatorioDAO.gerarRelatorioEspecificoPorMatricula(query.toString(), blocosParametros);
                exibirTabelaMatricula = true;*/

                //PESQUISANDO PELA MATRÍCULA, MAS RETORNANDO APENAS ALUNOS DISTINTOS, OU SEJA, QUANDO O ALUNO TIVER MAIS DE UMA MATRÍCULA ELE SERÁ RETORNA APENAS UMA VEZ
                query.append("SELECT DISTINCT m.aluno FROM MatriculaAC m WHERE m.status = true AND m.aluno.endereco.bairro.municipio.id =:idMunicipio ");
                blocosParametros.add(new BlocoParametro("idMunicipio", idMunicipio));

                if (idBairro > 0) {
                    query.append("AND m.aluno.endereco.bairro.id=:idBairro ");
                    blocosParametros.add(new BlocoParametro("idBairro", idBairro));
                }

                alunos = (ArrayList<AlunoAC>) relatorioDAO.gerarRelatorioEspecificoPorAluno(query.toString(), blocosParametros);
                exibirTabelaAluno = true;

            }
        } catch (DAOException e) {
            FacesUtils.addErrorMessage(e.getMessage() + ": " + e.getCause());
        }

    }
    
    /* Relatório Quantidade de alunos por bairro */
    public void imprimirRelQuantidadeAlunoBairro() {
        Relatorio<RetornoConsultaQuantidade> relatorioQtdAlunosBairro = new Relatorio<>();
        try {
            if ((blocosParametros.size() > 0)) {
                relatorioQtdAlunosBairro.getRelQuantidadeAlunoBairro(retornoConsultaQuantidades);
            } else {
                FacesUtils.addInfoMessage("Não há registros!");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            FacesUtils.addErrorMessage("Não há registros!");
        }
    }


    /* Relatório por Bairros */
    public void imprimirRelBairros() {
        Relatorio<AlunoAC> relatorioBgeral = new Relatorio<>();
        Relatorio<MatriculaAC> relatorioBMatriculados = new Relatorio<>();
        try {
            if ((matriculas.size() > 0) || (alunos.size() > 0)) {
                if (matriculas.size() == 0) {
                    relatorioBgeral.getRelPorBairroGeral(alunos);
                } else {
                    relatorioBMatriculados.getRelPorBairroMatriculados(matriculas);
                }
            } else {
                FacesUtils.addInfoMessage("Não há registros!");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            FacesUtils.addErrorMessage("Não há registros!");
        }
    }

    public void pesquisarBairroPorQuantidade() {
        try {
            bairros = new ArrayList<>();
            retornoConsultaQuantidades = new ArrayList<>();
            blocosParametros = new ArrayList<>();
            totalDeAlunos = 0;
            listarBairroPorMunicipio();

            StringBuilder query = new StringBuilder();

            //AQUI PESQUISARÁ EM ALUNO
            if (tipoRelatorio.equals("geral")) {
                for (BairroAC b : bairros) {
                    query.append("SELECT COUNT(DISTINCT a) FROM AlunoAC a WHERE a.endereco.bairro.id =:idBairro ");

                    blocosParametros.add(new BlocoParametro("idBairro", b.getId()));

                    RetornoConsultaQuantidade rcq = new RetornoConsultaQuantidade();
                    rcq.setTexto(b.getNome());
                    rcq.setQuantidade(relatorioDAO.gerarRelatorioPorQuantidade(query.toString(), blocosParametros));
                    totalDeAlunos += rcq.getQuantidade();

                    retornoConsultaQuantidades.add(rcq);
                    query = new StringBuilder();
                }
                retornoConsultaQuantidades.add(new RetornoConsultaQuantidade("TOTAL DE ALUNOS", totalDeAlunos));
            } //AQUI PESQUISARÁ EM MATRÍCULA
            else {
                for (BairroAC b : bairros) {
                    query.append("SELECT COUNT(DISTINCT m.aluno) FROM MatriculaAC m WHERE m.status =:status AND m.aluno.endereco.bairro.id =:idBairro ");

                    blocosParametros.add(new BlocoParametro("status", true));

                    blocosParametros.add(new BlocoParametro("idBairro", b.getId()));

                    RetornoConsultaQuantidade rcq = new RetornoConsultaQuantidade();
                    rcq.setTexto(b.getNome());
                    rcq.setQuantidade(relatorioDAO.gerarRelatorioPorQuantidade(query.toString(), blocosParametros));
                    totalDeAlunos += rcq.getQuantidade();

                    retornoConsultaQuantidades.add(rcq);
                    query = new StringBuilder();
                }
                retornoConsultaQuantidades.add(new RetornoConsultaQuantidade("TOTAL DE ALUNOS", totalDeAlunos));
            }
        } catch (DAOException e) {
            System.out.println(e.getCause());
            FacesUtils.addErrorMessage(e.getMessage());
        }
    }

    public void pesquisarPorTurmaPorQuantidade() {
        try {
            StringBuilder query = new StringBuilder();
            blocosParametros = new ArrayList<>();

            //------------------------------------------------------------------------------------------------------------------------
            //TRECHO RESPONSÁVEL POR PEGAR AS TURMAS
            //------------------------------------------------------------------------------------------------------------------------
            query.append("SELECT o FROM TurmaAC o ");

            //VERIFICA SE HÁ CONDIÇÃO NA CONSULTA
            if (statusTurma.equals("ativas") || statusTurma.equals("finalizadas") || idUnidade > 0) {
                query.append("WHERE ");
            }

            //VERIFICA SE PESQUISARÁ POR TURMAS ATIVAS OU FINALIZADAS, CASO CONTRÁRIO PESQUISARÁ POR AMBAS
            if (statusTurma.equals("ativas")) {
                query.append("o.iniciada=TRUE ");
            } else if (statusTurma.equals("finalizadas")) {
                query.append("o.iniciada=FALSE ");
            }

            //SE ENTRAR NA CONDIÇÃO PESQUISARÁ POR UNIDADE ESPECÍFICA, CASO CONTRÁRIO PESQUISARÁ EM TODAS
            if (idUnidade > 0) {
                if (statusTurma.equals("ativas") || statusTurma.equals("finalizadas")) {
                    query.append("AND o.unidade.id=:idUnidade ");
                } else {
                    query.append("o.unidade.id=:idUnidade ");
                }
                blocosParametros.add(new BlocoParametro("idUnidade", idUnidade));
            }

            query.append("ORDER BY o.atividade.descricao, o.nome ");

            turmas = (ArrayList<TurmaAC>) relatorioDAO.gerarRelatorioEspecificoPorTurma(query.toString(), blocosParametros);

            //------------------------------------------------------------------------------------------------------------------------
            //TRECHO RESPONSÁVEL POR CONTAR QUANTOS ALUNOS HÁ EM CADA TURMA
            //------------------------------------------------------------------------------------------------------------------------
            query = new StringBuilder();
            blocosParametros = new ArrayList<>();

            for (TurmaAC t : turmas) {
                query.append("SELECT COUNT(DISTINCT o.id) FROM MatriculaAC o WHERE o.turma.id=:idTurma ");
                blocosParametros.add(new BlocoParametro("idTurma", t.getId()));
                t.setQuantidadeDeAlunos(relatorioDAO.gerarRelatorioPorQuantidade(query.toString(), blocosParametros));
                totalDeAlunos += t.getQuantidadeDeAlunos();
                query = new StringBuilder();
            }

            //------------------------------------------------------------------------------------------------------------------------
            //TRECHO RESPONSÁVEL PELA CONTAGEM DE ALUNOS POR ATIVIDADE E INCLUSÃO DE UMA LINHA PARA SIMULAR O AGRUPAMENTO DA ATIVIDADE
            //------------------------------------------------------------------------------------------------------------------------
            ArrayList<TurmaAC> turmas2 = new ArrayList<>();
            Long quantidadeDeAlunosDaAtividade = 0l;
            int indexTurma = 1;
            String atividade = "";
            if (turmas.size() > 0) {
                atividade = turmas.get(0).getAtividade().getDescricao();
            }

            for (TurmaAC t : turmas) {
                indexTurma++;
                if (t.getAtividade().getDescricao().equals(atividade)) {
                    quantidadeDeAlunosDaAtividade += t.getQuantidadeDeAlunos();
                } else {
                    TurmaAC tm = new TurmaAC();
                    tm.setNome(" ");

                    Atividade a = new Atividade();
                    a.setDescricao(atividade);
                    tm.setAtividade(a);

                    tm.setDiasDaSemana("Total de alunos da atividade ");

                    HorarioAC h = new HorarioAC();
                    h.setDescricao(" ");
                    tm.setHorario(h);

                    tm.setQuantidadeDeAlunos(quantidadeDeAlunosDaAtividade);
                    turmas2.add(tm);
                    quantidadeDeAlunosDaAtividade = 0l;
                    quantidadeDeAlunosDaAtividade += t.getQuantidadeDeAlunos();
                    atividade = t.getAtividade().getDescricao();
                }
                turmas2.add(t);

                //SE FOR A TURMA FINAL
                if (indexTurma > turmas.size()) {
                    TurmaAC tm = new TurmaAC();
                    tm.setNome(" ");

                    Atividade a = new Atividade();
                    a.setDescricao(atividade);
                    tm.setAtividade(a);

                    tm.setDiasDaSemana("Total de alunos da atividade ");

                    HorarioAC h = new HorarioAC();
                    h.setDescricao(" ");
                    tm.setHorario(h);

                    tm.setQuantidadeDeAlunos(quantidadeDeAlunosDaAtividade);
                    turmas2.add(tm);
                }

            }

            turmas = turmas2;

            exibirTabelaMatricula = true;
        } catch (DAOException e) {
            System.out.println(e.getCause());
            FacesUtils.addErrorMessage(e.getMessage());
        }
    }

    /* Relatório Quantidade por Turmas */
    public void imprimirRelQuantidadeTurma() {
        Relatorio<TurmaAC> relatorioQtdTurma = new Relatorio<>();
        try {
            if ((turmas.size() > 0)) {
                relatorioQtdTurma.getRelQuantidadePorTurma(turmas);
            } else {
                FacesUtils.addInfoMessage("Não há registros!");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            FacesUtils.addErrorMessage("Não há registros!");
        }
    }

    public void gerarFrequncia() {
        Relatorio<MatriculaAC> relatorioFrequencia = new Relatorio<>();
        try {
            if ((matriculas.size() > 0)) {
                relatorioFrequencia.getListaFrequencia(matriculas);
            } else {
                FacesUtils.addInfoMessage("Não há registros!");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            FacesUtils.addErrorMessage("Não há registros!");
        }
    }

    public void gerarRenovacaoDeMatricula() {
        Relatorio<MatriculaAC> relatorioRenovacao = new Relatorio<>();
        try {
            if ((matriculas.size() > 0)) {
                relatorioRenovacao.getListaRenovacao(matriculas);
            } else {
                FacesUtils.addInfoMessage("Não há registros!");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            FacesUtils.addErrorMessage("Não há registros!");
        }
    }

    private void limparForm() {
        tipoRelatorio = "";
        statusTurma = "";
        exibirTabelaAluno = false;
        exibirTabelaMatricula = false;

        data1 = new Date();
        data2 = new Date();

        idUnidade = -1l;
        idTurma = -1l;
        idAtividade = -1l;
        idMunicipio = -1l;
        idBairro = -1l;
        totalDeAlunos = 0l;

        blocosParametros = new ArrayList<>();
        unidades = new ArrayList<>();
        turmas = new ArrayList<>();
        retornoConsultaQuantidades = new ArrayList<>();
        alunos = new ArrayList<>();
        matriculas = new ArrayList<>();
    }

    public String cancelarAniversariante() {
        return "relatorio-aniversariante.xhtml" + "?faces-redirect=true";
    }

    public String cancelarTurma() {
        return "relatorio-turma.xhtml" + "?faces-redirect=true";
    }

    public String cancelarTurmaMae() {
        return "relatorio-turma-mae.xhtml" + "?faces-redirect=true";
    }

    public String cancelarTurmaTelefone() {
        return "relatorio-turma-telefone.xhtml" + "?faces-redirect=true";
    }

    public String cancelarTurmaQuantidade() {
        return "relatorio-turma-quantidade.xhtml" + "?faces-redirect=true";
    }

    public String cancelarTurmaPorQuantidade() {
        return "relatorio-turma-quantidade.xhtml" + "?faces-redirect=true";
    }

    public String cancelarAtividade() {
        return "relatorio-atividade.xhtml" + "?faces-redirect=true";
    }

    public String cancelarAtividadePorQuantidade() {
        return "relatorio-atividade-quantidade.xhtml" + "?faces-redirect=true";
    }

    public String cancelarBairro() {
        return "relatorio-bairro.xhtml" + "?faces-redirect=true";
    }

    public String cancelarBairroPorQuantidade() {
        return "relatorio-bairro-quantidade.xhtml" + "?faces-redirect=true";
    }

    public String cancelarRenovacao() {
        return "relatorio-turma-renovacao.xhtml" + "?faces-redirect=true";
    }

//==========================================================================
    public ArrayList<AlunoAC> getAlunos() {
        return alunos;
    }

    public Date getData1() {
        return data1;
    }

    public void setData1(Date data1) {
        this.data1 = data1;
    }

    public Date getData2() {
        return data2;
    }

    public void setData2(Date data2) {
        this.data2 = data2;
    }

    public String getTipoRelatorio() {
        return tipoRelatorio;
    }

    public void setTipoRelatorio(String tipoRelatorio) {
        this.tipoRelatorio = tipoRelatorio;
    }

    public ArrayList<MatriculaAC> getMatriculas() {
        return matriculas;
    }

    public boolean isExibirTabelaAluno() {
        return exibirTabelaAluno;
    }

    public boolean isExibirTabelaMatricula() {
        return exibirTabelaMatricula;
    }

    public ArrayList<UnidadeAC> getUnidades() {
        return unidades;
    }

    public Long getIdUnidade() {
        return idUnidade;
    }

    public void setIdUnidade(Long idUnidade) {
        this.idUnidade = idUnidade;
    }

    public ArrayList<TurmaAC> getTurmas() {
        return turmas;
    }

    public long getIdTurma() {
        return idTurma;
    }

    public void setIdTurma(long idTurma) {
        this.idTurma = idTurma;
    }

    public String getStatusTurma() {
        return statusTurma;
    }

    public void setStatusTurma(String statusTurma) {
        this.statusTurma = statusTurma;
    }

    public ArrayList<Atividade> getAtividades() {
        return atividades;
    }

    public long getIdAtividade() {
        return idAtividade;
    }

    public void setIdAtividade(long idAtividade) {
        this.idAtividade = idAtividade;
    }

    public ArrayList<MunicipioAC> getMunicipios() {
        return municipios;
    }

    public ArrayList<BairroAC> getBairros() {
        return bairros;
    }

    public long getIdMunicipio() {
        return idMunicipio;
    }

    public void setIdMunicipio(long idMunicipio) {
        this.idMunicipio = idMunicipio;
    }

    public long getIdBairro() {
        return idBairro;
    }

    public void setIdBairro(long idBairro) {
        this.idBairro = idBairro;
    }

    public ArrayList<RetornoConsultaQuantidade> getRetornoConsultaQuantidades() {
        return retornoConsultaQuantidades;
    }

    public long getTotalDeAlunos() {
        return totalDeAlunos;
    }

}
