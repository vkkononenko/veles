package vkkononenko.models;

import vkkononenko.models.bases.EntityBase;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Version extends EntityBase {

    @javax.persistence.Version
    public long version = 0;

    @Column(columnDefinition = "text")
    private String data;

    public Version() {

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
}
