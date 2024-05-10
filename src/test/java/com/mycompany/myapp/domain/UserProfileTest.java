package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.ChannelTestSamples.*;
import static com.mycompany.myapp.domain.MentionTestSamples.*;
import static com.mycompany.myapp.domain.MessageTestSamples.*;
import static com.mycompany.myapp.domain.UserProfileTestSamples.*;
import static com.mycompany.myapp.domain.WorkspaceTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class UserProfileTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(UserProfile.class);
        UserProfile userProfile1 = getUserProfileSample1();
        UserProfile userProfile2 = new UserProfile();
        assertThat(userProfile1).isNotEqualTo(userProfile2);

        userProfile2.setId(userProfile1.getId());
        assertThat(userProfile1).isEqualTo(userProfile2);

        userProfile2 = getUserProfileSample2();
        assertThat(userProfile1).isNotEqualTo(userProfile2);
    }

    @Test
    void messagesTest() throws Exception {
        UserProfile userProfile = getUserProfileRandomSampleGenerator();
        Message messageBack = getMessageRandomSampleGenerator();

        userProfile.addMessages(messageBack);
        assertThat(userProfile.getMessages()).containsOnly(messageBack);
        assertThat(messageBack.getUserProfile()).isEqualTo(userProfile);

        userProfile.removeMessages(messageBack);
        assertThat(userProfile.getMessages()).doesNotContain(messageBack);
        assertThat(messageBack.getUserProfile()).isNull();

        userProfile.messages(new HashSet<>(Set.of(messageBack)));
        assertThat(userProfile.getMessages()).containsOnly(messageBack);
        assertThat(messageBack.getUserProfile()).isEqualTo(userProfile);

        userProfile.setMessages(new HashSet<>());
        assertThat(userProfile.getMessages()).doesNotContain(messageBack);
        assertThat(messageBack.getUserProfile()).isNull();
    }

    @Test
    void mentionTest() throws Exception {
        UserProfile userProfile = getUserProfileRandomSampleGenerator();
        Mention mentionBack = getMentionRandomSampleGenerator();

        userProfile.addMention(mentionBack);
        assertThat(userProfile.getMentions()).containsOnly(mentionBack);
        assertThat(mentionBack.getUserProfile()).isEqualTo(userProfile);

        userProfile.removeMention(mentionBack);
        assertThat(userProfile.getMentions()).doesNotContain(mentionBack);
        assertThat(mentionBack.getUserProfile()).isNull();

        userProfile.mentions(new HashSet<>(Set.of(mentionBack)));
        assertThat(userProfile.getMentions()).containsOnly(mentionBack);
        assertThat(mentionBack.getUserProfile()).isEqualTo(userProfile);

        userProfile.setMentions(new HashSet<>());
        assertThat(userProfile.getMentions()).doesNotContain(mentionBack);
        assertThat(mentionBack.getUserProfile()).isNull();
    }

    @Test
    void workspacesTest() throws Exception {
        UserProfile userProfile = getUserProfileRandomSampleGenerator();
        Workspace workspaceBack = getWorkspaceRandomSampleGenerator();

        userProfile.addWorkspaces(workspaceBack);
        assertThat(userProfile.getWorkspaces()).containsOnly(workspaceBack);

        userProfile.removeWorkspaces(workspaceBack);
        assertThat(userProfile.getWorkspaces()).doesNotContain(workspaceBack);

        userProfile.workspaces(new HashSet<>(Set.of(workspaceBack)));
        assertThat(userProfile.getWorkspaces()).containsOnly(workspaceBack);

        userProfile.setWorkspaces(new HashSet<>());
        assertThat(userProfile.getWorkspaces()).doesNotContain(workspaceBack);
    }

    @Test
    void channelsTest() throws Exception {
        UserProfile userProfile = getUserProfileRandomSampleGenerator();
        Channel channelBack = getChannelRandomSampleGenerator();

        userProfile.addChannels(channelBack);
        assertThat(userProfile.getChannels()).containsOnly(channelBack);

        userProfile.removeChannels(channelBack);
        assertThat(userProfile.getChannels()).doesNotContain(channelBack);

        userProfile.channels(new HashSet<>(Set.of(channelBack)));
        assertThat(userProfile.getChannels()).containsOnly(channelBack);

        userProfile.setChannels(new HashSet<>());
        assertThat(userProfile.getChannels()).doesNotContain(channelBack);
    }
}
