package vkkononenko.beans;

import vkkononenko.UserSession;
import vkkononenko.models.Grade;
import vkkononenko.models.Repository;
import vkkononenko.models.SystemUser;
import vkkononenko.models.Version;

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
public class RepositoryView implements Serializable {

    private Long id;

    @Inject
    private Repository repository;

    @Inject
    private Version version;

    private List<Grade> grades;

    @Inject
    private UserSession userSession;

    private List<SystemUser> systemUserList;

    private List<SystemUser> selectedUsers;

    @PersistenceContext(name = "veles")
    private EntityManager em;

    private String data;

    public void onLoad() {
        grades = new ArrayList<>();
        Query q = em.createQuery("select s from SystemUser s where s.id <> :myId");
        q.setParameter("myId", userSession.getSystemUser().getId());
        systemUserList = q.getResultList();
        if(id != null) {
            repository = em.find(Repository.class, id);
        }
    }

    public boolean Updatable() {
        if(repository.getMakeBy() == null) {
            repository.setMakeBy(userSession.getSystemUser());
        }
        return repository.getMakeBy().getId().equals(userSession.getSystemUser().getId());
    }

    @Transactional
    public void save() {
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
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Сообщение", "Репозиторий успешно сохранен!"));
    }

    @Transactional
    public void removeSubscriber(SystemUser systemUser) {
        repository.getFollowers().remove(systemUser);
        em.merge(repository);
    }

    @Transactional
    public void addSubscribers() {
        repository.getFollowers().addAll(selectedUsers);
        for(SystemUser systemUser:selectedUsers) {
            Grade grade = new Grade(systemUser);
            em.persist(grade);
            grades.add(grade);
        }
        em.merge(repository);
        for(SystemUser systemUser:selectedUsers) {
            systemUser.getNeedGrade().add(repository);
            em.merge(systemUser);
        }
    }

    @Transactional
    public void addVersion() {
        Version version = new Version();
        version.setData(data);
        em.persist(version);
        repository.getVersions().add(version);
        repository.setAccepted(false);
        em.merge(repository);
        for(SystemUser systemUser:repository.getFollowers()) {
            systemUser.getNeedGrade().add(repository);
            em.merge(systemUser);
        }
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
    }

    public void setVersionForMap(Version version) throws IOException {
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

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
