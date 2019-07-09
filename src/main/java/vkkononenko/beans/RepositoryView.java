package vkkononenko.beans;

import vkkononenko.utils.ApplicationUtils;
import vkkononenko.utils.SecurityUtils;
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
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Created by v.kononenko on 14.01.2019.
 */
@Named
@ViewScoped
public class RepositoryView extends SecurityUtils implements Serializable {

    @PersistenceContext(name = "veles")
    private EntityManager em;

    private Long id;

    @Inject
    private Repository repository;

    @Inject
    private Comment comment;

    @Inject
    private Version version;

    @Inject
    private UserSession userSession;
    
    @Inject
    private ApplicationUtils applicationUtils;

    private List<Grade> grades;

    private List<SystemUser> systemUserList;

    private List<SystemUser> selectedUsers;

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
            repository.setCount(repository.getCount() + 1);
            repository.getVersions().forEach(v -> Collections.sort(v.getComments()));
            em.merge(repository);
        }
    }

    public boolean Updatable() {
        if(repository.getMakeBy() == null) {
            return true;
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
        Message message = new Message(applicationUtils.getSystemUser(), systemUser, "Пользователь ".concat(userSession.getLogin()).concat(" отписал вас от репозитория \n" +
                "http://localhost:8080/veles/repository-view.xhtml?id=").concat(Objects.toString(repository.getId())));
        em.persist(em.merge(message));
        FacesContext.getCurrentInstance().getExternalContext().redirect("repository-view.xhtml?id=" + repository.getId());
    }

    @Transactional
    public void addSubscribers() throws IOException {
        selectedUsers.stream().filter((SystemUser s) -> !repository.getFollowers().contains(s)).collect(Collectors.toList())
                .forEach((SystemUser s) -> {
                    repository.getFollowers().add(s);
                    grades.add(new Grade(s));
                    Message message = new Message(applicationUtils.getSystemUser(), s, "Пользователь ".concat(userSession.getLogin()).concat(" подписал вас на репозиторий \n"+
                            "http://localhost:8080/veles/repository-view.xhtml?id=").concat(Objects.toString(repository.getId())));
                    em.persist(em.merge(message));
                });
        selectedUsers.stream().filter((SystemUser s) -> !s.getNeedGrade().contains(repository)).collect(Collectors.toList())
                .forEach((SystemUser s) -> { s.getNeedGrade().add(repository); em.merge(s);});

        repository.getGrades().addAll(grades);
        em.merge(repository);
        selectedUsers = null;
        FacesContext.getCurrentInstance().getExternalContext().redirect("repository-view.xhtml?id=" + repository.getId());
    }

    @Transactional
    public void addVersion() throws IOException {
        Version version = new Version(data);
        repository.getVersions().add(version);
        repository.setAccepted(false);
        repository.getFollowers().stream().filter((SystemUser s) -> !(s.getNeedGrade().contains(repository)))
                .forEach((SystemUser s) -> {
            s.getNeedGrade().add(repository);
            em.merge(s);
        });
        repository.getGrades().forEach((Grade g)  -> {
            g.setAccepted(false);
            em.merge(repository);
        });
        repository.getFollowers().forEach((SystemUser s) -> {
            Message message = new Message(applicationUtils.getSystemUser(), s, "Пользователь ".concat(userSession.getLogin()).concat(" обновил репозиторий \n" +
                    "http://localhost:8080/veles/repository-view.xhtml?id=").concat(Objects.toString(repository.getId())));
            em.persist(em.merge(message));
        });
        em.merge(repository);
        FacesContext.getCurrentInstance().getExternalContext().redirect("repository-view.xhtml?id=" + repository.getId());
    }

    @Transactional
    public void accept() {
        repository.getGrades().stream().filter((Grade grade) -> grade.getSystemUser().getId().equals(userSession.getSystemUser().getId()))
                .collect(Collectors.toList()).forEach((Grade grade) -> {
            grade.setAccepted(true);
            userSession.getSystemUser().getNeedGrade().remove(repository);
            em.merge(grade);
            em.merge(userSession.getSystemUser());
            Message message = new Message(applicationUtils.getSystemUser(), repository.getMakeBy(), "Пользователь ".concat(userSession.getLogin()).concat(" акцептовал репозиторий <br>" +
                    "http://localhost:8080/veles/repository-view.xhtml?id=").concat(Objects.toString(repository.getId())));
            em.persist(em.merge(message));
        });
        repository.setAccepted(repository.getGrades().stream().filter(Grade::isAccepted).count() == repository.getGrades().size());
        em.merge(repository);
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Сообщение!", "Вы акцептовали данный репозиторий"));
    }

    @Transactional
    public void addComment() throws IOException {
        comment.setMakeBy(userSession.getSystemUser());
        version.getComments().add(comment);
        em.merge(version);
        Message message = new Message(applicationUtils.getSystemUser(), repository.getMakeBy(), "Пользователь ".concat(userSession.getLogin()).concat(" прокомментировал репозиторий \n"+
                "http://localhost:8080/veles/repository-view.xhtml?id=").concat(Objects.toString(repository.getId())));
        em.persist(em.merge(message));
        FacesContext.getCurrentInstance().getExternalContext().redirect("repository-view.xhtml?id=" + repository.getId());
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
        return !comment.getGradeBy().stream().noneMatch((SystemUser s) -> s.getId().equals(userSession.getSystemUser().getId()));
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