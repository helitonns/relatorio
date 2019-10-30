package br.leg.alrr.cursos.controller;

import br.leg.alrr.cursos.model.Bairro;
import br.leg.alrr.cursos.model.Aluno;
import br.leg.alrr.cursos.model.Endereco;
import br.leg.alrr.cursos.model.Municipio;
import br.leg.alrr.relatorio.business.Sexo;
import br.leg.alrr.cursos.model.Pais;
import br.leg.alrr.cursos.model.Turma;
import br.leg.alrr.cursos.persistence.BairroDAO;
import br.leg.alrr.cursos.persistence.AlunoDAO;
import br.leg.alrr.cursos.persistence.MatriculaDAO;
import br.leg.alrr.cursos.persistence.MunicipioDAO;
import br.leg.alrr.cursos.persistence.PaisDAO;
import br.leg.alrr.cursos.persistence.TurmaDAO;
import br.leg.alrr.relatorio.util.DAOException;
import br.leg.alrr.relatorio.util.FacesUtils;
import java.io.Serializable;
import java.util.ArrayList;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.event.ValueChangeEvent;
import javax.inject.Named;
import javax.faces.view.ViewScoped;

/**
 *
 * @author heliton
 */
@Named
@ViewScoped
public class AlunoMB implements Serializable {

    private static final long serialVersionUID = 1L;

    @EJB
    private AlunoDAO alunoDAO;

    @EJB
    private MunicipioDAO municipioDAO;

    @EJB
    private BairroDAO bairroDAO;

    //Usada para fazer a matricula
    @EJB
    private TurmaDAO turmaDAO;

    @EJB
    private MatriculaDAO matriculaDAO;

    @EJB
    private PaisDAO paisDAO;

    private ArrayList<Pais> paises;
    private ArrayList<Municipio> municipios;
    private ArrayList<Bairro> bairros;
    private ArrayList<Turma> turmas;

    private Aluno aluno;
    private Endereco endereco;
    private Municipio municipio;
    private Bairro bairro;
    private Long pais;

    //Usada para fazer a matricula
    private Turma turma;

    private Sexo sexo = Sexo.MASCULINO;
    private Long idMunicipio;
    private boolean editandoALuno;
    private boolean matricularNaTurma;

    //==========================================================================
    @PostConstruct
    public void init() {
        limparForm();

        bairros = new ArrayList<>();
        municipios = new ArrayList<>();

        listarMunicipio();
        listarPaises();

        try {
            //VERIFICA SE HÁ CPF A SER VIRIFICADO
            if (FacesUtils.getBean("alunoCPF") != null) {
                aluno = (Aluno) FacesUtils.getBean("alunoCPF");
                FacesUtils.removeBean("alunoCPF");
            }

            // VERIFICA SE HÁ ALGUM ALUNO NA SESSÃO PARA SER EDITADO, SE HOUVER SETA OS VALORES CORRESPONDENTES
            if (FacesUtils.getBean("aluno") != null) {
                aluno = (Aluno) FacesUtils.getBean("aluno");
                endereco = aluno.getEndereco();
                bairro = endereco.getBairro();
                municipio = bairro.getMunicipio();
                idMunicipio = municipio.getId();
                listarBairroPorMunicipio();
                editandoALuno = true;
                pais = aluno.getPaisDeOrigem().getId();
                FacesUtils.removeBean("aluno");
            }
        } catch (Exception e) {
            FacesUtils.addInfoMessage("Erro ao tentar editar aluno.");
        }

        listarTurmasAtivas();
    }

    private void listarTurmasAtivas() {
        try {
            turmas = (ArrayList<Turma>) turmaDAO.listarTurmasIniciadas();
        } catch (DAOException e) {
            FacesUtils.addInfoMessage(e.getMessage());
        }
    }

    private void listarMunicipio() {
        try {
            municipios = (ArrayList<Municipio>) municipioDAO.listarTodos();
        } catch (DAOException e) {
            FacesUtils.addInfoMessage(e.getMessage());
        }
    }

    private void listarBairroPorMunicipio() {
        try {
            bairros = (ArrayList<Bairro>) bairroDAO.listarBairroPorMunicipio(municipio);
        } catch (DAOException e) {
            FacesUtils.addInfoMessage(e.getMessage());
        }
    }

    private void listarPaises() {
        try {
            paises = (ArrayList<Pais>) paisDAO.listarTodos();
        } catch (DAOException e) {
            FacesUtils.addInfoMessage(e.getMessage());
        }
    }

    public String verificarSeCpfCadastrado() {

        if (aluno.getCpf() != null && !aluno.getCpf().isEmpty()) {
            try {
                Aluno a = alunoDAO.pesquisarPorCPF(aluno.getCpf());
                if (a != null) {
                    FacesUtils.setBean("aluno", a);
                    return "matricula.xhtml" + "?faces-redirect=true";
                }
            } catch (DAOException e) {
                FacesUtils.setBean("alunoCPF", aluno);
                return "aluno.xhtml" + "?faces-redirect=true";
            }

        } else {
            FacesUtils.addWarnMessage("Informe o CPF!!!");
        }

        return null;
    }

    public void valueChanged(ValueChangeEvent event) {
        try {
            idMunicipio = Long.parseLong(event.getNewValue().toString());
            municipio.setId(idMunicipio);
            listarBairroPorMunicipio();
        } catch (NumberFormatException e) {
            FacesUtils.addInfoMessage(e.getMessage());
        }
    }

    public String salvarAluno() {
        try {
            endereco.setBairro(bairro);
            aluno.setEndereco(endereco);
            aluno.setPaisDeOrigem(new Pais(pais));

            if (aluno.getId() != null) {
                alunoDAO.atualizar(aluno);
                FacesUtils.addInfoMessageFlashScoped("Aluno atualizado com sucesso!!!");
            } 
        } catch (DAOException e) {
            FacesUtils.addErrorMessageFlashScoped(e.getMessage() + ": " + e.getCause());
        }
        FacesUtils.removeBean("cpf");
        return "listar-editar-aluno.xhtml" + "?faces-redirect=true";
    }

    private void limparForm() {
        aluno = new Aluno();
        aluno.setPossuiFilho(false);
        bairro = new Bairro();
        municipio = new Municipio();
        pais = 1l;
        endereco = new Endereco();
        turma = new Turma();
        idMunicipio = 0l;
        editandoALuno = false;
        matricularNaTurma = false;
    }

    public String cancelar() {
        return "listar-editar-aluno.xhtml" + "?faces-redirect=true";
    }

    public String incluirMatricula() {
        FacesUtils.setBean("idAluno", aluno.getId());
        return "matricula.xhtml" + "?faces-redirect=true";
    }

    public String editarAluno() {
        FacesUtils.setBean("aluno", aluno);
        return "aluno.xhtml" + "?faces-redirect=true";
    }

    public String enviarAlunoParaMatricula() {
        FacesUtils.setBean("aluno", aluno);
        return "matricula.xhtml" + "?faces-redirect=true";
    }

    public void salvarBairro() {
        try {
            bairro.setMunicipio(municipio);
            bairroDAO.salvar(bairro);
            cancelarBairro();
        } catch (DAOException e) {
            FacesUtils.addInfoMessage(e.getMessage());
        }
    }

    public void cancelarBairro() {
        municipio = new Municipio();
        bairro = new Bairro();
    }

//==========================================================================
    public Aluno getAluno() {
        return aluno;
    }

    public void setAluno(Aluno aluno) {
        this.aluno = aluno;
    }

    public Sexo getSexo() {
        return sexo;
    }

    public void setSexo(Sexo sexo) {
        this.sexo = sexo;
    }

    public Endereco getEndereco() {
        return endereco;
    }

    public void setEndereco(Endereco endereco) {
        this.endereco = endereco;
    }

    public ArrayList<Municipio> getMunicipios() {
        return municipios;
    }

    public ArrayList<Bairro> getBairros() {
        return bairros;
    }

    public Bairro getBairro() {
        return bairro;
    }

    public void setBairro(Bairro bairro) {
        this.bairro = bairro;
    }

    public Municipio getMunicipio() {
        return municipio;
    }

    public void setMunicipio(Municipio municipio) {
        this.municipio = municipio;
    }

    public Long getIdMunicipio() {
        return idMunicipio;
    }

    public void setIdMunicipio(Long idMunicipio) {
        this.idMunicipio = idMunicipio;
    }

    public boolean isEditandoALuno() {
        return editandoALuno;
    }

    public ArrayList<Turma> getTurmas() {
        return turmas;
    }

    public Turma getTurma() {
        return turma;
    }

    public void setTurma(Turma turma) {
        this.turma = turma;
    }

    public boolean isMatricularNaTurma() {
        return matricularNaTurma;
    }

    public void setMatricularNaTurma(boolean matricularNaTurma) {
        this.matricularNaTurma = matricularNaTurma;
    }

    public void setTurmas(ArrayList<Turma> turmas) {
        this.turmas = turmas;
    }

    public ArrayList<Pais> getPaises() {
        return paises;
    }

    public Long getPais() {
        return pais;
    }

    public void setPais(Long pais) {
        this.pais = pais;
    }

}
