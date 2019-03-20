package vkkononenko.beans;

import org.primefaces.event.SelectEvent;
import vkkononenko.SecurityUtils;
import vkkononenko.UserSession;
import vkkononenko.models.Message;
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
import java.io.IOException;
import java.io.Serializable;
import java.util.List;

/**
 * Created by v.kononenko on 11.03.2019.
 */
@Named
@ViewScoped
public class ViewMyMessages extends SecurityUtils implements Serializable {
    @PersistenceContext(name = "veles")
    private EntityManager em;

    @Inject
    private UserSession userSession;

    private List<Message> inputMessages;

    private List<Message> outputMessages;

    private String text;

    private Message selected;

    public void onLoad() {
        Query input = em.createQuery("SELECT m from Message m  where m.to.id = :inputId ORDER BY m.read DESC, m.dateCreated DESC");
        input.setParameter("inputId", userSession.getSystemUser().getId());
        inputMessages = input.getResultList();

        Query output = em.createQuery("select m from Message m where m.from.id = :outputId order by m.dateCreated DESC");
        output.setParameter("outputId", userSession.getSystemUser().getId());
        outputMessages = output.getResultList();
    }

    @Transactional
    public void sendMessageTo(Message answer) {
        answer.setRead(true);
        Message message = new Message(userSession.getSystemUser(), answer.getFrom(), text);
        em.persist(message);
        em.merge(answer);
        text = "";
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Сообщение!", "Сообщение успешно отправлено"));
    }

    @Transactional
    public void deleteMessage(Message message, boolean forMe) {
        if(forMe) {
            message.setTo(null);
            inputMessages.remove(message);
        } else {
            message.setFrom(null);
            outputMessages.remove(message);
        }
        em.merge(message);
    }

    @Transactional
    public void onRowSelect(SelectEvent event) throws IOException {
        this.selected = (Message) event.getObject();
        selected.setRead(true);
        em.merge(selected);
    }

    public List<Message> getInputMessages() {
        return inputMessages;
    }

    public void setInputMessages(List<Message> inputMessages) {
        this.inputMessages = inputMessages;
    }

    public List<Message> getOutputMessages() {
        return outputMessages;
    }

    public void setOutputMessages(List<Message> outputMessages) {
        this.outputMessages = outputMessages;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Message getSelected() {
        return selected;
    }

    public void setSelected(Message selected) {
        this.selected = selected;
    }

}
