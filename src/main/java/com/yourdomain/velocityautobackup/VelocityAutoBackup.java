package com.yourdomain.velocityautobackup;

import com.google.inject.Inject;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Plugin(
        id = "velocityautobackup",
        name = "Velocity Auto Backup",
        version = "1.0-SNAPSHOT",
        description = "Automatically backs up the entire proxy server to Google Drive.",
        authors = {"YourName"}
)
public class VelocityAutoBackup {

    private final ProxyServer server;
    private final Logger logger;
    private final Path dataDirectory;
    private SimpleConfig config;

    @Inject
    public VelocityAutoBackup(ProxyServer server, Logger logger, @DataDirectory Path dataDirectory) {
        this.server = server;
        this.logger = logger;
        this.dataDirectory = dataDirectory;
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        this.config = loadConfig();
        if (config == null) {
            logger.error("Could not load configuration. The plugin will not run.");
            return;
        }

        scheduleBackups();

        CommandManager commandManager = server.getCommandManager();
        CommandMeta backupNowMeta = commandManager.metaBuilder("backup-now")
                .aliases("bnow")
                .build();
        commandManager.register(backupNowMeta, new BackupNowCommand(this, server, logger, dataDirectory));
        logger.info("'/backup-now' command has been successfully registered.");
    }

    private void scheduleBackups() {
        List<String> backupTimes = config.getStringList("backup-times");
        if (backupTimes.isEmpty()) {
            logger.warn("No backup times are set in 'backup-times' in config.yml. Automatic backups will not run.");
            return;
        }

        String timezone = config.getString("timezone", "UTC");
        ZoneId zoneId;
        try {
            zoneId = ZoneId.of(timezone);
        } catch (Exception e) {
            logger.error("Invalid timezone ID '{}'. Defaulting to 'UTC'.", timezone);
            zoneId = ZoneId.of("UTC");
        }

        for (String timeStr : backupTimes) {
            try {
                String[] parts = timeStr.split(":");
                if (parts.length != 2) throw new DateTimeParseException("Invalid time format", timeStr, 0);

                int hour = Integer.parseInt(parts[0]);
                int minute = Integer.parseInt(parts[1]);

                ZonedDateTime now = ZonedDateTime.now(zoneId);
                ZonedDateTime nextRun = now.withHour(hour).withMinute(minute).withSecond(0).withNano(0);

                if (now.isAfter(nextRun)) {
                    nextRun = nextRun.plusDays(1);
                }

                Duration initialDelay = Duration.between(now, nextRun);
                long initialDelaySeconds = initialDelay.getSeconds();
                long repeatPeriodSeconds = TimeUnit.DAYS.toSeconds(1);

                logger.info("Automatic backup scheduled for '{}' (next run in {} hours and {} minutes).", timeStr, initialDelay.toHours(), initialDelay.toMinutesPart());

                server.getScheduler().buildTask(this, new BackupTask(logger, dataDirectory, config))
                        .delay(initialDelaySeconds, TimeUnit.SECONDS)
                        .repeat(repeatPeriodSeconds, TimeUnit.SECONDS)
                        .schedule();

            } catch (DateTimeParseException | NumberFormatException e) {
                logger.error("Invalid time format '{}' in 'backup-times'. Should be 'HH:mm'. Skipping this schedule.", timeStr);
            }
        }
    }

    public SimpleConfig loadConfig() {
        try {
            if (Files.notExists(dataDirectory)) {
                Files.createDirectories(dataDirectory);
            }
            Path configFile = dataDirectory.resolve("config.yml");
            if (Files.notExists(configFile)) {
                try (InputStream in = getClass().getClassLoader().getResourceAsStream("config.yml")) {
                    if (in != null) {
                        Files.copy(in, configFile);
                    } else {
                        logger.error("Could not find default config.yml inside the JAR.");
                        return null;
                    }
                }
            }
            return new SimpleConfig(configFile, logger);
        } catch (IOException e) {
            logger.error("Failed to load or create config file!", e);
            return null;
        }
    }
}