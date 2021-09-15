package util;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public final class HibernateUtil {

    private static EntityManagerFactory entityManagerFactory;

    private static EntityManagerFactory entityManagerTestFactory;

    public static EntityManagerFactory getEntityManagerFactory() {
        if (entityManagerFactory == null)
            entityManagerFactory = Persistence.createEntityManagerFactory("default");

        return entityManagerFactory;
    }

    public static EntityManagerFactory getEntityManagerTestFactory() {

        if(entityManagerTestFactory==null)
            entityManagerTestFactory=Persistence.createEntityManagerFactory("myTest");

        return entityManagerTestFactory;
    }
}
