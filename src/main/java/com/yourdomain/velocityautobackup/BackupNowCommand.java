package com.yourdomain.velocityautobackup;

import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.ProxyServer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.slf4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.GeneralSecurityException;

public class BackupNowCommand implements SimpleCommand {

    private final Logger logger;
    private final ProxyServer server;
    private final Path dataDirectory;
    private final VelocityAutoBackup plugin;

    public BackupNowCommand(VelocityAutoBackup plugin, ProxyServer server, Logger logger, Path dataDirectory) {
        this.plugin = plugin;
        this.server = server;
        this.logger = logger;
        this.dataDirectory = dataDirectory;
    }

    @Override
    public void execute(Invocation invocation) {
        try {
            GoogleDriveUploader uploader = new GoogleDriveUploader(dataDirectory, "credentials.json", logger);

            if (!uploader.isAuthorized()) {
                Path authCodeFile = dataDirectory.resolve("authcode.txt");

                if (Files.exists(authCodeFile)) {
                    String code = Files.readString(authCodeFile).trim();
                    invocation.source().sendMessage(Component.text("'authcode.txt' file found. Attempting to authorize...", NamedTextColor.YELLOW));
                    boolean success = uploader.authorizeWithCode(code);
                    if (success) {
                        invocation.source().sendMessage(Component.text("Authorization successful! Storing permanent token.", NamedTextColor.GREEN));
                        Files.delete(authCodeFile);
                    } else {
                        invocation.source().sendMessage(Component.text("Authorization FAILED. Code might be incorrect or expired. Delete 'authcode.txt' and try again.", NamedTextColor.RED));
                        return;
                    }
                }
                else {
                    uploader.generateAuthorizationUrl();
                    invocation.source().sendMessage(Component.text("AUTHORIZATION REQUIRED:", NamedTextColor.GOLD));
                    invocation.source().sendMessage(Component.text("1. Open the URL from the console in your browser & grant permission.", NamedTextColor.GRAY));
                    invocation.source().sendMessage(Component.text("2. Copy the code provided by Google.", NamedTextColor.GRAY));
                    invocation.source().sendMessage(Component.text("3. Create a file named 'authcode.txt' inside the /plugins/velocityautobackup/ folder.", NamedTextColor.GRAY));
                    invocation.source().sendMessage(Component.text("4. Paste the code into that file, save it, and run /backup-now again.", NamedTextColor.GRAY));
                    return;
                }
            }

            invocation.source().sendMessage(Component.text("Authorization found. Starting manual backup process...", NamedTextColor.GREEN));
            SimpleConfig config = plugin.loadConfig();
            if (config != null) {
                server.getScheduler()
                        .buildTask(plugin, new BackupTask(logger, dataDirectory, config))
                        .schedule();
            }

        } catch (IOException | GeneralSecurityException e) {
            logger.error("Error in backup-now command", e);
            invocation.source().sendMessage(Component.text("An error occurred. Check the server console.", NamedTextColor.RED));
        }
    }

    @Override
    public boolean hasPermission(Invocation invocation) {
        return invocation.source().hasPermission("velocityautobackup.command.backupnow");
    }
}