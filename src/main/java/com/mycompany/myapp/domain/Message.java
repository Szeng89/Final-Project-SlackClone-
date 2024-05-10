package com.mycompany.myapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Message.
 */
@Entity
@Table(name = "message")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Message implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "uploads")
    private String uploads;

    @Column(name = "pinned")
    private Integer pinned;

    @Column(name = "timestamp")
    private Integer timestamp;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "userProfile", "messages" }, allowSetters = true)
    private Mention mentions;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "user", "messages", "mentions", "workspaces", "channels" }, allowSetters = true)
    private UserProfile userProfile;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "messages")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "workspace", "messages", "members" }, allowSetters = true)
    private Set<Channel> channels = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Message id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUploads() {
        return this.uploads;
    }

    public Message uploads(String uploads) {
        this.setUploads(uploads);
        return this;
    }

    public void setUploads(String uploads) {
        this.uploads = uploads;
    }

    public Integer getPinned() {
        return this.pinned;
    }

    public Message pinned(Integer pinned) {
        this.setPinned(pinned);
        return this;
    }

    public void setPinned(Integer pinned) {
        this.pinned = pinned;
    }

    public Integer getTimestamp() {
        return this.timestamp;
    }

    public Message timestamp(Integer timestamp) {
        this.setTimestamp(timestamp);
        return this;
    }

    public void setTimestamp(Integer timestamp) {
        this.timestamp = timestamp;
    }

    public Mention getMentions() {
        return this.mentions;
    }

    public void setMentions(Mention mention) {
        this.mentions = mention;
    }

    public Message mentions(Mention mention) {
        this.setMentions(mention);
        return this;
    }

    public UserProfile getUserProfile() {
        return this.userProfile;
    }

    public void setUserProfile(UserProfile userProfile) {
        this.userProfile = userProfile;
    }

    public Message userProfile(UserProfile userProfile) {
        this.setUserProfile(userProfile);
        return this;
    }

    public Set<Channel> getChannels() {
        return this.channels;
    }

    public void setChannels(Set<Channel> channels) {
        if (this.channels != null) {
            this.channels.forEach(i -> i.setMessages(null));
        }
        if (channels != null) {
            channels.forEach(i -> i.setMessages(this));
        }
        this.channels = channels;
    }

    public Message channels(Set<Channel> channels) {
        this.setChannels(channels);
        return this;
    }

    public Message addChannel(Channel channel) {
        this.channels.add(channel);
        channel.setMessages(this);
        return this;
    }

    public Message removeChannel(Channel channel) {
        this.channels.remove(channel);
        channel.setMessages(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Message)) {
            return false;
        }
        return getId() != null && getId().equals(((Message) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Message{" +
            "id=" + getId() +
            ", uploads='" + getUploads() + "'" +
            ", pinned=" + getPinned() +
            ", timestamp=" + getTimestamp() +
            "}";
    }
}
