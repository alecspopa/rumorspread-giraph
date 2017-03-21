# Run in Eclipse

*NOTE:* make sure JDK and JRE are installed

Import the proiect `org.school.rumorspread` in Eclipse as a Maven Project

Right Click `GiraphAppRunner` run as `Java Application`

It will give an error about the runtime arguments in main, so go to Run -> Run Configuration arguments and give it 2 arguments:
- the first one the input path for the data file (ex: `../datasets/tiny-graph.txt`)
- the second one the output path for the result (ex: `../output`)

*NOTE:* paths are relative to `org.school.rumorspread` directory

*NOTE 2:* output directory must not exist and must be removed after each run
