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
 * A Channel.
 */
@Entity
@Table(name = "channel")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Channel implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "channels", "members" }, allowSetters = true)
    private Workspace workspace;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "mentions", "userProfile", "channels" }, allowSetters = true)
    private Message messages;

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "channels")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "user", "messages", "mentions", "workspaces", "channels" }, allowSetters = true)
    private Set<UserProfile> members = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Channel id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Channel name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public Channel description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Workspace getWorkspace() {
        return this.workspace;
    }

    public void setWorkspace(Workspace workspace) {
        this.workspace = workspace;
    }

    public Channel workspace(Workspace workspace) {
        this.setWorkspace(workspace);
        return this;
    }

    public Message getMessages() {
        return this.messages;
    }

    public void setMessages(Message message) {
        this.messages = message;
    }

    public Channel messages(Message message) {
        this.setMessages(message);
        return this;
    }

    public Set<UserProfile> getMembers() {
        return this.members;
    }

    public void setMembers(Set<UserProfile> userProfiles) {
        if (this.members != null) {
            this.members.forEach(i -> i.removeChannels(this));
        }
        if (userProfiles != null) {
            userProfiles.forEach(i -> i.addChannels(this));
        }
        this.members = userProfiles;
    }

    public Channel members(Set<UserProfile> userProfiles) {
        this.setMembers(userProfiles);
        return this;
    }

    public Channel addMembers(UserProfile userProfile) {
        this.members.add(userProfile);
        userProfile.getChannels().add(this);
        return this;
    }

    public Channel removeMembers(UserProfile userProfile) {
        this.members.remove(userProfile);
        userProfile.getChannels().remove(this);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Channel)) {
            return false;
        }
        return getId() != null && getId().equals(((Channel) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Channel{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", description='" + getDescription() + "'" +
            "}";
    }
}
