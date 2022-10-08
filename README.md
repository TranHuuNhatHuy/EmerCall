# EmerCall

<img src="https://user-images.githubusercontent.com/29034232/166426847-001b0a4e-9e42-4771-bd4f-5642094dcecf.png" width=75% height=75%>

## 1. Introduction to EmerCall system

EmerCall is a non-profit, Android-based project, developed to provide a fast, accurate communicative assistance system which can support the transportation process of victims in emergency cases across Vietnam - my home country. Comprised of 3 Android applications, this system can allow civilians and hospitals to real-time acquire and update location information of each other, as well as victim’s status, in order to help the ambulances to approach their designated victims faster, while preparing for his/her current status.
As stated, EmerCall system consists of 3 app modules, along with a database on Google Firebase Real-time Database. When an emergency case occurs, the system will be switched to “active state”, and 3 applications will automatically real-time synchronize with the database server, sharing information about the condition and location of the victim as well as the incoming ambulance. This will help the ambulance to approach the victim faster, meanwhile, the victim and people around can consult first-aid methods archived within the application, minimizing victim’s damages while waiting for the designated ambulance. In addition, with this system, medical facilities can monitor the information of users and ambulances for directing and controlling purposes. This serves as a foundation for a promising medical system for both civilians and hospitals in the future.

## 2. System workflow

The workflow of EmerCall system:

<img src="https://user-images.githubusercontent.com/29034232/166429400-c50d9b59-c04e-4d1e-90e4-6c92aa4f34dc.png" width=75% height=75%>

During an emergency case, EmerCall undergoes 6 main steps:

1. When an emergency case happens, civilians – EmerCall application users – will send an emergency signal. It contains information of the caller, as well as victim’s gender, age, and current status, all are sent to Firebase Real-time Database on the server. This signal also triggers user’s EmerCall application to the “active state”.
2. The database will redirect information, brought along by the signal, to the EmerCall Monitoring application used by hospital’s employees.
3. This employee will assign an available ambulance to approach the victim/caller. Then, EmerCall Ambulance application of the selected ambulance will be triggered to its “active state”. At this time, it will receive victim’s information – gender, age, and current status – from EmerCall Monitoring application.
4. On the way, EmerCall Ambulance and EmerCall applications will display each other’s geographical location (showed by Google Ambulance Map fragment of the 2 applications). Meanwhile, EmerCall application’s first-aid archive will be automatically activated for user’s reference purposes.
5. After finishing the transportation of victim, users of EmerCall Ambulance application will confirm the case’s result. In case it was a fake call, the user who activated the emergency call will be counted as one fake call. Finally, this result will be sent to EmerCall Monitoring, and be updated onto database server.
6. In case a smart device is detected to have sent 3 fake calls, the EmerCall Monitoring application will block this device’s accessibility to EmerCall system. Since EmerCall system uses Unique Device Identifier (UDID) of devices for recognition, this block is irreversible unless contacting system’s administrators for unblocking.

## 3. Technology used

- Android Studio 3.2.1:
  - Android 9.0 (Pie) SDK Platform, API Level 28
  - Android SDK Tools 26.0.2
  - Android Emulator 26.1.4
  - Google Play Services 49
- Firebase API:
  - Google Services plugin 4.2.0
  - Firebase Core 16.0.4
  - Realtime Database 16.0.4
- Google Map API:
  - Map Javascript 3.34

## 4. Some screenshots

<img src="https://user-images.githubusercontent.com/29034232/166431090-8c63185f-b1a4-4830-8824-dec1f32845e9.png" width=25% height=25%>
Emercall home screen, at the center is a big button for sending emergency signal
<img src="https://user-images.githubusercontent.com/29034232/166431564-79079d1f-876c-412d-82f2-83c295dd0b9e.png" width=25% height=25%>
EmerCall Dictionary, containing various emergency cases/symptoms and their corresponding proper treatment methods
<img src="https://user-images.githubusercontent.com/29034232/166431699-e381be33-6a42-4be9-a072-a806e786e094.png" width=25% height=25%>
EmerCall Account, containing user information: name, date of birth, gender, address, personal ID and medical ID
<img src="https://user-images.githubusercontent.com/29034232/166431952-351b80ff-7dbc-4f39-a723-cf3d245f8e35.png" width=25% height=25%>
First screen after sending emergency signal. It requires user to enter victim’s gender, age, and notable symptoms/status
<img src="https://user-images.githubusercontent.com/29034232/166432229-6c8dd93c-592e-4c1a-899d-198528bccede.png" width=25% height=25%>
Second screen after sending emergency signal, showing locations of user, ambulance and hospital. The user can also switch to firstaid method list by pressing the lower left button
