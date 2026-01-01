package com.roadmapsh.urlshortener.models;

import com.roadmapsh.urlshortener.HibernateUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "urlshortener")
@Getter
@Setter
@ToString
public class UrlShortener {
    @Id
    private String shortCode;
    private String url;
    private LocalDateTime creationDate;
    private LocalDateTime updatedDate;
    private int accessedTimes;

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        if (HibernateUtils.getHibernateEffectiveClass(this) != HibernateUtils.getHibernateEffectiveClass(o)) return false;
        UrlShortener that = (UrlShortener) o;
        return getShortCode() != null && Objects.equals(getShortCode(), that.getShortCode());
    }

    @Override
    public final int hashCode() {
        return HibernateUtils.getHibernateEffectiveClass(this).hashCode();
    }

}
