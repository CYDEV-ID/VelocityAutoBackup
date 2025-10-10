<p align="center">
  	<a href=""><img src="https://i.postimg.cc/d0xmh7LQ/Cream-and-Green-Illustrative-Coming-Soon-Email-Header-600-x-100-px-600-x-150-px-3.png" width="1719" alt="whoimai" /></a>
</p>

# Google Cloud Configuration

## Create New project
1. Open [Google Cloud](https://console.cloud.google.com/)
2. Click **Select a project** and click **New project**.
3. Fill in the project name as you want. For example:<br>
   <img width="543" height="217" alt="image" src="https://github.com/user-attachments/assets/46699005-8655-4572-aa07-f91e44b8ab8a" /> <br />
   and click **Create**.
4. A notification popup will appear, if the notification shows a ✅ sign, you have successfully created the project.<br>
   <img width="391" height="119" alt="image" src="https://github.com/user-attachments/assets/16367e9b-44b1-4f88-890d-20ce64f4a722" /> <br />
5. Select the project you created earlier.

## Enabled Google Drive API
6. Look at the top left, there are 3 lines, click and select **Marketplace**.
7. Search **Google Drive API**.<br>
   <img width="974" height="137" alt="image" src="https://github.com/user-attachments/assets/096e00ac-81ab-490f-9c17-73fa01eccb9e" /> <br />
8. Select **Google Drive API**.
9. Click **Enable**.

## Create OAuth Consent Screen
10. Look at the top left, there are 3 lines, click and select **APIs & Services**.
11. Look at the left side again, click **OAuth Consent Screen**.
12. Click **Get Started**.
13. In **App Information**, fill in the App name as you wish.<br>
   <img width="535" height="111" alt="image" src="https://github.com/user-attachments/assets/a945968e-5c7c-40df-939a-880a6b5192e0" /> <br />
   and for **User support email** fill in according to the email you use. Click next,
14. In **Audience** select **External**. Click next,
15. In **Contact Information** can be filled in according to your Google account email but it can also be different to. Click next
16. In **Finish**, click i agree and Click conitnue,
17. After all of that click **Create**.

## Create OAuth Client ID
18. On the Metrics line, click **Create OAuth Client** <br>
    <img width="1625" height="103" alt="image" src="https://github.com/user-attachments/assets/3aa8d93c-c968-4e6d-a207-e5c6b461727b" /> <br />
19. In **Application Type** choose **Dekstop App** and fill in **Desktop Name** as desired. <br>
    <img width="535" height="400" alt="image" src="https://github.com/user-attachments/assets/cbda02cc-e871-4f3a-b3bd-b3cf942d6cde" /> <br />
20. After all of that click **Create**, and you will see **OAuth client created** popup. <br>
    <img width="499" height="631" alt="image" src="https://github.com/user-attachments/assets/359f3dd1-a4c2-4789-bb34-8022e6ce8391" /> <br />
21. Before you click **OK** click **Download JSON**, if done you can click OK.

## Publish App
22. Look at the left side, click **Audience**.
23. In **Testing** click **Publish App** and click **Confirm**.
24. The **Publish App** will change to **In Production**.

# Google Drive Configuration
1. Create new Folder in your google drive
2. Go to folder and get the ID folder from your google drive URL<br>
   <img width="473" height="24" alt="image" src="https://github.com/user-attachments/assets/91c60052-c846-4782-8ab7-09cc2fd4b4d1" /> <br />
3. Chance your folder Access to Editor and Done
**Note: You can use the main account you created for credentials.json and you can also use other accounts too.**

# Velocity Configuration
1. Upload [velocityautobackup-1.0.jar](https://modrinth.com/plugin/velocityautobackup) to your `plugins/` folder and restart.
2. Before you access your velocityautobackup folder, change the name of the file you downloaded earlier from **Google Cloud to credentials.json**.
3. Access your velocityautobackup folder in `plugins/velocityautorestart/`.
4. Upload **credentials.json**.
5. Open **config.yml** in `plugins/velocityautorestart/config.yml/`
6. Change **google-drive-folder-id** replace with the ID you copied earlier. Save and restart your server.
```yaml
#
google-drive-folder-id: "PASTE_YOUR_FOLDER_ID_HERE"
```
7. Execute command /backup-now and you will get link, follow the instructions
8. If you are confused where to get the authcode, this is an example of the link I got after verification.<br>
<br />localhost:8888/?code=**"4/0AVGzR1C6qayRkDsxkPHfML9wydxj7ngLJueYuc9ZuwXdppaZNMe6NnsD0kOVHAqHa92OBw"**&scope= <br><br />
what I put in bold is the authcode.txt, copy and create new file in `plugins/velocityautorestart/` with name authcode.txt. Paste the code inside authcode.txt and restart your server again
9. After restart, use command /backup-now again.
10. Your plugin VelocityAutoRestart succesfully Configured. You can customize the restart time in config.yml

<p align="center">
  	<a href=""><img src="https://i.postimg.cc/Qx75X8LB/Cream-and-Green-Illustrative-Coming-Soon-Email-Header-600-x-100-px-600-x-150-px-550-x-150-px.png" width="1719" alt="whoimai" /></a>
</p>
