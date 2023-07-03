package com.tutorial.restful.api.entitymanagerfactory;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class EntityManagerFacotryTest {

    @Autowired
    EntityManagerFactory entityManagerFactory;

    @Test
    void testEntityManagerFactorySuccess(){
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction entityTransaction = entityManager.getTransaction();
        entityTransaction.begin();

        Assertions.assertNotNull(entityManager);
        Assertions.assertNotNull(entityTransaction);

        entityTransaction.commit();
        entityManager.close();
    }

}
