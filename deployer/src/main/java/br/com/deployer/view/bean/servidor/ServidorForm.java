package br.com.deployer.view.bean.servidor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.primefaces.context.RequestContext;

import br.com.deployer.exception.ValidationException;
import br.com.deployer.model.Servidor;
import br.com.deployer.model.ServletContainer;
import br.com.deployer.model.Usuario;
import br.com.deployer.service.ServidorService;
import br.com.deployer.service.ServletContainerService;
import br.com.deployer.util.ValidatorUrl;
import br.com.deployer.view.util.JsfUtil;

@ManagedBean
@ViewScoped
public class ServidorForm {

	private Servidor servidor = new Servidor();
	private Long idEdicao;
	private ServletContainer servletContainer = new ServletContainer();
	private List<ServletContainer> listaServletContainers = new ArrayList<ServletContainer>();
	private List<ServletContainer> listaDeRemocaoServletContainers = new ArrayList<ServletContainer>();

	@ManagedProperty("#{servidorService}")
	private ServidorService service;

	@ManagedProperty("#{servletContainerService}")
	private ServletContainerService servletContainerService;

	public void inicializa() {
		if (idEdicao != null) {
			this.servidor = service.buscaPorId(idEdicao);
			this.listaServletContainers = servletContainerService.listarServletContainersPorServidor(servidor);
		}
		if (this.servidor == null) {
			this.servidor = new Servidor();
			this.servletContainer = new ServletContainer();
		}
	}

	public void adicionarServletContainer() {
		try {
			this.servletContainer.setServidor(this.servidor);
			servletContainerService.validar(servletContainer);
			Usuario usuarioSessao = (Usuario) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("usuarioSessao");
			servletContainer.setDataCadastro(LocalDateTime.now());
			servletContainer.setUsuarioCadastro(usuarioSessao);
			this.listaServletContainers.add(servletContainer);
			this.servletContainer = new ServletContainer();
			RequestContext.getCurrentInstance().execute("PF('dlg2').hide()");
		} catch (ValidationException e) {
			List<String> errors = e.getErrors();
			errors.forEach(msgErro -> JsfUtil.addErrorMessage(msgErro));
		}
	}

	public void removerServletContainer(ServletContainer servletContainer) {
		try {
			this.listaServletContainers.remove(servletContainer);
			JsfUtil.addSucessMessage("Servlet Container removido com sucesso!");
			if(servletContainer.getId() != null) {
				this.listaDeRemocaoServletContainers.add(servletContainer);
			}
		} catch (ValidationException e) {
			List<String> errors = e.getErrors();
			errors.forEach(msgErro -> JsfUtil.addErrorMessage(msgErro));
		}
	}

	public String salvar() {
		try {
			servidor.setServletContainers(this.listaServletContainers);
			service.salvar(servidor);
			this.setServidor(new Servidor());
			this.setServletContainers(new ServletContainer());
			JsfUtil.addSucessMessage("Salvo com sucesso!");
			return "servidor-list.xhtml";
		} catch (ValidationException e) {
			List<String> errors = e.getErrors();
			errors.forEach(msgErro -> JsfUtil.addErrorMessage(msgErro));
		} catch (Exception e) {
			JsfUtil.addErrorMessage("Erro ao Salvar");
		}
		return null;
	}
	
	public String atualizar() {
		try {
			servidor.setServletContainers(this.listaServletContainers);
			servidor.setServletContainersDeletar(listaDeRemocaoServletContainers);
			service.atualizar(servidor);
			this.setServidor(new Servidor());
			this.setServletContainers(new ServletContainer());
			JsfUtil.addSucessMessage("Atualizado com sucesso!");
			return "servidor-list.xhtml";
		} catch (ValidationException e) {
			List<String> errors = e.getErrors();
			errors.forEach(msgErro -> JsfUtil.addErrorMessage(msgErro));
		} catch (Exception e) {
			JsfUtil.addErrorMessage("Erro ao Atualizar");
		}
		return null;
	}
	
	public void validarUrl() {
		try {
			boolean isValida = ValidatorUrl.isValida(servidor.getUrl());
			
			if(!isValida) {
				servidor.setUrl(null);
				throw new ValidationException("Formato de url inválido.");
			}
		} catch (ValidationException e) {
			List<String> errors = e.getErrors();
			errors.forEach(msgErro -> JsfUtil.addErrorMessage(msgErro));
		} catch (Exception e) {
			JsfUtil.addErrorMessage("Erro ao Salvar, Verifique a Url.");
		}
	}
	
	public Servidor getServidor() {
		return servidor;
	}

	public void setServidor(Servidor servidor) {
		this.servidor = servidor;
	}

	public Long getIdEdicao() {
		return idEdicao;
	}

	public void setIdEdicao(Long idEdicao) {
		this.idEdicao = idEdicao;
	}

	public ServidorService getService() {
		return service;
	}

	public void setService(ServidorService service) {
		this.service = service;
	}

	public ServletContainerService getServletContainerService() {
		return servletContainerService;
	}

	public void setServletContainerService(ServletContainerService servletContainerService) {
		this.servletContainerService = servletContainerService;
	}

	public ServletContainer getServletContainer() {
		return servletContainer;
	}

	public void setServletContainers(ServletContainer servletContainer) {
		this.servletContainer = servletContainer;
	}

	public List<ServletContainer> getListaServletContainers() {
		return listaServletContainers;
	}

	public void setListaServletContainers(List<ServletContainer> listaServletContainers) {
		this.listaServletContainers = listaServletContainers;
	}

	public List<ServletContainer> getListaDeRemocaoServletContainers() {
		return listaDeRemocaoServletContainers;
	}

	public void setListaDeRemocaoServletContainers(List<ServletContainer> listaDeRemocaoServletContainers) {
		this.listaDeRemocaoServletContainers = listaDeRemocaoServletContainers;
	}
}
