package br.leg.alrr.relatorio.controller;

import br.leg.alrr.relatorio.model.Autorizacao;
import br.leg.alrr.relatorio.model.Privilegio;
import br.leg.alrr.cursos.model.Unidade;
import br.leg.alrr.relatorio.model.Usuario;
import br.leg.alrr.relatorio.persistence.AutorizacaoDAO;
import br.leg.alrr.relatorio.persistence.PrivilegioDAO;
import br.leg.alrr.cursos.persistence.UnidadeDAO;
import br.leg.alrr.relatorio.persistence.UsuarioDAO;
import br.leg.alrr.relatorio.util.Criptografia;
import br.leg.alrr.relatorio.util.DAOException;
import br.leg.alrr.relatorio.util.FacesUtils;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

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
public class UsuarioMB implements Serializable {

    private static final long serialVersionUID = 1L;

    @EJB
    private AutorizacaoDAO autorizacaoDAO;

    @EJB
    private UsuarioDAO usuarioDAO;

    @EJB
    private UnidadeDAO unidadeDAO;

    @EJB
    private PrivilegioDAO permissaoDAO;

    private Autorizacao autorizacao;
    private Usuario usuario;
    private String senha;

    private List<Usuario> usuarios;
    private List<Unidade> unidades;
    private List<Privilegio> permissoes;
    private List<Autorizacao> autorizacoes;

    private boolean removerUsuario;

    private Long idUS = 0l;
    private Long idPermissao = 0l;
    
    //==========================================================================
    @PostConstruct
    public void init() {
        limparForm();
    }

    public void limparForm() {
        try {

            senha = "";
            usuario = new Usuario();
            usuario.setStatus(true);
            idUS = 0l;
            usuarios = new ArrayList<>();
            permissoes = new ArrayList<>();
            removerUsuario = false;
            autorizacao = new Autorizacao();

//            listarUnidades();
            String[] s = FacesUtils.getURL().split("/");
            Autorizacao a = (Autorizacao) FacesUtils.getBean("autorizacao");

            if (a.getPrivilegio().getDescricao().equals("SUPER_ADMIN")) {
                autorizacoes = autorizacaoDAO.listarTodasPorChaveDoSistema(s[1]);
                permissoes = permissaoDAO.listarTodosPelaChaveDoSistema(s[1]);
            } else {
                permissoes = permissaoDAO.listarTodosPelaChaveDoSistemaSemSuperAdmin(s[1]);
                autorizacoes = autorizacaoDAO.listarTodasPorChaveDoSistemaSemOsSuperAdmin(s[1]);
                Privilegio p1 = new Privilegio();
                for (Privilegio p : permissoes) {
                    if (p.getDescricao().equals("SUPER_ADMIN")) {
                        p1 = p;
                        break;
                    }
                }
                permissoes.remove(p1);
            }
        } catch (Exception e) {
        }
    }

    public String salvarUsuario() {
        try {

            //verifca se a senha de usuário é forte, se sim permite o cadastro
            if (verificarForcaDaSenha(senha)) {
                if (usuario.getId() != null) {
                    usuario.setSenha(Criptografia.criptografarEmMD5(senha));
                    usuarioDAO.atualizar(usuario);
                    autorizacao.setStatus(usuario.isStatus());
                    autorizacao.setPrivilegio(new Privilegio(idPermissao));
                    autorizacaoDAO.atualizar(autorizacao);
                    FacesUtils.addInfoMessageFlashScoped("Usuário atualizado com sucesso!");
                } else {
                    //verifica se já há usuario cadastrado com o mesmo login
                    if (!usuarioDAO.haUsuarioComEsteLogin(usuario.getLogin())) {
                        usuario.setSenha(Criptografia.criptografarEmMD5(senha));
                        usuarioDAO.salvar(usuario);
                        autorizacao.setUsuario(usuario);
                        autorizacao.setPrivilegio(new Privilegio(idPermissao));
                        autorizacao.setStatus(true);
                        autorizacaoDAO.salvar(autorizacao);
                        FacesUtils.addInfoMessageFlashScoped("Usuário salvo com sucesso!");
                    } else {
                        FacesUtils.addWarnMessageFlashScoped("O usuário não pode ser cadastrado, pois já há um usuário com este mesmo login!!!");
                    }
                }
            } else {
                FacesUtils.addWarnMessageFlashScoped("A senha deve atender aos seguintes requisitos: ter no mínimo 8 caracteres, possuir letra minúcula 'a', possuir letra maiúscula 'A' e número '123'!!!");
            }
        } catch (DAOException e) {
            FacesUtils.addErrorMessageFlashScoped("Erro ao salvar usuário!");
        }
        return "usuario.xhtml" + "?faces-redirect=true";
    }

//    public String salvarSuperUsuario() {
//        try {
//            //verifca se a senha de usuário é forte, se sim permite o cadastro
//            if (verificarForcaDaSenha(senha)) {
//                if (usuario.getId() != null) {
//                    usuario.setSenha(Criptografia.criptografarEmMD5(senha));
//                    usuarioDAO.atualizar(usuario);
//                    autorizacao.setStatus(usuario.isStatus());
//                    autorizacao.setPrivilegio(new Privilegio(idPermissao));
//                    autorizacaoDAO.atualizar(autorizacao);
//                    FacesUtils.addInfoMessageFlashScoped("Usuário atualizado com sucesso!");
//                } else {
//                    //verifica se já há usuario cadastrado com o mesmo login
//                    if (!usuarioDAO.haUsuarioComEsteLogin(usuario.getLogin())) {
//                        usuario.setSenha(Criptografia.criptografarEmMD5(senha));
//                        usuarioDAO.salvar(usuario);
//                        autorizacao.setUsuario(usuario);
//                        autorizacao.setPrivilegio(new Privilegio(idPermissao));
//                        autorizacao.setStatus(true);
//                        autorizacaoDAO.salvar(autorizacao);
//                        FacesUtils.addInfoMessageFlashScoped("Usuário salvo com sucesso!");
//                    } else {
//                        FacesUtils.addWarnMessageFlashScoped("O usuário não pode ser cadastrado, pois já há um usuário com este mesmo login!!!");
//                    }
//                }
//            } else {
//                FacesUtils.addWarnMessageFlashScoped("A senha deve atender aos seguintes requisitos: ter no mínimo 8 caracteres, possuir letra minúcula 'a', "
//                        + "possuir letra maiúscula 'A' e número '123'!!!");
//            }
//
//        } catch (DAOException e) {
//            FacesUtils.addErrorMessageFlashScoped("Erro ao salvar usuário!");
//        }
//        return "superusuario.xhtml" + "?faces-redirect=true";
//    }

//    public void listarUsuarios() {
//        try {
//            Usuario u = (Usuario) FacesUtils.getBean("usuario");
//            usuarios = usuarioDAO.listarTodosPorUnidade(u.getUnidade());
//        } catch (DAOException e) {
//            FacesUtils.addErrorMessage(e.getMessage());
//        }
//    }
//
//    public void listarUsuariosSemOSuperAdmin() {
//        try {
//            Usuario u = (Usuario) FacesUtils.getBean("usuario");
//            usuarios = usuarioDAO.listarTodosPorUnidadeSemOSuperAdmin(u.getUnidade());
//        } catch (DAOException e) {
//            FacesUtils.addErrorMessage(e.getMessage());
//        }
//    }

    public void listarTodosUsuarios() {
        try {
            usuarios = usuarioDAO.listarTodos();
        } catch (DAOException e) {
            FacesUtils.addErrorMessage(e.getMessage());
        }
    }

//    public void listarUnidades() {
//        try {
//            unidades = unidadeDAO.listarTodos();
//        } catch (DAOException e) {
//            FacesUtils.addErrorMessage(e.getMessage());
//        }
//    }
    public void removerUsuario() {
        try {
            if (removerUsuario) {
                autorizacaoDAO.remover(autorizacao);
                usuarioDAO.remover(usuario);
                FacesUtils.addInfoMessage("Usuário removido com sucesso!");
            }
        } catch (DAOException e) {
            FacesUtils.addErrorMessage(e.getMessage());
        }
        limparForm();
    }

//    public void prepararEdicao() {
//        idUS = usuario.getUnidade().getId();
//    }

    private boolean verificarForcaDaSenha(String senha) {
        if (senha.length() < 8) {
            return false;
        }

        boolean achouNumero = false;
        boolean achouMaiuscula = false;
        boolean achouMinuscula = false;
        boolean achouSimbolo = false;

        for (char c : senha.toCharArray()) {
            if (c >= '0' && c <= '9') {
                achouNumero = true;
            } else if (c >= 'A' && c <= 'Z') {
                achouMaiuscula = true;
            } else if (c >= 'a' && c <= 'z') {
                achouMinuscula = true;
            } else {
                achouSimbolo = true;
            }
        }

        //defini os parâmetros que serão avalidados
        //neste caso não irá levar em consideração o requisito "símbolo"
        return achouNumero && achouMaiuscula && achouMinuscula;
    }

    public String cancelar() {
        return "usuario.xhtml" + "?faces-redirect=true";
    }

    public String cancelarSuperUsuario() {
        return "superusuario.xhtml" + "?faces-redirect=true";
    }
    //==========================================================================

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public List<Usuario> getUsuarios() {
        return usuarios;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public boolean isRemoverUsuario() {
        return removerUsuario;
    }

    public void setRemoverUsuario(boolean removerUsuario) {
        this.removerUsuario = removerUsuario;
    }

    public List<Unidade> getUnidades() {
        return unidades;
    }

    public Long getIdUS() {
        return idUS;
    }

    public void setIdUS(Long idUS) {
        this.idUS = idUS;
    }

    public List<Privilegio> getPermissoes() {
        return permissoes;
    }

    public Long getIdPermissao() {
        return idPermissao;
    }

    public void setIdPermissao(Long idPermissao) {
        this.idPermissao = idPermissao;
    }

    public List<Autorizacao> getAutorizacoes() {
        return autorizacoes;
    }

    public Autorizacao getAutorizacao() {
        return autorizacao;
    }

    public void setAutorizacao(Autorizacao autorizacao) {
        this.autorizacao = autorizacao;
    }
}
