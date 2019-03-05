package vkkononenko.beans;

import org.primefaces.event.CloseEvent;
import org.primefaces.event.DashboardReorderEvent;
import org.primefaces.event.ToggleEvent;
import org.primefaces.model.DashboardColumn;
import org.primefaces.model.DashboardModel;
import org.primefaces.model.DefaultDashboardColumn;
import org.primefaces.model.DefaultDashboardModel;
import vkkononenko.UserSession;
import vkkononenko.models.Grade;
import vkkononenko.models.Repository;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.Serializable;

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
            if(repository.getGrades() == null) {
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

    public Integer getCountUnreadMessages() {
        Query count = em.createQuery("select count(*) from Message message where message.to.id = :id");
        count.setParameter("id", userSession.getSystemUser().getId());
        return count.getResultList().size() - 1;
    }


    public void handleClose(CloseEvent event) {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Panel Closed", "Closed panel id:'" + event.getComponent().getId() + "'");
        addMessage(message);
    }

    public void handleToggle(ToggleEvent event) {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, event.getComponent().getId() + " toggled", "Status:" + event.getVisibility().name());
        addMessage(message);
    }

    private void addMessage(FacesMessage message) {
        FacesContext.getCurrentInstance().addMessage(null, message);
    }

    public DashboardModel getModel() {
        return model;
    }
}
