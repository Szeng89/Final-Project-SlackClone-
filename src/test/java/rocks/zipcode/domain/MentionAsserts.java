package rocks.zipcode.domain;

import static org.assertj.core.api.Assertions.assertThat;

public class MentionAsserts {

    /**
     * Asserts that the entity has all properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertMentionAllPropertiesEquals(Mention expected, Mention actual) {
        assertMentionAutoGeneratedPropertiesEquals(expected, actual);
        assertMentionAllUpdatablePropertiesEquals(expected, actual);
    }

    /**
     * Asserts that the entity has all updatable properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertMentionAllUpdatablePropertiesEquals(Mention expected, Mention actual) {
        assertMentionUpdatableFieldsEquals(expected, actual);
        assertMentionUpdatableRelationshipsEquals(expected, actual);
    }

    /**
     * Asserts that the entity has all the auto generated properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertMentionAutoGeneratedPropertiesEquals(Mention expected, Mention actual) {
        assertThat(expected)
            .as("Verify Mention auto generated properties")
            .satisfies(e -> assertThat(e.getId()).as("check id").isEqualTo(actual.getId()));
    }

    /**
     * Asserts that the entity has all the updatable fields set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertMentionUpdatableFieldsEquals(Mention expected, Mention actual) {
        assertThat(expected)
            .as("Verify Mention relevant properties")
            .satisfies(e -> assertThat(e.getUserName()).as("check userName").isEqualTo(actual.getUserName()))
            .satisfies(e -> assertThat(e.getText()).as("check text").isEqualTo(actual.getText()));
    }

    /**
     * Asserts that the entity has all the updatable relationships set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertMentionUpdatableRelationshipsEquals(Mention expected, Mention actual) {}
}
