# CPU Scheduling Simulator source guide

[Home](README.md) · [Run the program](README.md#run-the-program) · [Program flowchart](README.md#how-the-simulator-works) · [Activity PDF](CPUScheduling_Larano_Froilan.pdf)

[Main](#main) · [CpuProcess](#cpuprocess) · [CpuScheduler](#cpuscheduler) · [GanttChart](#ganttchart) · [Worked example](#worked-example)

## Follow one run

1. **Main** reads the menu choices and creates an array of **CpuProcess** objects. The array stays in input order, so P1 is always the first row in the results.
2. **CpuScheduler** chooses what runs on one simulated CPU. It tracks the clock and remaining burst times, writes each process’s completion time, and records execution and idle intervals in a **GanttChart**.
3. **Main** prints the chart, then asks each **CpuProcess** to calculate its turnaround and waiting time. It prints the individual results, totals, and averages before offering another run.

An *arrival* is the earliest time a process can run. Its *burst* is the total CPU time it needs, and its *remaining time* falls as it runs. A process is *ready* when it has arrived and still needs CPU time. The simulation uses milliseconds, a single CPU burst per process, and zero overhead for switching between processes.

## Main

[Open Main.java](src/main/java/com/mycompany/cpuschedulerport/Main.java). This is the console entry point.

| Method | Who calls it and what it does |
| --- | --- |
| [`main(String[] args)`](src/main/java/com/mycompany/cpuschedulerport/Main.java#L17-L110) | Java calls this at launch. It checks whether the first argument is `--text-chart`, creates the scanner, random generator, and scheduler, then enters the simulation loop. Each run reads a process count, scheduling mode, and input mode. It prepares a quantum for Round Robin, dispatches to the scheduler, prints the chart and results, and asks whether to run again. |
| [`hasModeChoice(int algorithm)`](src/main/java/com/mycompany/cpuschedulerport/Main.java#L116-L118) | `main()` calls it for menu labels and to decide whether to show the preemption prompt. Only Shortest Job First and Priority have a choice. Preemptive Shortest Job First dispatches to Shortest Remaining Time. |
| [`fixedModeLabel(int algorithm)`](src/main/java/com/mycompany/cpuschedulerport/Main.java#L112-L114) | `main()` uses it for the fixed-mode labels. Round Robin is preemptive because time slices can end before a process finishes. FCFS is non-preemptive and runs the selected process to completion. |
| [`readNumber(Scanner input, String prompt, int min, int max)`](src/main/java/com/mycompany/cpuschedulerport/Main.java#L154-L170) | `main()` and `readProcesses()` call it for numeric input. It reads and trims a whole line, parses an integer, and retries until the number is in the inclusive range. End of input returns `-1`. The callers use that sentinel to leave the run cleanly. |
| [`readProcesses(Scanner input, Random random, int count, boolean manual, boolean usePriority)`](src/main/java/com/mycompany/cpuschedulerport/Main.java#L120-L152) | `main()` passes in the process count and input choices. The method assigns IDs starting at 1 and creates a `CpuProcess` for each job. In manual Priority modes it reads priority before arrival and burst. Other modes store priority as `0`. If input ends partway through, it returns `null` and `main()` exits. |
| [`printResults(CpuProcess[] processes, boolean usePriority)`](src/main/java/com/mycompany/cpuschedulerport/Main.java#L186-L228) | `main()` calls it after printing the chart. It calculates turnaround and waiting for each process, prints columns in input order, and sums the values as it goes. The Priority column appears only in Priority modes. It divides the turnaround and waiting totals by the process count using floating-point arithmetic and prints both averages to two decimal places. |
| [`runAgain(Scanner input)`](src/main/java/com/mycompany/cpuschedulerport/Main.java#L172-L184) | The simulation loop calls it after each run. It accepts trimmed, case-insensitive `Y` and `N` and retries other answers. `Y` starts a new run with a fresh process array. `N` or end of input exits. |

The menu accepts 1–50 processes. Manual values are arrival 0–999 ms, burst 1–99 ms, priority 1–99, and Round Robin quantum 1–99 ms. Random values are arrival 0–10 ms, burst 1–10 ms, priority 1–5 when used, and quantum 1–5 ms for Round Robin.

## CpuProcess

[Open CpuProcess.java](src/main/java/com/mycompany/cpuschedulerport/CpuProcess.java). Each object describes one job.

| Method | Who calls it and what it does |
| --- | --- |
| [`CpuProcess(int id, int arrival, int burst, int priority)`](src/main/java/com/mycompany/cpuschedulerport/CpuProcess.java#L16-L21) | `Main.readProcesses()` calls the constructor once per job. It saves four fixed input fields. The scheduler later sets the separate, mutable `completion` field when the job finishes. |
| [`turnaround()`](src/main/java/com/mycompany/cpuschedulerport/CpuProcess.java#L23-L25) | `Main.printResults()` calls it to compute `completion - arrival`. This is the entire time the process spends in the system, including time on the CPU. |
| [`waiting()`](src/main/java/com/mycompany/cpuschedulerport/CpuProcess.java#L27-L29) | `Main.printResults()` calls it for waiting time. It calls `turnaround()` and subtracts the original `burst`, leaving the time spent waiting to run. |

The scheduler keeps changing remaining times in a separate array. Preserving the original burst lets the results show the input and calculate waiting time after the schedule.

## CpuScheduler

[Open CpuScheduler.java](src/main/java/com/mycompany/cpuschedulerport/CpuScheduler.java). Every mode returns a `GanttChart` and fills in completion times on the supplied processes.

### Scheduling methods called by Main

| Menu choice | Method | Selection and execution |
| --- | --- | --- |
| `1` · FCFS | [`firstComeFirstServe(processes)`](src/main/java/com/mycompany/cpuschedulerport/CpuScheduler.java#L16-L18) | Calls `schedule()` with the arrival rule. The earliest ready arrival runs to completion. |
| `2 → 1` · Shortest Job First | [`shortestJobFirst(processes)`](src/main/java/com/mycompany/cpuschedulerport/CpuScheduler.java#L20-L22) | Calls `schedule()` with the burst rule. The ready process with the smallest original burst runs to completion. |
| `2 → 2` · Shortest Remaining Time | [`shortestRemainingTime(processes)`](src/main/java/com/mycompany/cpuschedulerport/CpuScheduler.java#L24-L26) | Calls `schedule()` with the remaining-time rule and preemption enabled. It rechecks the ready processes every millisecond. |
| `3` · Round Robin | [`roundRobin(processes, quantum)`](src/main/java/com/mycompany/cpuschedulerport/CpuScheduler.java#L101-L137) | Runs its own first-in, first-out queue. The front process runs for up to one quantum. |
| `4 → 1` or `4 → 2` · Priority | [`priority(processes, preemptive)`](src/main/java/com/mycompany/cpuschedulerport/CpuScheduler.java#L28-L30) | Passes the Priority rule and the chosen preemption flag to `schedule()`. A smaller priority number ranks higher. The non-preemptive mode runs a full burst, and the preemptive mode rechecks every millisecond. |

### Shared scheduling loop

[`schedule(CpuProcess[] processes, int rule, boolean preemptive)`](src/main/java/com/mycompany/cpuschedulerport/CpuScheduler.java#L32-L78) creates a chart and a copy of the burst times, starts the clock at zero, and repeats until all processes finish.

1. It skips finished jobs, remembers the next future arrival, and compares jobs that have already arrived.
2. If none is ready, it jumps directly to the next arrival and records an idle interval with process ID `0`.
3. If a job is ready, it runs the chosen job for its full remaining time in a non-preemptive mode, or for 1 ms in a preemptive mode. In preemptive modes, the currently running job keeps the CPU if it ties the best ready job on the primary rank.
4. It advances the clock, reduces the job’s remaining time, and calls `chart.add(id, time, true)`. If the remaining time reaches zero, it sets `completion` to the current time, increments the finished count, and clears the running job.

| Helper | Who calls it and what it does |
| --- | --- |
| [`rank(int index, CpuProcess[] processes, int[] remaining, int rule)`](src/main/java/com/mycompany/cpuschedulerport/CpuScheduler.java#L80-L87) | `comesBefore()` uses it to read the comparison value for the selected rule. It returns arrival, original burst, remaining time, or priority. `schedule()` also uses it to check whether the running job ties the best ready job. |
| [`comesBefore(int first, int second, CpuProcess[] processes, int[] remaining, int rule)`](src/main/java/com/mycompany/cpuschedulerport/CpuScheduler.java#L89-L99) | `schedule()` uses it to choose the best ready job. Smaller rank comes first, then earlier arrival, then lower process ID. `arrivalOrder()` also uses this comparison with the arrival rule. |
| [`copyBurstTimes(CpuProcess[] processes)`](src/main/java/com/mycompany/cpuschedulerport/CpuScheduler.java#L152-L158) | Both scheduling loops call it at the start. It makes a mutable array of remaining CPU times with one entry per process. |

### Round Robin queue

`roundRobin()` keeps a clock, an `ArrayDeque<Integer>` of ready process indices, and a pointer to the next arrival. [`arrivalOrder(CpuProcess[] processes, int[] remaining)`](src/main/java/com/mycompany/cpuschedulerport/CpuScheduler.java#L139-L150) builds an array of indices using insertion sort. It orders them by arrival and then by process ID, leaving the original process array in input order.

If the queue is empty, `roundRobin()` jumps to the next arrival when needed, records any idle time, and enqueues the jobs that are now ready. It takes the front job, runs it for `min(quantum, remaining)`, and records the turn with `chart.add(id, time, false)`. It then enqueues all arrivals through the end of the turn **before** it puts an unfinished job back at the tail. For a finished job, it records the completion time. The loop ends when there are no future arrivals and the ready queue is empty.

## GanttChart

[Open GanttChart.java](src/main/java/com/mycompany/cpuschedulerport/GanttChart.java). The chart stores process IDs and end times in parallel arrays. The previous interval’s end is the next interval’s start.

| Method | Who calls it and what it does |
| --- | --- |
| [`GanttChart(CpuProcess[] processes)`](src/main/java/com/mycompany/cpuschedulerport/GanttChart.java#L14-L22) | Both scheduling loops create a new chart. The constructor reserves capacity equal to the total burst time plus the number of processes. Each busy interval uses at least 1 ms, and each idle gap ends at a process arrival, so that capacity covers the worst case. |
| [`add(int processId, int endTime, boolean merge)`](src/main/java/com/mycompany/cpuschedulerport/GanttChart.java#L24-L32) | The scheduler calls it after advancing the clock. When merging is enabled and the previous interval has the same ID, it extends that interval. Otherwise it appends the ID and end time. Round Robin passes `false` for CPU turns so separate turns stay visible. |
| [`print(boolean textOnly)`](src/main/java/com/mycompany/cpuschedulerport/GanttChart.java#L34-L59) | `main()` calls it when the schedule is complete. It prints a heading, delegates to `printIntervals()` in text mode, or prints a boxed timeline in rows of up to four intervals. |
| [`printIntervals()`](src/main/java/com/mycompany/cpuschedulerport/GanttChart.java#L61-L67) | `print()` calls it for `--text-chart`. It prints one interval per line with the label, start, end, and duration. |
| [`startTime(int index)`](src/main/java/com/mycompany/cpuschedulerport/GanttChart.java#L69-L74) | Both layouts call it. It returns zero for the first interval and the previous interval’s end time for every later interval. |
| [`label(int index)`](src/main/java/com/mycompany/cpuschedulerport/GanttChart.java#L76-L81) | Both layouts call it. It turns ID `0` into `Idle` and a process ID such as `2` into `P2`. |

## Worked example

[Start the simulator](README.md#run-the-program) with `--text-chart`. Enter `3` for the process count, `2` for Shortest Job First, `2` for Preemptive, and `1` for manual entry. Enter each arrival and burst separately, in row order.

| Process | Arrival (ms) | Burst (ms) |
| --- | ---: | ---: |
| P1 | 0 | 6 |
| P2 | 1 | 3 |
| P3 | 2 | 1 |

`Main.readProcesses()` creates the three jobs and `main()` calls `shortestRemainingTime()`. At 1 ms, P2’s 3 ms burst ranks ahead of P1’s 5 ms remaining. At 2 ms, P3’s 1 ms burst ranks ahead of P2’s 2 ms remaining. P3 finishes at 3 ms, P2 resumes and finishes at 5 ms, then P1 finishes at 10 ms. `GanttChart.add()` merges consecutive milliseconds belonging to the same process, so the chart shows five intervals.

```text
P1  0 - 1 ms (1 ms)
P2  1 - 2 ms (1 ms)
P3  2 - 3 ms (1 ms)
P2  3 - 5 ms (2 ms)
P1  5 - 10 ms (5 ms)
```

For P2, `turnaround()` gives `5 - 1 = 4 ms` and `waiting()` gives `4 - 3 = 1 ms`. The waiting times for P1, P2, and P3 are 4, 1, and 0 ms. `Main.printResults()` reports an average waiting time of 1.67 ms.

To follow the other loop in the debugger, reuse those inputs with menu option `3` and a quantum of 2 ms. When P1’s first turn ends, P2 and P3 have both arrived. The queue becomes P2, P3, P1 because arrivals join before P1 can rejoin.
