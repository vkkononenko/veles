package vkkononenko.beans;

import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;
import vkkononenko.UserSession;
import vkkononenko.models.SystemUser;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.Serializable;

/**
 * Created by v.kononenko on 05.03.2019.
 */
@Named
@ViewScoped
public class ProfileView implements Serializable {

    @PersistenceContext(name = "veles")
    private EntityManager em;

    @Inject
    private UserSession userSession;

    @Inject
    private SystemUser systemUser;

    private Long id;

    private boolean itsMe;

    private UploadedFile file;

    public void onLoad() {
        if(id == null) {
            systemUser = userSession.getSystemUser();
            itsMe = true;
        } else {
            systemUser = em.find(SystemUser.class, id);
            itsMe = false;
        }
    }

    public void handleFileUpload(FileUploadEvent event) throws Exception {
        systemUser.setAvatarPath(systemUser.getLogin() + "avatar");
        event.getFile().write(systemUser.getLogin() + "avatar");
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


    public UploadedFile getFile() {
        return file;
    }

    public void setFile(UploadedFile file) {
        this.file = file;
    }

}
