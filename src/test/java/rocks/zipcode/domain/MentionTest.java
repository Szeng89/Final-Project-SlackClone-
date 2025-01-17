package rocks.zipcode.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static rocks.zipcode.domain.MentionTestSamples.*;
import static rocks.zipcode.domain.MessageTestSamples.*;

import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;
import rocks.zipcode.web.rest.TestUtil;

class MentionTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Mention.class);
        Mention mention1 = getMentionSample1();
        Mention mention2 = new Mention();
        assertThat(mention1).isNotEqualTo(mention2);

        mention2.setId(mention1.getId());
        assertThat(mention1).isEqualTo(mention2);

        mention2 = getMentionSample2();
        assertThat(mention1).isNotEqualTo(mention2);
    }

    @Test
    void messageTest() throws Exception {
        Mention mention = getMentionRandomSampleGenerator();
        Message messageBack = getMessageRandomSampleGenerator();

        mention.addMessage(messageBack);
        assertThat(mention.getMessages()).containsOnly(messageBack);
        assertThat(messageBack.getMentions()).isEqualTo(mention);

        mention.removeMessage(messageBack);
        assertThat(mention.getMessages()).doesNotContain(messageBack);
        assertThat(messageBack.getMentions()).isNull();

        mention.messages(new HashSet<>(Set.of(messageBack)));
        assertThat(mention.getMessages()).containsOnly(messageBack);
        assertThat(messageBack.getMentions()).isEqualTo(mention);

        mention.setMessages(new HashSet<>());
        assertThat(mention.getMessages()).doesNotContain(messageBack);
        assertThat(messageBack.getMentions()).isNull();
    }
}
