package com.mycompany.cpuschedulerport;

/*
CS-1A Fundamentals of Programming - CPU Scheduling
Developed by Froilan Requejo and Gabriel Laraño
*/

import java.util.Locale;
import java.util.Random;
import java.util.Scanner;

public class Main {
    private static final String[] ALGORITHMS = {
        "First Come First Serve", "Shortest Job First", "Round Robin", "Priority Scheduling"
    };

    public static void main(String[] args) {
        boolean textChart = args.length > 0 && args[0].equals("--text-chart");
        Scanner input = new Scanner(System.in);
        Random random = new Random();
        CpuScheduler scheduler = new CpuScheduler();
        System.out.printf("CPU Scheduling Simulator\n\n");
        int simulation = 0;

        do {
            System.out.printf("Select the amount of processes\n");
            int count = readNumber(input, "Number of processes", 1, 50);
            if (count == -1) return;
            System.out.printf("\nSelect your scheduling algorithm.\n\n");
            for (int i = 0; i < ALGORITHMS.length; i++) {
                int algorithmNumber = i + 1;
                String menuModeLabel;
                if (hasModeChoice(algorithmNumber)) {
                    menuModeLabel = "Choose a mode";
                } else {
                    menuModeLabel = fixedModeLabel(algorithmNumber) + " only";
                }
                System.out.printf("%d. %s (%s)\n", algorithmNumber, ALGORITHMS[i], menuModeLabel);
            }
            System.out.printf("\n");
            int algorithm = readNumber(input, "Scheduling algorithm", 1, ALGORITHMS.length);
            if (algorithm == -1) return;

            boolean preemptive = algorithm == 3;
            if (hasModeChoice(algorithm)) {
                System.out.printf("\nChoose a scheduling mode for %s.\n", ALGORITHMS[algorithm - 1]);
                if (algorithm == 4) {
                    System.out.printf("Smaller numbers indicate higher priority.\n");
                }
                System.out.printf("\n1. Non-preemptive (each process runs to completion)\n");
                if (algorithm == 2) {
                    System.out.printf("2. Preemptive (a shorter remaining time can interrupt the running process)\n\n");
                } else {
                    System.out.printf("2. Preemptive (a higher-priority process can interrupt the running process)\n\n");
                }
                int variant = readNumber(input, "Scheduling mode", 1, 2);
                if (variant == -1) return;
                preemptive = variant == 2;
            } else {
                System.out.printf("\n%s (%s only).\n", ALGORITHMS[algorithm - 1],
                                  fixedModeLabel(algorithm));
                if (preemptive) {
                    System.out.printf("An unfinished process returns to the ready queue when its time quantum expires.\n");
                } else {
                    System.out.printf("Each process runs to completion.\n");
                }
            }

            System.out.printf("\nHow would you like to provide the process values?\n\n");
            System.out.printf("1. Enter values\n");
            System.out.printf("2. Randomize values\n\n");
            int mode = readNumber(input, "Input mode", 1, 2);
            if (mode == -1) return;

            int quantum = 0;
            if (algorithm == 3) {
                if (mode == 1) {
                    System.out.printf("\n");
                    quantum = readNumber(input, "Time quantum in ms", 1, 99);
                } else {
                    quantum = random.nextInt(5) + 1;
                }
                if (quantum == -1) return;
            }
            CpuProcess[] processes = readProcesses(input, random, count, mode == 1, algorithm == 4);
            if (processes == null) return;

            GanttChart chart;
            switch (algorithm) {
                case 1: chart = scheduler.firstComeFirstServe(processes); break;
                case 2:
                    if (preemptive) {
                        chart = scheduler.shortestRemainingTime(processes);
                    } else {
                        chart = scheduler.shortestJobFirst(processes);
                    }
                    break;
                case 3: chart = scheduler.roundRobin(processes, quantum); break;
                default: chart = scheduler.priority(processes, preemptive); break;
            }

            System.out.printf("\n");
            String modeLabel;
            if (preemptive) {
                modeLabel = "Preemptive";
                if (algorithm == 2) {
                    modeLabel += " — Shortest Remaining Time";
                }
            } else {
                modeLabel = "Non-preemptive";
            }
            System.out.printf("Simulation %d — %s (%s)\n", ++simulation, ALGORITHMS[algorithm - 1], modeLabel);
            if (algorithm == 3) {
                System.out.printf("Time quantum = %d ms\n", quantum);
            }
            System.out.printf("\n");
            chart.print(textChart);
            printResults(processes, algorithm == 4);
            System.out.printf("\n");
        } while (runAgain(input));
    }

    private static String fixedModeLabel(int algorithm) {
        if (algorithm == 3) {
            return "Preemptive";
        }
        return "Non-preemptive";
    }

    private static boolean hasModeChoice(int algorithm) {
        return algorithm == 2 || algorithm == 4;
    }

    private static CpuProcess[] readProcesses(Scanner input, Random random, int count,
                                              boolean manual, boolean usePriority) {
        CpuProcess[] processes = new CpuProcess[count];
        for (int i = 0; i < count; i++) {
            String label = "P" + (i + 1);
            if (manual) System.out.printf("\n");
            int priority = 0;
            if (usePriority && manual) {
                priority = readNumber(input, String.format("%s priority", label), 1, 99);
                if (priority == -1) return null;
            }
            int arrival;
            if (manual) {
                arrival = readNumber(input, String.format("%s arrival time in ms", label), 0, 999);
            } else {
                arrival = random.nextInt(11);
            }
            if (arrival == -1) return null;
            int burst;
            if (manual) {
                burst = readNumber(input, String.format("%s burst time in ms", label), 1, 99);
            } else {
                burst = random.nextInt(10) + 1;
            }
            if (burst == -1) return null;

            if (usePriority && !manual) {
                priority = random.nextInt(5) + 1;
            }
            processes[i] = new CpuProcess(i + 1, arrival, burst, priority);
        }
        return processes;
    }

    private static int readNumber(Scanner input, String prompt, int min, int max) {
        while (true) {
            System.out.printf("%s (%d - %d): ", prompt, min, max);
            if (!input.hasNextLine()) {
                return -1;
            }
            try {
                int value = Integer.parseInt(input.nextLine().trim());
                if (value >= min && value <= max) {
                    return value;
                }
            } catch (NumberFormatException ex) {
                // The same prompt handles text and out-of-range numbers.
            }
            System.out.printf("Enter a whole number in the range %d - %d.\n", min, max);
        }
    }

    private static boolean runAgain(Scanner input) {
        while (true) {
            System.out.print("Run a new set of processes? (Y/N): ");
            if (!input.hasNextLine()) return false;
            String answer = input.nextLine().trim();
            if (answer.equalsIgnoreCase("Y")) {
                System.out.printf("\n");
                return true;
            }
            if (answer.equalsIgnoreCase("N")) return false;
            System.out.printf("Enter Y or N.\n");
        }
    }

    private static void printResults(CpuProcess[] processes, boolean usePriority) {
        String headerFormat = "%-7s  %12s  %10s  %15s  %15s  %12s";
        String rowFormat = "%-7s  %12d  %10d  %15d  %15d  %12d";
        String header = String.format(headerFormat, "Process", "Arrival (ms)", "Burst (ms)",
                                      "Completion (ms)", "Turnaround (ms)", "Waiting (ms)");
        if (usePriority) {
            header = String.format("%s  %8s", header, "Priority");
        }
        System.out.printf("\n\n%s\n\n", header);

        int totalArrival = 0;
        int totalBurst = 0;
        int totalCompletion = 0;
        int totalTurnaround = 0;
        int totalWaiting = 0;
        int totalPriority = 0;
        for (CpuProcess process : processes) {
            int turnaround = process.turnaround();
            int waiting = process.waiting();
            System.out.printf(rowFormat, String.format("P%d", process.id), process.arrival,
                              process.burst, process.completion, turnaround, waiting);
            if (usePriority) System.out.printf("  %8d", process.priority);
            System.out.printf("\n");

            totalArrival += process.arrival;
            totalBurst += process.burst;
            totalCompletion += process.completion;
            totalTurnaround += turnaround;
            totalWaiting += waiting;
            totalPriority += process.priority;
        }

        System.out.printf("%s\n", "_".repeat(header.length()));
        System.out.printf(rowFormat, "Total", totalArrival, totalBurst, totalCompletion,
                          totalTurnaround, totalWaiting);
        if (usePriority) System.out.printf("  %8d", totalPriority);
        System.out.printf("\n");

        System.out.printf(Locale.ROOT, "\nAverage turnaround time = %.2f ms\n",
                          (double) totalTurnaround / processes.length);
        System.out.printf(Locale.ROOT, "Average waiting time = %.2f ms\n",
                          (double) totalWaiting / processes.length);
    }
}
