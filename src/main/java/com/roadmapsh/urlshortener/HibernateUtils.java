package com.roadmapsh.urlshortener;

public class HibernateUtils {

    private HibernateUtils () {
        // Private constructor to prevent instantiation
    }

    public static Class<?> getHibernateEffectiveClass(Object entity) {
        if (entity instanceof org.hibernate.proxy.HibernateProxy hibernateProxy) {
            return hibernateProxy.getHibernateLazyInitializer().getPersistentClass();
        } else {
            return entity.getClass();
        }
    }
}
