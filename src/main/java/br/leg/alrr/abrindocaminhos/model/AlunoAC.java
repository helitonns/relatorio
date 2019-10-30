package br.leg.alrr.abrindocaminhos.model;

import br.leg.alrr.relatorio.business.Sexo;
import java.io.Serializable;
import java.util.Date;
import java.util.GregorianCalendar;
import javax.persistence.Basic;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;


/**
 *
 * @author Heliton
 */
@Table(name = "aluno", schema = "abrindo_caminhos")
@Entity
public class AlunoAC implements Serializable, Comparable<AlunoAC>, Cloneable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;

    private String cpf;

    private String rg;

    @Enumerated(EnumType.STRING)
    private Sexo sexo;

    @Temporal(TemporalType.DATE)
    private Date dataNascimento;

    private String celular;

    private String fixo;

    private String informacoesComplementares;

    private String naturalidade;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    private Genitores genitores;

    @ManyToOne
    private PaisAC paisDeOrigem;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Instrucao instrucao;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private EnderecoAC endereco;

    @ManyToOne
    private UnidadeAC unidade;

    @Lob
    @Basic(fetch = FetchType.LAZY, optional = true)
    private byte[] foto;

    @ManyToOne(fetch = FetchType.EAGER)
    private Situacao situacao;

    @Temporal(TemporalType.TIMESTAMP)
    private Date dataDeCadastro;

    @Transient
    private String idade;
    //=========================================================================//

    public AlunoAC() {
    }

    public AlunoAC(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getRg() {
        return rg;
    }

    public void setRg(String rg) {
        this.rg = rg;
    }

    public Sexo getSexo() {
        return sexo;
    }

    public void setSexo(Sexo sexo) {
        this.sexo = sexo;
    }

    public Date getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(Date dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public String getCelular() {
        return celular;
    }

    public void setCelular(String celular) {
        this.celular = celular;
    }

    public String getFixo() {
        return fixo;
    }

    public void setFixo(String fixo) {
        this.fixo = fixo;
    }

    public String getInformacoesComplementares() {
        return informacoesComplementares;
    }

    public void setInformacoesComplementares(String informacoesComplementares) {
        this.informacoesComplementares = informacoesComplementares;
    }

    public PaisAC getPaisDeOrigem() {
        return paisDeOrigem;
    }

    public void setPaisDeOrigem(PaisAC paisDeOrigem) {
        this.paisDeOrigem = paisDeOrigem;
    }

    public String getNaturalidade() {
        return naturalidade;
    }

    public void setNaturalidade(String naturalidade) {
        this.naturalidade = naturalidade;
    }

    public EnderecoAC getEndereco() {
        return endereco;
    }

    public void setEndereco(EnderecoAC endereco) {
        this.endereco = endereco;
    }

    public UnidadeAC getUnidade() {
        return unidade;
    }

    public void setUnidade(UnidadeAC unidade) {
        this.unidade = unidade;
    }

    public Genitores getGenitores() {
        return genitores;
    }

    public void setGenitores(Genitores genitores) {
        this.genitores = genitores;
    }

    public Instrucao getInstrucao() {
        return instrucao;
    }

    public void setInstrucao(Instrucao instrucao) {
        this.instrucao = instrucao;
    }

    public byte[] getFoto() {
        return foto;
    }

    public void setFoto(byte[] foto) {
        this.foto = foto;
    }

    public Situacao getSituacao() {
        return situacao;
    }

    public void setSituacao(Situacao situacao) {
        this.situacao = situacao;
    }

    public Date getDataDeCadastro() {
        return dataDeCadastro;
    }

    public void setDataDeCadastro(Date dataDeCadastro) {
        this.dataDeCadastro = dataDeCadastro;
    }

    @Override
    public int compareTo(AlunoAC o) {
        if (this.nome.compareTo(o.getNome()) > 0) {
            return 1;
        } else {
            return -1;
        }
    }

    public String getIdade() {
        StringBuilder sb = new StringBuilder();
        if (dataNascimento != null) {
            GregorianCalendar g1 = new GregorianCalendar();
            GregorianCalendar g2 = new GregorianCalendar();
            g2.setTime(dataNascimento);

            int idade1 = g1.get(GregorianCalendar.YEAR) - g2.get(GregorianCalendar.YEAR);
            int mesAtual = g1.get(GregorianCalendar.MONTH) + 1;
            int mesDaIdade = g2.get(GregorianCalendar.MONTH) + 1;
            int fracaoDeMes = mesAtual - mesDaIdade;

            if (fracaoDeMes == 0) {
                return sb.append(idade1).toString();
            } else if (fracaoDeMes > 0) {
                return sb.append(idade1).append(" anos e ").append(fracaoDeMes).append(" meses").toString();
            } else if (fracaoDeMes < 0) {
                return sb.append(idade1 - 1).append(" anos e ").append(12 + fracaoDeMes).append(" meses").toString();
            }
        }
        return "0";
    }

    public int getIdadeNumero() {
        StringBuilder sb = new StringBuilder();
        if (dataNascimento != null) {
            GregorianCalendar g1 = new GregorianCalendar();
            GregorianCalendar g2 = new GregorianCalendar();
            g2.setTime(dataNascimento);

            return g1.get(GregorianCalendar.YEAR) - g2.get(GregorianCalendar.YEAR);
        }
        return 0;
    }

    public void setIdade(String i) {
        idade = i;
    }

    @Override
    public AlunoAC clone() throws CloneNotSupportedException {
        try {
            return (AlunoAC) super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }

}
