package vkkononenko.beans;

import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import vkkononenko.SecurityUtils;
import vkkononenko.UserSession;
import vkkononenko.models.Message;
import vkkononenko.models.SystemUser;

import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseId;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.io.*;
import java.util.Objects;

/**
 * Created by v.kononenko on 05.03.2019.
 */
@Named
@ViewScoped
public class ProfileView extends SecurityUtils implements Serializable {

    @PersistenceContext(name = "veles")
    private EntityManager em;

    @Inject
    private SystemUser systemUser;

    @Inject
    private SystemUser to;

    private Long id;

    private boolean itsMe;

    private String text;

    @Transactional
    public void onLoad() {
        systemUser = em.find(SystemUser.class, id);
        if(systemUser.getId().equals(userSession.getSystemUser().getId())) {
            itsMe = true;
        } else {
            itsMe = false;
        }
        em.refresh(systemUser);
        RequestContext.getCurrentInstance().update("mainform");
    }

    public void redirectToRepositories(SystemUser systemUser) throws IOException {
        if(systemUser == null) {
            FacesContext.getCurrentInstance().getExternalContext().redirect("view-repositories.xhtml?id=" + Objects.toString(this.systemUser.getId()));
        } else {
            FacesContext.getCurrentInstance().getExternalContext().redirect("view-repositories.xhtml?id=" + Objects.toString(systemUser.getId()));
        }
    }

    @Transactional
    public void deleteFromFriends(SystemUser friend) {
        if(friend != null) {
            systemUser.getFriends().remove(friend);
            em.merge(systemUser);
            userSession.setSystemUser(systemUser);
        } else {
            userSession.getSystemUser().getFriends().remove(systemUser);
            em.merge(userSession.getSystemUser());
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Сообщение!", "Вы перестали отслеживать данного пользователя!"));
        }
    }

    @Transactional
    public void saveChanges() {
        em.merge(systemUser);
        userSession.setSystemUser(systemUser);
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Сообщение!", "Профиль успешно обновлен!"));
    }

    @Transactional
    public void addToFriends() {
        userSession.getSystemUser().getFriends().add(systemUser);
        em.merge(userSession.getSystemUser());
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Сообщение!", "Вы отслеживаете данного пользователя!"));
    }

    @Transactional
    public void sendMessageTo(SystemUser systemUser) {
        SystemUser from = userSession.getSystemUser();
        Message message = new Message(from, systemUser, text);
        em.persist(message);
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Сообщение!", "Сообщение отправлено!"));
    }

    public boolean isMyFriend() {
        for(SystemUser friend : userSession.getSystemUser().getFriends()) {
            if(friend.getId().equals(systemUser.getId())) {
                return true;
            }
        }
        return false;
    }

    @Transactional
    public void handleFileUpload(FileUploadEvent event) throws Exception {
        if(event.getFile() != null) {
            systemUser.setAvatar(event.getFile().getContents());
            systemUser.setAvatarType(event.getFile().getContentType());
            em.merge(systemUser);
        }
        RequestContext.getCurrentInstance().update("mainform");
        UpdateFromServer();
    }

    public void UpdateFromServer() throws IOException {
        FacesContext.getCurrentInstance().getExternalContext().redirect("profile-view.xhtml?id=" + Objects.toString(this.systemUser.getId()));
    }

    public StreamedContent getImageFromDB() throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        if (context.getCurrentPhaseId() == PhaseId.RENDER_RESPONSE) {
            return new DefaultStreamedContent();
        } else {
            DefaultStreamedContent content = new DefaultStreamedContent(new ByteArrayInputStream(systemUser.getAvatar()),
                    systemUser.getAvatarType());
            return content;
        }
    }

    public UserSession getUserSession() {
        return userSession;
    }

    public void setUserSession(UserSession userSession) {
        this.userSession = userSession;
    }

    public SystemUser getSystemUser() {
        return systemUser;
    }

    public void setSystemUser(SystemUser systemUser) {
        this.systemUser = systemUser;
    }

    public SystemUser getTo() {
        return to;
    }

    public void setUserForMessageTo(SystemUser to) {
        this.to = to;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean isItsMe() {
        return itsMe;
    }

    public void setItsMe(boolean itsMe) {
        this.itsMe = itsMe;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
