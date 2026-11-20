package com.mycompany.cpuschedulerport;

/*
CS-1A Fundamentals of Programming - CPU Scheduling
Developed by Froilan Requejo and Gabriel Laraño
*/


class GanttChart {
    private final int[] processIds;
    private final int[] endTimes;
    private int size;

    GanttChart(CpuProcess[] processes) {
        // Each busy segment uses at least one millisecond. Each idle gap ends at an arrival.
        int capacity = processes.length;
        for (CpuProcess process : processes) {
            capacity += process.burst;
        }
        processIds = new int[capacity];
        endTimes = new int[capacity];
    }

    void add(int processId, int endTime, boolean merge) {
        if (merge && size > 0 && processIds[size - 1] == processId) {
            endTimes[size - 1] = endTime;
        } else {
            processIds[size] = processId;
            endTimes[size] = endTime;
            size++;
        }
    }

    void print(boolean textOnly) {
        System.out.printf("Gantt chart (ms)\n\n");
        if (textOnly) {
            printIntervals();
            return;
        }

        int boxesPerRow = 4;
        for (int first = 0; first < size; first += boxesPerRow) {
            if (first > 0) System.out.printf("\n");
            int limit = Math.min(first + boxesPerRow, size);
            StringBuilder border = new StringBuilder("+");
            StringBuilder labels = new StringBuilder("|");
            StringBuilder times = new StringBuilder("|");
            for (int i = first; i < limit; i++) {
                border.append("----------------+");
                labels.append(String.format(" %-14s |", label(i)));
                String range = String.format("%d - %d ms", startTime(i), endTimes[i]);
                times.append(String.format(" %-14s |", range));
            }
            System.out.printf("%s\n", border);
            System.out.printf("%s\n", labels);
            System.out.printf("%s\n", times);
            System.out.printf("%s\n", border);
        }
    }

    private void printIntervals() {
        for (int i = 0; i < size; i++) {
            int start = startTime(i);
            int duration = endTimes[i] - start;
            System.out.printf("%s  %d - %d ms (%d ms)\n", label(i), start, endTimes[i], duration);
        }
    }

    private int startTime(int index) {
        if (index == 0) {
            return 0;
        }
        return endTimes[index - 1];
    }

    private String label(int index) {
        if (processIds[index] == 0) {
            return "Idle";
        }
        return "P" + processIds[index];
    }
}
