package com.sismics.docs.core.dao;

import com.sismics.docs.core.model.jpa.UserRequest;
import com.sismics.util.context.ThreadLocalContext;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.Query;
import java.util.Date;
import java.util.List;

/**
 * DAO for handling user registration requests.
 */
public class UserRequestDao {

    /**
     * Creates a new user registration request.
     *
     * @param request Request entity to persist
     */
    public void create(UserRequest request) {
        EntityManager em = ThreadLocalContext.get().getEntityManager();
        em.persist(request);
    }

    /**
     * Updates an existing user request.
     *
     * @param request Request entity to update
     */
    public void update(UserRequest request) {
        EntityManager em = ThreadLocalContext.get().getEntityManager();
        em.merge(request);
    }

    /**
     * Finds a user request by ID.
     *
     * @param id Request ID
     * @return Matching UserRequest, or null if not found
     */
    public UserRequest findById(Long id) {
        EntityManager em = ThreadLocalContext.get().getEntityManager();
        return em.find(UserRequest.class, id);
    }

    /**
     * Finds a user request by username.
     *
     * @param username Username to look for
     * @return UserRequest or null
     */
    public UserRequest findByUsername(String username) {
        EntityManager em = ThreadLocalContext.get().getEntityManager();
        try {
            Query q = em.createQuery("select r from UserRequest r where r.username = :username");
            q.setParameter("username", username);
            return (UserRequest) q.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    /**
     * Lists all registration requests (ordered by creation date descending).
     *
     * @return List of all requests
     */
    @SuppressWarnings("unchecked")
    public List<UserRequest> listAll() {
        EntityManager em = ThreadLocalContext.get().getEntityManager();
        Query q = em.createQuery("select r from UserRequest r order by r.createDate desc");
        return q.getResultList();
    }

    /**
     * Lists all pending registration requests.
     *
     * @return List of pending requests
     */
    @SuppressWarnings("unchecked")
    public List<UserRequest> listPending() {
        EntityManager em = ThreadLocalContext.get().getEntityManager();
        Query q = em.createQuery("select r from UserRequest r where r.status = 'pending' order by r.createDate desc");
        return q.getResultList();
    }

    /**
     * Soft-deletes a request (optional, not in table now).
     * Placeholder for possible future extension.
     */
    public void delete(Long id) {
        EntityManager em = ThreadLocalContext.get().getEntityManager();
        UserRequest r = em.find(UserRequest.class, id);
        if (r != null) {
            // No deleteDate field yet; just remove if needed
            em.remove(r);
        }
    }
}
