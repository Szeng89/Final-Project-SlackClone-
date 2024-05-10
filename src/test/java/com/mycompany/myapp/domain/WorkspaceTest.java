package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.ChannelTestSamples.*;
import static com.mycompany.myapp.domain.UserProfileTestSamples.*;
import static com.mycompany.myapp.domain.WorkspaceTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class WorkspaceTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Workspace.class);
        Workspace workspace1 = getWorkspaceSample1();
        Workspace workspace2 = new Workspace();
        assertThat(workspace1).isNotEqualTo(workspace2);

        workspace2.setId(workspace1.getId());
        assertThat(workspace1).isEqualTo(workspace2);

        workspace2 = getWorkspaceSample2();
        assertThat(workspace1).isNotEqualTo(workspace2);
    }

    @Test
    void channelsTest() throws Exception {
        Workspace workspace = getWorkspaceRandomSampleGenerator();
        Channel channelBack = getChannelRandomSampleGenerator();

        workspace.addChannels(channelBack);
        assertThat(workspace.getChannels()).containsOnly(channelBack);
        assertThat(channelBack.getWorkspace()).isEqualTo(workspace);

        workspace.removeChannels(channelBack);
        assertThat(workspace.getChannels()).doesNotContain(channelBack);
        assertThat(channelBack.getWorkspace()).isNull();

        workspace.channels(new HashSet<>(Set.of(channelBack)));
        assertThat(workspace.getChannels()).containsOnly(channelBack);
        assertThat(channelBack.getWorkspace()).isEqualTo(workspace);

        workspace.setChannels(new HashSet<>());
        assertThat(workspace.getChannels()).doesNotContain(channelBack);
        assertThat(channelBack.getWorkspace()).isNull();
    }

    @Test
    void membersTest() throws Exception {
        Workspace workspace = getWorkspaceRandomSampleGenerator();
        UserProfile userProfileBack = getUserProfileRandomSampleGenerator();

        workspace.addMembers(userProfileBack);
        assertThat(workspace.getMembers()).containsOnly(userProfileBack);
        assertThat(userProfileBack.getWorkspaces()).containsOnly(workspace);

        workspace.removeMembers(userProfileBack);
        assertThat(workspace.getMembers()).doesNotContain(userProfileBack);
        assertThat(userProfileBack.getWorkspaces()).doesNotContain(workspace);

        workspace.members(new HashSet<>(Set.of(userProfileBack)));
        assertThat(workspace.getMembers()).containsOnly(userProfileBack);
        assertThat(userProfileBack.getWorkspaces()).containsOnly(workspace);

        workspace.setMembers(new HashSet<>());
        assertThat(workspace.getMembers()).doesNotContain(userProfileBack);
        assertThat(userProfileBack.getWorkspaces()).doesNotContain(workspace);
    }
}
