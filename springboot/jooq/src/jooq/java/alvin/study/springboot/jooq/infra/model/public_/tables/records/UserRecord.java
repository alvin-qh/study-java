/*
 * This file is generated by jOOQ.
 */
package alvin.study.springboot.jooq.infra.model.public_.tables.records;


import alvin.study.springboot.jooq.infra.model.UserType;
import alvin.study.springboot.jooq.infra.model.public_.tables.User;

import java.time.LocalDateTime;

import org.jooq.Record2;
import org.jooq.impl.UpdatableRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes", "this-escape" })
public class UserRecord extends UpdatableRecordImpl<UserRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>PUBLIC.USER.ID</code>.
     */
    public UserRecord setId(Long value) {
        set(0, value);
        return this;
    }

    /**
     * Getter for <code>PUBLIC.USER.ID</code>.
     */
    public Long getId() {
        return (Long) get(0);
    }

    /**
     * Setter for <code>PUBLIC.USER.ORG_ID</code>.
     */
    public UserRecord setOrgId(Long value) {
        set(1, value);
        return this;
    }

    /**
     * Getter for <code>PUBLIC.USER.ORG_ID</code>.
     */
    public Long getOrgId() {
        return (Long) get(1);
    }

    /**
     * Setter for <code>PUBLIC.USER.ACCOUNT</code>.
     */
    public UserRecord setAccount(String value) {
        set(2, value);
        return this;
    }

    /**
     * Getter for <code>PUBLIC.USER.ACCOUNT</code>.
     */
    public String getAccount() {
        return (String) get(2);
    }

    /**
     * Setter for <code>PUBLIC.USER.PASSWORD</code>.
     */
    public UserRecord setPassword(String value) {
        set(3, value);
        return this;
    }

    /**
     * Getter for <code>PUBLIC.USER.PASSWORD</code>.
     */
    public String getPassword() {
        return (String) get(3);
    }

    /**
     * Setter for <code>PUBLIC.USER.TYPE</code>.
     */
    public UserRecord setType(UserType value) {
        set(4, value);
        return this;
    }

    /**
     * Getter for <code>PUBLIC.USER.TYPE</code>.
     */
    public UserType getType() {
        return (UserType) get(4);
    }

    /**
     * Setter for <code>PUBLIC.USER.DELETED</code>.
     */
    public UserRecord setDeleted(Long value) {
        set(5, value);
        return this;
    }

    /**
     * Getter for <code>PUBLIC.USER.DELETED</code>.
     */
    public Long getDeleted() {
        return (Long) get(5);
    }

    /**
     * Setter for <code>PUBLIC.USER.CREATED_BY</code>.
     */
    public UserRecord setCreatedBy(Long value) {
        set(6, value);
        return this;
    }

    /**
     * Getter for <code>PUBLIC.USER.CREATED_BY</code>.
     */
    public Long getCreatedBy() {
        return (Long) get(6);
    }

    /**
     * Setter for <code>PUBLIC.USER.UPDATED_BY</code>.
     */
    public UserRecord setUpdatedBy(Long value) {
        set(7, value);
        return this;
    }

    /**
     * Getter for <code>PUBLIC.USER.UPDATED_BY</code>.
     */
    public Long getUpdatedBy() {
        return (Long) get(7);
    }

    /**
     * Setter for <code>PUBLIC.USER.CREATED_AT</code>.
     */
    public UserRecord setCreatedAt(LocalDateTime value) {
        set(8, value);
        return this;
    }

    /**
     * Getter for <code>PUBLIC.USER.CREATED_AT</code>.
     */
    public LocalDateTime getCreatedAt() {
        return (LocalDateTime) get(8);
    }

    /**
     * Setter for <code>PUBLIC.USER.UPDATED_AT</code>.
     */
    public UserRecord setUpdatedAt(LocalDateTime value) {
        set(9, value);
        return this;
    }

    /**
     * Getter for <code>PUBLIC.USER.UPDATED_AT</code>.
     */
    public LocalDateTime getUpdatedAt() {
        return (LocalDateTime) get(9);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record2<Long, Long> key() {
        return (Record2) super.key();
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached UserRecord
     */
    public UserRecord() {
        super(User.USER);
    }

    /**
     * Create a detached, initialised UserRecord
     */
    public UserRecord(Long id, Long orgId, String account, String password, UserType type, Long deleted, Long createdBy, Long updatedBy, LocalDateTime createdAt, LocalDateTime updatedAt) {
        super(User.USER);

        setId(id);
        setOrgId(orgId);
        setAccount(account);
        setPassword(password);
        setType(type);
        setDeleted(deleted);
        setCreatedBy(createdBy);
        setUpdatedBy(updatedBy);
        setCreatedAt(createdAt);
        setUpdatedAt(updatedAt);
        resetChangedOnNotNull();
    }

    /**
     * Create a detached, initialised UserRecord
     */
    public UserRecord(alvin.study.springboot.jooq.infra.model.public_.tables.pojos.User value) {
        super(User.USER);

        if (value != null) {
            setId(value.getId());
            setOrgId(value.getOrgId());
            setAccount(value.getAccount());
            setPassword(value.getPassword());
            setType(value.getType());
            setDeleted(value.getDeleted());
            setCreatedBy(value.getCreatedBy());
            setUpdatedBy(value.getUpdatedBy());
            setCreatedAt(value.getCreatedAt());
            setUpdatedAt(value.getUpdatedAt());
            resetChangedOnNotNull();
        }
    }
}
