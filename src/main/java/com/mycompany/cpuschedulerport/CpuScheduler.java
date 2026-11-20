package com.mycompany.cpuschedulerport;

/*
CS-1A Fundamentals of Programming - CPU Scheduling
Developed by Froilan Requejo and Gabriel Laraño
*/

import java.util.ArrayDeque;

class CpuScheduler {
    private static final int ARRIVAL = 1;
    private static final int BURST = 2;
    private static final int REMAINING = 3;
    private static final int PRIORITY = 4;

    GanttChart firstComeFirstServe(CpuProcess[] processes) {
        return schedule(processes, ARRIVAL, false);
    }

    GanttChart shortestJobFirst(CpuProcess[] processes) {
        return schedule(processes, BURST, false);
    }

    GanttChart shortestRemainingTime(CpuProcess[] processes) {
        return schedule(processes, REMAINING, true);
    }

    GanttChart priority(CpuProcess[] processes, boolean preemptive) {
        return schedule(processes, PRIORITY, preemptive);
    }

    private GanttChart schedule(CpuProcess[] processes, int rule, boolean preemptive) {
        GanttChart chart = new GanttChart(processes);
        int[] remaining = copyBurstTimes(processes);
        int time = 0;
        int finished = 0;
        int running = -1;

        while (finished < processes.length) {
            int selected = -1;
            int nextArrival = Integer.MAX_VALUE;
            for (int i = 0; i < processes.length; i++) {
                if (remaining[i] == 0) {
                    continue;
                }
                if (processes[i].arrival > time) {
                    nextArrival = Math.min(nextArrival, processes[i].arrival);
                } else if (selected == -1 || comesBefore(i, selected, processes, remaining, rule)) {
                    selected = i;
                }
            }

            if (selected == -1) {
                time = nextArrival;
                chart.add(0, time, true);
                continue;
            }
            if (preemptive && running != -1
                    && rank(selected, processes, remaining, rule) == rank(running, processes, remaining, rule)) {
                selected = running;
            }

            int duration = remaining[selected];
            if (preemptive) {
                duration = 1;
            }
            time += duration;
            remaining[selected] -= duration;
            chart.add(processes[selected].id, time, true);
            running = selected;
            if (remaining[selected] == 0) {
                processes[selected].completion = time;
                finished++;
                running = -1;
            }
        }
        return chart;
    }

    private int rank(int index, CpuProcess[] processes, int[] remaining, int rule) {
        switch (rule) {
            case BURST: return processes[index].burst;
            case REMAINING: return remaining[index];
            case PRIORITY: return processes[index].priority;
            default: return processes[index].arrival;
        }
    }

    private boolean comesBefore(int first, int second, CpuProcess[] processes, int[] remaining, int rule) {
        int firstRank = rank(first, processes, remaining, rule);
        int secondRank = rank(second, processes, remaining, rule);
        if (firstRank != secondRank) {
            return firstRank < secondRank;
        }
        if (processes[first].arrival != processes[second].arrival) {
            return processes[first].arrival < processes[second].arrival;
        }
        return processes[first].id < processes[second].id;
    }

    GanttChart roundRobin(CpuProcess[] processes, int quantum) {
        GanttChart chart = new GanttChart(processes);
        int[] remaining = copyBurstTimes(processes);
        int[] arrivals = arrivalOrder(processes, remaining);
        ArrayDeque<Integer> queue = new ArrayDeque<>();
        int next = 0;
        int time = 0;

        while (next < arrivals.length || !queue.isEmpty()) {
            if (queue.isEmpty()) {
                if (processes[arrivals[next]].arrival > time) {
                    time = processes[arrivals[next]].arrival;
                    chart.add(0, time, true);
                }
                while (next < arrivals.length && processes[arrivals[next]].arrival <= time) {
                    queue.addLast(arrivals[next++]);
                }
            }

            int current = queue.removeFirst();
            int duration = Math.min(quantum, remaining[current]);
            time += duration;
            remaining[current] -= duration;
            chart.add(processes[current].id, time, false);

            // New arrivals join the queue before the current process can rejoin.
            while (next < arrivals.length && processes[arrivals[next]].arrival <= time) {
                queue.addLast(arrivals[next++]);
            }
            if (remaining[current] == 0) {
                processes[current].completion = time;
            } else {
                queue.addLast(current);
            }
        }
        return chart;
    }

    private int[] arrivalOrder(CpuProcess[] processes, int[] remaining) {
        int[] order = new int[processes.length];
        for (int i = 0; i < order.length; i++) {
            int position = i;
            while (position > 0 && comesBefore(i, order[position - 1], processes, remaining, ARRIVAL)) {
                order[position] = order[position - 1];
                position--;
            }
            order[position] = i;
        }
        return order;
    }

    private int[] copyBurstTimes(CpuProcess[] processes) {
        int[] remaining = new int[processes.length];
        for (int i = 0; i < processes.length; i++) {
            remaining[i] = processes[i].burst;
        }
        return remaining;
    }
}
