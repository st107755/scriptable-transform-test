package io.grnry.scriptable.transform.test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import groovy.lang.Binding;
import groovy.util.GroovyScriptEngine;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import groovy.util.ResourceException;
import groovy.util.ScriptException;
import org.json.simple.JSONObject;



public final class GroovyRunner {

    public static JsonNode run(File script, String payload) throws IOException, ScriptException, ResourceException {
        //Build the roots (the scripts to load via script engine)
        String roots[] = new String[] { "." };

        //Create the variable to bind with - and then bind the payload as variable with name payload to the binding.
        Binding bind = new Binding();
        bind.setProperty("payload", payload);

        //Instantiate the script engine
        GroovyScriptEngine engine = new GroovyScriptEngine(roots);

        //Start the script
        Object obj = engine.run(getFilePath(script), bind);
        LinkedHashMap result = (LinkedHashMap)bind.getVariable("result");
        Iterator i = result.entrySet().iterator();
        while (i.hasNext()){
            Map.Entry entry = (Map.Entry) i.next();
            if (entry.getValue() instanceof Timestamp){
                String time = result.get(entry.getKey()).toString();
                result.replace(entry.getKey(),time);
            }
        }
        JSONObject simpleJson = new JSONObject(result);
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readTree(simpleJson.toJSONString());
    }

    public static String getFilePath(File script){
        try {
            return script.toURI().toURL().toString();
        } catch (Exception e) {
            return "";
        }
    }
}
