# CPU Scheduling Simulator

Several jobs need to use one CPU. Which job runs first? This Java program lets you try six schedules and see when each job runs, waits, and finishes.

[Learn the basics](#learn-the-basics) · [Run the program](#run-the-program) · [Try the example](#try-the-example) · [Read the code](#read-the-code) · [Screenshot guide (PDF)](docs/CPU_Scheduling_Simulator.pdf)

## Learn the basics

A **process** is a job that needs time on the CPU. The program calls the jobs **P1**, **P2**, and so on. The **scheduler** decides which job gets the CPU next.

1. A job joins the **ready queue** at its **arrival time**. The ready queue holds jobs that can run and are waiting for the CPU.
2. The scheduler chooses one ready job. If no job is ready, the CPU stays **idle** until the next job arrives.
3. A job's **burst time** is the total CPU time it needs. Its **remaining time** is the CPU time it still needs after it has run for a while.
4. In a **non-preemptive** schedule, a job runs until it finishes. In a **preemptive** schedule, the scheduler can pause a job and give the CPU to another job. Round Robin gives each job a turn. The **time quantum** is the longest a turn can last.
5. The time when a job finishes is its **completion time**.

The program uses one CPU and one burst per job. All times are in milliseconds (ms). The model skips input/output waits and the time it takes to switch jobs.

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

## Try the example

Use these three jobs for every run. Enter each value on its own line. Enter the priority values only for option 4.

| Job | Arrival (ms) | Burst (ms) | Priority |
| --- | ---: | ---: | ---: |
| P1 | 0 | 6 | 3 |
| P2 | 1 | 3 | 1 |
| P3 | 2 | 1 | 2 |

For your first run, try **Shortest Remaining Time**. Enter `3` for the job count, `2` for Shortest Job First, `2` for preemptive mode, and `1` for manual input. Then enter `0`, `6`, `1`, `3`, `2`, and `1` for the three jobs.

### Read the Gantt chart

A **Gantt chart** is a timeline. It shows who uses the CPU and when. Read it from left to right. `P1 0–1` means P1 runs from time 0 up to time 1. A job can appear again after a pause.

| Menu choice | Why the jobs run in this order | Time intervals (ms) |
| --- | --- | --- |
| **1 · First Come First Serve (FCFS)** | P1 arrives first and runs until it finishes. P2 arrived before P3, so P2 goes next. | `P1 0–6 · P2 6–9 · P3 9–10` |
| **2 → 1 · Shortest Job First (SJF)** | Only P1 is ready at time 0, so it runs until time 6. P3 goes next because its burst is shorter than P2's. | `P1 0–6 · P3 6–7 · P2 7–10` |
| **2 → 2 · Shortest Remaining Time (SRT)** | P2 arrives with 3 ms of work. P1 has 5 ms left, so P2 takes over. At time 2, P3 has 1 ms of work and P2 has 2 ms left. P3 takes over. When P3 finishes, P2 and then P1 finish their work. | `P1 0–1 · P2 1–2 · P3 2–3 · P2 3–5 · P1 5–10` |
| **3 · Round Robin (quantum 2 ms)** | P1 runs for up to 2 ms. By the end of that turn, P2 and P3 have arrived. They get in line before P1 returns to the back. | `P1 0–2 · P2 2–4 · P3 4–5 · P1 5–7 · P2 7–8 · P1 8–10` |
| **4 → 1 · Priority, non-preemptive** | A lower number means higher priority. P1 finishes its whole burst. P2 goes before P3 because 1 is lower than 2. | `P1 0–6 · P2 6–9 · P3 9–10` |
| **4 → 2 · Priority, preemptive** | P2 has higher priority and takes over at time 1. P3 waits for P2 to finish, then goes before P1. | `P1 0–1 · P2 1–4 · P3 4–5 · P1 5–10` |

A **tie** happens when two jobs are equally good choices. In FCFS, SJF, SRT, and Priority, the earlier arrival wins a tie. If both arrived at the same time, the lower job number wins. In SRT and preemptive Priority, the running job keeps the CPU if another job ties with it.

Round Robin puts jobs that arrive at the same time in job number order. If a job arrives just as a turn ends, it joins the queue before the job that just ran goes back in line.

### Read the results

- **Completion** is the end time of a job's last turn.
- **Turnaround** is the time from arrival to completion. Subtract arrival from completion.
- **Waiting** is the total time the job spent ready but waiting for the CPU. Subtract burst from turnaround.

In the SRT run, P2 arrives at 1 ms and finishes at 5 ms. Its turnaround is `5 − 1 = 4 ms`. Its burst is 3 ms, so it waited `4 − 3 = 1 ms`.

| Job | Completion (ms) | Turnaround (ms) | Waiting (ms) |
| --- | ---: | ---: | ---: |
| P1 | 10 | 10 | 4 |
| P2 | 5 | 4 | 1 |
| P3 | 3 | 1 | 0 |

To find the average waiting time, add the three waiting times and divide by 3. For these jobs, SRT has the lowest average waiting time. Try a different set of jobs to see how the results change.

| Schedule | Average turnaround (ms) | Average waiting (ms) |
| --- | ---: | ---: |
| FCFS | 7.33 | 4.00 |
| SJF | 6.67 | 3.33 |
| SRT | 5.00 | 1.67 |
| Round Robin, quantum 2 ms | 6.67 | 3.33 |
| Priority, non-preemptive | 7.33 | 4.00 |
| Priority, preemptive | 5.33 | 2.00 |

## Read the code

| Java file | Where to start |
| --- | --- |
| [`Main.java`](src/main/java/com/mycompany/cpuschedulerport/Main.java) | Start at `main()`. It reads your choices, creates the jobs, calls the scheduler, and prints the results. |
| [`CpuProcess.java`](src/main/java/com/mycompany/cpuschedulerport/CpuProcess.java) | See what the program stores for each job. Read `turnaround()` and `waiting()` to find the two formulas. |
| [`CpuScheduler.java`](src/main/java/com/mycompany/cpuschedulerport/CpuScheduler.java) | Read `schedule()` to see how the program picks a ready job or waits for the next arrival. Then read `roundRobin()`. It uses an `ArrayDeque`, a Java queue that lets the program take a job from the front and add it to the back. |
| [`GanttChart.java`](src/main/java/com/mycompany/cpuschedulerport/GanttChart.java) | Read `add()` to see how the chart stores each turn's end time. A turn begins where the last one ends. The chart shows each Round Robin turn on its own. |

**Try it yourself.** Run Round Robin with the same jobs and a 1 ms quantum. Which jobs get the first three turns? When does P3 finish? Make a guess before you run it.

<details>
<summary>Check your answer</summary>

The first three turns are P1, P2, and P1. P3 runs from 3 to 4 ms, so it finishes at 4 ms.

</details>

Developed by Froilan Requejo and Gabriel Laraño for Fundamentals of Programming.
