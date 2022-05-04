import groovy.json.JsonSlurper

import java.util.logging.Level
import java.util.logging.Logger

Logger logger = Logger.getLogger('')


String errorMsg(String existingError, String newError) {
    if (existingError) {
        return existingError.concat(' | ').concat(newError)
    } else {
        newError
    }
}

result = [:]
//DEFAULT-PLACEHOLDER
result.full_body = payload
try {
    JsonSlurper jsonSlurper = new JsonSlurper()

    def rawEvent = jsonSlurper.parseText(payload)
    logger.log(Level.INFO, 'raw Payload = ' + rawEvent.inspect());

    result.created = System.currentTimeMillis()
    // result.created_timestamp = new java.sql.Timestamp(System.currentTimeMillis())

    if (rawEvent.event != null) {
        //  def event = rawEvent.data[0]
        result.use_case = rawEvent.application.use_case
        result.timestamp_start = rawEvent.environment.timestampStart
        result.confirmation_touch_point  = rawEvent.event.body.confirmationTouchPoint
        result.confirmation_wewe  = rawEvent.event.body.confirmationWewe
    }

    result.full_body = payload

} catch (Exception ex) {
    result.error = errorMsg(result.error, '10:' + ex.getMessage())
    logger.log(Level.SEVERE, ex.getMessage(), ex)
    logger.log(Level.SEVERE, 'cink transform exception: ' + ex.getMessage())
    logger.log(Level.SEVERE, 'cink transform payload: ' + payload)
}
