package io.grnry.scriptable.transform.test;

import groovy.lang.GroovyClassLoader;
import groovy.lang.Script;
import groovy.util.GroovyScriptEngine;
import groovy.lang.Binding;
import groovy.util.ResourceException;
import groovy.util.ScriptException;

import junit.framework.Test;
import junit.framework.TestResult;
import org.apache.tools.ant.taskdefs.optional.junit.JUnitResultFormatter;
import org.apache.tools.ant.taskdefs.optional.junit.JUnitTest;
import org.apache.tools.ant.taskdefs.optional.junit.XMLJUnitResultFormatter;
import org.junit.runner.Description;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.*;
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

    public static void resultReport(Result result) {
        System.out.println("Finished. Result: Failures: " +
                result.getFailureCount() + ". Ignored: " +
                result.getIgnoreCount() + ". Tests run: " +
                result.getRunCount() + ". Time: " +
                result.getRunTime() + "ms.");
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
        JUnitCore junit = new JUnitCore();
        junit.addListener(new JUnitResultFormatterAsRunListener(new XMLJUnitResultFormatter()) {
            @Override
            public void testStarted(Description description) throws Exception {
                formatter.setOutput(new FileOutputStream(new File("test-result.xml")));
                super.testStarted(description);
            }
        });
        junit.run(Tests.class);
    }

    /**
     * Adopts {@link JUnitResultFormatter} into {@link RunListener},
     * and also captures stdout/stderr by intercepting the likes of {@link System#out}.
     *
     * Because Ant JUnit formatter uses one stderr/stdout per one test suite,
     * we capture each test case into a separate report file.
     */
    public static class JUnitResultFormatterAsRunListener extends RunListener {
        protected final JUnitResultFormatter formatter;
        private ByteArrayOutputStream stdout,stderr;
        private PrintStream oldStdout,oldStderr;
        private int problem;
        private long startTime;

        private JUnitResultFormatterAsRunListener(JUnitResultFormatter formatter) {
            this.formatter = formatter;
        }

        @Override
        public void testRunStarted(Description description) throws Exception {
        }

        @Override
        public void testRunFinished(Result result) throws Exception {
        }

        @Override
        public void testStarted(Description description) throws Exception {
            formatter.startTestSuite(new JUnitTest(description.getDisplayName()));
            formatter.startTest(new DescriptionAsTest(description));
            problem = 0;
            startTime = System.currentTimeMillis();

            this.oldStdout = System.out;
            //this.oldStderr = System.err;
            System.setOut(new PrintStream(stdout = new ByteArrayOutputStream()));
            //System.setErr(new PrintStream(stderr = new ByteArrayOutputStream()));
        }

        @Override
        public void testFinished(Description description) throws Exception {
            System.out.flush();
            //System.err.flush();
            System.setOut(oldStdout);
            //System.setErr(oldStderr);

            formatter.setSystemOutput(stdout.toString());
            //formatter.setSystemError(stderr.toString());
            formatter.endTest(new DescriptionAsTest(description));

            JUnitTest suite = new JUnitTest(description.getDisplayName());
            suite.setCounts(1,problem,0);
            suite.setRunTime(System.currentTimeMillis()-startTime);
            formatter.endTestSuite(suite);
        }

        @Override
        public void testFailure(Failure failure) throws Exception {
            testAssumptionFailure(failure);
        }

        @Override
        public void testAssumptionFailure(Failure failure) {
            problem++;
            formatter.addError(new DescriptionAsTest(failure.getDescription()), failure.getException());
        }

        @Override
        public void testIgnored(Description description) throws Exception {
            super.testIgnored(description);
        }
    }

    public static class DescriptionAsTest implements Test {
        private final Description description;

        public DescriptionAsTest(Description description) {
            this.description = description;
        }

        public int countTestCases() {
            return 1;
        }

        public void run(TestResult result) {
            throw new UnsupportedOperationException();
        }

        /**
         * {@link JUnitResultFormatter} determines the test name by reflection.
         */
        public String getName() {
            return description.getDisplayName();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            DescriptionAsTest that = (DescriptionAsTest) o;

            if (!description.equals(that.description)) return false;

            return true;
        }

        @Override
        public int hashCode() {
            return description.hashCode();
        }
    }
}
