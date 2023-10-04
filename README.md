# Tyne Finance
This is a web application to help manage your finances.  
Keep track of your accounts, expenses (both one-time and recurring), budgets and your wish lists.


## This Repo
This repo contains the backend code for the application.
It's a migration from the python implementation in django, found [here](https://github.com/muremwa/Tyne-Finance "Tyne-Finance Python (Django)").  
The previous implementation in Django contains user management which will be used here and is meant to be cross compatible.  
This means users, passwords, groups and permissions are meant to be shared by both implementations.
Passwords created and saved by the Spring application can be authenticated by Django and vice-versa.  
Same goes for permissions, groups and users. Auth token compatibility is TBD.


[DB repo found here](https://github.com/muremwa/Tyne-Finance-DB "Tyne-Finance-DB")
