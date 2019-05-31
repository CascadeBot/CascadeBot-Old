/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.utils;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.filter.ThresholdFilter;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.joran.spi.JoranException;
import lombok.experimental.UtilityClass;
import org.slf4j.LoggerFactory;

import java.io.InputStream;

/**
 * Provides utility methods for interfacing with Logback
 */
@UtilityClass
public class LogbackUtils {

    /**
     * Sets the level of the root logger defined by {@code Logger.ROOT_LOGGER_NAME}.
     *
     * @param level What level to set for the root logger.
     * @see Logger#setLevel(Level)
     * @see LogbackUtils#getRootLogger()
     */
    public static void setRootLoggerLevel(Level level) {
        Logger root = getRootLogger();
        root.setLevel(level);
    }

    /**
     * Gets the root logger defined by {@code Logger.ROOT_LOGGER_NAME}.
     *
     * @return The root logger
     * @see LoggerFactory#getLogger(String)
     */
    public static Logger getRootLogger() {
        return (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
    }

    /**
     * Sets the level for the logger specified by the provided name.
     *
     * @param name  The name of the logger to set the level for.
     * @param level What level to set the logger at.
     * @see Logger#setLevel(Level)
     * @see LogbackUtils#getLogger(String)
     */
    public static void setLoggerLevel(String name, Level level) {
        Logger logger = getLogger(name);
        logger.setLevel(level);
    }

    /**
     * Gets a logger by the specified name from SLF4J's {@code LoggerFactory}.
     * <br><i>Warning: If the logger does not exist the LoggerFactory will just create a new logger. No exception will be thrown.</i>
     *
     * @param name The name of the logger to retrieve.
     * @return The requested logger.
     * @see LoggerFactory#getLogger(String)
     */
    public static Logger getLogger(String name) {
        return (Logger) LoggerFactory.getLogger(name);
    }

    /**
     * Sets the specified appender's level by disabling all other ThresholdFilters and programmatically adding
     * a threshold filter set at the specified level.
     *
     * @param name  The name of the appender to add to change the level of.
     * @param level The level to set the appender to.
     * @throws IllegalArgumentException If the provided name does not map to a assigned appender.
     */
    public static void setAppenderLevel(String name, Level level) {
        Logger root = getRootLogger();
        Appender<ILoggingEvent> appender = root.getAppender(name);
        if (appender != null) {
            ThresholdFilter filter = new ThresholdFilter();
            for (Filter<ILoggingEvent> eventFilter : appender.getCopyOfAttachedFiltersList()) {
                if (eventFilter instanceof ThresholdFilter) {
                    eventFilter.stop(); // Disable all other threshold filters to the filters conflicting
                }
            }
            filter.setLevel(level.toString());
            appender.addFilter(filter);
        } else {
            throw new IllegalArgumentException("The provided name does not have a defined appender");
        }
    }

    /**
     * Reloads the logback configuration from the logback.xml resource.
     *
     * @throws JoranException If the configurator encounters an issue with the config.
     * @see LogbackUtils#reloadFromConfig(InputStream)
     */
    public static void reloadFromConfig() throws JoranException {
        reloadFromConfig(LogbackUtils.class.getResourceAsStream("/logback.xml"));
    }

    /**
     * Reloads the logback configuration from the specified InputStream.
     *
     * @param inputStream The input stream to read the configuration from.
     * @throws JoranException If the configurator encounters an issue with the config.
     */
    public static void reloadFromConfig(InputStream inputStream) throws JoranException {
        LoggerContext context = getLoggerContext();
        context.reset();
        JoranConfigurator configurator = new JoranConfigurator();
        configurator.setContext(context);
        configurator.doConfigure(inputStream);
    }

    /**
     * Gets the Logback LoggerContext from the SLF4J's LoggerFactory.
     *
     * @return The LogBack logger context
     */
    public static LoggerContext getLoggerContext() {
        return (LoggerContext) LoggerFactory.getILoggerFactory();
    }


}
