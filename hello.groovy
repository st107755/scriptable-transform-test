//@Grab("org.codehaus.groovy:groovy-json")
import groovy.json.JsonSlurper
import groovy.json.JsonBuilder
import groovy.json.JsonOutput
import java.util.logging.Logger

Logger logger = Logger.getLogger("Groovy script")

def line = payload

logger.info("Here I am executing Groovy")

return "${line} has been modified"