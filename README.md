# Trip Visualiser [Java, Maven, Spring Boot, JavaFX GUI]

More info:

* We have information about some trips in two tables - trips [id, driverId, startTs, endTs] and
  points [id, tripId, x, y, ts].
* Trips tables are stored in TWO formats - excel file and SQLite database -  for which we have
  two DataHandlers to extract data from the file we need.
* We give the application one of those files to work with.
* AppConfig class which helps us choose which DataHandler implementation to use, based on
  what file we actually give the application.
* Then in simple GUI we give the driverId, for which we want to see the trips and an output file name.
* We also give format type PNG/.txt - in this format we actually visualise the trips.
* Based on the driverId we actually get the x and y coordinates for every point of every trip and
  we basically print them on a text file or png file. (when we want text file every point of the same trip
  is visualized with a letter on the file and for png file with some color, different trips are with different
  letters or different colors )

View files TEXTFILE and PNGFILE_EXAMPLE to understand better!

Conclusion:

Small app that can work with DIFFERENT files (SQL and Excel) to extract info from tables,
has a basic gui to give the program the driver - of which we want to see trips, 
and has DIFFERENT options for an outputfile ( PNG and .txt) , which when generated 
visualises the trips based of the x & y coordinates of their points.
