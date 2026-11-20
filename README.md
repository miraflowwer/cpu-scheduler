# CPU Scheduling Simulator

When several processes are ready to run, which one gets the CPU next? This Java console simulator lets you give the same jobs to different scheduling algorithms and see how their decisions change who finishes first and how long each process waits.

[Learn the basics](#what-the-scheduler-does) · [Run the simulator](#run-the-simulator) · [Try the example](#try-the-same-jobs-six-ways) · [Read the Java code](#follow-the-java-code) · [Screenshot guide (PDF)](docs/CPU_Scheduling_Simulator.pdf)

## What the scheduler does

Imagine a single CPU and a line of jobs. Each job is a **process**, shown as `P1`, `P2`, and so on.

1. At its **arrival time**, a process becomes eligible to run. The **ready queue** is the set of processes that have arrived, still have work left, and are waiting for the CPU.
2. The scheduler picks a ready process using the selected algorithm. If nobody is ready, the CPU stays **idle** until the next arrival.
3. The running process uses some CPU time. Its **burst time** is the total CPU time it needs. Its **remaining time** is the part of that burst it still needs.
4. In a **non-preemptive** schedule, the selected process keeps the CPU until it finishes. In a **preemptive** schedule, the scheduler can pause a process and let another one run. In Round Robin, a **time quantum** is the maximum length of one turn.
5. The clock time when the process finishes is its **completion time**. The simulator uses that time to calculate turnaround and waiting time.

This model uses one CPU, one burst per process, and simulated milliseconds. It leaves out I/O and the time an operating system spends switching between processes.

## Run the simulator

Install a [JDK](https://adoptium.net/) that can compile for Java 26 and [Apache Maven](https://maven.apache.org/install.html) 3.9 or later. JDK 26 matches the project's build target. Use `java -version` and `mvn -version` to check your setup. Maven's output should show that it is using the intended JDK.

Open a terminal and run the following. Cloning requires access to this private repository. If you downloaded the project as a ZIP, extract it and open a terminal in the folder containing `pom.xml`, then run the last two commands.

```sh
git clone https://github.com/miraflowwer/cpu-scheduler.git
cd cpu-scheduler
mvn compile
java -cp target/classes com.mycompany.cpuschedulerport.Main
```

Add `--text-chart` after `Main` to print each time interval on a separate line instead of drawing boxes.

At the prompts, enter the process count and choose the algorithm by number. SJF and Priority ask you to choose a scheduling mode. Select **Enter values** to reproduce an example, or **Randomize values** to explore a new workload. Manual Round Robin asks for the quantum before the process values. Each process needs an arrival and a burst. Only Priority Scheduling asks for a priority value. After the results, use `Y` for another run or `N` to exit.

## Try the same jobs six ways

Enter these three processes in order. All times are in milliseconds.

| Process | Arrival | Burst | Priority (only for option 4) |
| --- | ---: | ---: | ---: |
| P1 | 0 | 6 | 3 |
| P2 | 1 | 3 | 1 |
| P3 | 2 | 1 | 2 |

To run **Shortest Remaining Time** first, enter `3` for the number of processes, `2` for Shortest Job First, `2` for preemptive mode, and `1` for manual input. Enter each process's arrival and burst on separate lines, in table order.

### How the choices change the timeline

An interval like `P1 0–1` means P1 runs from time 0 up to time 1. Read the intervals left to right. A process can appear more than once because it can pause and resume.

| Menu choice | What happens with these jobs | Gantt chart intervals (ms) |
| --- | --- | --- |
| **1 · First Come First Serve (FCFS)** | P1 starts first and finishes its burst. P2 arrived ahead of P3, so it goes next. | `P1 0–6 · P2 6–9 · P3 9–10` |
| **2 → 1 · Shortest Job First (SJF)** | At time 0, only P1 is ready. When it finishes, P3 has the shorter burst of the two waiting jobs. | `P1 0–6 · P3 6–7 · P2 7–10` |
| **2 → 2 · Shortest Remaining Time (SRT)** | P2 arrives with 3 ms of work while P1 still has 5 ms left. P3 then arrives with 1 ms while P2 has 2 ms left. After P3, P2 and then P1 resume. | `P1 0–1 · P2 1–2 · P3 2–3 · P2 3–5 · P1 5–10` |
| **3 · Round Robin (2 ms quantum)** | P1 uses its first turn. P2 and P3 have arrived by time 2, so they queue ahead of the returning P1. Each unfinished job goes to the back after its turn. | `P1 0–2 · P2 2–4 · P3 4–5 · P1 5–7 · P2 7–8 · P1 8–10` |
| **4 → 1 · Priority, non-preemptive** | P1 finishes its whole burst. P2 then goes ahead of P3 because a smaller number means a higher priority in this simulator. | `P1 0–6 · P2 6–9 · P3 9–10` |
| **4 → 2 · Priority, preemptive** | P2 interrupts P1 at time 1. P3 arrives while P2 is running and waits because P2 still has higher priority. When P2 finishes, P3 goes before P1. | `P1 0–1 · P2 1–4 · P3 4–5 · P1 5–10` |

For FCFS, SJF, SRT, and Priority, the simulator breaks equal ranks by earlier arrival and then lower process ID. When SRT or preemptive Priority finds a ready process tied with the running one, the running process keeps the CPU. In Round Robin, simultaneous arrivals enter in process ID order. Arrivals through the end of a slice, including the exact endpoint, join the queue before the unfinished process reenters it.

### Read the results table

- **Completion** is the end of the last interval for that process.
- **Turnaround** is completion minus arrival. It covers the process's entire time in the simulation, from arrival to finish.
- **Waiting** is turnaround minus burst. It adds up all the time spent ready and waiting, including any wait between turns.

For SRT, P2 runs from 1 to 2, waits while P3 runs, then finishes at 5. Its turnaround is `5 − 1 = 4 ms`. Its waiting time is `4 − 3 = 1 ms`.

| Process | Completion (ms) | Turnaround (ms) | Waiting (ms) |
| --- | ---: | ---: | ---: |
| P1 | 10 | 10 | 4 |
| P2 | 5 | 4 | 1 |
| P3 | 3 | 1 | 0 |

Add the per-process times and divide by 3 to get the averages. For these particular inputs, SRT reduces average waiting time from FCFS's 4.00 ms to 1.67 ms.

| Schedule | Average turnaround (ms) | Average waiting (ms) |
| --- | ---: | ---: |
| FCFS | 7.33 | 4.00 |
| SJF | 6.67 | 3.33 |
| SRT | 5.00 | 1.67 |
| Round Robin, quantum 2 | 6.67 | 3.33 |
| Priority, non-preemptive | 7.33 | 4.00 |
| Priority, preemptive | 5.33 | 2.00 |

## Follow the Java code

| Open this file | What to look for |
| --- | --- |
| [`Main.java`](src/main/java/com/mycompany/cpuschedulerport/Main.java) | Start at `main()`. It reads settings, builds the processes, calls the selected scheduler, and prints the results. |
| [`CpuProcess.java`](src/main/java/com/mycompany/cpuschedulerport/CpuProcess.java) | See which values each process stores and how `turnaround()` and `waiting()` use its completion time. |
| [`CpuScheduler.java`](src/main/java/com/mycompany/cpuschedulerport/CpuScheduler.java) | Follow `schedule()` as it scans eligible jobs, runs the best-ranked one, or advances to the next arrival when idle. Then read `roundRobin()` to see how the `ArrayDeque` takes jobs from the front and adds them at the back. |
| [`GanttChart.java`](src/main/java/com/mycompany/cpuschedulerport/GanttChart.java) | Follow `add()` as it records a process and its interval end. The print methods derive each interval start from the preceding interval's end. The chart keeps Round Robin turns visible as separate slices. |

**Try it yourself.** Keep the same inputs and change the Round Robin quantum from 2 to 1 ms. Predict the first three turns and P3's completion time before you run it. Compare the resulting waiting times with the 2 ms run.

Developed by Froilan Requejo and Gabriel Laraño for Fundamentals of Programming.
