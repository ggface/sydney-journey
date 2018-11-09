# Sydney Journey
Android application of different Sydney' venues.

Features:

* Start screen allows watch and manage to some venue's marker.
* If you allow application to use geo location you can view all venues sorted by distance from you. Also you can sort venues by name (ASC, DESC).
* You can add a new venue using a long touch gesture. Touch gesture on any venue' marker allows you manage to selected venue (change description or delete one). You can't delete any venues loaded from server side.
* You can press My Location button to move camera to your geo position.

Notes:

* I performed application based on MVP pattern. 
* I used RxJava and Room library from Android Architecture Components.
* Location feature includes checking of enabled gps and permissions for Android M and later.
