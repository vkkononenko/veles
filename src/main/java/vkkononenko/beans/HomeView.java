package vkkononenko.beans;

import org.apache.commons.lang3.StringUtils;
import org.primefaces.event.CloseEvent;
import org.primefaces.event.DashboardReorderEvent;
import org.primefaces.event.ToggleEvent;
import org.primefaces.model.DashboardColumn;
import org.primefaces.model.DashboardModel;
import org.primefaces.model.DefaultDashboardColumn;
import org.primefaces.model.DefaultDashboardModel;
import vkkononenko.UserSession;
import vkkononenko.models.*;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.*;
import javax.transaction.Transactional;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

@Named
@ViewScoped
public class HomeView implements Serializable {

    @PersistenceContext(name="veles")
    private EntityManager em;

    @Inject
    private UserSession userSession;

    private DashboardModel model;

    public void onLoad() {
        model = new DefaultDashboardModel();
        DashboardColumn column1 = new DefaultDashboardColumn();
        DashboardColumn column2 = new DefaultDashboardColumn();
        DashboardColumn column3 = new DefaultDashboardColumn();
        column1.addWidget("repositories");
        column2.addWidget("friends");
        column3.addWidget("needGrade");
        model.addColumn(column1);
        model.addColumn(column2);
        model.addColumn(column3);
    }

    public void handleReorder(DashboardReorderEvent event) {
        FacesMessage message = new FacesMessage();
        message.setSeverity(FacesMessage.SEVERITY_INFO);
        message.setSummary("Reordered: " + event.getWidgetId());
        message.setDetail("Item index: " + event.getItemIndex() + ", Column index: " + event.getColumnIndex() + ", Sender index: " + event.getSenderColumnIndex());

        addMessage(message);
    }

    public long getCountMyRepositories() {
        if(userSession.getSystemUser().getRepositories() == null) {
            return 0;
        }
        return userSession.getSystemUser().getRepositories().size();
    }

    public long getCountMyNeedUpdateRepositories() {
        Long count = 0L;
        if(userSession.getSystemUser().getRepositories() == null) {
            return 0;
        }
        for(Repository repository : userSession.getSystemUser().getRepositories()) {
            if(!repository.isAccepted()) {
                count++;
            }
        }
        return count;
    }

    public long getCountMyNeedGradeRepositories() {
        Long count = 0L;
        if(userSession.getSystemUser().getRepositories() == null) {
            return 0;
        }
        for(Repository repository : userSession.getSystemUser().getRepositories()) {
            if(!repository.isAccepted()) {
                count++;
            }
        }
        return count;
    }

    public long getCountMeNeedGradeRepositories() {
        Long count = 0L;
        if(userSession.getSystemUser().getNeedGrade() == null) {
            return 0;
        }
        for(Repository repository : userSession.getSystemUser().getNeedGrade()) {
            for(Grade grade : repository.getGrades()) {
                if(grade.getSystemUser().getId().equals(userSession.getSystemUser().getId()) && !grade.isAccepted()) {
                    count++;
                }
            }
        }
        return count;
    }

    @Transactional
    public Integer getCountUnreadMessages() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery cq = cb.createQuery(Message.class);
        List<Predicate> predicate_list = new LinkedList<>();
        Root<Message> root = cq.from(Message.class);
        predicate_list.add(cb.equal(root.get(Message_.read), false));
        Join<Message, SystemUser> systemUserJoin =  root.join(Message_.to, JoinType.INNER);
        predicate_list.add(cb.equal(systemUserJoin.get(SystemUser_.id), userSession.getId()));
        cq.where(predicate_list.toArray(new Predicate[0]));
        return em.createQuery(cq).getResultList().size();
    }

    private void addMessage(FacesMessage message) {
        FacesContext.getCurrentInstance().addMessage(null, message);
    }

    public DashboardModel getModel() {
        return model;
    }
}
