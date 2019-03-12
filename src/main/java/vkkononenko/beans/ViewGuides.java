package vkkononenko.beans;

import vkkononenko.UserSession;
import vkkononenko.models.HelpUnit;
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
public class ViewGuides implements Serializable {

    @PersistenceContext(name = "veles")
    private EntityManager em;

    private Long id;

    @Inject
    private UserSession userSession;

    @Inject
    private SystemUser systemUser;

    private List<HelpUnit> helpUnitList;

    @Transactional
    public void onLoad() {
        if(id == null) {
            helpUnitList = userSession.getSystemUser().getGuides();
        } else {
            systemUser = em.find(SystemUser.class, id);
            helpUnitList = systemUser.getGuides();
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

    public UserSession getUserSession() {
        return userSession;
    }

    public void setUserSession(UserSession userSession) {
        this.userSession = userSession;
    }

    public List<HelpUnit> getHelpUnitList() {
        return helpUnitList;
    }

    public void setHelpUnitList(List<HelpUnit> helpUnitList) {
        this.helpUnitList = helpUnitList;
    }
}
