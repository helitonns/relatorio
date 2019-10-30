package br.leg.alrr.relatorio.controller;

import br.leg.alrr.relatorio.model.Privilegio;
import br.leg.alrr.relatorio.model.Sistema;
import br.leg.alrr.relatorio.persistence.PrivilegioDAO;
import br.leg.alrr.relatorio.persistence.SistemaDAO;
import br.leg.alrr.relatorio.util.DAOException;
import br.leg.alrr.relatorio.util.FacesUtils;
import java.io.Serializable;
import java.util.ArrayList;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

/**
 *
 * @author heliton
 */
@Named
@ViewScoped
public class PrivilegioMB implements Serializable {

    private static final long serialVersionUID = 1L;

    @EJB
    private PrivilegioDAO privilegioDAO;

    @EJB
    private SistemaDAO sistemaDAO;

    private Privilegio privilegio;

    private ArrayList<Privilegio> privilegios;
    private ArrayList<Sistema> sistemas;

    private Long idSistema;
    private boolean removerPrivilegio = false;

    //==========================================================================
    @PostConstruct
    public void init() {
        listarSistemas();
        limparForm();
    }

    private void listarPrivilegios() {
        try {
            String url = FacesUtils.getURL();
            String[] pedacosURL = url.split("/");

            privilegios = (ArrayList<Privilegio>) privilegioDAO.listarTodosPelaChaveDoSistema(pedacosURL[1]);
        } catch (DAOException e) {
            FacesUtils.addErrorMessage(e.getMessage());
        }
    }

    private void listarSistemas() {
        try {
            sistemas = (ArrayList<Sistema>) sistemaDAO.listarTodosAtivos();
        } catch (DAOException e) {
            FacesUtils.addErrorMessage(e.getMessage());
        }
    }

    public String salvarPrivilegio() {
        try {
            privilegio.setSistema(new Sistema(idSistema));

            if (!privilegioDAO.haPrivilegioComMesmoNomeESistema(privilegio)) {
                if (privilegio.getId() != null) {
                    privilegioDAO.atualizar(privilegio);
                    FacesUtils.addInfoMessageFlashScoped("Privilégio atualizada com sucesso!");
                } else {
                    privilegioDAO.salvar(privilegio);
                    FacesUtils.addInfoMessageFlashScoped("Privilégio salva com sucesso!");
                }
            } else {
                FacesUtils.addWarnMessageFlashScoped("Já há um mesmo privilégio salvo para o sistema escolido!");
            }

            limparForm();
        } catch (DAOException e) {
            FacesUtils.addErrorMessageFlashScoped(e.getMessage());
        }
        return "privilegio.xhtml" + "?faces-redirect=true";
    }

    public void removerPrivilegio() {
        try {
            if (removerPrivilegio) {
                privilegioDAO.remover(privilegio);
                FacesUtils.addInfoMessage("Privilégio removida com sucesso!");
            }
            limparForm();
        } catch (DAOException e) {
            FacesUtils.addErrorMessage(e.getMessage());
        }
    }

    private void limparForm() {
        privilegio = new Privilegio();
        privilegio.setStatus(true);
        privilegios = new ArrayList<>();
        removerPrivilegio = false;
        listarPrivilegios();
        idSistema = 0l;
    }

    public String cancelar() {
        return "permissao.xhtml" + "?faces-redirect=true";
    }

    //==========================================================================
    public Privilegio getPrivilegio() {
        return privilegio;
    }

    public void setPrivilegio(Privilegio privilegio) {
        this.privilegio = privilegio;
    }

    public ArrayList<Privilegio> getPrivilegios() {
        return privilegios;
    }

    public ArrayList<Sistema> getSistemas() {
        return sistemas;
    }

    public boolean isRemoverPrivilegio() {
        return removerPrivilegio;
    }

    public void setRemoverPrivilegio(boolean removerPrivilegio) {
        this.removerPrivilegio = removerPrivilegio;
    }

    public Long getIdSistema() {
        return idSistema;
    }

    public void setIdSistema(Long idSistema) {
        this.idSistema = idSistema;
    }

}
