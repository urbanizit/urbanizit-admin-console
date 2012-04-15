import ch.qos.logback.classic.encoder.PatternLayoutEncoder
import ch.qos.logback.core.status.OnConsoleStatusListener
import ch.qos.logback.core.FileAppender
import ch.qos.logback.core.ConsoleAppender
import static ch.qos.logback.classic.Level.DEBUG

statusListener(OnConsoleStatusListener)


appender("FILE", FileAppender) {
  file = "urbanizit.log"
  append = true
  encoder(PatternLayoutEncoder) {
    pattern = "%level %date [%thread] %logger - %msg%n"
  }
}


appender("CONSOLE", ConsoleAppender) {
	encoder(PatternLayoutEncoder) {
		pattern = "%level %date [%thread] %logger - %msg%n"
	}
}

root(DEBUG, ["CONSOLE","FILE"])