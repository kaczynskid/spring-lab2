package com.example;

import static org.assertj.core.api.Assertions.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.hateoas.Resource;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@JsonTest
public class ReservationsRepresentationTest {

    @Autowired JacksonTester<Resource<Reservation>> json;

    @Test
    public void should_serialize_reservation() throws Exception {
        // given
        Reservation reservation = new Reservation("John", "Test");
        Resource<Reservation> resource = new Resource<>(reservation);

        // when
        JsonContent<Resource<Reservation>> result = json.write(resource);

        // then
        assertThat(result).extractingJsonPathStringValue("@.name").startsWith("John");
    }
}
