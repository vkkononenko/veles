package vkkononenko.beans;

import vkkononenko.SecurityUtils;
import vkkononenko.UserSession;
import vkkononenko.models.Guide;
import vkkononenko.models.Repository;
import vkkononenko.models.SystemUser;

import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;

/**
 * Created by v.kononenko on 12.03.2019.
 */
@Named
@ViewScoped
public class ViewGuides extends SecurityUtils implements Serializable {

    @PersistenceContext(name = "veles")
    private EntityManager em;

    private Long id;

    @Inject
    private UserSession userSession;

    @Inject
    private SystemUser systemUser;

    private List<Guide> GuideList;

    @Transactional
    public void onLoad() {
        if(id == null) {
            GuideList = userSession.getSystemUser().getGuides();
        } else {
            systemUser = em.find(SystemUser.class, id);
            GuideList = systemUser.getGuides();
        }
    }

    public void redirectToCreateNewEntity() throws IOException {
        FacesContext.getCurrentInstance().getExternalContext().redirect("guide-view.xhtml");
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public EntityManager getEm() {
        return em;
    }

    public void setEm(EntityManager em) {
        this.em = em;
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

    public List<Guide> getGuideList() {
        return GuideList;
    }

    public void setGuideList(List<Guide> guideList) {
        GuideList = guideList;
    }
}
