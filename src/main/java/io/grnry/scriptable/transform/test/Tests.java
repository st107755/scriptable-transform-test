package io.grnry.scriptable.transform.test;

import com.fasterxml.jackson.databind.JsonNode;
import org.json.simple.JSONObject;
import org.junit.Before;
import org.junit.Test;

import static net.javacrumbs.jsonunit.JsonMatchers.jsonPartEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
//import static net.javacrumbs.jsonunit.JsonMatchers.*;


public class Tests {

    TestFramework framework;
    @Before
    public void setUpScript(){
        framework = new TestFramework("dp-ocs-segments/transform.groovy");
    }

    @Test
    public void firstTry(){
        // setup
        String event = framework.readJson("src/main/resources/events/test.json");
        String expected = "";
        // testrun
        JsonNode result = framework.run(event);
        // assert
        assertThat(result, jsonPartEquals("response_id","123"));
        //assertThat(result, jsonPartEquals("created_timestamp" ,));
    }
}
