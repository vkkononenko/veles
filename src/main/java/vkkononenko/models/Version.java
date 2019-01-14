package vkkononenko.models;

import vkkononenko.models.bases.EntityBase;

import javax.persistence.*;
import java.util.List;

@Entity
public class Version extends EntityBase {

    @javax.persistence.Version
    public long version;

    @Column(columnDefinition = "text")
    private String data;

    @OneToMany(fetch = FetchType.EAGER)
    private List<Grade> grades;

    public boolean isAccepted() {
        for(Grade grade : grades) {
            if(!grade.isAccepted()) {
                return false;
            }
        }
        return true;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public List<Grade> getGrade() {
        return grades;
    }

    public void setGrade(List<Grade> grade) {
        this.grades = grade;
    }

}
