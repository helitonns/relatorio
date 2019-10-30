package br.leg.alrr.cursos.controller;

import br.leg.alrr.cursos.model.Aluno;
import br.leg.alrr.cursos.model.Curso;
import br.leg.alrr.cursos.model.Turma;
import br.leg.alrr.cursos.persistence.AlunoDAO;
import br.leg.alrr.cursos.persistence.TurmaDAO;
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
 * @author heliton
 */
@Named
@ViewScoped
public class AlunoListagemMB implements Serializable {

    private static final long serialVersionUID = 1L;

    @EJB
    private AlunoDAO alunoDAO;

    @EJB
    private TurmaDAO turmaDAO;

    private ArrayList<Aluno> alunos;
    private ArrayList<Turma> turmas;

    private Aluno aluno;
    private Curso curso;

    private boolean removerAluno = false;
    private boolean mostrarTabelaDeCurso = false;

//==============================================================================
    @PostConstruct
    public void init() {
        limparForm();
    }

    public void pesquisarAluno() {
        try {
            alunos = new ArrayList<>();

            if (aluno.getCpf() != null && !aluno.getCpf().isEmpty()) {
                Aluno a = alunoDAO.pesquisarPorCPF(aluno.getCpf());
                alunos.add(a);
            } else if (aluno.getNome() != null && !aluno.getNome().isEmpty()) {
                alunos = (ArrayList<Aluno>) alunoDAO.pesquisarPorNome(aluno.getNome());
            } else {
                FacesUtils.addWarnMessage("Indique o CPF ou o nome do aluno!");
            }
        } catch (DAOException e) {
            //FacesMessages.error(e.getMessage()+" - "+e.getCause());
            FacesUtils.addWarnMessage("Sem resultado!");
        }
        aluno = new Aluno();
    }

    public String removerAluno() {
        try {
            if (removerAluno) {
                alunoDAO.remover(aluno);
                FacesUtils.addInfoMessageFlashScoped("Aluno removido com sucesso!");
            }
        } catch (Exception e) {
            FacesUtils.addErrorMessageFlashScoped(e.getCause().toString());
        }
        return "listar-editar-aluno.xhtml" + "?faces-redirect=true";
    }

    public void pegarTurma() {
        try {
            turmas = (ArrayList<Turma>) turmaDAO.listarTurmasPorAluno(aluno);
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

    public String verificarAprovados() {
        FacesUtils.setBean("curso", curso);
        return "aprovados.xhtml" + "?faces-redirect=true";
    }
    
    public String cancelar() {
        return "listar-editar-aluno.xhtml" + "?faces-redirect=true";
    }

    private void limparForm() {
        aluno = new Aluno();
        curso = new Curso();
        alunos = new ArrayList<>();
        turmas = new ArrayList<>();
        mostrarTabelaDeCurso = false;
        removerAluno = false;
    }

//==============================================================================
    public ArrayList<Aluno> getAlunos() {
        return alunos;
    }

    public boolean isRemoverAluno() {
        return removerAluno;
    }

    public void setRemoverAluno(boolean removerAluno) {
        this.removerAluno = removerAluno;
    }

    public Aluno getAluno() {
        return aluno;
    }

    public void setAluno(Aluno aluno) {
        this.aluno = aluno;
    }

    public ArrayList<Turma> getTurmas() {
        return turmas;
    }

    public boolean isMostrarTabelaDeCurso() {
        return mostrarTabelaDeCurso;
    }

    public Curso getCurso() {
        return curso;
    }

    public void setCurso(Curso curso) {
        this.curso = curso;
    }

}
