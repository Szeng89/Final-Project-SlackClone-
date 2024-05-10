package com.mycompany.myapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Workspace.
 */
@Entity
@Table(name = "workspace")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Workspace implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "workspace")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "workspace", "messages", "members" }, allowSetters = true)
    private Set<Channel> channels = new HashSet<>();

    @ManyToMany(fetch = FetchType.EAGER, mappedBy = "workspaces")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "user", "messages", "mentions", "workspaces", "channels" }, allowSetters = true)
    private Set<UserProfile> members = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Workspace id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Workspace name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Channel> getChannels() {
        return this.channels;
    }

    public void setChannels(Set<Channel> channels) {
        if (this.channels != null) {
            this.channels.forEach(i -> i.setWorkspace(null));
        }
        if (channels != null) {
            channels.forEach(i -> i.setWorkspace(this));
        }
        this.channels = channels;
    }

    public Workspace channels(Set<Channel> channels) {
        this.setChannels(channels);
        return this;
    }

    public Workspace addChannels(Channel channel) {
        this.channels.add(channel);
        channel.setWorkspace(this);
        return this;
    }

    public Workspace removeChannels(Channel channel) {
        this.channels.remove(channel);
        channel.setWorkspace(null);
        return this;
    }

    public Set<UserProfile> getMembers() {
        return this.members;
    }

    public void setMembers(Set<UserProfile> userProfiles) {
        if (this.members != null) {
            this.members.forEach(i -> i.removeWorkspaces(this));
        }
        if (userProfiles != null) {
            userProfiles.forEach(i -> i.addWorkspaces(this));
        }
        this.members = userProfiles;
    }

    public Workspace members(Set<UserProfile> userProfiles) {
        this.setMembers(userProfiles);
        return this;
    }

    public Workspace addMembers(UserProfile userProfile) {
        this.members.add(userProfile);
        userProfile.getWorkspaces().add(this);
        return this;
    }

    public Workspace removeMembers(UserProfile userProfile) {
        this.members.remove(userProfile);
        userProfile.getWorkspaces().remove(this);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Workspace)) {
            return false;
        }
        return getId() != null && getId().equals(((Workspace) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Workspace{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            "}";
    }
}
