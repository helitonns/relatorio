package br.leg.alrr.abrindocaminhos.model;

import br.leg.alrr.relatorio.model.Usuario;
import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;


/**
 *
 * @author Heliton
 */
@Table(name = "usuariocomunidade", schema = "abrindo_caminhos")
@Entity
@PrimaryKeyJoinColumn(name = "id")
public class UsuarioComUnidadeAC extends Usuario implements Serializable{

    
    @ManyToOne
    private UnidadeAC unidade;
    //========================================================================//
    public UsuarioComUnidadeAC() {
        super();
    }

    public UsuarioComUnidadeAC(Long id) {
        super(id);
    }

    public UnidadeAC getUnidade() {
        return unidade;
    }

    public void setUnidade(UnidadeAC unidade) {
        this.unidade = unidade;
    }
    
}
