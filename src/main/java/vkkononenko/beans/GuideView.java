package vkkononenko.beans;

import vkkononenko.UserSession;
import vkkononenko.models.HelpUnit;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.io.IOException;
import java.io.Serializable;

/**
 * Created by v.kononenko on 12.03.2019.
 */
@Named
@ViewScoped
public class GuideView implements Serializable {

    @PersistenceContext(name = "veles")
    private EntityManager em;
    
    private Long id;

    @Inject
    private UserSession userSession;

    @Inject
    private HelpUnit helpUnit;

    @Transactional
    public void onLoad() {
        if(id != null) {
            helpUnit = em.find(HelpUnit.class, id);
        } else {
            helpUnit = new HelpUnit();
        }
    }

    public boolean Updatable() {
        if(helpUnit.getMakeBy() == null) {
            helpUnit.setMakeBy(userSession.getSystemUser());
        }
        return helpUnit.getMakeBy().getId().equals(userSession.getSystemUser().getId());
    }

    @Transactional
    public void up() {
        helpUnit.setRank(helpUnit.getRank() + 1);
        em.merge(helpUnit);
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Сообщение!", "Вы положительно оценили данный гайд"));
    }

    @Transactional
    public void down() {
        helpUnit.setRank(helpUnit.getRank() - 1);
        em.merge(helpUnit);
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Сообщение!", "Вы отрицательно оценили данный гайд"));
    }

    @Transactional
    public void save() throws IOException {
        try {
            if(id == null) {
                helpUnit.setMakeBy(userSession.getSystemUser());
                em.persist(helpUnit);
                em.flush();
                userSession.getSystemUser().getGuides().add(helpUnit);
                userSession.setSystemUser(em.merge(userSession.getSystemUser()));
            }
            else {
                em.merge(helpUnit);
                userSession.setSystemUser(em.merge(userSession.getSystemUser()));
            }
        } catch (Exception ex) {
            return;
        }
        FacesContext.getCurrentInstance().getExternalContext().redirect("guide-view.xhtml?id=" + helpUnit.getId());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public HelpUnit getHelpUnit() {
        return helpUnit;
    }

    public void setHelpUnit(HelpUnit helpUnit) {
        this.helpUnit = helpUnit;
    }
}
