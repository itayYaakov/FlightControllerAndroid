# Flight Control Android Application​


We created a joystick app that can control a plane inside the flight gear simulator.​
The app was written for android devices using Kotlin and implamenting the MVVM design pattern​
The app was created as a part of our Advanced Programming course. 

## FlightGear Installation & Configuration

Before using this application, we need the to install the following:
 - FlightGear Simulator:
   - Version 2020.3.x Download [here](https://www.flightgear.org/download/)
   - Version 2019.1.2 Download [here](https://sourceforge.net/projects/flightgear/files/release-2019.1/)
   
In order two run our app correctly you will need to paste this setting into your flightGear simulator setting: --telnet=socket,in,10,127.0.0.1,6400,tcp , 6400 is just an option for the port number, choose any port avilable on your machine.
After you applied the correct setting start the simulator. 

![alt text](https://github.com/yana-sidnich/pictures/blob/main/pic1.png)

## App Preview
![alt text](https://github.com/yana-sidnich/pictures/blob/main/pic1.png)

Our app has one main screen. 
At the top of the screen you can see the connection section. In order to connect to the simulator you will need to enter the IP of the mechine that is running the simulator, and the PORT number you chose for the simulator settings, make sure its the same port number !
To complete the connection press on the connect button. You can disconnect from the simulator at any given momment by simply pressing the disconnect button. 
