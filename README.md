# Hack2018 - Auxnet
Zach Allegretti, Joe Forsman, Jack Plank, and Sahil Khatri

This is our project for HackOhio 2018, called Auxnet, which won Most Impactful Hack and 3rd Overall. 

Auxnet is an Android app which implements a auxiliary network between phones with the app installed.
This network does not depend on wifi or cell service, but should either be present, the data in the app syncs up to an external (firebase) database, which is then accessible from anyone with internet access.
This app was designed with a focus on disaster recovery in mind, and the data stored and transmitted is reflective of that. 

For example, should two people in a disaster zone be near one another, but only one has internet access, the 'disconnected' person can use this app to broadcast their location. Then, the connected app will automatically sync this data to the firebase server, allowing disaster recovery personnel to access the database and find the disconnected person.


