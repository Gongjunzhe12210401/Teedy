package com.sismics.docs.core.dao;

import com.sismics.docs.core.model.jpa.UserRequest;
import com.sismics.util.context.ThreadLocalContext;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.List;

public class UserRequestDao {
    /**
     * 创建用户注册请求
     */
    public void create(UserRequest request) {
        EntityManager em = ThreadLocalContext.get().getEntityManager();
        em.persist(request);
    }

    /**
     * 更新用户注册请求
     */
    public void update(UserRequest request) {
        EntityManager em = ThreadLocalContext.get().getEntityManager();
        em.merge(request);
    }

    /**
     * 根据 ID 查找用户注册请求
     */
    public UserRequest findById(Long id) {
        EntityManager em = ThreadLocalContext.get().getEntityManager();
        return em.find(UserRequest.class, id);
    }

    /**
     * 查询所有用户注册请求（按时间倒序）
     */
    public List<UserRequest> listAll() {
        EntityManager em = ThreadLocalContext.get().getEntityManager();
        TypedQuery<UserRequest> query = em.createQuery(
            "SELECT r FROM UserRequest r ORDER BY r.createDate DESC", UserRequest.class);
        return query.getResultList();
    }

    /**
     * 查询所有待处理的用户注册请求
     */
    public List<UserRequest> listPending() {
        EntityManager em = ThreadLocalContext.get().getEntityManager();
        TypedQuery<UserRequest> query = em.createQuery(
            "SELECT r FROM UserRequest r WHERE r.status = 'pending' ORDER BY r.createDate DESC", UserRequest.class);
        return query.getResultList();
    }
}
