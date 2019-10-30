package br.leg.alrr.abrindocaminhos.model;

import br.leg.alrr.relatorio.util.BaseEntity;
import java.io.Serializable;
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
public class Receita implements Serializable, BaseEntity{

    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
   
    @ManyToOne
    private Alergia alergia;
    
    @ManyToOne
    private Medicacao medicacao;

//==============================================================================

    public Receita() {
    }
   
    public Receita(Long id) {
        this.id = id;
    }
    
    @Override
    public Long getId() {
        return id;
    }

    public Alergia getAlergia() {
        return alergia;
    }

    public void setAlergia(Alergia alergia) {
        this.alergia = alergia;
    }

    public Medicacao getMedicacao() {
        return medicacao;
    }

    public void setMedicacao(Medicacao medicacao) {
        this.medicacao = medicacao;
    }

    @Override
    public void setId(Long id) {
         this.id = id;
    }
    
}
