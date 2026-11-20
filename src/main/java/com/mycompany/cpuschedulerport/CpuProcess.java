package com.mycompany.cpuschedulerport;

/*
CS-1A Fundamentals of Programming - CPU Scheduling
Developed by Froilan Requejo and Gabriel Laraño
*/


class CpuProcess {
    final int id;
    final int arrival;
    final int burst;
    final int priority;
    int completion;

    CpuProcess(int id, int arrival, int burst, int priority) {
        this.id = id;
        this.arrival = arrival;
        this.burst = burst;
        this.priority = priority;
    }

    int turnaround() {
        return completion - arrival;
    }

    int waiting() {
        return turnaround() - burst;
    }
}
