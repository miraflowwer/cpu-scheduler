<p align="center">
  <img src="assets/java.svg" width="64" alt="Java">
  &nbsp;
  <a href="https://github.com/miraflowwer"><img src="assets/295236160.png" width="64" alt="miraflowwer"></a>
  &nbsp;
  <img src="assets/qcu.png" width="64" alt="Quezon City University">
</p>

# CPU Scheduling Simulator

Which job gets the CPU next? Run this Java console program to compare six schedules for the same jobs. Read a Gantt chart to see when each job runs and how long it waits.

<div align="center">
<table align="center">
  <tr><th>Start here</th><th>What you can do</th></tr>
  <tr><td><a href="TUTORIAL.md">Learn CPU scheduling</a></td><td>Compare six schedules and check your answer.</td></tr>
  <tr><td><a href="BRIEF.md">Explore the Java code</a></td><td>Follow one run from the menu to the results.</td></tr>
  <tr><td><a href="CPUScheduling_Larano_Froilan.pdf">View the activity PDF</a></td><td>Read the code screenshots and sample output.</td></tr>
  <tr><td><a href="#run-the-program">Run the program</a></td><td>Build the project and enter your own jobs.</td></tr>
</table>
</div>

## Video lessons

Select a preview to open the lesson on YouTube.

<p align="center"><a href="https://www.youtube.com/watch?v=VSMAjMfJ6KQ"><img width="280" src="https://i.ytimg.com/vi/VSMAjMfJ6KQ/mqdefault.jpg" alt=""><br>First Come First Serve</a><br><sub>Work through jobs in arrival order.</sub><br><br></p>

<p align="center"><a href="https://www.youtube.com/watch?v=t0g9b3SJECg"><img width="280" src="https://i.ytimg.com/vi/t0g9b3SJECg/mqdefault.jpg" alt=""><br>Shortest Job First</a><br><sub>Pick the ready job with the shortest burst.</sub><br><br></p>

<p align="center"><a href="https://www.youtube.com/watch?v=lpM14aWgl3Q"><img width="280" src="https://i.ytimg.com/vi/lpM14aWgl3Q/mqdefault.jpg" alt=""><br>Shortest Remaining Time</a><br><sub>See when a shorter job can take over.</sub><br><br></p>

<p align="center"><a href="https://www.youtube.com/watch?v=YzBBJYfwdi8"><img width="280" src="https://i.ytimg.com/vi/YzBBJYfwdi8/mqdefault.jpg" alt=""><br>Round Robin</a><br><sub>Give each job a turn with a fixed time limit.</sub><br><br></p>

<p align="center"><a href="https://www.youtube.com/watch?v=gHKSmz-W0x0"><img width="280" src="https://i.ytimg.com/vi/gHKSmz-W0x0/mqdefault.jpg" alt=""><br>Priority (non-preemptive)</a><br><sub>Pick by priority when the running job finishes.</sub><br><br></p>

<p align="center"><a href="https://www.youtube.com/watch?v=A860nFJ0FsA"><img width="280" src="https://i.ytimg.com/vi/A860nFJ0FsA/mqdefault.jpg" alt=""><br>Priority (preemptive)</a><br><sub>See when a higher-priority job can take over.</sub><br><br></p>

## From your choices to the results

The default chart uses boxes. Add `--text-chart` after `Main` to show one time interval per line.

```mermaid
flowchart TD
    A["Enter the number of jobs and choose an algorithm"] --> B{"Shortest Job First or Priority?"}
    B -->|Yes| C["Choose whether jobs can be paused"]
    B -->|No| D{"Enter values or randomize?"}
    C --> D
    D -->|Randomize| E["Program picks job values and a quantum for Round Robin"]
    D -->|Enter values| F["For Round Robin, enter a quantum first"]
    F --> T["For Priority, enter each job’s priority before its arrival and burst"]
    T --> G["Enter each job’s arrival and burst"]
    G --> S["Store the job values in CpuProcess"]
    E --> S
    S --> H{"Are any jobs ready?"}
    H -->|No| I["Mark the CPU idle until the next arrival"]
    I --> H
    H -->|Yes| J["Pick a job using the chosen rule"]
    J --> K["Run it until it finishes, uses its turn, or is paused by the rule"]
    K --> L["Record the interval and save the finish time if the job is done"]
    L --> M{"Have all jobs finished?"}
    M -->|No| H
    M -->|Yes| U["GanttChart prints the timeline"]
    U --> V["CpuProcess calculates turnaround and waiting times"]
    V --> N["Main prints results and averages"]
    N --> O{"Run again?"}
    O -->|Y| A
    O -->|N| P["Exit"]
    classDef main fill:#daeaff,color:#142740,stroke:#456897
    classDef process fill:#d9f3e2,color:#153629,stroke:#428060
    classDef scheduler fill:#ffedd5,color:#462812,stroke:#98642f
    classDef chart fill:#f0deff,color:#35184a,stroke:#80539b
    class A,B,C,D,E,F,T,G,N,O,P main
    class S,V process
    class H,I,J,K,L,M scheduler
    class U chart
```

**Class legend** · 🔵 `Main` reads input and shows results · 🟢 `CpuProcess` stores job data and calculates times · 🟠 `CpuScheduler` picks and runs jobs · 🟣 `GanttChart` shows the timeline.

## See how to code the algorithms

- **FCFS, SJF, and Round Robin.** Read how the program picks the next job, sorts jobs by length, and uses a queue for turns. [OSTEP scheduler source in Python](https://github.com/remzi-arpacidusseau/ostep-homework/blob/master/cpu-sched/scheduler.py)
- **All six modes in Java.** Follow the methods in this project. [Source guide](BRIEF.md)

## Run the program

Install [JDK 26](https://adoptium.net/) and [Apache Maven](https://maven.apache.org/install.html) 3.9 or later. The JDK is the tool that builds and runs Java programs. Maven builds this project. Run `java -version` and `mvn -version` to check your setup. The Maven output should show Java 26. A newer JDK also works if it can build for Java 26.

### Build and start the simulator

Open a terminal. You need access to this private repository to clone it. If you have a ZIP copy, unzip it, open a terminal in the folder with `pom.xml`, and start at step 3.

1. **Copy the project to your computer.**

   ```sh
   git clone https://github.com/miraflowwer/cpu-scheduler.git
   ```

2. **Open the project folder in the terminal.**

   ```sh
   cd cpu-scheduler
   ```

3. **Build the Java code.**

   ```sh
   mvn compile
   ```

4. **Start the program.**

   ```sh
   java -cp target/classes com.mycompany.cpuschedulerport.Main
   ```

---

Developed by [REQUEJO, Froilan B.](https://www.facebook.com/froilan.requejo) and [LARAÑO, Gabriel Ardee R.](https://www.facebook.com/ardeesiopaoenthusiast/) for Fundamentals of Programming.
