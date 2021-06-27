# Flight Control Android Application​


We created a joystick app that can control a plane inside the flight gear simulator.​
The app was written for android devices using Kotlin and implamenting the MVVM design pattern​
The app was created as a part of our Advanced Programming course. 

## FlightGear Installation & Configuration

Before using this application, we need the to install the following:
 - FlightGear Simulator:
   - Version 2020.3.x Download [here](https://www.flightgear.org/download/)
   - Version 2019.1.2 Download [here](https://sourceforge.net/projects/flightgear/files/release-2019.1/)
 - To run the application we recommend installing android studio and clonning the code from this repository.
   
You will need to paste this setting into your flightGear simulator setting tab:
--telnet=socket,in,10,127.0.0.1,5678,tcp
(5678 is just an option for the port number, choose any port available on your machine). 

![alt text](https://github.com/yana-sidnich/pictures/blob/main/pic1.png)

After you applied the correct setting start the simulator, when you see the plane on the runway you can connect to the simulator from our app.


## App Preview
![alt text](https://github.com/yana-sidnich/pictures/blob/main/pic3.png)

Our app has one main screen. 
At the top of the screen you can see the connection section. In order to connect to the simulator you will need to enter the IP of the mechine that is running the simulator, and the PORT number you chose for the simulator settings, make sure its the same port number !
To complete the connection press on the connect button. You can disconnect from the simulator at any given momment by simply pressing the disconnect button. 

## Flying The Plane

![alt text](https://github.com/yana-sidnich/pictures/blob/main/pic4.png)

After you connected to the simulator you can start flying the plane:
- In the tabs at top section of the screen click on the `Cessna C172P` tab, and click on the `Autostart`, now the plane is ready to fly.
- Set the thruttle bar to its max in order to start the engine.
- Use the rudder bar to keep the plane in the center of the runway.
- Wait for the plane to gain enough speed.
- Pull the joystick down, this will make the plane start ascending.
- Once the plane is in the air you can use the joystick to move it to any direction you want:
  - to control the Aileron value - move the joystick right and left.
  - to control the Elevator value - move the joystick up and down.
  - to control the Thruttle value - move the bar that is located to the left of the joystick.
  - to control the Rudder value - move the bar that is located below the joystick.


## Design And Class Diagram

![alt text](https://github.com/yana-sidnich/pictures/blob/main/UML%20class%20-%20yana%20(1).png)

In the image above you can see a UML class diagram of our project. This diagram showcases the relations and connection between diffrent classes in our project.
In this project we used the MVVM design pattern. As shown in the class diagram, the project files were organized in three packages:
- The model package.
- The view package.
- The viewModel package.

In the following link you can watch a video getting into more details about the design of the application and the MVVM architecture:

You can get the presentation you see on the video, in the following link:

