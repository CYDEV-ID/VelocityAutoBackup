# VelocityAutoBackup
is a comprehensive plugin for the Velocity proxy server, designed to provide a fully automated, flexible, and secure backup solution. With direct integration into Google Drive, this plugin ensures all your critical server data is stored safely off-site and is accessible at any time.

This plugin is specifically designed to work with personal Google accounts, utilizing a unique authorization flow to bypass common technical limitations.

### ✨ Key Features
**_Flexible Scheduling_** ⏰: Configure multiple backup times within a single day through the config.yml. You can schedule backups to run twice, three times, or as often as you need, complete with timezone settings for precise scheduling.

**_Instant Manual Backups_** ⚡: With the /backup-now command, administrators can trigger a full backup process at any moment without waiting for the schedule. This feature is perfect before performing maintenance or significant server changes.

**_Dynamic File Naming_** 📝: Take full control of your backup file naming. Use placeholders like {server_name} and {timestamp} with a customizable date format (e.g., yyyy-M-d--HH-mm) to produce organized and easily searchable filename.

**_Full Google Drive Integration_** ☁️: Backups, compressed into a .zip format, are automatically uploaded to a specific folder in your Google Drive. The plugin supports the use of a personal Google account after completing a secure, one-time authorization process.

**_Reliable Anti-Loop Logic_** 🛡️: Built with a smart mechanism to automatically exclude its own directory from the backup process, preventing "backup loop" issues that can cause file sizes to grow uncontrollably.

[How to setup VelocityAutoBackup](https://github.com/DeluzeKitsuu/VelocityAutoBackup/blob/master/SETUP.md)

## Configuration

All settings are managed in `/plugins/velocityautobackup/config.yml`.

```yaml
# A list of daily backup times in "HH:mm" format.
backup-times:
  - "02:00"
  - "14:00"

# The timezone for the schedule.
timezone: "UTC"

# The ID of the Google Drive folder where backups will be uploaded.
google-drive-folder-id: "PASTE_YOUR_FOLDER_ID_HERE"

# The name of the credentials JSON file.
credentials-file-name: "credentials.json"

# The server name to be used in the backup filename.
server-name: "proxy"

# The format for the backup filename.
# Use {timestamp} and {server_name}.
backup-file-name: "Backup-{server_name}-{timestamp}.zip"
```

## 💻 Permissions

| Command       | Description                |                Permissions                |
| ------------- | -------------------------- | ----------------------------------------- |
| `/backup-now` | Force Backup.              | `velocityautobackup.command.backupnow`    |

## 📝 Licence

This project is licensed under [MIT](LICENSE).
