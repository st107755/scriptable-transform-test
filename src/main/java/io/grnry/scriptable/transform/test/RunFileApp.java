package io.grnry.scriptable.transform.test;

import groovy.lang.GroovyClassLoader;
import groovy.lang.Script;
import groovy.util.GroovyScriptEngine;
import groovy.lang.Binding;
import groovy.util.ResourceException;
import groovy.util.ScriptException;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Logger;

/**
 * This is the main App, it checks for the basic parameters given with the command line arguments and execute the appropriate script.
 * This tool is intended to ease development of SCDF scriptable transform objects.
 */
@SpringBootApplication
public class RunFileApp implements ApplicationRunner {

    public static void main(String[] args){
        SpringApplication.run(RunFileApp.class, args);
    }

    public boolean executeScript(){
        return true;
    }

    /**
     * This method is intended to print the help directly into the console.
     */
    public void printHelp(){
        System.out.println("-------------------------------------------------------------------------\n" +
                "---------------------------------HELP------------------------------------\n" +
                "-------------------------------------------------------------------------\n" +
                "This Java archive helps you testing your scriptable transform written in Groovy." +
                " Other languages are not supported.\n" +
                "In order to use this script please fill the following parameters:" +
                "\n" +
                "\n" +
                "--script\tfollowed by = and the path to the groovy script you would like to execute." +
                "\n" +
                "--payload\tfollowed by = and the payload you want to pass to your groovy function, which should be a string representation of the real input");
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        Logger logger = Logger.getLogger("RunFileApp");

        //Check input
        logger.finer("NonOptionArguments: " + args.getNonOptionArgs());
        logger.finer("Arguments: " + args.getOptionNames());
        Set<String> options = args.getOptionNames();
        Iterator<String> it = options.iterator();
        while (it.hasNext()  ) {
            String option = it.next();
            logger.finer("Option: " + option);
        }

        String script;
        //Get the script file
        if (args.getOptionValues("script") == null || args.getOptionValues("script").get(0) == null){
            logger.severe("Variable script not reported. See help.");
            printHelp();
            return;
        } else {
            script = args.getOptionValues("script").get(0);
        }
        logger.info("Value of script: " + script);
        //Get the payload
        String payload;
        if (args.getOptionValues("payload") == null || args.getOptionValues("payload").get(0) == null ){
            logger.severe("Variable payload not reported. See help.");
            printHelp();
            return;
        } else {
            payload = args.getOptionValues("payload").get(0);
        }

        //Get the file
        File groovyFile = new File(script);
        if(!groovyFile.isFile() || !groovyFile.canRead()){
            System.out.println("Cannot read the file " + groovyFile);
        }
        //Build the roots (the scripts to load via script engine)
        String roots[] = new String[] { "." };

        //Create the variable to bind with - and then bind the payload as variable with name payload to the binding.
        Binding bind = new Binding();
        bind.setProperty("payload", payload);

        //Instantiate the script engine
        GroovyScriptEngine engine = new GroovyScriptEngine(roots);

        //Start the script
        logger.info("Writing following payload to Groovy Script: " + payload);
        logger.info("Starting Groovy script now!");
        Object obj = engine.run(groovyFile.toURI().toURL().toString(), bind);
        logger.info("Groovy script returned: "+ obj.toString());
    }
}
