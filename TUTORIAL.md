# CPU scheduling tutorial

[Home](README.md) · [Run the program](README.md#run-the-program) · [Source guide](BRIEF.md) · [Video lessons](README.md#video-lessons)

Run the same three processes through all six scheduling modes and see how the choice of algorithm changes their waiting times.

## Learn the basics

| Term | Meaning |
| --- | --- |
| **Process** | A job that needs CPU time. The simulator labels each one P1, P2, and so on. |
| **Arrival time** | When a process first becomes ready to run. The CPU stays idle while no process is ready. |
| **Burst time** | The total CPU time a process needs. **Remaining time** is how much of that burst it still needs. |
| **Non-preemptive** | The selected process keeps the CPU until it finishes. |
| **Preemptive** | The scheduler can pause the running process and give the CPU to another one. |
| **Time quantum** | The maximum length of one Round Robin turn. |

The simulator models one CPU and one burst per process. All times are in milliseconds (ms). It skips input/output waits and the time needed to switch processes.

## Run the example

Use these inputs for every mode.

| Process | Arrival (ms) | Burst (ms) | Priority |
| --- | ---: | ---: | ---: |
| P1 | 0 | 6 | 3 |
| P2 | 1 | 3 | 1 |
| P3 | 2 | 1 | 2 |

Start with **Shortest Remaining Time (SRT)**.

1. Enter `3` for the number of processes.
2. Choose `2` for **Shortest Job First**, then `2` for **Preemptive**.
3. Choose `1` to enter values manually.
4. Enter each arrival and burst on its own line. Enter `0` then `6` for P1, `1` then `3` for P2, and `2` then `1` for P3.

When you try **Priority**, enter each process’s priority before its arrival and burst. A smaller number means higher priority. For **Round Robin**, enter a quantum of `2` after choosing manual input and before entering the process values.

### Read the Gantt chart

Read the timeline from left to right. The first interval means P1 uses the CPU from 0 up to 1 ms. P1 appears again when it resumes at 5 ms. With [`--text-chart`](README.md#run-the-program), the SRT run prints these intervals.

```text
Gantt chart (ms)

P1  0 - 1 ms (1 ms)
P2  1 - 2 ms (1 ms)
P3  2 - 3 ms (1 ms)
P2  3 - 5 ms (2 ms)
P1  5 - 10 ms (5 ms)
```

P2 takes over at 1 ms because its 3 ms burst is shorter than P1’s 5 ms remaining. At 2 ms, P3 has 1 ms of work and P2 has 2 ms left, so P3 takes over. After P3 finishes, P2 resumes, followed by P1.

### Read the results

| Metric | How to find it | P2 in the SRT run |
| --- | --- | --- |
| **Completion** | End time of the process’s final interval | 5 ms |
| **Turnaround** | Completion − arrival | 5 − 1 = 4 ms |
| **Waiting** | Turnaround − burst | 4 − 3 = 1 ms |

| Process | Completion (ms) | Turnaround (ms) | Waiting (ms) |
| --- | ---: | ---: | ---: |
| P1 | 10 | 10 | 4 |
| P2 | 5 | 4 | 1 |
| P3 | 3 | 1 | 0 |

The average waiting time is `(4 + 1 + 0) ÷ 3 ≈ 1.67 ms`.

## Compare all six schedules

Each timeline entry gives the process and its start and end times in milliseconds. In the menu column, an arrow means to choose an algorithm and then its mode.

| Mode | Menu | Timeline (ms) |
| --- | --- | --- |
| First Come First Serve (FCFS) | `1` | `P1 0–6` · `P2 6–9` · `P3 9–10` |
| Shortest Job First (SJF) | `2 → 1` | `P1 0–6` · `P3 6–7` · `P2 7–10` |
| Shortest Remaining Time (SRT) | `2 → 2` | `P1 0–1` · `P2 1–2` · `P3 2–3` · `P2 3–5` · `P1 5–10` |
| Round Robin (2 ms quantum) | `3` | `P1 0–2` · `P2 2–4` · `P3 4–5` · `P1 5–7` · `P2 7–8` · `P1 8–10` |
| Priority (non-preemptive) | `4 → 1` | `P1 0–6` · `P2 6–9` · `P3 9–10` |
| Priority (preemptive) | `4 → 2` | `P1 0–1` · `P2 1–4` · `P3 4–5` · `P1 5–10` |

FCFS follows arrival order. In non-preemptive SJF, P1 is the only process ready at 0 ms, so P3 can take its turn only after P1 finishes. In non-preemptive Priority, P2 also waits for P1 to finish. In preemptive Priority, P2 can take over as soon as it arrives. In Round Robin, P2 and P3 join the queue before P1 returns to the back after its first turn.

<details>
<summary>How the simulator handles ties</summary>

- **FCFS, SJF, SRT, and Priority.** When two ready processes rank equally, the earlier arrival wins. If they arrived together, the lower process number wins.
- **SRT and preemptive Priority.** The running process keeps the CPU when another process ties with it.
- **Round Robin.** Simultaneous arrivals join in process number order. Any process arriving at the end of a turn joins the queue before the process that just ran can rejoin.

</details>

### Compare the averages

| Schedule | Average turnaround (ms) | Average waiting (ms) |
| --- | ---: | ---: |
| FCFS | 7.33 | 4.00 |
| SJF | 6.67 | 3.33 |
| SRT | 5.00 | 1.67 |
| Round Robin (2 ms quantum) | 6.67 | 3.33 |
| Priority (non-preemptive) | 7.33 | 4.00 |
| Priority (preemptive) | 5.33 | 2.00 |

For these inputs, SRT has the lowest average waiting time.

## Try it yourself

Run Round Robin again with a 1 ms quantum. Which processes get the first three turns? When does P3 finish?

<details>
<summary>Check your answer</summary>

The first three turns go to P1, P2, and P1. P3 runs from 3 to 4 ms, so it finishes at 4 ms.

</details>

[Trace P2 through the Java code](BRIEF.md#trace-p2-through-the-code)
