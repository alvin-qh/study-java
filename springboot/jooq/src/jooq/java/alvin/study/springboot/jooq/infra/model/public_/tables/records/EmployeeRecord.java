/*
 * This file is generated by jOOQ.
 */
package alvin.study.springboot.jooq.infra.model.public_.tables.records;


import alvin.study.springboot.jooq.infra.model.EmployeeInfo;
import alvin.study.springboot.jooq.infra.model.public_.tables.Employee;

import java.time.LocalDateTime;

import org.jooq.Record2;
import org.jooq.impl.UpdatableRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes", "this-escape" })
public class EmployeeRecord extends UpdatableRecordImpl<EmployeeRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>PUBLIC.EMPLOYEE.ID</code>.
     */
    public EmployeeRecord setId(Long value) {
        set(0, value);
        return this;
    }

    /**
     * Getter for <code>PUBLIC.EMPLOYEE.ID</code>.
     */
    public Long getId() {
        return (Long) get(0);
    }

    /**
     * Setter for <code>PUBLIC.EMPLOYEE.ORG_ID</code>.
     */
    public EmployeeRecord setOrgId(Long value) {
        set(1, value);
        return this;
    }

    /**
     * Getter for <code>PUBLIC.EMPLOYEE.ORG_ID</code>.
     */
    public Long getOrgId() {
        return (Long) get(1);
    }

    /**
     * Setter for <code>PUBLIC.EMPLOYEE.NAME</code>.
     */
    public EmployeeRecord setName(String value) {
        set(2, value);
        return this;
    }

    /**
     * Getter for <code>PUBLIC.EMPLOYEE.NAME</code>.
     */
    public String getName() {
        return (String) get(2);
    }

    /**
     * Setter for <code>PUBLIC.EMPLOYEE.EMAIL</code>.
     */
    public EmployeeRecord setEmail(String value) {
        set(3, value);
        return this;
    }

    /**
     * Getter for <code>PUBLIC.EMPLOYEE.EMAIL</code>.
     */
    public String getEmail() {
        return (String) get(3);
    }

    /**
     * Setter for <code>PUBLIC.EMPLOYEE.TITLE</code>.
     */
    public EmployeeRecord setTitle(String value) {
        set(4, value);
        return this;
    }

    /**
     * Getter for <code>PUBLIC.EMPLOYEE.TITLE</code>.
     */
    public String getTitle() {
        return (String) get(4);
    }

    /**
     * Setter for <code>PUBLIC.EMPLOYEE.INFO</code>.
     */
    public EmployeeRecord setInfo(EmployeeInfo value) {
        set(5, value);
        return this;
    }

    /**
     * Getter for <code>PUBLIC.EMPLOYEE.INFO</code>.
     */
    public EmployeeInfo getInfo() {
        return (EmployeeInfo) get(5);
    }

    /**
     * Setter for <code>PUBLIC.EMPLOYEE.DELETED</code>.
     */
    public EmployeeRecord setDeleted(Long value) {
        set(6, value);
        return this;
    }

    /**
     * Getter for <code>PUBLIC.EMPLOYEE.DELETED</code>.
     */
    public Long getDeleted() {
        return (Long) get(6);
    }

    /**
     * Setter for <code>PUBLIC.EMPLOYEE.CREATED_BY</code>.
     */
    public EmployeeRecord setCreatedBy(Long value) {
        set(7, value);
        return this;
    }

    /**
     * Getter for <code>PUBLIC.EMPLOYEE.CREATED_BY</code>.
     */
    public Long getCreatedBy() {
        return (Long) get(7);
    }

    /**
     * Setter for <code>PUBLIC.EMPLOYEE.UPDATED_BY</code>.
     */
    public EmployeeRecord setUpdatedBy(Long value) {
        set(8, value);
        return this;
    }

    /**
     * Getter for <code>PUBLIC.EMPLOYEE.UPDATED_BY</code>.
     */
    public Long getUpdatedBy() {
        return (Long) get(8);
    }

    /**
     * Setter for <code>PUBLIC.EMPLOYEE.CREATED_AT</code>.
     */
    public EmployeeRecord setCreatedAt(LocalDateTime value) {
        set(9, value);
        return this;
    }

    /**
     * Getter for <code>PUBLIC.EMPLOYEE.CREATED_AT</code>.
     */
    public LocalDateTime getCreatedAt() {
        return (LocalDateTime) get(9);
    }

    /**
     * Setter for <code>PUBLIC.EMPLOYEE.UPDATED_AT</code>.
     */
    public EmployeeRecord setUpdatedAt(LocalDateTime value) {
        set(10, value);
        return this;
    }

    /**
     * Getter for <code>PUBLIC.EMPLOYEE.UPDATED_AT</code>.
     */
    public LocalDateTime getUpdatedAt() {
        return (LocalDateTime) get(10);
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
     * Create a detached EmployeeRecord
     */
    public EmployeeRecord() {
        super(Employee.EMPLOYEE);
    }

    /**
     * Create a detached, initialised EmployeeRecord
     */
    public EmployeeRecord(Long id, Long orgId, String name, String email, String title, EmployeeInfo info, Long deleted, Long createdBy, Long updatedBy, LocalDateTime createdAt, LocalDateTime updatedAt) {
        super(Employee.EMPLOYEE);

        setId(id);
        setOrgId(orgId);
        setName(name);
        setEmail(email);
        setTitle(title);
        setInfo(info);
        setDeleted(deleted);
        setCreatedBy(createdBy);
        setUpdatedBy(updatedBy);
        setCreatedAt(createdAt);
        setUpdatedAt(updatedAt);
        resetChangedOnNotNull();
    }

    /**
     * Create a detached, initialised EmployeeRecord
     */
    public EmployeeRecord(alvin.study.springboot.jooq.infra.model.public_.tables.pojos.Employee value) {
        super(Employee.EMPLOYEE);

        if (value != null) {
            setId(value.getId());
            setOrgId(value.getOrgId());
            setName(value.getName());
            setEmail(value.getEmail());
            setTitle(value.getTitle());
            setInfo(value.getInfo());
            setDeleted(value.getDeleted());
            setCreatedBy(value.getCreatedBy());
            setUpdatedBy(value.getUpdatedBy());
            setCreatedAt(value.getCreatedAt());
            setUpdatedAt(value.getUpdatedAt());
            resetChangedOnNotNull();
        }
    }
}
