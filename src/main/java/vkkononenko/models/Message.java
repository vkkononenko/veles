package vkkononenko.models;

import vkkononenko.models.bases.EntityBase;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

/**
 * Created by v.kononenko on 05.03.2019.
 */
@Entity
public class Message extends EntityBase {

    @ManyToOne
    private SystemUser from;

    @ManyToOne
    private SystemUser to;

    @Column(columnDefinition = "text")
    private String text;

    private boolean read = false;

    public Message() {

    }

    public Message(SystemUser from, SystemUser to, String text) {
        this.from = from;
        this.to = to;
        this.text = text;
    }

    public SystemUser getFrom() {
        return from;
    }

    public void setFrom(SystemUser from) {
        this.from = from;
    }

    public SystemUser getTo() {
        return to;
    }

    public void setTo(SystemUser to) {
        this.to = to;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }
}
