# SOURCE GUIDE

![Kurisu Makise from Steins;Gate in a retro computer lab lit by monitors and Nixie tubes](assets/source-guide-kurisu.jpg)

This guide follows one run of the Java CPU Scheduling Simulator from console input to the Gantt chart and results. It explains where each class fits, what the scheduler keeps in memory, and how to reproduce the decisions for a set of processes.

## Table of contents

1. [How a run fits together](#how-a-run-fits-together)
2. [Main](#main)
   - [Menu and input](#menu-and-input)
   - [Results and repeat runs](#results-and-repeat-runs)
3. [CpuProcess](#cpuprocess)
4. [CpuScheduler](#cpuscheduler)
   - [Algorithm selection](#algorithm-selection)
   - [Shared scheduling loop](#shared-scheduling-loop)
   - [Round Robin queue](#round-robin-queue)
   - [Work and storage](#work-and-storage)
5. [GanttChart](#ganttchart)
   - [Recording intervals](#recording-intervals)
   - [Printing intervals](#printing-intervals)
6. [Worked example](#worked-example)
   - [Shortest Remaining Time trace](#shortest-remaining-time-trace)
   - [Round Robin trace](#round-robin-trace)
   - [Idle gaps and equal ranks](#idle-gaps-and-equal-ranks)

## How a run fits together

1. `Main` reads the menu choices and builds a `CpuProcess[]` in input order. Process IDs start at 1, so the first row represents P1 throughout the run.
2. `CpuScheduler` starts a simulated clock at zero and copies each burst into a separate array of remaining times. It chooses which process runs, advances the clock, and records completion times on the process objects.
3. `GanttChart` collects the intervals as execution advances. `Main` prints that chart and calculates the results from the completed process objects.

The simulation models one CPU with one burst per process. All times are simulated milliseconds, with no I/O or time charged for context switches. A process becomes **ready** when its arrival time is at or before the current clock and it still has CPU time left. Several processes can be ready at once, but only one can execute during a given interval.

## Main

[Main.java](src/main/java/com/mycompany/cpuschedulerport/Main.java) is the console entry point. [`main()`](src/main/java/com/mycompany/cpuschedulerport/Main.java#L17-L110) creates a scanner, random generator, and scheduler before entering the simulation loop. The first command-line argument controls chart formatting. With `--text-chart`, each interval prints on its own line. The default is a boxed chart.

### Menu and input

1. Enter the number of processes and select an algorithm. [`hasModeChoice()`](src/main/java/com/mycompany/cpuschedulerport/Main.java#L116-L118) enables the mode prompt for Shortest Job First and Priority. [`fixedModeLabel()`](src/main/java/com/mycompany/cpuschedulerport/Main.java#L112-L114) labels FCFS as non-preemptive and Round Robin as preemptive.
2. Choose manual or random input. For Round Robin, the time quantum is entered or generated before the process values.
3. [`readProcesses()`](src/main/java/com/mycompany/cpuschedulerport/Main.java#L120-L152) constructs one `CpuProcess` per row. Manual Priority modes ask for priority, arrival, and burst in that order. Other manual modes ask for arrival and burst and store `0` in the priority field.
4. The [dispatch switch](src/main/java/com/mycompany/cpuschedulerport/Main.java#L82-L94) passes the process array to the selected scheduler method. Round Robin also receives the quantum, and Priority receives the preemption flag.

| Value | Manual entry | Randomized run |
| --- | ---: | ---: |
| Process count | 1–50 | Still entered by the user |
| Arrival | 0–999 ms | 0–10 ms |
| Burst | 1–99 ms | 1–10 ms |
| Priority, in Priority mode | 1–99 | 1–5 |
| Quantum, in Round Robin | 1–99 ms | 1–5 ms |

[`readNumber()`](src/main/java/com/mycompany/cpuschedulerport/Main.java#L154-L170) reads and trims a whole line, parses an integer, and retries when the value is invalid or outside the inclusive range. It returns `-1` when input ends. If that happens while reading a process, `readProcesses()` returns `null`, which ends the run before scheduling a partially populated array.

### Results and repeat runs

[`printResults()`](src/main/java/com/mycompany/cpuschedulerport/Main.java#L186-L228) walks the original array and prints arrival, burst, completion, turnaround, and waiting time for each process. It includes a Priority column only in Priority modes. While printing, it adds up the columns and then divides the turnaround and waiting totals by the process count using floating-point arithmetic. Both averages are displayed to two decimal places. The total in the completion column is the sum of all individual completion timestamps. The end of the schedule is the last time shown in the chart.

[`runAgain()`](src/main/java/com/mycompany/cpuschedulerport/Main.java#L172-L184) accepts trimmed, case-insensitive `Y` or `N`. A yes starts another iteration with a fresh process array and a new chart. A no or end of input exits.

## CpuProcess

[CpuProcess.java](src/main/java/com/mycompany/cpuschedulerport/CpuProcess.java) holds the input and final completion timestamp for one process.

| Field | Meaning |
| --- | --- |
| `id` | Identifies the process in the console, chart, and tie-breaking rules. |
| `arrival` | Earliest simulated time when the process can use the CPU. |
| `burst` | Original amount of CPU time required to finish. |
| `priority` | Numerical rank used by Priority scheduling. Smaller values rank higher. |
| `completion` | Clock time at the end of the process’s final interval. The scheduler fills this in. |

The [constructor](src/main/java/com/mycompany/cpuschedulerport/CpuProcess.java#L16-L21) assigns the four fixed input fields. The scheduler decrements values in its own `remaining[]` array, so the process object always retains the original burst for reporting.

- [`turnaround()`](src/main/java/com/mycompany/cpuschedulerport/CpuProcess.java#L23-L25) returns `completion - arrival`. This covers the entire time from arrival through the final execution interval.
- [`waiting()`](src/main/java/com/mycompany/cpuschedulerport/CpuProcess.java#L27-L29) returns `turnaround() - burst`. In this single-burst model, the result includes time spent waiting both before the first turn and between turns.

## CpuScheduler

[CpuScheduler.java](src/main/java/com/mycompany/cpuschedulerport/CpuScheduler.java) implements the scheduling policies. Every entry point fills in completion times on the supplied processes and returns a chart that records the timeline.

### Algorithm selection

| Menu choice | Scheduler method | Which ready process runs and for how long |
| --- | --- | --- |
| `1` | [`firstComeFirstServe()`](src/main/java/com/mycompany/cpuschedulerport/CpuScheduler.java#L16-L18) | The earliest arrival runs until it finishes. |
| `2 → 1` | [`shortestJobFirst()`](src/main/java/com/mycompany/cpuschedulerport/CpuScheduler.java#L20-L22) | The smallest original burst runs until the process finishes. |
| `2 → 2` | [`shortestRemainingTime()`](src/main/java/com/mycompany/cpuschedulerport/CpuScheduler.java#L24-L26) | The smallest remaining burst runs for 1 ms before the next comparison. |
| `3` | [`roundRobin()`](src/main/java/com/mycompany/cpuschedulerport/CpuScheduler.java#L101-L137) | The front of the ready queue runs until the quantum expires or the process finishes, whichever comes first. |
| `4 → 1` | [`priority(processes, false)`](src/main/java/com/mycompany/cpuschedulerport/CpuScheduler.java#L28-L30) | The smallest priority number runs until the process finishes. |
| `4 → 2` | [`priority(processes, true)`](src/main/java/com/mycompany/cpuschedulerport/CpuScheduler.java#L28-L30) | The smallest priority number runs for 1 ms before the next comparison. |

Shortest Job First and Shortest Remaining Time both favor short work. In the non-preemptive mode, a shorter job arriving during an active burst must wait until the CPU is free. In the preemptive mode, it can take over at the next decision if its burst is strictly less than the current process’s remaining time. The two Priority modes differ in the same way, using priority numbers for the comparison.

### Shared scheduling loop

[`schedule()`](src/main/java/com/mycompany/cpuschedulerport/CpuScheduler.java#L32-L78) handles FCFS, Shortest Job First, Shortest Remaining Time, and both Priority variants. The `rule` argument chooses how to compare ready processes, and `preemptive` controls the execution step.

1. **Initialize.** [`copyBurstTimes()`](src/main/java/com/mycompany/cpuschedulerport/CpuScheduler.java#L152-L158) puts each original burst into `remaining[]` at the same index as its process. The clock and finished count start at zero, with `running = -1` to indicate that no process owns the CPU.
2. **Scan.** Skip entries whose remaining time is zero. Remember the earliest future arrival and consider every unfinished process whose arrival is at or before the clock. [`rank()`](src/main/java/com/mycompany/cpuschedulerport/CpuScheduler.java#L80-L87) reads the primary comparison value. [`comesBefore()`](src/main/java/com/mycompany/cpuschedulerport/CpuScheduler.java#L89-L99) picks the smaller rank, breaking ties by earlier arrival and then lower process ID.
3. **Handle idle time.** If the scan finds no ready process, advance directly to the next arrival and record an interval with ID `0`. The next scan can then choose from the arrivals that just became ready.
4. **Execute.** In a preemptive mode, keep the running process if it ties the best ready process on the primary rank. A non-preemptive dispatch consumes the selected process’s entire remaining burst. A preemptive dispatch consumes 1 ms and then checks again. Advance the clock by the duration, subtract it from the remaining time, and record the interval.
5. **Finish.** When the remaining time reaches zero, save the new clock value as `completion`, increment the finished count, and clear `running`. The loop returns the chart after every process has completed.

The tie rule in step 4 applies only while a job is still running in a preemptive mode. When the CPU next becomes free, the usual arrival and ID tie-breakers apply.

### Round Robin queue

Round Robin’s state includes a FIFO `ArrayDeque<Integer>`, a clock, and a pointer called `next` to the first arrival that has yet to enter the queue. [`arrivalOrder()`](src/main/java/com/mycompany/cpuschedulerport/CpuScheduler.java#L139-L150) uses insertion sort to order process indices by arrival, then by ID. The original process array stays in input order for the results table.

1. If the queue is empty, advance to the next arrival when necessary, record any idle gap, and enqueue the processes ready at the new time.
2. Remove the first index and run it for `min(quantum, remaining[current])`. An arrival during that slice waits while the current job uses the rest of its turn. Advance the clock, reduce the remaining time, and append the turn to the chart.
3. Enqueue everyone who arrived through the end of the turn. Jobs that were waiting before the turn stay at the front. New arrivals go behind them, including a job that arrives exactly at the slice boundary.
4. Record completion if the current job finished. Otherwise, put it at the back of the queue after those new arrivals. Continue until the queue is empty and every arrival has been admitted.

This ordering matters even if two successive slices belong to the same process. Each slice reflects a separate dispatch, so the chart preserves both.

### Work and storage

For `n` processes, each selection in the shared loop scans up to `n` entries. A non-preemptive run has one busy selection per process, while a preemptive run has one per millisecond of CPU work. Each loop jumps over idle gaps. Round Robin’s insertion sort can take quadratic time in `n`. After sorting, queue work grows with the number of processes and time slices. All modes use a remaining-time array with `n` entries. The chart has room for up to the sum of all bursts plus `n` intervals.

## GanttChart

[GanttChart.java](src/main/java/com/mycompany/cpuschedulerport/GanttChart.java) stores a process ID and an end time for each segment in parallel arrays. The first segment starts at zero. Each later segment starts where the preceding one ends. A segment from 1 to 3 ms therefore represents 2 ms of elapsed time.

### Recording intervals

The [constructor](src/main/java/com/mycompany/cpuschedulerport/GanttChart.java#L14-L22) allocates enough space for one busy segment per millisecond of work and up to one idle gap per process. Each busy segment consumes at least 1 ms, and each idle gap ends when at least one process arrives.

[`add(processId, endTime, merge)`](src/main/java/com/mycompany/cpuschedulerport/GanttChart.java#L24-L32) receives the clock value after each step. When `merge` is true and the previous segment has the same ID, it extends that segment by updating its end time. For example, three consecutive 1 ms updates for P1 can become a single 3 ms segment. The shared scheduler uses this to keep the chart readable in preemptive modes. Round Robin passes `false` for every busy turn so quantum boundaries remain visible.

### Printing intervals

[`print()`](src/main/java/com/mycompany/cpuschedulerport/GanttChart.java#L34-L59) displays the default boxed timeline in groups of up to four intervals. In text mode it calls [`printIntervals()`](src/main/java/com/mycompany/cpuschedulerport/GanttChart.java#L61-L67), which prints one row per interval with the label, start, end, and duration. Both views use [`startTime()`](src/main/java/com/mycompany/cpuschedulerport/GanttChart.java#L69-L74) to derive the start and [`label()`](src/main/java/com/mycompany/cpuschedulerport/GanttChart.java#L76-L81) to map `0` to `Idle` and other IDs to labels such as `P2`.

## Worked example

From the project directory, compile with `mvn compile` and launch the text view with `java -cp target/classes com.mycompany.cpuschedulerport.Main --text-chart`. The [Maven configuration](pom.xml) targets Java 26. Use the same three processes in both traces.

| Process | Arrival (ms) | Burst (ms) |
| --- | ---: | ---: |
| P1 | 0 | 6 |
| P2 | 1 | 3 |
| P3 | 2 | 1 |

### Shortest Remaining Time trace

Open menu choices `2` (Shortest Job First), `2` (Preemptive), and `1` (manual input) after selecting a count of `3`. Enter each arrival and burst on separate lines in table order.

| Clock (ms) | Decision |
| ---: | --- |
| 0 | P1 is the only ready process and runs for 1 ms. |
| 1 | P2 arrives with 3 ms of work. P1 has 5 ms left, so P2 takes over. |
| 2 | P3 arrives with 1 ms of work. P2 has 2 ms left, so P3 takes over and finishes at 3 ms. |
| 3 | P2 resumes with 2 ms left, ahead of P1 with 5 ms left. P2 finishes at 5 ms. |
| 5 | P1 resumes and finishes at 10 ms. |

```text
P1  0 - 1 ms (1 ms)
P2  1 - 2 ms (1 ms)
P3  2 - 3 ms (1 ms)
P2  3 - 5 ms (2 ms)
P1  5 - 10 ms (5 ms)
```

P2 turns around in `5 - 1 = 4 ms` and waits for `4 - 3 = 1 ms`. Its only wait is from 2 to 3 ms while P3 executes.

| Process | Completion (ms) | Turnaround (ms) | Waiting (ms) |
| --- | ---: | ---: | ---: |
| P1 | 10 | 10 | 4 |
| P2 | 5 | 4 | 1 |
| P3 | 3 | 1 | 0 |
| Total | 18 | 15 | 5 |
| Average | | 5.00 | 1.67 |

### Round Robin trace

Select a count of `3`, algorithm `3` (Round Robin), and input mode `1` (manual). Enter a quantum of `2` ms, followed by the same arrivals and bursts. The queue entries below are shown front to back after each turn and before the next dispatch.

| Interval | What happens | Queue after turn |
| --- | --- | --- |
| 0–2 ms | P1 uses its quantum. P2 arrives at 1 ms and P3 arrives at 2 ms, so both join before P1 goes to the tail. | P2, P3, P1 |
| 2–4 ms | P2 uses its quantum and rejoins with 1 ms left. | P3, P1, P2 |
| 4–5 ms | P3 finishes after 1 ms. | P1, P2 |
| 5–7 ms | P1 uses another quantum and rejoins with 2 ms left. | P2, P1 |
| 7–8 ms | P2 finishes after its last millisecond. | P1 |
| 8–10 ms | P1 finishes after its last 2 ms. | Empty |

P1, P2, and P3 finish at 10, 8, and 5 ms. Their waiting times are 4, 4, and 2 ms. The average turnaround is 6.67 ms and the average waiting time is 3.33 ms. The total CPU time is still 10 ms because both runs execute the same bursts with no idle time.

### Idle gaps and equal ranks

- **Initial and later idle gaps.** Give P1 an arrival of 2 ms and a burst of 2 ms, and P2 an arrival of 6 ms and a burst of 1 ms. FCFS and Round Robin with a quantum of 2 ms both record `Idle 0–2`, `P1 2–4`, `Idle 4–6`, and `P2 6–7`. Both processes have zero waiting time because they execute as soon as they arrive.
- **Simultaneous arrivals.** In FCFS, two processes with arrival 0 are ordered by ID. With one millisecond of work each, the chart shows P1 from 0 to 1 ms, then P2 from 1 to 2 ms.
- **Equal rank during preemption.** In preemptive Priority, let P1 arrive at 1 ms with burst 1 and priority 3. Let P2 arrive at 0 ms with burst 4 and priority 3. At 1 ms P1 has the lower ID, but the two jobs have equal priority, so P2 keeps the CPU and finishes at 4 ms. P1 then runs from 4 to 5 ms.
