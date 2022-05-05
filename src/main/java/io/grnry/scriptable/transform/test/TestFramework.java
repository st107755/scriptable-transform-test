package io.grnry.scriptable.transform.test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.File;
import java.io.FileReader;
import java.util.logging.Logger;

public class TestFramework {
    File groovyFile;
    Logger log;

    public TestFramework(String scriptPath) {
        log = Logger.getLogger("RunFileApp");
        // Read File
        groovyFile = new File(scriptPath);
        assert groovyFile.isFile() && groovyFile.canRead();
    }


    public void printResult(String payload) {
        try {
            log.info(GroovyRunner.run(groovyFile, payload).toString());
        } catch (Exception e) {
            log.info("Failure in script execution");
        }
    }

    public JsonNode run(String payload) {
        try {
            return GroovyRunner.run(groovyFile, payload);
        } catch (Exception e) {
            return null;
        }
    }

    public String readJson(String jsonPath) {
        try {
            JSONParser parser = new JSONParser();
            JSONObject data = (JSONObject) parser.parse(
                    new FileReader(jsonPath));
            return data.toJSONString();
        } catch (Exception e) {
            log.info("Failure on json read");
            return "";
        }
    }

    public JSONObject stringToJson(String json){
        try {
            JSONParser parser = new JSONParser();
            return (JSONObject) parser.parse(json);
        } catch (Exception e){
            log.info("Failure on json read");
            return new JSONObject();
        }
    }

}
