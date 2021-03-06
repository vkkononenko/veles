package vkkononenko.beans;

import vkkononenko.utils.SecurityUtils;
import vkkononenko.UserSession;
import vkkononenko.models.Comment;
import vkkononenko.models.Guide;
import vkkononenko.models.SystemUser;

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
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by v.kononenko on 12.03.2019.
 */
@Named
@ViewScoped
public class GuideView extends SecurityUtils implements Serializable {

    @PersistenceContext(name = "veles")
    private EntityManager em;
    
    private Long id;

    @Inject
    private Comment comment;

    @Inject
    private Guide Guide;

    @Transactional
    public void onLoad() {
        if(id != null) {
            Guide = em.find(Guide.class, id);
            Guide.setCount(Guide.getCount() + 1);
            Collections.sort(Guide.getComments());
        } else {
            Guide = new Guide();
        }
    }

    public boolean Updatable() {
        if(Guide.getMakeBy() == null) {
            Guide.setMakeBy(userSession.getSystemUser());
        }
        return Guide.getMakeBy().getId().equals(userSession.getSystemUser().getId());
    }

    @Transactional
    public void save() throws IOException {
        try {
            if(id == null) {
                Guide.setMakeBy(userSession.getSystemUser());
                em.persist(Guide);
                em.flush();
                userSession.getSystemUser().getGuides().add(Guide);
                userSession.setSystemUser(em.merge(userSession.getSystemUser()));
            }
            else {
                userSession.setSystemUser(em.merge(userSession.getSystemUser()));
                for(SystemUser systemUser : Guide.getGradeBy()) {
                    Guide.getGradeBy().remove(systemUser);
                }
                Guide.setRank(0L);
                em.merge(Guide);
            }
        } catch (Exception ex) {
            return;
        }
        FacesContext.getCurrentInstance().getExternalContext().redirect("guide-view.xhtml?id=" + Guide.getId());
    }

    public boolean gradableGuide() {
        if(Guide.getGradeBy() == null) {
            return true;
        }
        for(SystemUser user:Guide.getGradeBy()) {
            if(user.getId().equals(userSession.getSystemUser().getId())) {
                return true;
            }
        }
        return false;
    }

    @Transactional
    public void changeRank(Comment comment, Long num) {
        comment.setRank(comment.getRank() + num);
        comment.getGradeBy().add(userSession.getSystemUser());
        em.merge(comment);
        if(num > 0) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Сообщение!", "Вы положительно оценили данный комментарий"));
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Сообщение!", "Вы отрицательно оценили данный комментарий"));
        }
    }

    @Transactional
    public void changeRank(Long num) throws IOException {
        if(num > 0) {
            Guide.setRank(Guide.getRank() + 1);
            Guide.getGradeBy().add(userSession.getSystemUser());
            em.merge(Guide);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Сообщение!", "Вы положительно оценили данный гайд"));
        }
        else{
            Guide.setRank(Guide.getRank() - 1);
            Guide.getGradeBy().add(userSession.getSystemUser());
            if (Guide.getRank() <= -10) {
                em.remove(em.contains(Guide) ? Guide : em.merge(Guide));
                FacesContext.getCurrentInstance().getExternalContext().redirect("view-guides.xhtml");
            } else {
                em.merge(Guide);
            }
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Сообщение!", "Вы отрицательно оценили данный гайд"));
        }
    }

    public boolean gradable(Comment comment) {
        if(comment.getGradeBy() == null) {
            comment.setGradeBy(new ArrayList<SystemUser>());
        }
        for(SystemUser user:comment.getGradeBy()) {
            if(user.getId().equals(userSession.getSystemUser().getId())) {
                return true;
            }
        }
        return false;
    }

    @Transactional
    public void addComment() {
        comment.setMakeBy(userSession.getSystemUser());
        em.persist(comment);
        Guide.getComments().add(comment);
        em.merge(Guide);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Comment getComment() {
        return comment;
    }

    public void setComment(Comment comment) {
        this.comment = comment;
    }

    public UserSession getUserSession() {
        return userSession;
    }

    public void setUserSession(UserSession userSession) {
        this.userSession = userSession;
    }

    public vkkononenko.models.Guide getGuide() {
        return Guide;
    }

    public void setGuide(vkkononenko.models.Guide guide) {
        Guide = guide;
    }
}
