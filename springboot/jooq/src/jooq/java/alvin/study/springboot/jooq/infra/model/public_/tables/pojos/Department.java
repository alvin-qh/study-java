/*
 * This file is generated by jOOQ.
 */
package alvin.study.springboot.jooq.infra.model.public_.tables.pojos;


import java.io.Serializable;
import java.time.LocalDateTime;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Department implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private Long orgId;
    private String name;
    private Long parentId;
    private Long deleted;
    private Long createdBy;
    private Long updatedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Department() {}

    public Department(Department value) {
        this.id = value.id;
        this.orgId = value.orgId;
        this.name = value.name;
        this.parentId = value.parentId;
        this.deleted = value.deleted;
        this.createdBy = value.createdBy;
        this.updatedBy = value.updatedBy;
        this.createdAt = value.createdAt;
        this.updatedAt = value.updatedAt;
    }

    public Department(
        Long id,
        Long orgId,
        String name,
        Long parentId,
        Long deleted,
        Long createdBy,
        Long updatedBy,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
    ) {
        this.id = id;
        this.orgId = orgId;
        this.name = name;
        this.parentId = parentId;
        this.deleted = deleted;
        this.createdBy = createdBy;
        this.updatedBy = updatedBy;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /**
     * Getter for <code>PUBLIC.DEPARTMENT.ID</code>.
     */
    public Long getId() {
        return this.id;
    }

    /**
     * Setter for <code>PUBLIC.DEPARTMENT.ID</code>.
     */
    public Department setId(Long id) {
        this.id = id;
        return this;
    }

    /**
     * Getter for <code>PUBLIC.DEPARTMENT.ORG_ID</code>.
     */
    public Long getOrgId() {
        return this.orgId;
    }

    /**
     * Setter for <code>PUBLIC.DEPARTMENT.ORG_ID</code>.
     */
    public Department setOrgId(Long orgId) {
        this.orgId = orgId;
        return this;
    }

    /**
     * Getter for <code>PUBLIC.DEPARTMENT.NAME</code>.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Setter for <code>PUBLIC.DEPARTMENT.NAME</code>.
     */
    public Department setName(String name) {
        this.name = name;
        return this;
    }

    /**
     * Getter for <code>PUBLIC.DEPARTMENT.PARENT_ID</code>.
     */
    public Long getParentId() {
        return this.parentId;
    }

    /**
     * Setter for <code>PUBLIC.DEPARTMENT.PARENT_ID</code>.
     */
    public Department setParentId(Long parentId) {
        this.parentId = parentId;
        return this;
    }

    /**
     * Getter for <code>PUBLIC.DEPARTMENT.DELETED</code>.
     */
    public Long getDeleted() {
        return this.deleted;
    }

    /**
     * Setter for <code>PUBLIC.DEPARTMENT.DELETED</code>.
     */
    public Department setDeleted(Long deleted) {
        this.deleted = deleted;
        return this;
    }

    /**
     * Getter for <code>PUBLIC.DEPARTMENT.CREATED_BY</code>.
     */
    public Long getCreatedBy() {
        return this.createdBy;
    }

    /**
     * Setter for <code>PUBLIC.DEPARTMENT.CREATED_BY</code>.
     */
    public Department setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
        return this;
    }

    /**
     * Getter for <code>PUBLIC.DEPARTMENT.UPDATED_BY</code>.
     */
    public Long getUpdatedBy() {
        return this.updatedBy;
    }

    /**
     * Setter for <code>PUBLIC.DEPARTMENT.UPDATED_BY</code>.
     */
    public Department setUpdatedBy(Long updatedBy) {
        this.updatedBy = updatedBy;
        return this;
    }

    /**
     * Getter for <code>PUBLIC.DEPARTMENT.CREATED_AT</code>.
     */
    public LocalDateTime getCreatedAt() {
        return this.createdAt;
    }

    /**
     * Setter for <code>PUBLIC.DEPARTMENT.CREATED_AT</code>.
     */
    public Department setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    /**
     * Getter for <code>PUBLIC.DEPARTMENT.UPDATED_AT</code>.
     */
    public LocalDateTime getUpdatedAt() {
        return this.updatedAt;
    }

    /**
     * Setter for <code>PUBLIC.DEPARTMENT.UPDATED_AT</code>.
     */
    public Department setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
        return this;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final Department other = (Department) obj;
        if (this.id == null) {
            if (other.id != null)
                return false;
        }
        else if (!this.id.equals(other.id))
            return false;
        if (this.orgId == null) {
            if (other.orgId != null)
                return false;
        }
        else if (!this.orgId.equals(other.orgId))
            return false;
        if (this.name == null) {
            if (other.name != null)
                return false;
        }
        else if (!this.name.equals(other.name))
            return false;
        if (this.parentId == null) {
            if (other.parentId != null)
                return false;
        }
        else if (!this.parentId.equals(other.parentId))
            return false;
        if (this.deleted == null) {
            if (other.deleted != null)
                return false;
        }
        else if (!this.deleted.equals(other.deleted))
            return false;
        if (this.createdBy == null) {
            if (other.createdBy != null)
                return false;
        }
        else if (!this.createdBy.equals(other.createdBy))
            return false;
        if (this.updatedBy == null) {
            if (other.updatedBy != null)
                return false;
        }
        else if (!this.updatedBy.equals(other.updatedBy))
            return false;
        if (this.createdAt == null) {
            if (other.createdAt != null)
                return false;
        }
        else if (!this.createdAt.equals(other.createdAt))
            return false;
        if (this.updatedAt == null) {
            if (other.updatedAt != null)
                return false;
        }
        else if (!this.updatedAt.equals(other.updatedAt))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.id == null) ? 0 : this.id.hashCode());
        result = prime * result + ((this.orgId == null) ? 0 : this.orgId.hashCode());
        result = prime * result + ((this.name == null) ? 0 : this.name.hashCode());
        result = prime * result + ((this.parentId == null) ? 0 : this.parentId.hashCode());
        result = prime * result + ((this.deleted == null) ? 0 : this.deleted.hashCode());
        result = prime * result + ((this.createdBy == null) ? 0 : this.createdBy.hashCode());
        result = prime * result + ((this.updatedBy == null) ? 0 : this.updatedBy.hashCode());
        result = prime * result + ((this.createdAt == null) ? 0 : this.createdAt.hashCode());
        result = prime * result + ((this.updatedAt == null) ? 0 : this.updatedAt.hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Department (");

        sb.append(id);
        sb.append(", ").append(orgId);
        sb.append(", ").append(name);
        sb.append(", ").append(parentId);
        sb.append(", ").append(deleted);
        sb.append(", ").append(createdBy);
        sb.append(", ").append(updatedBy);
        sb.append(", ").append(createdAt);
        sb.append(", ").append(updatedAt);

        sb.append(")");
        return sb.toString();
    }
}
