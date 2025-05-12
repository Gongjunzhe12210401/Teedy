package com.sismics.docs.core.service;

import com.sismics.docs.core.constant.Constants;
import com.sismics.docs.core.dao.UserDao;
import com.sismics.docs.core.dao.UserRequestDao;
import com.sismics.docs.core.model.jpa.User;
import com.sismics.docs.core.model.jpa.UserRequest;
import com.sismics.docs.core.util.EncryptionUtil;


import java.util.Date;
import java.util.List;
import java.util.UUID;

public class UserRequestService {

    private final UserRequestDao dao = new UserRequestDao();
    private final UserDao userDao = new UserDao();

    /**
     * 访客提交注册申请
     */
    public void submitRequest(String username, String email, String reason) {
        UserRequest request = new UserRequest();
        request.setUsername(username);
        request.setEmail(email);
        request.setReason(reason);
        request.setStatus("pending");
        request.setCreateDate(new Date());

        dao.create(request);
    }

    /**
     * 管理员批准某个请求并创建用户
     */
    public void approveRequest(Long requestId) {
        UserRequest request = dao.findById(requestId);
        if (request != null && "pending".equals(request.getStatus())) {
            try {
                // 检查用户名是否已存在
                if (userDao.getActiveByUsername(request.getUsername()) != null) {
                    throw new RuntimeException("用户名已存在，无法创建用户: " + request.getUsername());
                }
    
                // 创建用户
                User user = new User();
                user.setId(UUID.randomUUID().toString());
                user.setRoleId(Constants.DEFAULT_USER_ROLE);
                user.setUsername(request.getUsername());
                user.setEmail(request.getEmail());
                user.setPassword("default123"); 
                user.setCreateDate(new Date());
                user.setPrivateKey(EncryptionUtil.generatePrivateKey());
                user.setStorageCurrent(0L);
                user.setStorageQuota(100_000L);
                user.setOnboarding(true);
    
                // 使用固定管理员 ID 创建用户
                userDao.create(user, "admin");
    
                // 更新注册请求状态
                request.setStatus("approved");
                dao.update(request);
    
            } catch (Exception e) {
                throw new RuntimeException("创建用户失败: " + e.getMessage(), e);
            }
        }
    }
    

    /**
     * 管理员拒绝某个请求
     */
    public void rejectRequest(Long requestId) {
        UserRequest request = dao.findById(requestId);
        if (request != null && "pending".equals(request.getStatus())) {
            request.setStatus("rejected");
            dao.update(request);
        }
    }

    /**
     * 获取所有待审核的注册请求
     */
    public List<UserRequest> getPendingRequests() {
        return dao.listPending();
    }

    /**
     * 获取所有注册请求
     */
    public List<UserRequest> getAllRequests() {
        return dao.listAll();
    }
}
