The project consists of ``FindMeetings`` algorithm and ``DrawTraces`` utility.
Both are configured via system environment variables (see below).
For quick run you can launch ``test.sh`` in project directory.

# Overview

``FindMeetings`` has 2 modes:
* First mode is search of all meetings of 2 selected persons. It is possible to stop after 1st found meeting.
By default, it prints all found meetings with time intervals they occurred in.
You can switch verbosity off and the program will show only number of meetings found.
* Second one is "Full Graph" mode and developed for quick appraising of algorithm.
In this mode, program can take list of selected uids (so called "task") as input and will search for meetings of every pair of uids present in the list.
If no task provided, the program will scan input CSV file for uids first and then use them as task.

There are couple of sample logs in ``examples`` folder: ``top.5min.log`` and ``top.1min.log``.
5min and 1min are values for configured parameter of minimal meeting duration.

``DrawTraces`` utility is implemented for quick analysis of the provided dataset.
When 2 uids provided (same way as ``FindMeetings``), the utility will draw trajectories of both persons on every floor where they were.
By default, the drawer places output images into ``draw-${uid1}-${uid2}`` directory.
You can look for sample output in ``examples`` directory.

As I understand, the given dataset is generated -- that explains why some trajectories look strange :) 
But there are couple of images that can make sense, e.g. ``examples\draw-b53b76f9-1bd4e1e0\3-floor.png``
or ``examples\draw-b53b76f9-74d917a1\3-floor.png``

# Configuration and run

The program can be launched via sbt with following command:

``SOURCE=reduced.csv A=<uid1> B=<uid2> sbt "run-main FindMeetings"``

For more convenient way I change variables in ``test.sh`` which I run then.

*Mandatory* options:
* **SOURCE** -- path to CSV file with input data
* **A** and **B** in plain mode -- uids whose meetings should be found

All other variables are *optional*: 

*Mode* options:
* **FULL_GRAPH** -- enables ``Full Graph`` mode if defined (no matter what value)
* **TASK_FILE** -- path to a file with uids to be scanned
* **ONLY_FIRST** -- if defined, the algorithm will stop after 1st meeting found
* **ONLY_COUNT** -- output only amount of meetings occurred, default for ``Full Graph`` mode

*Algorithm* settings:
* **DURATION** in seconds -- minimal interval for meeting to recognized
* **DISTANCE** in meters -- minimal distance between people to consider them met
* **EPSILON** in seconds -- if some timestamps are in this interval then they are considered equal

*DrawTraces* options:
* **TARGET** -- path to a directory, where to save images
