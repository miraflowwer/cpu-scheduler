# Java source guide

[Home](README.md) · [Tutorial](TUTORIAL.md) · [Run the program](README.md#run-the-program) · [Activity PDF](CPUScheduling_Larano_Froilan.pdf)

Follow a simulation from the console inputs to the Gantt chart and timing results.

## Follow one run

| Step | Where to read | What happens |
| --- | --- | --- |
| Read the input | [`Main.main()`](src/main/java/com/mycompany/cpuschedulerport/Main.java#L17) and [`readProcesses()`](src/main/java/com/mycompany/cpuschedulerport/Main.java#L120) | Read the menu choices, create a `CpuProcess` for each job, and call the selected scheduling method. |
| Run the schedule | [`CpuScheduler.schedule()`](src/main/java/com/mycompany/cpuschedulerport/CpuScheduler.java#L32) and [`roundRobin()`](src/main/java/com/mycompany/cpuschedulerport/CpuScheduler.java#L101) | Choose ready processes, advance the clock, and save each completion time. When none are ready, jump to the next arrival and record an idle interval. |
| Build and print the chart | [`GanttChart.add()`](src/main/java/com/mycompany/cpuschedulerport/GanttChart.java#L24) and [`print()`](src/main/java/com/mycompany/cpuschedulerport/GanttChart.java#L34) | Store each interval’s process ID and end time. An interval starts where the previous one ends. Consecutive intervals for the same process can merge, while Round Robin keeps each turn visible. |
| Calculate and display results | [`CpuProcess.turnaround()` and `waiting()`](src/main/java/com/mycompany/cpuschedulerport/CpuProcess.java#L23-L29) and [`Main.printResults()`](src/main/java/com/mycompany/cpuschedulerport/Main.java#L186) | Subtract arrival from completion for turnaround, then subtract burst from turnaround for waiting. Print the per-process results, totals, and averages. |

## Find the algorithm in Java

| Mode | Method in `CpuScheduler` | How it chooses |
| --- | --- | --- |
| First Come First Serve | [`firstComeFirstServe()`](src/main/java/com/mycompany/cpuschedulerport/CpuScheduler.java#L16-L18) | Earliest arrival runs to completion. |
| Shortest Job First | [`shortestJobFirst()`](src/main/java/com/mycompany/cpuschedulerport/CpuScheduler.java#L20-L22) | Shortest burst among ready processes runs to completion. |
| Shortest Remaining Time | [`shortestRemainingTime()`](src/main/java/com/mycompany/cpuschedulerport/CpuScheduler.java#L24-L26) | Recheck every millisecond. A shorter remaining time can take over. |
| Round Robin | [`roundRobin()`](src/main/java/com/mycompany/cpuschedulerport/CpuScheduler.java#L101-L137) | Take the next process from the front of an `ArrayDeque` and run it for up to one quantum. Put new arrivals at the back before requeuing an unfinished process. |
| Priority (non-preemptive) | [`priority(processes, false)`](src/main/java/com/mycompany/cpuschedulerport/CpuScheduler.java#L28-L30) | Ready process with the lowest priority number runs to completion. |
| Priority (preemptive) | [`priority(processes, true)`](src/main/java/com/mycompany/cpuschedulerport/CpuScheduler.java#L28-L30) | Recheck every millisecond. A lower priority number can take over. |

FCFS, SJF, SRT, and both Priority modes share [`schedule()`](src/main/java/com/mycompany/cpuschedulerport/CpuScheduler.java#L32). It checks which processes have arrived, uses [`rank()`](src/main/java/com/mycompany/cpuschedulerport/CpuScheduler.java#L80) to compare them, and uses [`comesBefore()`](src/main/java/com/mycompany/cpuschedulerport/CpuScheduler.java#L89) to break ties by arrival and then process number. In preemptive modes, a tie with the running process lets it keep the CPU.

## Trace P2 through the code

In the [tutorial’s SRT run](TUTORIAL.md#read-the-gantt-chart), P2 arrives at 1 ms and finishes at 5 ms.

1. At **1 ms**, `schedule()` compares P2’s 3 ms burst with P1’s 5 ms remaining and gives the CPU to P2.
2. At **2 ms**, P3 arrives with 1 ms of work and takes over. The chart has recorded P2’s first interval, **1–2 ms**.
3. At **3 ms**, P2 resumes and runs until **5 ms**. The scheduler saves `completion = 5`, and the chart shows P2’s second interval as **3–5 ms**.
4. `CpuProcess` uses those values to calculate P2’s turnaround (**4 ms**) and waiting time (**1 ms**).

[Compare FCFS, SJF, and Round Robin with the OSTEP Python implementation](README.md#read-the-implementations)
