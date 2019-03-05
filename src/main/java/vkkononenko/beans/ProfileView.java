package vkkononenko.beans;

import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import vkkononenko.UserSession;
import vkkononenko.models.Message;
import vkkononenko.models.SystemUser;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseId;
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
@SessionScoped
public class ProfileView implements Serializable {

    @PersistenceContext(name = "veles")
    private EntityManager em;

    @Inject
    private UserSession userSession;

    @Inject
    private SystemUser systemUser;

    private Long id;

    private boolean itsMe;

    private String text;

    @Transactional
    public void onLoad() {
        if(id == null) {
            systemUser = userSession.getSystemUser();
            itsMe = true;
        } else {
            systemUser = em.find(SystemUser.class, id);
            itsMe = false;
        }
    }

    @Transactional
    public void handleFileUpload(FileUploadEvent event) throws Exception {
        if(event.getFile() != null) {
            systemUser.setAvatar(event.getFile().getContents());
            systemUser.setAvatarType(event.getFile().getContentType());
            em.merge(systemUser);
        }
        RequestContext.getCurrentInstance().update("mainform");
    }

    public void redirectToRepositories() throws IOException {
        FacesContext.getCurrentInstance().getExternalContext().redirect("view-repositories.xhtml?id=" + Objects.toString(systemUser.getId()));
    }

    public StreamedContent getImageFromDB() throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        if (context.getCurrentPhaseId() == PhaseId.RENDER_RESPONSE) {
            return new DefaultStreamedContent();
        } else {

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            return new DefaultStreamedContent(new ByteArrayInputStream(systemUser.getAvatar()),
                    systemUser.getAvatarType());
        }
    }

    @Transactional
    public void saveChanges() {
        em.merge(systemUser);
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Сообщение!", "Профиль успешно обновлен!"));
    }

    @Transactional
    public void addToFriends() {
        userSession.getSystemUser().getFriends().add(systemUser);
        em.merge(userSession.getSystemUser());
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Сообщение!", "Вы отслеживаете данного пользователя!"));
    }

    @Transactional
    public void sendMessageTo() {
        Message message = new Message(userSession.getSystemUser(), systemUser, text);
        em.persist(message);
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
