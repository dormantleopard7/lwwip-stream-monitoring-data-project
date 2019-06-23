# LWWIP Stream-Monitoring Data Project

As part of my 3rd year at the Lake Washington Watershed 
Internship Program, I chose to do a technical project, 
particularly analyzing and visualizing our stream monitoring 
data.

Note: This project began in November 2018, and was 
added to GitHub in March 2019.

## Usage Instructions

For this project to work correctly, you must have Java
downloaded on your machine, and be able to navigate to 
a directory (folder) using a terminal window. The
following instructions will help with this, and should
be enough to get the program up and running on your
computer.

NOTE: These instructions are mainly meant for Windows,
though usage instructions for Mac or Linux should be quite 
similar (e.g. using terminal instead of Command Prompt).

NOTE: These instructions are meant for anyone to run
the program. They are not meant for development purposes.
If you would like to work on the code (say, to expand on
it as part of your LWWIP project), please contact me.

1. **Download Java.** 
    * Follow the instructions 
[here](https://courses.cs.washington.edu/courses/cse142/17au/software/jdk.html)
to get the Java Develpment Kit (JDK) on your machine.
This is necessary for running the program. 
    * Note: You
may already have Java downloaded on your computer (e.g. 
bsd laptops already have Java). If this is the case, you
may skip this step.
2. **Download the program.** 
    * Navigate to 
[out/artifacts/lwwip_stream_monitoring_data_project_jar/lwwip-stream-monitoring-data-project.jar](https://github.com/dormantleopard7/lwwip-stream-monitoring-data-project/blob/master/out/artifacts/lwwip_stream_monitoring_data_project_jar/lwwip-stream-monitoring-data-project.jar)
within the project structure here. Click *Download*.
    * Note: It is advised that you store this file in an
easy-to-access directory like Desktop, Downloads, or 
Documents, as it will make a later step (5) easier.
3. **Download the raw data.** 
    * You can find our Coal Creek data as a Google Sheet 
[here](https://docs.google.com/spreadsheets/d/13m4NraA7xr7I0MvVwElQNLROCSjlR1wWpsUXs-TIdJM/edit#gid=0).
Go to *File > Download as > Tab-separated values (.tsv, 
current sheet)*. Save this file to the same folder/directory 
as the .jar file from step 2. 
    * You can find a sample raw
data file in 
[out/artifacts/lwwip_stream_monitoring_data_project_jar/coal_creek_data_3-2019_raw.tsv](https://github.com/dormantleopard7/lwwip-stream-monitoring-data-project/blob/master/out/artifacts/lwwip_stream_monitoring_data_project_jar/coal_creek_data_3-2019_raw.tsv).
        * Note: the format of your data file must match the format
of this file (i.e. same order of headers).
4. **Get the data ready.**
Assuming there aren't any issues with the data, this
is a simple step: 
    * Open the .tsv file from step 3 with
a text editor 
        * Notepad, the default text editor on 
Windows, works fine, but if you would like, you may
download/use (for free) a more sophisticated text editor like 
[Sublime Text](https://www.sublimetext.com/).
    * Delete the first line (the headers line) completely.
This means that the new first line of the file should 
contain data (in the case of out data, this line begins
with 5/12/2006). 
        * You can find a sample ready data file in
[out/artifacts/lwwip_stream_monitoring_data_project_jar/coal_creek_data_3-2019.tsv](https://github.com/dormantleopard7/lwwip-stream-monitoring-data-project/blob/master/out/artifacts/lwwip_stream_monitoring_data_project_jar/coal_creek_data_3-2019.tsv).
        * Note: In text editors without line numbers,
like Notepad, it might be hard to figure out when the
first line ends. For our data, the first line (which we
want to delete) begins with *Date* and ends with *Notes*.
Make sure that you completely delete this first line --
this means that there should not be a blank line in its
place; instead the first line must be a data line.
5. **Run the program.**
    * To do this, you need to open a Command Prompt (or Terminal)
window. You can do this by holding down the *Windows* key
and pressing *R*, and typing *cmd*. This will open *cmd.exe*
in your home directory (likely something similar to 
`C:\Users\Username`). 
    * Now you need to navigate to the
directory with your .jar and .tsv files. 
        * To do this, you
will likely only need two commands: `dir` (list the
contents of the current directory), and `cd` (change 
directory). 
        * If you stored them in an easy directory,
this could be as simple as the one command `cd Desktop`
or `cd Downloads`. It could be a bit more complicated,
but it only requires `dir` and `cd`. 
        * Note: If you accidentally
go into the wrong directory, you can type `cd ..` to go
back up one directory. 
            * Also, fun fact: you can press
*tab* to auto-complete a directory or file in cmd. 
        * If you
would like more help with Command Prompt, there are
numerous basic online tutorials like 
[this one](https://www.cs.princeton.edu/courses/archive/spr05/cos126/cmd-prompt.html).
    * Once you are in the correct directory, run the command:
`java -jar lwwip-stream-monitoring-data-project.jar`
        * Tip: start typing the jar file name, then press *tab* 
for auto-complete. 
        * It should prompt for a file name; 
enter the name of the .tsv data file. 
        * If it displays 
a Menu, then you should be good to go! The program is 
now running, and you should be able to analyze and 
visualize the data! 
    * Note: You may put the .tsv file 
in a different location than the .jar file, though it
requires a bit more work. When prompted for the
file name, enter the path to the file, either from
the root (from `C:` down), or from the current
directory.

**Example Run**
```
C:\Users\Username>cd Desktop
C:\Users\Username\Desktop>java -jar lwwip-stream-monitoring-data-project.jar`

Enter the stream data .tsv file name/path.
Specify the path from the root directory or from the current directory.
File name/path: coal_creek_data_3-2019.tsv

Menu:
        w to write cleaned data to file
        a to analyze data statistically
        v to visualize the data
        q to quit

Enter an option (m to see the menu): a
Start date (leave blank if want first data date): 1/1/18
End date (leave blank if want today's date): 12/31/2018
Site (1 or 2; 0 if want both): 1
Data type ({1=Turbidity (NTU), 2=Turbidity (m), 3=Air Temp (°C), 4=Water Temp (°C), 5=pH, 6=DO (ppm), 7=Conductivity (?S/cm)}): 3
Chosen data type: Air Temp (°C)

Resulting Statistics
--------------------
Mean: 11.619047619047619
Standard Deviation: 5.892700815627273
Mode (frequency 7): [10.0]
Min, Q1, Median, Q3, Max: [2.0, 8.0, 10.0, 15.0, 25.0]
Outliers (based on 3 std devs): []
Outliers (based on 1.5 IQRs): []

Enter an option (m to see the menu): v
Start date (leave blank if want first data date): 1/1/17
End date (leave blank if want today's date): 12/31/17
Site (1 or 2; 0 if want both): 2
Data type ({1=Turbidity (NTU), 2=Turbidity (m), 3=Air Temp (°C), 4=Water Temp (°C), 5=pH, 6=DO (ppm), 7=Conductivity (?S/cm)}): 4
Chosen data type: Water Temp (°C)
(S)catter plot OR (H)istogram: s
Scatter Plot Generated!

Enter an option (m to see the menu): v
Start date (leave blank if want first data date): 1/1/16
End date (leave blank if want today's date): 12/31/16
Site (1 or 2; 0 if want both): 0
Data type ({1=Turbidity (NTU), 2=Turbidity (m), 3=Air Temp (°C), 4=Water Temp (°C), 5=pH, 6=DO (ppm), 7=Conductivity (?S/cm)}): 5
Chosen data type: pH
(S)catter plot OR (H)istogram: h
Bucket Size (0 for individual counts): 1

[7.00    -    8.00) : *********************************************************************************
[8.00    -    9.00) : *******
counts (from 7.00 to 9.00): [81, 7]
Histogram Generated!

Enter an option (m to see the menu): v
Start date (leave blank if want first data date):
Start date set to 1/14/2006
End date (leave blank if want today's date):
End date set to 6/22/2019
Site (1 or 2; 0 if want both): 0
Data type ({1=Turbidity (NTU), 2=Turbidity (m), 3=Air Temp (°C), 4=Water Temp (°C), 5=pH, 6=DO (ppm), 7=Conductivity (?S/cm)}): 7
Chosen data type: Conductivity (?S/cm)
(S)catter plot OR (H)istogram: h
Bucket Size (0 for individual counts): 10.0

[80.00   -   90.00) : *
[90.00   -  100.00) :
[100.00  -  110.00) : **
[110.00  -  120.00) : *
[120.00  -  130.00) : *******
[130.00  -  140.00) : ***************
[140.00  -  150.00) : *********
[150.00  -  160.00) : **
[160.00  -  170.00) : *******
[170.00  -  180.00) : *****
[180.00  -  190.00) : *******
[190.00  -  200.00) : ****
[200.00  -  210.00) : *******
[210.00  -  220.00) : **
[220.00  -  230.00) : ****
[230.00  -  240.00) : ***
[240.00  -  250.00) : ********
[250.00  -  260.00) :
[260.00  -  270.00) :
[270.00  -  280.00) : *
[280.00  -  290.00) : **
[290.00  -  300.00) : *
[300.00  -  310.00) :
[310.00  -  320.00) : *
counts (from 80.00 to 320.00): [1, 0, 2, 1, 7, 15, 9, 2, 7, 5, 7, 4, 7, 2, 4, 3, 8, 0, 0, 1, 2, 1, 0, 1]
Histogram Generated!

Enter an option (m to see the menu): m
Menu:
        w to write cleaned data to file
        a to analyze data statistically
        v to visualize the data
        q to quit

Enter an option (m to see the menu): q

C:\Users\Username\Desktop>
```
Note: For visualization, graphical charts should be
generated and will open in a new window, where the user
can zoom in and out, and save the image as a .png.
All of these graphics windows must be closed for the
quit option to work.

Note: When it shows, `Conductivity (?S/cm)`, this is
simply because Command Prompt does not recognize the
μ (mu for micro) symbol. It is supposed to be `Conductivity (μS/cm)`.
