package vkkononenko.beans;

import vkkononenko.SecurityUtils;
import vkkononenko.UserSession;
import vkkononenko.models.*;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by v.kononenko on 14.01.2019.
 */
@Named
@ViewScoped
public class RepositoryView extends SecurityUtils implements Serializable {

    private Long id;

    @Inject
    private Repository repository;

    @Inject
    private Version version;

    @Inject
    private Comment comment;

    private List<Grade> grades;

    @Inject
    private UserSession userSession;

    private List<SystemUser> systemUserList;

    private List<SystemUser> selectedUsers;

    @PersistenceContext(name = "veles")
    private EntityManager em;

    private String data;

    private Double lat;

    private Double lon;

    private Integer zoom;

    @Transactional
    public void onLoad() {
        grades = new ArrayList<>();
        Query q = em.createQuery("select s from SystemUser s where s.id <> :myId");
        q.setParameter("myId", userSession.getSystemUser().getId());
        systemUserList = q.getResultList();
        if(id != null) {
            repository = em.find(Repository.class, id);
            em.refresh(repository);
        }
    }

    public boolean Updatable() {
        if(repository.getMakeBy() == null) {
            repository.setMakeBy(userSession.getSystemUser());
        }
        return repository.getMakeBy().getId().equals(userSession.getSystemUser().getId());
    }

    @Transactional
    public void save() throws IOException {
        try {
            if(id == null) {
                repository.setMakeBy(userSession.getSystemUser());
                em.persist(repository);
                em.flush();
                userSession.getSystemUser().getRepositories().add(repository);
                userSession.setSystemUser(em.merge(userSession.getSystemUser()));
            }
            else {
                em.merge(repository);
                userSession.setSystemUser(em.merge(userSession.getSystemUser()));
            }
        } catch (Exception ex) {
            return;
        }
        FacesContext.getCurrentInstance().getExternalContext().redirect("repository-view.xhtml?id=" + repository.getId());
    }

    @Transactional
    public void removeSubscriber(SystemUser systemUser) throws IOException {
        repository.getFollowers().remove(systemUser);
        systemUser.getNeedGrade().remove(repository);
        Grade grade = repository.getGradeByUser(systemUser);
        repository.getGrades().remove(grade);
        grade = em.merge(grade);
        em.remove(grade);
        em.merge(repository);
        em.merge(systemUser);
        FacesContext.getCurrentInstance().getExternalContext().redirect("repository-view.xhtml?id=" + repository.getId());
    }

    @Transactional
    public void addSubscribers() throws IOException {
        for(SystemUser follower : selectedUsers) {
            repository.getFollowers().add(follower);
        }
        for(SystemUser systemUser:selectedUsers) {
            Grade grade = new Grade(systemUser);
            em.persist(grade);
            grades.add(grade);
        }
        repository.getGrades().addAll(grades);
        em.merge(repository);
        for(SystemUser systemUser:selectedUsers) {
            if(!systemUser.getRepositories().contains(repository)) {
                systemUser.getNeedGrade().add(repository);
                em.merge(systemUser);
            }
        }
        selectedUsers = null;
        FacesContext.getCurrentInstance().getExternalContext().redirect("repository-view.xhtml?id=" + repository.getId());
    }

    @Transactional
    public void addVersion() throws IOException {
        Version version = new Version();
        version.setData(data);
        version.setLat(lat);
        version.setLon(lon);
        version.setZoom(zoom);
        em.persist(version);
        repository.getVersions().add(version);
        repository.setAccepted(false);
        em.merge(repository);
        for(SystemUser systemUser:repository.getFollowers()) {
            systemUser.getNeedGrade().add(repository);
            em.merge(systemUser);
        }
        FacesContext.getCurrentInstance().getExternalContext().redirect("repository-view.xhtml?id=" + repository.getId());
    }

    @Transactional
    public void accept() {
        for(Grade grade : repository.getGrades()) {
            if(grade.getSystemUser().getId().equals(userSession.getSystemUser().getId())) {
                grade.setAccepted(true);
                em.merge(grade);
                userSession.getSystemUser().getNeedGrade().remove(repository);
                em.merge(userSession.getSystemUser());
            }
        }
        for(Grade grade : repository.getGrades()) {
            if(!grade.isAccepted()) {
                break;
            }
            repository.setAccepted(true);
            em.merge(repository);
        }
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Сообщение!", "Вы акцептовали данный репозиторий"));
    }

    @Transactional
    public void addComment() {
        comment.setMakeBy(userSession.getSystemUser());
        em.persist(comment);
        version.getComments().add(comment);
        em.merge(version);
    }

    @Transactional
    public void up(Comment comment) {
        comment.setRank(comment.getRank() + 1);
        comment.getGradeBy().add(userSession.getSystemUser());
        em.merge(comment);
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Сообщение!", "Вы положительно оценили данный комментарий"));
    }

    @Transactional
    public void down(Comment comment) {
        comment.setRank(comment.getRank() - 1);
        comment.getGradeBy().add(userSession.getSystemUser());
        em.merge(comment);
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Сообщение!", "Вы отрицательно оценили данный комментарий"));
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

    public void setVersionForSomething(Version version) throws IOException {
        this.version = version;
    }

    public Long getVersionId() throws IOException {
        return version.getId();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Repository getRepository() {
        return repository;
    }

    public void setRepository(Repository repository) {
        this.repository = repository;
    }

    public Version getVersion() {
        return version;
    }

    public void setVersion(Version version) {
        this.version = version;
    }

    public Comment getComment() {
        return comment;
    }

    public void setComment(Comment comment) {
        this.comment = comment;
    }

    public List<Grade> getGrades() {
        return grades;
    }

    public void setGrades(List<Grade> grades) {
        this.grades = grades;
    }

    public UserSession getUserSession() {
        return userSession;
    }

    public void setUserSession(UserSession userSession) {
        this.userSession = userSession;
    }

    public List<SystemUser> getSystemUserList() {
        return systemUserList;
    }

    public void setSystemUserList(List<SystemUser> systemUserList) {
        this.systemUserList = systemUserList;
    }

    public List<SystemUser> getSelectedUsers() {
        return selectedUsers;
    }

    public void setSelectedUsers(List<SystemUser> selectedUsers) {
        this.selectedUsers = selectedUsers;
    }

    public EntityManager getEm() {
        return em;
    }

    public void setEm(EntityManager em) {
        this.em = em;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLon() {
        return lon;
    }

    public void setLon(Double lon) {
        this.lon = lon;
    }

    public Integer getZoom() {
        return zoom;
    }

    public void setZoom(Integer zoom) {
        this.zoom = zoom;
    }
}
