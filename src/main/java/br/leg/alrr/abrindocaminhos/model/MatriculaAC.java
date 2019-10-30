package br.leg.alrr.abrindocaminhos.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


/**
 *
 * @author Heliton
 */
@Table(name = "matricula",schema = "abrindo_caminhos")
@Entity
public class MatriculaAC implements Serializable{

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.EAGER)
    private AlunoAC aluno;

    @ManyToOne(fetch = FetchType.EAGER)
    private TurmaAC turma;
    
    @Temporal(TemporalType.DATE)
    private Date dataMatricula;

    private boolean status;
    
    @ManyToOne
    private UnidadeAC unidade;
    //==========================================================================
    public MatriculaAC() {
    }

    public MatriculaAC(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AlunoAC getAluno() {
        return aluno;
    }

    public void setAluno(AlunoAC aluno) {
        this.aluno = aluno;
    }

    public TurmaAC getTurma() {
        return turma;
    }

    public void setTurma(TurmaAC turma) {
        this.turma = turma;
    }

    public Date getDataMatricula() {
        return dataMatricula;
    }

    public void setDataMatricula(Date dataMatricula) {
        this.dataMatricula = dataMatricula;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public UnidadeAC getUnidade() {
        return unidade;
    }

    public void setUnidade(UnidadeAC unidade) {
        this.unidade = unidade;
    }
    
    public boolean podeMatricular(TurmaAC t, AlunoAC a){
        return a.getIdadeNumero() >= t.getIdadeMinima() && a.getIdadeNumero() <= t.getIdadeMaxima();
    }

   
}
