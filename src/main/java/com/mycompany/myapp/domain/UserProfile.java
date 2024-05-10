package com.mycompany.myapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A UserProfile.
 */
@Entity
@Table(name = "user_profile")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class UserProfile implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "timezone")
    private String timezone;

    @Column(name = "phone")
    private String phone;

    @Column(name = "status")
    private String status;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(unique = true)
    private User user;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "userProfile")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "mentions", "userProfile", "channels" }, allowSetters = true)
    private Set<Message> messages = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "userProfile")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "userProfile", "messages" }, allowSetters = true)
    private Set<Mention> mentions = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "rel_user_profile__workspaces",
        joinColumns = @JoinColumn(name = "user_profile_id"),
        inverseJoinColumns = @JoinColumn(name = "workspaces_id")
    )
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "channels", "members" }, allowSetters = true)
    private Set<Workspace> workspaces = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "rel_user_profile__channels",
        joinColumns = @JoinColumn(name = "user_profile_id"),
        inverseJoinColumns = @JoinColumn(name = "channels_id")
    )
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "workspace", "messages", "members" }, allowSetters = true)
    private Set<Channel> channels = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public UserProfile id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullName() {
        return this.fullName;
    }

    public UserProfile fullName(String fullName) {
        this.setFullName(fullName);
        return this;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getTimezone() {
        return this.timezone;
    }

    public UserProfile timezone(String timezone) {
        this.setTimezone(timezone);
        return this;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public String getPhone() {
        return this.phone;
    }

    public UserProfile phone(String phone) {
        this.setPhone(phone);
        return this;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getStatus() {
        return this.status;
    }

    public UserProfile status(String status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public UserProfile user(User user) {
        this.setUser(user);
        return this;
    }

    public Set<Message> getMessages() {
        return this.messages;
    }

    public void setMessages(Set<Message> messages) {
        if (this.messages != null) {
            this.messages.forEach(i -> i.setUserProfile(null));
        }
        if (messages != null) {
            messages.forEach(i -> i.setUserProfile(this));
        }
        this.messages = messages;
    }

    public UserProfile messages(Set<Message> messages) {
        this.setMessages(messages);
        return this;
    }

    public UserProfile addMessages(Message message) {
        this.messages.add(message);
        message.setUserProfile(this);
        return this;
    }

    public UserProfile removeMessages(Message message) {
        this.messages.remove(message);
        message.setUserProfile(null);
        return this;
    }

    public Set<Mention> getMentions() {
        return this.mentions;
    }

    public void setMentions(Set<Mention> mentions) {
        if (this.mentions != null) {
            this.mentions.forEach(i -> i.setUserProfile(null));
        }
        if (mentions != null) {
            mentions.forEach(i -> i.setUserProfile(this));
        }
        this.mentions = mentions;
    }

    public UserProfile mentions(Set<Mention> mentions) {
        this.setMentions(mentions);
        return this;
    }

    public UserProfile addMention(Mention mention) {
        this.mentions.add(mention);
        mention.setUserProfile(this);
        return this;
    }

    public UserProfile removeMention(Mention mention) {
        this.mentions.remove(mention);
        mention.setUserProfile(null);
        return this;
    }

    public Set<Workspace> getWorkspaces() {
        return this.workspaces;
    }

    public void setWorkspaces(Set<Workspace> workspaces) {
        this.workspaces = workspaces;
    }

    public UserProfile workspaces(Set<Workspace> workspaces) {
        this.setWorkspaces(workspaces);
        return this;
    }

    public UserProfile addWorkspaces(Workspace workspace) {
        this.workspaces.add(workspace);
        return this;
    }

    public UserProfile removeWorkspaces(Workspace workspace) {
        this.workspaces.remove(workspace);
        return this;
    }

    public Set<Channel> getChannels() {
        return this.channels;
    }

    public void setChannels(Set<Channel> channels) {
        this.channels = channels;
    }

    public UserProfile channels(Set<Channel> channels) {
        this.setChannels(channels);
        return this;
    }

    public UserProfile addChannels(Channel channel) {
        this.channels.add(channel);
        return this;
    }

    public UserProfile removeChannels(Channel channel) {
        this.channels.remove(channel);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UserProfile)) {
            return false;
        }
        return getId() != null && getId().equals(((UserProfile) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "UserProfile{" +
            "id=" + getId() +
            ", fullName='" + getFullName() + "'" +
            ", timezone='" + getTimezone() + "'" +
            ", phone='" + getPhone() + "'" +
            ", status='" + getStatus() + "'" +
            "}";
    }
}
