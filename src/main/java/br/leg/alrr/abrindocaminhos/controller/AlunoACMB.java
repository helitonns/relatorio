package br.leg.alrr.abrindocaminhos.controller;

import br.leg.alrr.abrindocaminhos.model.AlunoAC;
import br.leg.alrr.abrindocaminhos.model.BairroAC;
import br.leg.alrr.abrindocaminhos.model.Denominacao;
import br.leg.alrr.abrindocaminhos.model.EnderecoAC;
import br.leg.alrr.abrindocaminhos.model.Escola;
import br.leg.alrr.abrindocaminhos.model.Escolaridade;
import br.leg.alrr.abrindocaminhos.model.Genitores;
import br.leg.alrr.abrindocaminhos.model.Instrucao;
import br.leg.alrr.abrindocaminhos.model.ListaDeEspera;
import br.leg.alrr.abrindocaminhos.model.MatriculaAC;
import br.leg.alrr.abrindocaminhos.model.MunicipioAC;
import br.leg.alrr.abrindocaminhos.model.PaisAC;
import br.leg.alrr.abrindocaminhos.model.Periodo;
import br.leg.alrr.abrindocaminhos.model.Serie;
import br.leg.alrr.abrindocaminhos.model.Situacao;
import br.leg.alrr.abrindocaminhos.model.TurmaAC;
import br.leg.alrr.abrindocaminhos.model.UsuarioComUnidadeAC;
import br.leg.alrr.abrindocaminhos.persistence.AlunoACDAO;
import br.leg.alrr.abrindocaminhos.persistence.BairroACDAO;
import br.leg.alrr.abrindocaminhos.persistence.DenominacaoDAO;
import br.leg.alrr.abrindocaminhos.persistence.EscolaDAO;
import br.leg.alrr.abrindocaminhos.persistence.EscolaridadeDAO;
import br.leg.alrr.abrindocaminhos.persistence.InscricaoDAO;
import br.leg.alrr.abrindocaminhos.persistence.ListaDeEsperaDAO;
import br.leg.alrr.abrindocaminhos.persistence.MatriculaACDAO;
import br.leg.alrr.abrindocaminhos.persistence.MunicipioACDAO;
import br.leg.alrr.abrindocaminhos.persistence.PaisACDAO;
import br.leg.alrr.abrindocaminhos.persistence.PeriodoDAO;
import br.leg.alrr.abrindocaminhos.persistence.SerieDAO;
import br.leg.alrr.abrindocaminhos.persistence.SituacaoDAO;
import br.leg.alrr.abrindocaminhos.persistence.TurmaACDAO;
import br.leg.alrr.relatorio.business.Sexo;
import br.leg.alrr.relatorio.util.DAOException;
import br.leg.alrr.relatorio.util.FacesUtils;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import javax.faces.event.ValueChangeEvent;
import org.primefaces.event.CaptureEvent;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

/**
 *
 * @author heliton
 */
@Named
@ViewScoped
public class AlunoACMB implements Serializable {

    private static final long serialVersionUID = 1L;

    @EJB
    private AlunoACDAO alunoDAO;

    @EJB
    private MunicipioACDAO municipioDAO;

    @EJB
    private BairroACDAO bairroDAO;

    @EJB
    private SituacaoDAO situacaoDAO;

    @EJB
    private MatriculaACDAO matriculaDAO;

    @EJB
    private PaisACDAO paisDAO;

    @EJB
    private EscolaridadeDAO escolaridadeDAO;

    @EJB
    private PeriodoDAO periodoDAO;

    @EJB
    private DenominacaoDAO denominacaoDAO;

    @EJB
    private EscolaDAO escolaDAO;

    @EJB
    private SerieDAO serieDAO;

    @EJB
    private ListaDeEsperaDAO listaDAO;

    @EJB
    private TurmaACDAO turmaDAO;

    @EJB
    private InscricaoDAO inscricaoDAO;

    private ArrayList<PaisAC> paises;
    private ArrayList<MunicipioAC> municipios;
    private ArrayList<BairroAC> bairros;
    private ArrayList<Situacao> situacoes;
    private ArrayList<Escolaridade> escolaridades;
    private ArrayList<Serie> series;
    private ArrayList<Periodo> periodos;
    private ArrayList<Denominacao> denominacoes;
    private ArrayList<Escola> escolas;
    private ArrayList<TurmaAC> turmas;
    private ArrayList<ListaDeEspera> listas;
    private ArrayList<MatriculaAC> matriculas;

    private AlunoAC aluno;
    private EnderecoAC endereco;
    private MunicipioAC municipio;
    private BairroAC bairro;
    private Genitores genitores;
    private Instrucao instrucao;
    private TurmaAC turma;
    private ListaDeEspera listaDeEspera;
    private Escola escola;
    private PaisAC pais;

    private Sexo sexo = Sexo.MASCULINO;
    private Long idMunicipio;
    private Long idPais;
    private Long idSituacao;
    private Long idEscolaridade;
    private Long idSerie;
    private Long idPeriodo;
    private Long idEscola;
    private Long idDenominacao;
    private String cpfPesquisa;
    private boolean editandoALuno;
    private boolean matricularNaTurma;
    private boolean inscreverNaLista;
    private boolean encontrouGenitores;
    private boolean exibirMinhasAtividades;

    private byte[] imagem;

    //==========================================================================
    @PostConstruct
    public void init() {
        limparForm();

        bairros = new ArrayList<>();
        municipios = new ArrayList<>();
        matriculas = new ArrayList<>();

        listarMunicipio();
        listarPaises();
        listarSituacoesAtivas();

        listarEscolaridadesAtivas();
        listarSeriesAtivas();
        listarPeriodosAtivos();
        listarDenominacoesAtivas();
//        listarTurmasAtivas();
//        listarListasAtivas();

        try {
            //VERIFICA SE HÁ CPF A SER VIRIFICADO
            if (FacesUtils.getBean("alunoCPF") != null) {
                aluno = (AlunoAC) FacesUtils.getBean("alunoCPF");
                FacesUtils.removeBean("alunoCPF");
            }

            // VERIFICA SE HÁ ALGUM ALUNO NA SESSÃO PARA SER EDITADO, SE HOUVER SETA OS VALORES CORRESPONDENTES
            if (FacesUtils.getBean("aluno") != null) {
                preEditar();
                FacesUtils.removeBean("aluno");
            }
        } catch (Exception e) {
            FacesUtils.addInfoMessage("Erro ao tentar editar aluno. \n" + e.getCause());
        }
    }

    public void verificarMinhasAtividades() {
        try {
            matriculas = (ArrayList<MatriculaAC>) turmaDAO.listarTurmasPorAluno(aluno);
            exibirMinhasAtividades = true;
        } catch (DAOException e) {
            FacesUtils.addErrorMessage(e.getMessage() + ": " + e.getCause());
        }
    }

    private byte[] getFileContents(InputStream in) {
        byte[] bytes = null;
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            int read = 0;
            bytes = new byte[1024];

            while ((read = in.read(bytes)) != -1) {
                bos.write(bytes, 0, read);
            }
            bytes = bos.toByteArray();
            in.close();
            in = null;
            bos.flush();
            bos.close();
            bos = null;
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return bytes;
    }

    public void handleFileUpload(FileUploadEvent event) throws IOException {
        imagem = getFileContents(event.getFile().getInputstream());
    }

    public void onCapture(CaptureEvent captureEvent) {
        imagem = captureEvent.getData();
    }

    public StreamedContent getImagemStreamed() {
        return new DefaultStreamedContent(new ByteArrayInputStream(getImagem()));
    }

    private void preEditar() {
        aluno = (AlunoAC) FacesUtils.getBean("aluno");
        endereco = aluno.getEndereco();
        bairro = endereco.getBairro();
        municipio = bairro.getMunicipio();
        idMunicipio = municipio.getId();
        listarBairroPorMunicipio();
        editandoALuno = true;
        idPais = aluno.getPaisDeOrigem().getId();

        if (aluno.getInstrucao() != null) {
            instrucao = aluno.getInstrucao();

            if (aluno.getInstrucao().getEscola() != null) {
                idEscola = instrucao.getEscola().getId();
                idDenominacao = instrucao.getEscola().getDenominacao().getId();
                listarEscolaPorDenominacao(new Denominacao(idDenominacao));
            }

            if (aluno.getInstrucao().getEscolaridade() != null) {
                idEscolaridade = instrucao.getEscolaridade().getId();
            }

            if (aluno.getInstrucao().getSerie() != null) {
                idSerie = instrucao.getSerie().getId();
            }

            if (aluno.getInstrucao().getPeriodo() != null) {
                idPeriodo = instrucao.getPeriodo().getId();
            }

        }

        genitores = aluno.getGenitores();

        if (aluno.getFoto() != null) {
            imagem = aluno.getFoto();
        }
    }

    private void preSalvar() {
        try {
            
            boolean podeIncluirInstrucao = false;

            if (idEscola != 0l) {
                instrucao.setEscola(escolaDAO.buscarPorID(idEscola));
                podeIncluirInstrucao = true;
            }
            if (idPeriodo != 0l) {
                instrucao.setPeriodo(new Periodo(idPeriodo));
                podeIncluirInstrucao = true;
            }
            if (idSerie != 0l) {
                instrucao.setSerie(new Serie(idSerie));
                podeIncluirInstrucao = true;
            }
            if (idEscolaridade != 0l) {
                instrucao.setEscolaridade(new Escolaridade(idEscolaridade));
                podeIncluirInstrucao = true;
            }

            if (podeIncluirInstrucao) {
                aluno.setInstrucao(instrucao);
            }

            endereco.setBairro(bairro);
            aluno.setEndereco(endereco);
            aluno.setPaisDeOrigem(new PaisAC(idPais));

            //VERIFICAR SE GENITORES JÁ EXISTEM
            if (!editandoALuno) {
                if (genitores.getCpfMae() != null && !genitores.getCpfMae().isEmpty()) {
                    cpfPesquisa = genitores.getCpfMae();
                    pesquisarGenitores();
                } else if (genitores.getCpfPai() != null && !genitores.getCpfPai().isEmpty()) {
                    cpfPesquisa = genitores.getCpfPai();
                    pesquisarGenitores();
                }
            }

            aluno.setGenitores(genitores);
            aluno.setFoto(imagem);
        } catch (DAOException e) {
            FacesUtils.addErrorMessageFlashScoped(e.getMessage() + ": " + e.getCause());
        }

    }

    public String salvarAluno() {
        try {
            preSalvar();
            if (aluno.getId() != null) {
                alunoDAO.atualizar(aluno);
                FacesUtils.addInfoMessageFlashScoped("Aluno atualizado com sucesso!!!");
            } 
        } catch (DAOException e) {
            FacesUtils.addErrorMessageFlashScoped(e.getMessage() + ": " + e.getCause());
            System.out.println(e.getCause());
        }
        FacesUtils.removeBean("cpf");
        return "listar-editar-aluno.xhtml" + "?faces-redirect=true";
    }

//    private void listarTurmasAtivas() {
//        try {
//            UsuarioComUnidadeAC u = (UsuarioComUnidadeAC) FacesUtils.getBean("usuarioComUnidadeAC");
//            turmas = (ArrayList<TurmaAC>) turmaDAO.listarTurmasIniciadasPorUnidade(u.getUnidade());
//        } catch (DAOException e) {
//            FacesUtils.addInfoMessage(e.getMessage());
//        }
//    }

//    private void listarListasAtivas() {
//        try {
//            UsuarioComUnidadeAC u = (UsuarioComUnidadeAC) FacesUtils.getBean("usuarioComUnidadeAC");
//            listas = (ArrayList<ListaDeEspera>) listaDAO.listarListaDeEsperasIniciadasPorUnidade(u.getUnidade());
//        } catch (DAOException e) {
//            FacesUtils.addInfoMessage(e.getMessage());
//        }
//    }

    private void listarMunicipio() {
        try {
            municipios = (ArrayList<MunicipioAC>) municipioDAO.listarTodos();
        } catch (DAOException e) {
            FacesUtils.addInfoMessage(e.getMessage());
        }
    }

    private void listarBairroPorMunicipio() {
        try {
            bairros = (ArrayList<BairroAC>) bairroDAO.listarBairroPorMunicipio(municipio);
        } catch (DAOException e) {
            FacesUtils.addInfoMessage(e.getMessage());
        }
    }

    public void listarPaises() {
        try {
            paises = (ArrayList<PaisAC>) paisDAO.listarAtivos();
        } catch (DAOException e) {
            FacesUtils.addInfoMessage(e.getMessage());
        }
    }

    private void listarSituacoesAtivas() {
        try {
            situacoes = (ArrayList<Situacao>) situacaoDAO.listarSituacoesAtivas();
        } catch (DAOException e) {
            FacesUtils.addInfoMessage(e.getMessage());
        }
    }

    private void listarEscolaridadesAtivas() {
        try {
            escolaridades = (ArrayList<Escolaridade>) escolaridadeDAO.listarAtivas();
        } catch (DAOException e) {
            FacesUtils.addInfoMessage(e.getMessage());
        }
    }

    private void listarSeriesAtivas() {
        try {
            series = (ArrayList<Serie>) serieDAO.listarAtivas();
        } catch (DAOException e) {
            FacesUtils.addInfoMessage(e.getMessage());
        }
    }

    private void listarPeriodosAtivos() {
        try {
            periodos = (ArrayList<Periodo>) periodoDAO.listarPeriodosAtivos();
        } catch (DAOException e) {
            FacesUtils.addInfoMessage(e.getMessage());
        }
    }

    private void listarDenominacoesAtivas() {
        try {
            denominacoes = (ArrayList<Denominacao>) denominacaoDAO.listarAtivas();
        } catch (DAOException e) {
            FacesUtils.addInfoMessage(e.getMessage());
        }
    }

    private void listarEscolaPorDenominacao(Denominacao d) {
        try {
            if (aluno != null) {
                escolas = (ArrayList<Escola>) escolaDAO.listarEscolaAtivasPorDenominacaoEPorUnidade(d, aluno.getUnidade());
            }
        } catch (DAOException e) {
            FacesUtils.addInfoMessage(e.getMessage());
        }
    }

    public String verificarCodigoOuCpfCadastrado() {

        if (aluno.getId() != null && aluno.getId() != 0l) {
            try {
                AlunoAC a = alunoDAO.pesquisarPorID(aluno.getId());
                if (a != null) {
                    FacesUtils.setBean("aluno", a);
                    return "matricula.xhtml" + "?faces-redirect=true";
                }
            } catch (DAOException e) {
                FacesUtils.addWarnMessageFlashScoped("Código não encontrado!!!");
                return "aluno.xhtml" + "?faces-redirect=true";
            }

        }

        if (aluno.getCpf() != null && !aluno.getCpf().isEmpty()) {
            try {
                AlunoAC a = alunoDAO.pesquisarPorCPF(aluno.getCpf());
                if (a != null) {
                    FacesUtils.setBean("aluno", a);
                    return "matricula.xhtml" + "?faces-redirect=true";
                }
            } catch (DAOException e) {
                FacesUtils.setBean("alunoCPF", aluno);
                return "aluno.xhtml" + "?faces-redirect=true";
            }

        } else {
            FacesUtils.addWarnMessageFlashScoped("Informe o CPF!!!");
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

    public void valueChangedDenominacao(ValueChangeEvent event) {
        try {
            Long idDenominacao = Long.parseLong(event.getNewValue().toString());
            listarEscolaPorDenominacao(new Denominacao(idDenominacao));
        } catch (NumberFormatException e) {
            FacesUtils.addInfoMessage(e.getMessage());
        }
    }

    public void salvarBairro() {
        try {
            bairro.setMunicipio(municipio);
            bairroDAO.salvar(bairro);
            FacesUtils.addInfoMessageFlashScoped("Bairro salvo com sucesso!");
            listarBairroPorMunicipio();
            bairro = new BairroAC();
        } catch (DAOException e) {
            FacesUtils.addInfoMessage(e.getMessage());
        }
    }

    private void limparForm() {
        aluno = new AlunoAC();
        aluno.setCpf("");
        bairro = new BairroAC();
        municipio = new MunicipioAC();
        endereco = new EnderecoAC();
        genitores = new Genitores();
        instrucao = new Instrucao();
        escola = new Escola();
        pais = new PaisAC();

        cpfPesquisa = "";

        idPais = 0l;
        idSituacao = 0l;
        idMunicipio = 0l;
        idEscola = 0l;
        idEscolaridade = 0l;
        idPeriodo = 0l;
        idSerie = 0l;

        editandoALuno = false;
        matricularNaTurma = false;
        encontrouGenitores = false;
        exibirMinhasAtividades = false;
    }

    public String cancelar() {
        return "listar-editar-aluno.xhtml" + "?faces-redirect=true";
    }

    public String incluirMatricula() {
        FacesUtils.setBean("idAluno", aluno.getId());
        return "matricula.xhtml" + "?faces-redirect=true";
    }

    public String enviarMinhasAtividades() {
        try {
            AlunoAC a = new AlunoAC(aluno.getId());
            FacesUtils.setBean("alunoAtividade", a);
            return "minhas-atividades.xhtml" + "?faces-redirect=true";
        } catch (Exception e) {
            return null;
        }
    }

    public String editarAluno() {
        FacesUtils.setBean("aluno", aluno);
        return "aluno.xhtml" + "?faces-redirect=true";
    }

    public String enviarAlunoParaMatricula() {
        FacesUtils.setBean("aluno", aluno);
        return "matricula.xhtml" + "?faces-redirect=true";
    }

    public void pesquisarGenitores() {
        Genitores g = new Genitores();
        try {
            if (cpfPesquisa != null && !cpfPesquisa.isEmpty()) {
                g = alunoDAO.pesquisarGenitoresPorCPF(cpfPesquisa);
            }

            if (g.getId() != null) {
                genitores = g;
                encontrouGenitores = true;
            }
        } catch (DAOException e) {
        }
    }

//    public void salvarEscola2() {
//        try {
//            Denominacao d = denominacaoDAO.buscarPorID(idDenominacao);
//            escola.setDenominacao(d);
//
//            UsuarioComUnidade u = (UsuarioComUnidade) FacesUtils.getBean("usuario");
//            escola.setUnidade(u.getUnidade());
//            escola.setStatus(true);
//            escolaDAO.salvar(escola);
//            FacesUtils.addInfoMessageFlashScoped("Escola salva com sucesso!");
//
//            escola = new Escola();
//            listarEscolaPorDenominacao(d);
//
//        } catch (DAOException e) {
//            FacesUtils.addErrorMessageFlashScoped(e.getMessage());
//        }
//    }

    public void salvarPais() {
        try {
            pais.setStatus(true);
            paisDAO.salvar(pais);
            FacesUtils.addInfoMessage("País salvo com sucesso!");

            pais = new PaisAC();

            listarPaises();

        } catch (DAOException e) {
            FacesUtils.addErrorMessageFlashScoped(e.getMessage());
        }
    }

//==========================================================================
    public AlunoAC getAluno() {
        return aluno;
    }

    public void setAluno(AlunoAC aluno) {
        this.aluno = aluno;
    }

    public Sexo getSexo() {
        return sexo;
    }

    public void setSexo(Sexo sexo) {
        this.sexo = sexo;
    }

    public EnderecoAC getEndereco() {
        return endereco;
    }

    public void setEndereco(EnderecoAC endereco) {
        this.endereco = endereco;
    }

    public ArrayList<MunicipioAC> getMunicipios() {
        return municipios;
    }

    public ArrayList<BairroAC> getBairros() {
        return bairros;
    }

    public BairroAC getBairro() {
        return bairro;
    }

    public void setBairro(BairroAC bairro) {
        this.bairro = bairro;
    }

    public MunicipioAC getMunicipio() {
        return municipio;
    }

    public void setMunicipio(MunicipioAC municipio) {
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

    public boolean isMatricularNaTurma() {
        return matricularNaTurma;
    }

    public void setMatricularNaTurma(boolean matricularNaTurma) {
        this.matricularNaTurma = matricularNaTurma;
    }

    public ArrayList<PaisAC> getPaises() {
        return paises;
    }

    public Long getIdPais() {
        return idPais;
    }

    public void setIdPais(Long idPais) {
        this.idPais = idPais;
    }

    public ArrayList<Situacao> getSituacoes() {
        return situacoes;
    }

    public Long getIdSituacao() {
        return idSituacao;
    }

    public void setIdSituacao(Long idSituacao) {
        this.idSituacao = idSituacao;
    }

    public Genitores getGenitores() {
        return genitores;
    }

    public void setGenitores(Genitores genitores) {
        this.genitores = genitores;
    }

    public byte[] getImagem() {
        return imagem;
    }

    public void setImagem(byte[] imagem) {
        this.imagem = imagem;
    }

    public ArrayList<Escolaridade> getEscolaridades() {
        return escolaridades;
    }

    public ArrayList<Serie> getSeries() {
        return series;
    }

    public ArrayList<Periodo> getPeriodos() {
        return periodos;
    }

    public ArrayList<Denominacao> getDenominacoes() {
        return denominacoes;
    }

    public ArrayList<Escola> getEscolas() {
        return escolas;
    }

    public Long getIdEscolaridade() {
        return idEscolaridade;
    }

    public void setIdEscolaridade(Long idEscolaridade) {
        this.idEscolaridade = idEscolaridade;
    }

    public Long getIdSerie() {
        return idSerie;
    }

    public void setIdSerie(Long idSerie) {
        this.idSerie = idSerie;
    }

    public Long getIdPeriodo() {
        return idPeriodo;
    }

    public void setIdPeriodo(Long idPeriodo) {
        this.idPeriodo = idPeriodo;
    }

    public Long getIdEscola() {
        return idEscola;
    }

    public void setIdEscola(Long idEscola) {
        this.idEscola = idEscola;
    }

    public Long getIdDenominacao() {
        return idDenominacao;
    }

    public void setIdDenominacao(Long idDenominacao) {
        this.idDenominacao = idDenominacao;
    }

    public String getCpfPesquisa() {
        return cpfPesquisa;
    }

    public void setCpfPesquisa(String cpfPesquisa) {
        this.cpfPesquisa = cpfPesquisa;
    }

    public ArrayList<TurmaAC> getTurmas() {
        return turmas;
    }

    public TurmaAC getTurma() {
        return turma;
    }

    public void setTurma(TurmaAC turma) {
        this.turma = turma;
    }

    public ListaDeEspera getListaDeEspera() {
        return listaDeEspera;
    }

    public void setListaDeEspera(ListaDeEspera listaDeEspera) {
        this.listaDeEspera = listaDeEspera;
    }

    public boolean isInscreverNaLista() {
        return inscreverNaLista;
    }

    public void setInscreverNaLista(boolean inscreverNaLista) {
        this.inscreverNaLista = inscreverNaLista;
    }

    public ArrayList<ListaDeEspera> getListas() {
        return listas;
    }

    public ArrayList<MatriculaAC> getMatriculas() {
        return matriculas;
    }

    public boolean isExibirMinhasAtividades() {
        return exibirMinhasAtividades;
    }

    public Escola getEscola() {
        return escola;
    }

    public void setEscola(Escola escola) {
        this.escola = escola;
    }

    public PaisAC getPais() {
        return pais;
    }

    public void setPais(PaisAC pais) {
        this.pais = pais;
    }

}
