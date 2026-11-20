<img src="docs/assets/java.svg" width="64" alt="Java logo">

# CPU Scheduling Simulator

Which job gets the CPU next? Run this Java console program to compare six schedules for the same jobs. Read a Gantt chart to see when each job runs and how long it waits.

| Start here | What you can do |
| --- | --- |
| [Learn CPU scheduling](docs/TUTORIAL.md) | Try three jobs, compare six schedules, and check your answer. |
| [Explore the Java code](docs/BRIEF.md) | Follow one run from the menu to the results. |
| [View the activity PDF](docs/CPU_Scheduling_Simulator.pdf) | Read the code screenshots and see sample output. |
| [Run the program](#run-the-program) | Build the project and enter your own jobs. |

## Watch a worked example

Select a preview to watch the lesson on YouTube. Both lessons are from Neso Academy.

| Shortest Job First | Round Robin |
| --- | --- |
| [![Watch the solved SJF problem](https://i.ytimg.com/vi/lpM14aWgl3Q/mqdefault.jpg)](https://www.youtube.com/watch?v=lpM14aWgl3Q) | [![Watch the Round Robin lesson](https://i.ytimg.com/vi/YzBBJYfwdi8/mqdefault.jpg)](https://www.youtube.com/watch?v=YzBBJYfwdi8) |
| Follow a job with a shorter time left. | See how jobs take turns in a queue. |

Developed by REQUEJO, Froilan B. and LARAÑO, Gabriel Ardee R. for Fundamentals of Programming.

## Run the program

Install [JDK 26](https://adoptium.net/) and [Apache Maven](https://maven.apache.org/install.html) 3.9 or later. The JDK is the tool that builds and runs Java programs. Maven builds this project. Run `java -version` and `mvn -version` to check your setup. The Maven output should show Java 26. A newer JDK also works if it can build for Java 26.

Open a terminal and run these commands. You need access to the private GitHub repository to clone it. If you have a ZIP copy, unzip it, open a terminal in the folder with `pom.xml`, and run the last two commands.

```sh
git clone https://github.com/miraflowwer/cpu-scheduler.git
cd cpu-scheduler
mvn compile
java -cp target/classes com.mycompany.cpuschedulerport.Main
```

Add `--text-chart` after `Main` to print one time interval per line. The default view draws boxes.

1. Enter the number of jobs. Choose an algorithm by its menu number.
2. If you choose Shortest Job First or Priority, also choose a scheduling mode.
3. Choose **Enter values** to type your own numbers. Choose **Randomize values** to let the program pick them.
4. For a manual Round Robin run, enter the quantum first. Then enter an arrival and a burst for each job. Only Priority Scheduling also asks for a priority for each job.
5. When you see the results, enter `Y` to run again or `N` to exit.
