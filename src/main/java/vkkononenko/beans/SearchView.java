package vkkononenko.beans;

import org.apache.commons.lang3.StringUtils;
import vkkononenko.utils.SecurityUtils;
import vkkononenko.filters.GuideFilter;
import vkkononenko.filters.RepositoryFilter;
import vkkononenko.filters.SystemUserFilter;
import vkkononenko.models.*;

import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.*;
import javax.transaction.Transactional;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Created by v.kononenko on 12.03.2019.
 */
@Named
@ViewScoped
public class SearchView extends SecurityUtils implements Serializable {

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
            predicate_list.add(cb.like(cb.lower(root.get(SystemUser_.name)), "%" + systemUserFilter.getName().toLowerCase() + "%"));
        }
        if(StringUtils.isNotBlank(systemUserFilter.getOrgName())) {
            predicate_list.add(cb.like(cb.lower(root.get(SystemUser_.orgName)), "%" + systemUserFilter.getOrgName().toLowerCase() + "%"));
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
        for(int j = 0; j <  repositoryList.size() - 1; j++) {
            for (int i = 0; i < repositoryList.size() - 1; i++) {
                Long first = getSumRec(repositoryList.get(i));
                Long second = getSumRec(repositoryList.get(i + 1));

                if (Repository.compare(first, second)) {
                    Repository repository = repositoryList.get(i);
                    repositoryList.set(i, repositoryList.get(i + 1));
                    repositoryList.set(i + 1, repository);
                }
            }
        }
    }

    @Transactional
    public void selectRepository(Long id) throws IOException {
        if(StringUtils.isNotBlank(repositoryFilter.getName())) {
            String [] keywords = repositoryFilter.getName().split(" ");
            List<String> keywordsList = new ArrayList<>();
            for(String key : keywords) {
                keywordsList.add(key.substring(0, key.length() - 2).trim().toLowerCase());
            }
            keywordsList.forEach((String s) -> {
                userSession.getSystemUser().getMentalProfiles().stream().filter((MentalProfile m) -> m.getRecordId().equals(id) && m.getClazz().equals(Repository.class.getSimpleName())).filter(m -> m.getKeyWord().contains(s)).collect(Collectors.toList())
                        .forEach(m -> {
                            m.setCount(m.getCount() + 1);
                            em.merge(m);
                        });
            });

            keywordsList.forEach((String s) -> {
                        if (userSession.getSystemUser().getMentalProfiles().stream().noneMatch((MentalProfile m) -> m.getKeyWord().contains(s) && m.getRecordId().equals(id) && m.getClazz().equals(Repository.class.getSimpleName()))) {
                            MentalProfile mentalProfile = new MentalProfile(s, id, Repository.class.getSimpleName());
                            em.persist(mentalProfile);
                            userSession.getSystemUser().getMentalProfiles().add(mentalProfile);
                            em.merge(userSession.getSystemUser());
                        }
                    }
            );

        }
        FacesContext.getCurrentInstance().getExternalContext().redirect("repository-view.xhtml?id=".concat(Objects.toString(id)));
    }


    public Long getSumRec(Repository repository) {
        List<MentalProfile> mentalProfiles = userSession.getSystemUser().getMentalProfiles().stream().filter((MentalProfile m) -> m.getRecordId()
                .equals(repository.getId()) && m.getClazz().equals(Repository.class.getSimpleName())).collect(Collectors.toList());
        return mentalProfiles.stream().mapToLong(MentalProfile::getCount).sum();
    }

    public Long getSumRec(Guide guide) {
        List<MentalProfile> mentalProfiles = userSession.getSystemUser().getMentalProfiles().stream().filter((MentalProfile m) -> m.getRecordId()
                .equals(guide.getId()) && m.getClazz().equals(Guide.class.getSimpleName())).collect(Collectors.toList());
        return mentalProfiles.stream().mapToLong(MentalProfile::getCount).sum();
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
        for(int j = 0; j <  guideList.size() - 1; j++) {
            for (int i = 0; i < guideList.size() - 1; i++) {
                Long first = getSumRec(guideList.get(i));
                Long second = getSumRec(guideList.get(i + 1));

                if (Guide.compare(first, second)) {
                    Guide repository = guideList.get(i);
                    guideList.set(i, guideList.get(i + 1));
                    guideList.set(i + 1, repository);
                }
            }
        }
    }

    @Transactional
    public void selectGuide(Long id) throws IOException {
        if(StringUtils.isNotBlank(guideFilter.getName())) {
            String [] keywords = guideFilter.getName().split(" ");
            List<String> keywordsList = new ArrayList<>();
            for(String key : keywords) {
                keywordsList.add(key.substring(0, key.length() - 2).trim().toLowerCase());
            }
            keywordsList.forEach((String s) -> {
                userSession.getSystemUser().getMentalProfiles().stream().filter((MentalProfile m) -> m.getRecordId().equals(id) && m.getClazz().equals(Guide.class.getSimpleName())).filter(m -> m.getKeyWord().contains(s)).collect(Collectors.toList())
                        .forEach(m -> {
                            m.setCount(m.getCount() + 1);
                            em.merge(m);
                        });
            });
            keywordsList.forEach((String s) -> {
                        if (userSession.getSystemUser().getMentalProfiles().stream().noneMatch((MentalProfile m) -> m.getKeyWord().contains(s) && m.getRecordId().equals(id) && m.getClazz().equals(Guide.class.getSimpleName()))) {
                            MentalProfile mentalProfile = new MentalProfile(s, id, Guide.class.getSimpleName());
                            em.persist(mentalProfile);
                            userSession.getSystemUser().getMentalProfiles().add(mentalProfile);
                            em.merge(userSession.getSystemUser());
                        }
                    }
            );

        }
        FacesContext.getCurrentInstance().getExternalContext().redirect("guide-view.xhtml?id=".concat(Objects.toString(id)));
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
