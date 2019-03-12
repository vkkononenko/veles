package vkkononenko.beans;

import org.apache.commons.lang3.StringUtils;
import vkkononenko.filters.RepositoryFilter;
import vkkononenko.filters.SystemUserFilter;
import vkkononenko.models.Repository;
import vkkononenko.models.Repository_;
import vkkononenko.models.SystemUser;
import vkkononenko.models.SystemUser_;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.*;
import javax.transaction.Transactional;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by v.kononenko on 12.03.2019.
 */
@Named
@ViewScoped
public class SearchView implements Serializable {

    @PersistenceContext(name = "veles")
    private EntityManager em;
    
    @Inject
    private SystemUserFilter systemUserFilter;

    @Inject
    private RepositoryFilter repositoryFilter;

    private List<SystemUser> systemUserList;

    private List<RepositoryView> repositoryList;

    public void onLoad() {
        systemUserList = new ArrayList<>();
        repositoryList = new ArrayList<>();
    }

    @Transactional
    public void searchUsers() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery cq = cb.createQuery(SystemUser.class);
        List<Predicate> predicate_list = new LinkedList<>();
        Root<SystemUser> root = cq.from(SystemUser.class);
        if(StringUtils.isNotBlank(systemUserFilter.getLogin())) {
            predicate_list.add(cb.like(cb.lower(root.get(SystemUser_.login)), "%" + systemUserFilter.getLogin().toLowerCase() + "%"));
        }
        if(StringUtils.isNotBlank(systemUserFilter.getName())) {
            predicate_list.add(cb.like(cb.lower(root.get(SystemUser_.name)), "%" + systemUserFilter.getLogin().toLowerCase() + "%"));
        }
        if(StringUtils.isNotBlank(systemUserFilter.getOrgName())) {
            predicate_list.add(cb.like(cb.lower(root.get(SystemUser_.orgName)), "%" + systemUserFilter.getLogin().toLowerCase() + "%"));
        }
        cq.where(predicate_list.toArray(new Predicate[0]));
        systemUserList = em.createQuery(cq).getResultList();
    }

    @Transactional
    public void searchRepositories() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery cq = cb.createQuery(Repository.class);
        List<Predicate> predicate_list = new LinkedList<>();
        Root<Repository> root = cq.from(Repository.class);
        if(repositoryFilter.getId() != null) {
            predicate_list.add(cb.equal(root.get(Repository_.id), repositoryFilter.getId()));
        }
        if(StringUtils.isNotBlank(repositoryFilter.getName())) {
            predicate_list.add(cb.like(cb.lower(root.get(Repository_.name)), "%" + repositoryFilter.getName().toLowerCase() + "%"));
        }
        if(StringUtils.isNotBlank(repositoryFilter.getAuthor())) {
            Join<Repository, SystemUser> systemUserJoin =  root.join(Repository_.makeBy, JoinType.INNER);;
            predicate_list.add(cb.like(cb.lower(systemUserJoin.get(SystemUser_.login)), "%" + repositoryFilter.getAuthor().toLowerCase() + "%"));
        }
        predicate_list.add(cb.equal(root.get(Repository_.secret), false));
        cq.where(predicate_list.toArray(new Predicate[0]));
        repositoryList = em.createQuery(cq).getResultList();
    }

    public void searchGuide() {

    }

    public SystemUserFilter getSystemUserFilter() {
        return systemUserFilter;
    }

    public void setSystemUserFilter(SystemUserFilter systemUserFilter) {
        this.systemUserFilter = systemUserFilter;
    }

    public RepositoryFilter getRepositoryFilter() {
        return repositoryFilter;
    }

    public void setRepositoryFilter(RepositoryFilter repositoryFilter) {
        this.repositoryFilter = repositoryFilter;
    }

    public List<SystemUser> getSystemUserList() {
        return systemUserList;
    }

    public void setSystemUserList(List<SystemUser> systemUserList) {
        this.systemUserList = systemUserList;
    }

    public List<RepositoryView> getRepositoryList() {
        return repositoryList;
    }

    public void setRepositoryList(List<RepositoryView> repositoryList) {
        this.repositoryList = repositoryList;
    }
}
