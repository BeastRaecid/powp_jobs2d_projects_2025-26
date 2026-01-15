package edu.kis.powp.jobs2d.features;

import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import edu.kis.powp.appbase.Application;
import edu.kis.powp.jobs2d.drivers.UsageTrackingDriverDecorator;

/**
 * Provides monitoring capabilities for tracked drivers. Users can select drivers
 * with monitoring enabled from the driver menu, and use this feature to view
 * usage summaries and reset counters.
 */
public final class MonitoringFeature {

    /** Holds all registered monitored drivers by their label. */
    private static Map<String, UsageTrackingDriverDecorator> monitoredDrivers = new HashMap<>();

    /** Target logger where summaries are printed. */
    private static Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    private MonitoringFeature() {
    }

    /**
     * Sets up the Monitoring menu with actions to report usage and reset counters.
     * Called once during application startup.
     *
     * @param app               The application context.
     * @param monitoringLogger  Custom logger; if {@code null}, uses the default application logger.
     */
    public static void setupMonitoringPlugin(Application app, Logger monitoringLogger) {
        if (monitoringLogger != null) {
            logger = monitoringLogger;
        }

        app.addComponentMenu(MonitoringFeature.class, "Monitoring", 0);
        app.addComponentMenuElement(MonitoringFeature.class, "Report usage summary", MonitoringFeature::logUsage);
        app.addComponentMenuElement(MonitoringFeature.class, "Reset counters", MonitoringFeature::resetCounters);
    }

    /**
     * Registers a tracked driver so it can be monitored and reset.
     *
     * @param label  The label identifying this driver.
     * @param driver The tracked driver to register.
     */
    public static void registerMonitoredDriver(String label, UsageTrackingDriverDecorator driver) {
        monitoredDrivers.put(label, driver);
    }

    /**
     * Menu action that prints usage summaries for all monitored drivers.
     * Reports the total travel distance and drawing distance (ink/filament usage)
     * for each registered driver.
     *
     * @param e The action event triggered by the menu selection.
     */
    private static void logUsage(ActionEvent e) {
        if (monitoredDrivers.isEmpty()) {
            logger.info("Monitoring: no monitored drivers registered");
            return;
        }
        logger.info("=== Usage Summary ===");
        for (Map.Entry<String, UsageTrackingDriverDecorator> entry : monitoredDrivers.entrySet()) {
            UsageTrackingDriverDecorator driver = entry.getValue();
            logger.info(String.format("[%s] travel=%.2f, ink=%.2f",
                    driver.getLabel(), driver.getTravelDistance(), driver.getDrawingDistance()));
        }
    }

    /**
     * Menu action that resets usage counters on all monitored drivers.
     * Sets both travel distance and drawing distance to zero for each.
     *
     * @param e The action event triggered by the menu selection.
     */
    private static void resetCounters(ActionEvent e) {
        if (monitoredDrivers.isEmpty()) {
            logger.info("Monitoring: no monitored drivers registered");
            return;
        }
        for (UsageTrackingDriverDecorator driver : monitoredDrivers.values()) {
            driver.reset();
        }
        logger.info("Monitoring: all counters reset");
    }
}
