package vkkononenko.models;

import vkkononenko.models.bases.EntityBase;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Version extends EntityBase {
    private static final long serialVersionUID = 4073670823157462669L;

    @javax.persistence.Version
    public long version = 0;

    private Double lat;

    private Double lon;

    private Integer zoom;

    @Column(columnDefinition = "text")
    private String data;

    @OneToMany(fetch = FetchType.EAGER)
    private List<Comment> comments;

    public Version() {

    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLon() {
        return lon;
    }

    public void setLon(Double lon) {
        this.lon = lon;
    }

    public Integer getZoom() {
        return zoom;
    }

    public void setZoom(Integer zoom) {
        this.zoom = zoom;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

}
