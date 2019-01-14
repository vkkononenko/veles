package vkkononenko.beans;

import vkkononenko.UserSession;
import vkkononenko.models.Repository;
import vkkononenko.models.SystemUser;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.io.Serializable;
import java.util.List;

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
    private UserSession userSession;

    private List<SystemUser> systemUserList;

    private List<SystemUser> selectedUsers;

    @PersistenceContext(name = "veles")
    private EntityManager em;

    public void onLoad() {
        Query q = em.createQuery("select s from SystemUser s where s.id <> :myId");
        q.setParameter("myId", userSession.getSystemUser().getId());
        systemUserList = q.getResultList();
        if(id != null) {
            repository = em.find(Repository.class, id);
        }
    }

    @Transactional
    public void save() {
        try {
            if(id == null) {
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
    }

    @Transactional
    public void addSubscribers() {
        repository.getFollowers().addAll(selectedUsers);
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
}
