package io.grnry.scriptable.transform.test;

import com.fasterxml.jackson.databind.JsonNode;
import org.json.simple.JSONObject;
import org.junit.Before;
import org.junit.Test;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.LinkedHashMap;

import static net.javacrumbs.jsonunit.JsonMatchers.jsonPartEquals;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
//import static net.javacrumbs.jsonunit.JsonMatchers.*;


public class Tests {

    TestFramework framework;
    @Before
    public void setUpScript(){
        String developmentPath = "segments/transform.groovy";
        String productionPath = "dp-ocs-segments/transform.groovy";
        framework = new TestFramework(developmentPath);
    }

    @Test
    public void happyCase() throws ParseException {
        String event = framework.readJson("src/main/resources/events/test.json");
        LinkedHashMap result = framework.run(event);

        assertThat(result.get("response_id"),is(123) );
        assertThat(result.get("caller"),is("caller"));
        assertThat(result.get("created_timestamp"),instanceOf(Timestamp.class));
        assertThat(result.get("error"),is(""));
    }
    @Test
    public void invalidEvent() {
        String event = "()";
        LinkedHashMap result = framework.run(event);
        assertThat(result.get("error"),is(not("")));
        assertThat(result.get("full_body"),is(event));
    }
}
