package com.stormerg.gbotj.services.logging.impl;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.ConsoleAppender;
import com.stormerg.gbotj.services.logging.LoggingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LoggingServiceImpl implements LoggingService {

    private static final String LOGGING_FORMAT = "{\"timestamp\":\"%d\",\"level\":\"%level\",\"thread\":\"%thread\",\"logger\":\"%logger\",\"message\":\"%msg\"}\n";
    private static final Level DEFAULT_LOG_LEVEL = Level.INFO;

    private final Logger LOGGER;

    public LoggingServiceImpl() {
        LOGGER = getLogger(LoggingServiceImpl.class);
        setGlobalLogFormatting(LOGGING_FORMAT);
        setGlobalLogLevel(null);
        LOGGER.info("LoggingServiceImpl initialized successfully.");
    }

    public Logger getLogger(final Class<?> clazz) {
        return LoggerFactory.getLogger(clazz);
    }

    public void setGlobalLogLevel(final String level) {
        Level logLevel = resolveLevel(level);

        ch.qos.logback.classic.Logger rootLogger = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        rootLogger.setLevel(logLevel);

        List<ch.qos.logback.classic.Logger> loggers = rootLogger.getLoggerContext().getLoggerList();
        for (ch.qos.logback.classic.Logger logger : loggers) {
            logger.setLevel(logLevel);
        }

        LOGGER.info("Global log level set to: {}", level == null ? DEFAULT_LOG_LEVEL : level);
    }

    public String escapeForJson(final String content) {
        return content
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replaceAll("\n", "\\\\n");
    }

    private void setGlobalLogFormatting(final String format) {
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        ch.qos.logback.classic.Logger rootLogger = loggerContext.getLogger(Logger.ROOT_LOGGER_NAME);

        // Remove existing appenders
        rootLogger.detachAndStopAllAppenders();

        // Create a new console appender
        ConsoleAppender<ILoggingEvent> consoleAppender = createConsoleAppender(loggerContext, format);

        // Add the new appender
        rootLogger.addAppender(consoleAppender);

        LOGGER.info("Global log format set to: {}", escapeForJson(format));
    }

    private ConsoleAppender<ILoggingEvent> createConsoleAppender(final LoggerContext loggerContext, String format) {
        // Create a new console appender
        ConsoleAppender<ILoggingEvent> consoleAppender = new ConsoleAppender<>();

        // Create a new pattern layout encoder
        PatternLayoutEncoder encoder = new PatternLayoutEncoder();
        encoder.setContext(loggerContext);

        // Set the desired logging format
        encoder.setPattern(format);
        encoder.start();

        // Set the encoder for the console appender
        consoleAppender.setEncoder(encoder);

        // Start the console appender
        consoleAppender.start();

        return consoleAppender;
    }

    private Level resolveLevel(final String level) {
        if (level != null) {
            return Level.toLevel(level, DEFAULT_LOG_LEVEL);
        }
        return DEFAULT_LOG_LEVEL;
    }
}
