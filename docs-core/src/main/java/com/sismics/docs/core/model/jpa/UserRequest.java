package com.sismics.docs.core.model.jpa;

import jakarta.persistence.*;
import java.util.Date;

/**
 * User registration request entity.
 */
@Entity
@Table(name = "T_USER_REQUEST")
public class UserRequest {

    /**
     * Request ID (primary key).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    /**
     * Username requested.
     */
    @Column(name = "USERNAME", nullable = false, unique = true, length = 255)
    private String username;

    /**
     * Email address.
     */
    @Column(name = "EMAIL", nullable = false, length = 255)
    private String email;

    /**
     * Reason for requesting an account.
     */
    @Column(name = "REASON", length = 1000)
    private String reason;

    /**
     * Status: pending / approved / rejected.
     */
    @Column(name = "REQUEST_STATUS", nullable = false, length = 10)
    private String status;

    /**
     * Creation timestamp.
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "CREATE_DATE", nullable = false)
    private Date createDate;

    // ======= Getters and Setters =======

    public Long getId() {
        return id;
    }

    public UserRequest setId(Long id) {
        this.id = id;
        return this;
    }

    public String getUsername() {
        return username;
    }

    public UserRequest setUsername(String username) {
        this.username = username;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public UserRequest setEmail(String email) {
        this.email = email;
        return this;
    }

    public String getReason() {
        return reason;
    }

    public UserRequest setReason(String reason) {
        this.reason = reason;
        return this;
    }

    public String getStatus() {
        return status;
    }

    public UserRequest setStatus(String status) {
        this.status = status;
        return this;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public UserRequest setCreateDate(Date createDate) {
        this.createDate = createDate;
        return this;
    }
}
