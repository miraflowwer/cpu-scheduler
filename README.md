<p align="center">
  <img src="assets/java.svg" height="112" alt="Java">
  &nbsp;&nbsp;&nbsp;
  <a href="https://github.com/miraflowwer"><img src="assets/295236160.png" height="112" alt="miraflowwer on GitHub"></a>
  &nbsp;&nbsp;&nbsp;
  <img src="assets/qcu.png" height="112" alt="Quezon City University seal">
</p>

# CPU Scheduling Simulator

Compare six CPU scheduling modes with this Java console program. Enter your own processes or generate them randomly, then use the Gantt chart and timing results to see who runs next and how long each process waits.

[Tutorial](TUTORIAL.md) · [Source guide](BRIEF.md) · [Activity PDF](CPUScheduling_Larano_Froilan.pdf)

## Run the program

Install [JDK 26](https://adoptium.net/) and [Apache Maven 3.9 or later](https://maven.apache.org/install.html). Run `java -version` and `mvn -version` to check your setup. Maven should report Java 26 or a newer JDK that can build for Java 26.

If you have access to the repository, clone it and open the project directory.

```sh
git clone https://github.com/miraflowwer/cpu-scheduler.git
cd cpu-scheduler
```

If you have a ZIP copy, extract it and open a terminal in the folder containing `pom.xml`. Then build and start the simulator.

```sh
mvn compile
java -cp target/classes com.mycompany.cpuschedulerport.Main
```

The default chart uses boxes. Add `--text-chart` after `Main` to print one interval per line. For a set of inputs you can try immediately, use the [tutorial’s three-process example](TUTORIAL.md#run-the-example).

## Learn the scheduling algorithms

### Video lessons

Select a preview or title to watch the lesson on YouTube.

<table>
  <tr>
    <td align="center" width="50%">
      <a href="https://www.youtube.com/watch?v=VSMAjMfJ6KQ"><img src="https://i.ytimg.com/vi/VSMAjMfJ6KQ/mqdefault.jpg" width="320" alt=""><br><strong>First Come First Serve</strong></a><br>
      <sub>Run processes in arrival order.</sub>
    </td>
    <td align="center" width="50%">
      <a href="https://www.youtube.com/watch?v=t0g9b3SJECg"><img src="https://i.ytimg.com/vi/t0g9b3SJECg/mqdefault.jpg" width="320" alt=""><br><strong>Shortest Job First</strong></a><br>
      <sub>Choose the ready process with the shortest burst.</sub>
    </td>
  </tr>
  <tr>
    <td align="center" width="50%">
      <a href="https://www.youtube.com/watch?v=lpM14aWgl3Q"><img src="https://i.ytimg.com/vi/lpM14aWgl3Q/mqdefault.jpg" width="320" alt=""><br><strong>Shortest Remaining Time</strong></a><br>
      <sub>Work through a solved example of preemption.</sub>
    </td>
    <td align="center" width="50%">
      <a href="https://www.youtube.com/watch?v=YzBBJYfwdi8"><img src="https://i.ytimg.com/vi/YzBBJYfwdi8/mqdefault.jpg" width="320" alt=""><br><strong>Round Robin</strong></a><br>
      <sub>Give each ready process a turn with a time limit.</sub>
    </td>
  </tr>
  <tr>
    <td align="center" width="50%">
      <a href="https://www.youtube.com/watch?v=gHKSmz-W0x0"><img src="https://i.ytimg.com/vi/gHKSmz-W0x0/mqdefault.jpg" width="320" alt=""><br><strong>Priority Scheduling (Non-preemptive)</strong></a><br>
      <sub>Choose by priority when the current process finishes.</sub>
    </td>
    <td align="center" width="50%">
      <a href="https://www.youtube.com/watch?v=A860nFJ0FsA"><img src="https://i.ytimg.com/vi/A860nFJ0FsA/mqdefault.jpg" width="320" alt=""><br><strong>Priority Scheduling (Preemptive)</strong></a><br>
      <sub>See when a higher-priority process can take over.</sub>
    </td>
  </tr>
</table>

### Read the implementations

| Resource | What to look for |
| --- | --- |
| [OSTEP scheduling chapter (PDF)](https://pages.cs.wisc.edu/~remzi/OSTEP/cpu-sched.pdf) | Worked examples of FIFO, shortest-job scheduling, and Round Robin. |
| [OSTEP Python scheduler](https://github.com/remzi-arpacidusseau/ostep-homework/blob/master/cpu-sched/scheduler.py#L51-L120) | Follow how FIFO (also called FCFS) walks the job list, SJF sorts by job length, and Round Robin moves unfinished jobs to the back of the queue. |
| [All six modes in Java](BRIEF.md#find-the-algorithm-in-java) | Jump to each method and see how the simulator handles arrivals, ties, and preemption. |

## How the simulator works

```mermaid
flowchart TD
    input["Main<br/>Read the mode and process inputs"]
    processes["CpuProcess<br/>Store arrival, burst, and priority"]
    scheduler["CpuScheduler<br/>Run the schedule and record completion times"]
    chart["GanttChart<br/>Record and print CPU intervals"]
    results["Main<br/>Print turnaround, waiting, and averages"]

    input --> processes --> scheduler --> chart --> results

    classDef main fill:#daeaff,color:#142740,stroke:#456897
    classDef process fill:#d9f3e2,color:#153629,stroke:#428060
    classDef scheduler fill:#ffedd5,color:#462812,stroke:#98642f
    classDef chart fill:#f0deff,color:#35184a,stroke:#80539b
    class input,results main
    class processes process
    class scheduler scheduler
    class chart chart
```

| Class | Role |
| --- | --- |
| [`Main`](BRIEF.md#follow-one-run) | Reads your choices, starts the schedule, and prints the results. |
| [`CpuProcess`](src/main/java/com/mycompany/cpuschedulerport/CpuProcess.java) | Stores process data and calculates turnaround and waiting times. |
| [`CpuScheduler`](BRIEF.md#find-the-algorithm-in-java) | Chooses which process runs, advances the clock, and records when processes finish. |
| [`GanttChart`](src/main/java/com/mycompany/cpuschedulerport/GanttChart.java) | Records and displays CPU intervals, including idle time. |

---

Developed by [REQUEJO, Froilan B.](https://www.facebook.com/froilan.requejo) and [LARAÑO, Gabriel Ardee R.](https://www.facebook.com/ardeesiopaoenthusiast/) for Fundamentals of Programming.
