<p align="center">
  <a href="https://www.oracle.com/java/technologies/downloads/"><img src="assets/java.svg" height="112" alt="Download the Java Development Kit"></a>
  &nbsp;&nbsp;&nbsp;
  <a href="https://github.com/miraflowwer"><img src="assets/295236160.png" height="112" alt="miraflowwer on GitHub"></a>
  &nbsp;&nbsp;&nbsp;
  <a href="https://qcu.edu.ph/"><img src="assets/qcu.png" height="112" alt="Quezon City University official website"></a>
</p>

# CPU Scheduling Simulator

Compare six CPU scheduling modes with this Java console program. Enter your own processes or generate them randomly, then use the Gantt chart and timing results to see who runs next and how long each process waits.

<p align="center">
  <a href="TUTORIAL.md">Tutorial</a> ·
  <a href="BRIEF.md">Source guide</a> ·
  <a href="CPUScheduling_Larano_Froilan.pdf">Activity PDF</a>
</p>

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

<div align="center">
<table align="center">
  <tr>
    <td align="center" width="50%">
      <a href="https://www.youtube.com/watch?v=VSMAjMfJ6KQ"><img src="https://i.ytimg.com/vi/VSMAjMfJ6KQ/mqdefault.jpg" width="320" alt=""><br><strong>FCFS</strong></a><br>
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
</div>

### Read how to code

| Resource | What to look for |
| --- | --- |
| [OSTEP scheduling chapter (PDF)](https://pages.cs.wisc.edu/~remzi/OSTEP/cpu-sched.pdf) | Worked examples of FIFO, shortest-job scheduling, and Round Robin. |
| [OSTEP Python scheduler](https://github.com/remzi-arpacidusseau/ostep-homework/blob/master/cpu-sched/scheduler.py#L51-L120) | Follow how FIFO (also called FCFS) walks the job list, SJF sorts by job length, and Round Robin moves unfinished jobs to the back of the queue. |
| [All six modes in Java](BRIEF.md#find-the-algorithm-in-java) | Jump to each method and see how the simulator handles arrivals, ties, and preemption. |

## How the simulator works

```mermaid
---
config:
  flowchart:
    nodeSpacing: 24
    rankSpacing: 32
    wrappingWidth: 195
---
flowchart TD
    start(["Main.main()<br/>Read --text-chart and initialize the console"]):::main
    menu["Main.readNumber()<br/>Read process count and algorithm<br/>Retry invalid values"]
    variant{"Main.hasModeChoice()"}
    choose["Read preemptive or non-preemptive<br/>for Shortest Job First or Priority"]
    fixed["Main.fixedModeLabel()<br/>FCFS runs to completion<br/>Round Robin rotates at quantum expiry"]
    input["Read manual or random input mode<br/>For Round Robin, read quantum or generate 1–5 ms"]
    read["Main.readProcesses()<br/>Read or generate arrival and burst<br/>Set priority only in Priority modes"]
    process["CpuProcess()<br/>Store ID, arrival, burst, and priority<br/>Repeat for each process"]:::process
    dispatch{"Main.main()<br/>Which algorithm and mode?"}
    start --> menu --> variant
    variant -->|Yes| choose --> input
    variant -->|No| fixed --> input
    input --> read --> process --> dispatch

    subgraph rankedModes["FCFS, SJF, SRT, and Priority"]
        ranked["CpuScheduler.firstComeFirstServe() → arrival<br/>shortestJobFirst() → burst<br/>shortestRemainingTime() → remaining time<br/>priority() → priority number, with either mode"]:::scheduler
        shared["CpuScheduler.schedule()"]:::scheduler
        rankedChart["GanttChart()<br/>Allocate storage for interval IDs and end times"]:::chart
        init["copyBurstTimes()<br/>Initialize remaining times, clock, and running index"]:::scheduler
        pending{"Unfinished processes?"}:::scheduler
        scan["Scan unfinished processes<br/>comesBefore() uses rank(), then arrival, then ID<br/>Remember the earliest future arrival"]:::scheduler
        ready{"Any process ready?"}:::scheduler
        idle["Advance to next arrival<br/>GanttChart.add(0, time, true) records Idle"]:::chart
        run["In preemptive mode, keep the running process on a rank tie<br/>Run 1 ms if preemptive, otherwise run to completion<br/>Advance the clock and subtract the elapsed time"]:::scheduler
        rankedAdd["GanttChart.add(id, time, true)<br/>Extend the previous interval if its ID matches"]:::chart
        rankedDone{"Remaining time is zero?"}:::scheduler
        rankedFinish["Set CpuProcess.completion<br/>Increment finished and clear running index"]:::process
        ranked --> shared --> rankedChart --> init --> pending
        pending -->|Yes| scan --> ready
        ready -->|No| idle --> pending
        ready -->|Yes| run --> rankedAdd --> rankedDone
        rankedDone -->|Yes| rankedFinish --> pending
        rankedDone -->|No| pending
    end

    subgraph roundRobinMode["Round Robin"]
        rr["CpuScheduler.roundRobin()"]:::scheduler
        rrChart["GanttChart()<br/>Allocate storage for interval IDs and end times"]:::chart
        rrInit["copyBurstTimes() initializes remaining times<br/>arrivalOrder() sorts by arrival, then ID<br/>Start with an empty FIFO queue and clock at 0"]:::scheduler
        rrPending{"Future arrivals or queued jobs?"}:::scheduler
        empty{"Queue empty?"}:::scheduler
        jump{"Next arrival after clock?"}:::scheduler
        rrIdle["Advance to next arrival<br/>GanttChart.add(0, time, true) records Idle"]:::chart
        enqueue["Enqueue all processes that have arrived"]:::scheduler
        slice["Dequeue the front process<br/>Run for min(quantum, remaining)<br/>Advance the clock and subtract the elapsed time"]:::scheduler
        rrAdd["GanttChart.add(id, time, false)<br/>Keep each turn as a separate interval"]:::chart
        arrivals["Enqueue arrivals through the end of the slice"]:::scheduler
        rrDone{"Remaining time is zero?"}:::scheduler
        rrFinish["Set CpuProcess.completion"]:::process
        requeue["Put the unfinished process at the back"]:::scheduler
        rr --> rrChart --> rrInit --> rrPending
        rrPending -->|Yes| empty
        empty -->|Yes| jump
        jump -->|Yes| rrIdle --> enqueue
        jump -->|No| enqueue
        empty -->|No| slice
        enqueue --> slice --> rrAdd --> arrivals --> rrDone
        rrDone -->|Yes| rrFinish --> rrPending
        rrDone -->|No| requeue --> rrPending
    end

    dispatch --> ranked
    dispatch --> rr

    summary["Main.main()<br/>Print simulation number, mode, and Round Robin quantum"]:::main
    print{"GanttChart.print()<br/>--text-chart?"}:::chart
    intervals["printIntervals()<br/>Print one interval per line with its duration"]:::chart
    boxes["Print intervals in rows of up to four boxes"]:::chart
    labels["Both formats use label() for Idle or Pn<br/>and startTime() for 0 or the previous end time"]:::chart
    results["Main.printResults() loops through the processes"]:::main
    timing["CpuProcess.turnaround() = completion − arrival<br/>CpuProcess.waiting() = turnaround − burst"]:::process
    totals["Print each row, column totals, and average<br/>turnaround and waiting times"]:::main
    again{"Main.runAgain()<br/>Retry answers other than Y or N"}:::main
    repeat(["Start the next simulation<br/>Return to the process count prompt"]):::main
    exit(["Exit on N or end of input<br/>End of input also exits during setup"]):::main

    pending -->|No| summary
    rrPending -->|No| summary
    summary --> print
    print -->|Yes| intervals --> labels
    print -->|No| boxes --> labels
    labels --> results --> timing --> totals --> again
    again -->|Y| repeat
    again -->|N or end of input| exit

    subgraph legend["Legend"]
        direction LR
        legendMain["Main · console and results"]:::main
        legendProcess["CpuProcess · data and timing"]:::process
        legendScheduler["CpuScheduler · scheduling"]:::scheduler
        legendChart["GanttChart · intervals and display"]:::chart
        legendMain ~~~ legendProcess ~~~ legendScheduler ~~~ legendChart
    end
    exit ~~~ legend

    classDef main fill:#daeaff,color:#142740,stroke:#456897
    classDef process fill:#d9f3e2,color:#153629,stroke:#428060
    classDef scheduler fill:#ffedd5,color:#462812,stroke:#98642f
    classDef chart fill:#f0deff,color:#35184a,stroke:#80539b
    class menu,variant,choose,fixed,input,read,dispatch main
```

| Class | Role |
| --- | --- |
| [`Main`](BRIEF.md#follow-one-run) | Reads your choices, starts the schedule, and prints the results. |
| [`CpuProcess`](src/main/java/com/mycompany/cpuschedulerport/CpuProcess.java) | Stores process data and calculates turnaround and waiting times. |
| [`CpuScheduler`](BRIEF.md#find-the-algorithm-in-java) | Chooses which process runs, advances the clock, and records when processes finish. |
| [`GanttChart`](src/main/java/com/mycompany/cpuschedulerport/GanttChart.java) | Records and displays CPU intervals, including idle time. |

---

Developed by [REQUEJO, Froilan B.](https://www.facebook.com/froilan.requejo) and [LARAÑO, Gabriel Ardee R.](https://www.facebook.com/ardeesiopaoenthusiast/) for Fundamentals of Programming.
