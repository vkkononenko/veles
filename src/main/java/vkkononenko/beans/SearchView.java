package vkkononenko.beans;

import org.apache.commons.lang3.StringUtils;
import vkkononenko.filters.GuideFilter;
import vkkononenko.filters.RepositoryFilter;
import vkkononenko.filters.SystemUserFilter;
import vkkononenko.models.*;

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

    @Inject
    private GuideFilter guideFilter;

    private List<SystemUser> systemUserList;

    private List<Repository> repositoryList;

    private List<Guide> guideList;


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

    @Transactional
    public void searchGuide() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery cq = cb.createQuery(Guide.class);
        List<Predicate> predicate_list = new LinkedList<>();
        Root<Guide> root = cq.from(Guide.class);
        if(guideFilter.getId() != null) {
            predicate_list.add(cb.equal(root.get(Guide_.id), guideFilter.getId()));
        }
        if(StringUtils.isNotBlank(guideFilter.getName())) {
            predicate_list.add(cb.like(cb.lower(root.get(Guide_.name)), "%" + guideFilter.getName().toLowerCase() + "%"));
        }
        if(StringUtils.isNotBlank(guideFilter.getAuthor())) {
            Join<Guide, SystemUser> systemUserJoin =  root.join(Guide_.makeBy, JoinType.INNER);;
            predicate_list.add(cb.like(cb.lower(systemUserJoin.get(SystemUser_.login)), "%" + guideFilter.getAuthor().toLowerCase() + "%"));
        }
        if(StringUtils.isNotBlank(guideFilter.getInclude())) {
            String []keywords = guideFilter.getInclude().split(" ");
            for(String keyword : keywords) {
                predicate_list.add(cb.like(cb.lower(root.get(Guide_.text)), "%" + keyword + "%"));
            }
        }
        cq.where(predicate_list.toArray(new Predicate[0]));
        guideList = em.createQuery(cq).getResultList();
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

    public GuideFilter getGuideFilter() {
        return guideFilter;
    }

    public void setGuideFilter(GuideFilter guideFilter) {
        this.guideFilter = guideFilter;
    }

    public List<SystemUser> getSystemUserList() {
        return systemUserList;
    }

    public void setSystemUserList(List<SystemUser> systemUserList) {
        this.systemUserList = systemUserList;
    }

    public List<Repository> getRepositoryList() {
        return repositoryList;
    }

    public void setRepositoryList(List<Repository> repositoryList) {
        this.repositoryList = repositoryList;
    }

    public List<Guide> getGuideList() {
        return guideList;
    }

    public void setGuideList(List<Guide> guideList) {
        this.guideList = guideList;
    }
}
