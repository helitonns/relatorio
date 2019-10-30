package br.leg.alrr.abrindocaminhos.controller;

import br.leg.alrr.abrindocaminhos.model.AlunoAC;
import br.leg.alrr.abrindocaminhos.model.Genitores;
import br.leg.alrr.abrindocaminhos.model.MatriculaAC;
import br.leg.alrr.abrindocaminhos.persistence.AlunoACDAO;
import br.leg.alrr.abrindocaminhos.persistence.TurmaACDAO;
import br.leg.alrr.relatorio.util.DAOException;
import br.leg.alrr.relatorio.util.FacesUtils;
import java.io.Serializable;
import java.util.ArrayList;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.faces.view.ViewScoped;

/**
 *
 * @author Heliton
 */
@Named
@ViewScoped
public class AlunoACListagemMB implements Serializable {

    private static final long serialVersionUID = 1L;

    @EJB
    private AlunoACDAO alunoDAO;

    @EJB
    private TurmaACDAO turmaDAO;

    private ArrayList<AlunoAC> alunos;
    private ArrayList<AlunoAC> irmaos;
    private ArrayList<MatriculaAC> matriculas;

    private AlunoAC aluno;
    private AlunoAC irmao;
    private Genitores genitores;

    private boolean removerAluno = false;
    private boolean mostrarTabelaDeCurso = false;
    private boolean exibirAtividade = false;

    private Long idAluno;
    private Long idGenitor;
    private Long idEndereco;

//==============================================================================
    @PostConstruct
    public void init() {
        limparForm();

        try {
            //VERIFICA SE HÁ ALUNO PARA EXIBIR SUAS ATIVIDADES
            if (FacesUtils.getBean("alunoAtividade") != null) {
                aluno = (AlunoAC) FacesUtils.getBean("alunoAtividade");
                FacesUtils.removeBean("alunoAtividade");
                pegarTurma2();
            }

        } catch (Exception e) {
            FacesUtils.addInfoMessage("Erro ao tentar editar aluno.");
        }
    }

    public void pesquisarAluno() {
        try {
            alunos = new ArrayList<>();
            irmaos = new ArrayList<>();
            genitores = new Genitores();
            exibirAtividade = false;

            if (aluno.getId() != null) {
                AlunoAC a = alunoDAO.pesquisarPorID(aluno.getId());
                alunos.add(a);
            } else if (aluno.getCpf() != null && !aluno.getCpf().isEmpty()) {
                AlunoAC a = alunoDAO.pesquisarPorCPF(aluno.getCpf());
                alunos.add(a);
            } else if (aluno.getNome() != null && !aluno.getNome().isEmpty()) {
                alunos = (ArrayList<AlunoAC>) alunoDAO.pesquisarPorNome(aluno.getNome());
            } else {
                FacesUtils.addWarnMessageFlashScoped("Indique o código, o CPF ou o nome do aluno!");
            }
        } catch (DAOException e) {
            FacesUtils.addWarnMessageFlashScoped("Sem resultado!");
        }
        aluno = new AlunoAC();
    }

    public void removerAluno() {
        try {
            if (removerAluno) {
                if (alunoDAO.genitorTemMaisDeUmFilho(new Genitores(idGenitor))) {
                    alunoDAO.DeleteAluno(new AlunoAC(idAluno));
                    alunoDAO.DeleteEndereco(idEndereco);
                } else {
                    alunoDAO.DeleteAluno(new AlunoAC(idAluno));
                    alunoDAO.DeleteEndereco(idEndereco);
                    alunoDAO.removerGenitor(alunoDAO.buscarGenitor(idGenitor));
                }
                FacesUtils.addInfoMessage("Aluno removido com sucesso!");
            }
        } catch (DAOException e) {
            FacesUtils.addErrorMessage(e.getMessage());
        }
        limparForm();
    }

    public void pegarTurma() {
        try {
            exibirAtividade = true;

            matriculas = (ArrayList<MatriculaAC>) turmaDAO.listarTurmasPorAluno(irmao);
            mostrarTabelaDeCurso = true;
        } catch (DAOException e) {
            FacesUtils.addErrorMessage(e.getMessage() + ": " + e.getCause());
        }
    }
    
    public void pegarTurma2() {
        try {
            exibirAtividade = true;

            matriculas = (ArrayList<MatriculaAC>) turmaDAO.listarTurmasPorAluno(aluno);
            mostrarTabelaDeCurso = true;
        } catch (DAOException e) {
            FacesUtils.addErrorMessage(e.getMessage() + ": " + e.getCause());
        }
    }

    public String editarAluno() {
        FacesUtils.setBean("aluno", aluno);
        return "aluno.xhtml" + "?faces-redirect=true";
    }

    public String exibirAluno() {
        FacesUtils.setBean("aluno", aluno);
        return "exibir-aluno.xhtml" + "?faces-redirect=true";
    }

    public String enviarAlunoParaMatricula() {
        FacesUtils.setBean("aluno", aluno);
        return "matricula.xhtml" + "?faces-redirect=true";
    }

    public String cancelar() {
        return "minhas-atividades.xhtml" + "?faces-redirect=true";
    }

    private void limparForm() {
        aluno = new AlunoAC();
        genitores = new Genitores();
        alunos = new ArrayList<>();
        mostrarTabelaDeCurso = false;
        removerAluno = false;
    }

    public void pesquisarFamilia() {
        try {
            genitores = aluno.getGenitores();
            irmaos = (ArrayList<AlunoAC>) alunoDAO.pesquisarFamilia(aluno.getGenitores());
        } catch (DAOException e) {
        }
    }

    public void verificarMInhasAtividades() {
        try {
            matriculas = (ArrayList<MatriculaAC>) turmaDAO.listarTurmasPorAluno(aluno);
        } catch (DAOException e) {
            FacesUtils.addErrorMessage(e.getMessage() + ": " + e.getCause());
        }
    }
//==============================================================================

    public ArrayList<AlunoAC> getAlunos() {
        return alunos;
    }

    public boolean isRemoverAluno() {
        return removerAluno;
    }

    public void setRemoverAluno(boolean removerAluno) {
        this.removerAluno = removerAluno;
    }

    public AlunoAC getAluno() {
        return aluno;
    }

    public void setAluno(AlunoAC aluno) {
        this.aluno = aluno;
    }

    public boolean isMostrarTabelaDeCurso() {
        return mostrarTabelaDeCurso;
    }

    public ArrayList<MatriculaAC> getMatriculas() {
        return matriculas;
    }

    public ArrayList<AlunoAC> getIrmaos() {
        return irmaos;
    }

    public Genitores getGenitores() {
        return genitores;
    }

    public void setGenitores(Genitores genitores) {
        this.genitores = genitores;
    }

    public AlunoAC getIrmao() {
        return irmao;
    }

    public void setIrmao(AlunoAC irmao) {
        this.irmao = irmao;
    }

    public boolean isExibirAtividade() {
        return exibirAtividade;
    }

    public void setIdAlunoGenitor(Long idAluno, Long idGenitor, Long idEndereco) {
        this.idAluno = idAluno;
        this.idGenitor = idGenitor;
        this.idEndereco = idEndereco;
    }

}
