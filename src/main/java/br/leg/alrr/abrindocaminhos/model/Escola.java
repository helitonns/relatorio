package br.leg.alrr.abrindocaminhos.model;

import br.leg.alrr.relatorio.util.BaseEntity;
import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;


/**
 *
 * @author Heliton
 */
@Table(schema = "abrindo_caminhos")
@Entity
public class Escola implements Serializable, BaseEntity{

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column
    private String nome;
    
    @Column
    private boolean status;

    @ManyToOne
    private Denominacao denominacao;
    
    @ManyToOne
    private UnidadeAC unidade;
    //========================================================================//

    public Escola() {
    }

    public Escola(Long id) {
        this.id = id;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Denominacao getDenominacao() {
        return denominacao;
    }

    public void setDenominacao(Denominacao denominacao) {
        System.out.println("escutou");
        this.denominacao = denominacao;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Escola other = (Escola) obj;
        if (id == null) {
            if (other.id != null) {
                return false;
            }
        } else if (!id.equals(other.id)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 41 * hash + Objects.hashCode(this.id);
        return hash;
    }

    public UnidadeAC getUnidade() {
        return unidade;
    }

    public void setUnidade(UnidadeAC unidade) {
        this.unidade = unidade;
    }
    
    
}
