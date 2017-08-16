package org.wildfly.maven.plugins.quickstart.documentation.drupal.json.hal;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Jason Porter <jporter@redhat.com>
 *         Copyright 2017 Red Hat, Inc. and/or its affiliates.
 */
public class TagDeserializerTest {
    @Test
    public void assertDeserializationWorks() throws IOException {
        String taxonomyTagJson = "{\"name\":\".NET\",\"uuid\":\"89760308-a111-4528-88ac-0358289a93f6\",\"tid\":\"29\"}";

        ObjectMapper mapper = new ObjectMapper();
        Tag deserializedTag = mapper.readValue(taxonomyTagJson, Tag.class);

        Tag manualTag = new Tag("89760308-a111-4528-88ac-0358289a93f6", "29", "en", ".NET");

        assertThat(manualTag).isEqualTo(deserializedTag);
    }

    @Test
    public void assertCanFindInAList() throws IOException {
        String json = "[{\"name\":\".NET\",\"uuid\":\"89760308-a111-4528-88ac-0358289a93f6\",\"tid\":\"29\"},{\"name\":\".NET Core\",\"uuid\":\"8700c398-bccd-45d1-ab87-5fd2ca7e722c\",\"tid\":\"8445\"},{\"name\":\".net framework\",\"uuid\":\"caec5dec-909b-4620-949b-6c1e44dcf638\",\"tid\":\"705\"},{\"name\":\"2013 summit\",\"uuid\":\"46c1677b-11c9-482f-80d7-5e09182f0c96\",\"tid\":\"715\"},{\"name\":\"a-mq\",\"uuid\":\"3c40f7f5-f281-419f-8bdd-f0f930ead9c1\",\"tid\":\"725\"},{\"name\":\"A-MQ 7 Beta\",\"uuid\":\"47970641-eee8-49fb-81a5-7af487f11834\",\"tid\":\"515\"},{\"name\":\"abroo shah\",\"uuid\":\"80466dd7-38fc-4f04-9d0b-e98015200100\",\"tid\":\"735\"},{\"name\":\"accenture\",\"uuid\":\"d666672f-945c-4a5f-a285-4b300e7f0a10\",\"tid\":\"745\"},{\"name\":\"activemq\",\"uuid\":\"75cb842d-cbd3-446d-a284-5f194c3dfb01\",\"tid\":\"755\"},{\"name\":\"adaptive\",\"uuid\":\"1adbf19b-332d-42b3-97c9-ac2082e1e1b8\",\"tid\":\"765\"},{\"name\":\"admin\",\"uuid\":\"b19022cf-adaf-4ca6-93ec-be953849d280\",\"tid\":\"775\"},{\"name\":\"administration\",\"uuid\":\"2371d00e-9fd2-47d0-a6cf-a540b5e8169f\",\"tid\":\"785\"}]";
        Tag manualTag = new Tag("89760308-a111-4528-88ac-0358289a93f6", "29", "en", ".NET");

        ObjectMapper mapper = new ObjectMapper();
        List<Tag> tags = mapper.readValue(json, new TypeReference<List<Tag>>() {
        });

        assertThat(tags.contains(manualTag)).isTrue();
        assertThat(tags.stream().filter(tag -> tag.getName().equals(manualTag.getName())).count() == 1).isTrue();
    }
}
