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
public class Org implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String name;
    private Long deleted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Org() {}

    public Org(Org value) {
        this.id = value.id;
        this.name = value.name;
        this.deleted = value.deleted;
        this.createdAt = value.createdAt;
        this.updatedAt = value.updatedAt;
    }

    public Org(
        Long id,
        String name,
        Long deleted,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
    ) {
        this.id = id;
        this.name = name;
        this.deleted = deleted;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /**
     * Getter for <code>PUBLIC.ORG.ID</code>.
     */
    public Long getId() {
        return this.id;
    }

    /**
     * Setter for <code>PUBLIC.ORG.ID</code>.
     */
    public Org setId(Long id) {
        this.id = id;
        return this;
    }

    /**
     * Getter for <code>PUBLIC.ORG.NAME</code>.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Setter for <code>PUBLIC.ORG.NAME</code>.
     */
    public Org setName(String name) {
        this.name = name;
        return this;
    }

    /**
     * Getter for <code>PUBLIC.ORG.DELETED</code>.
     */
    public Long getDeleted() {
        return this.deleted;
    }

    /**
     * Setter for <code>PUBLIC.ORG.DELETED</code>.
     */
    public Org setDeleted(Long deleted) {
        this.deleted = deleted;
        return this;
    }

    /**
     * Getter for <code>PUBLIC.ORG.CREATED_AT</code>.
     */
    public LocalDateTime getCreatedAt() {
        return this.createdAt;
    }

    /**
     * Setter for <code>PUBLIC.ORG.CREATED_AT</code>.
     */
    public Org setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    /**
     * Getter for <code>PUBLIC.ORG.UPDATED_AT</code>.
     */
    public LocalDateTime getUpdatedAt() {
        return this.updatedAt;
    }

    /**
     * Setter for <code>PUBLIC.ORG.UPDATED_AT</code>.
     */
    public Org setUpdatedAt(LocalDateTime updatedAt) {
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
        final Org other = (Org) obj;
        if (this.id == null) {
            if (other.id != null)
                return false;
        }
        else if (!this.id.equals(other.id))
            return false;
        if (this.name == null) {
            if (other.name != null)
                return false;
        }
        else if (!this.name.equals(other.name))
            return false;
        if (this.deleted == null) {
            if (other.deleted != null)
                return false;
        }
        else if (!this.deleted.equals(other.deleted))
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
        result = prime * result + ((this.name == null) ? 0 : this.name.hashCode());
        result = prime * result + ((this.deleted == null) ? 0 : this.deleted.hashCode());
        result = prime * result + ((this.createdAt == null) ? 0 : this.createdAt.hashCode());
        result = prime * result + ((this.updatedAt == null) ? 0 : this.updatedAt.hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Org (");

        sb.append(id);
        sb.append(", ").append(name);
        sb.append(", ").append(deleted);
        sb.append(", ").append(createdAt);
        sb.append(", ").append(updatedAt);

        sb.append(")");
        return sb.toString();
    }
}
