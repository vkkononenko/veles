package vkkononenko.models.bases;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@MappedSuperclass
public class EntityBase implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Temporal(TemporalType.DATE)
    @Column(name = "date_created", updatable = false)
    private Date dateCreated;

    @Temporal(TemporalType.DATE)
    @Column(name = "date_update", updatable = true)
    private Date dateUpdated;

    @PrePersist
    public void init() {
        dateCreated = new Date();
        dateUpdated = new Date();
    }

    @PreUpdate
    public void update() {
        dateUpdated = new Date();
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Date getDateUpdated() {
        return dateUpdated;
    }

    public void setDateUpdated(Date dateUpdated) {
        this.dateUpdated = dateUpdated;
    }
}
