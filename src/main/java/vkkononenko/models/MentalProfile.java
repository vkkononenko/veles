package vkkononenko.models;

import vkkononenko.models.bases.EntityBase;

import javax.persistence.Entity;

@Entity
public class MentalProfile extends EntityBase {

    private String keyWord;

    private Long count = 1L;

    private Long recordId;

    private String clazz;

    public MentalProfile() {

    }

    public MentalProfile(String keyWord, Long id, String clazz) {
        this.keyWord = keyWord;
        this.recordId = id;
        this.clazz = clazz;
    }

    public MentalProfile(MentalProfile m) {
        this.keyWord = m.keyWord;
        this.clazz = m.clazz;
    }

    public String getKeyWord() {
        return keyWord;
    }

    public void setKeyWord(String keyWord) {
        this.keyWord = keyWord;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    public Long getRecordId() {
        return recordId;
    }

    public void setRecordId(Long recordId) {
        this.recordId = recordId;
    }

    public String getClazz() {
        return clazz;
    }

    public void setClazz(String clazz) {
        this.clazz = clazz;
    }
}