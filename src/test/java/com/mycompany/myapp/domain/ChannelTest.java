package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.ChannelTestSamples.*;
import static com.mycompany.myapp.domain.MessageTestSamples.*;
import static com.mycompany.myapp.domain.UserProfileTestSamples.*;
import static com.mycompany.myapp.domain.WorkspaceTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class ChannelTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Channel.class);
        Channel channel1 = getChannelSample1();
        Channel channel2 = new Channel();
        assertThat(channel1).isNotEqualTo(channel2);

        channel2.setId(channel1.getId());
        assertThat(channel1).isEqualTo(channel2);

        channel2 = getChannelSample2();
        assertThat(channel1).isNotEqualTo(channel2);
    }

    @Test
    void workspaceTest() throws Exception {
        Channel channel = getChannelRandomSampleGenerator();
        Workspace workspaceBack = getWorkspaceRandomSampleGenerator();

        channel.setWorkspace(workspaceBack);
        assertThat(channel.getWorkspace()).isEqualTo(workspaceBack);

        channel.workspace(null);
        assertThat(channel.getWorkspace()).isNull();
    }

    @Test
    void messagesTest() throws Exception {
        Channel channel = getChannelRandomSampleGenerator();
        Message messageBack = getMessageRandomSampleGenerator();

        channel.setMessages(messageBack);
        assertThat(channel.getMessages()).isEqualTo(messageBack);

        channel.messages(null);
        assertThat(channel.getMessages()).isNull();
    }

    @Test
    void membersTest() throws Exception {
        Channel channel = getChannelRandomSampleGenerator();
        UserProfile userProfileBack = getUserProfileRandomSampleGenerator();

        channel.addMembers(userProfileBack);
        assertThat(channel.getMembers()).containsOnly(userProfileBack);
        assertThat(userProfileBack.getChannels()).containsOnly(channel);

        channel.removeMembers(userProfileBack);
        assertThat(channel.getMembers()).doesNotContain(userProfileBack);
        assertThat(userProfileBack.getChannels()).doesNotContain(channel);

        channel.members(new HashSet<>(Set.of(userProfileBack)));
        assertThat(channel.getMembers()).containsOnly(userProfileBack);
        assertThat(userProfileBack.getChannels()).containsOnly(channel);

        channel.setMembers(new HashSet<>());
        assertThat(channel.getMembers()).doesNotContain(userProfileBack);
        assertThat(userProfileBack.getChannels()).doesNotContain(channel);
    }
}
