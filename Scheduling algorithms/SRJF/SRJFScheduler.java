package SRJF;

import java.util.Scanner;

class Process {
    String name, color;
    int arrivalTime, burstTime, remainingTime, waitingTime, turnaroundTime;
    boolean completed;

    public Process(String name, String color, int arrivalTime, int burstTime) {
        this.name = name;
        this.color = color;
        this.arrivalTime = arrivalTime;
        this.burstTime = burstTime;
        this.remainingTime = burstTime; // Initialize remaining time
        this.waitingTime = 0;
        this.turnaroundTime = 0;
        this.completed = false;
    }
}

public class SRJFScheduler {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Input number of processes
        System.out.print("Enter the number of processes: ");
        int n = scanner.nextInt();

        // Array of processes
        Process[] processes = new Process[n];

        // Input process details
        for (int i = 0; i < n; i++) {
            System.out.println("\nProcess " + (i + 1) + " details:");
            System.out.print("Name: ");
            String name = scanner.next();
            System.out.print("Color: ");
            String color = scanner.next();
            System.out.print("Arrival Time: ");
            int arrivalTime = scanner.nextInt();
            System.out.print("Burst Time: ");
            int burstTime = scanner.nextInt();

            processes[i] = new Process(name, color, arrivalTime, burstTime);
        }

        // Input context switching time
        System.out.print("\nEnter context switching time: ");
        int contextSwitchTime = scanner.nextInt();

        // Initialize variables for scheduling
        int currentTime = 0, completedProcesses = 0, previousProcess = -1;
        double totalWaitingTime = 0, totalTurnaroundTime = 0;
        StringBuilder executionOrder = new StringBuilder();

        System.out.println("\nStarting Scheduler...\n");

        // SRTF scheduling loop
        while (completedProcesses < n) {
            int shortestJobIndex = -1;
            int shortestRemainingTime = Integer.MAX_VALUE;

            // Find the process with the shortest remaining time among ready processes
            for (int i = 0; i < n; i++) {
                if (!processes[i].completed && processes[i].arrivalTime <= currentTime
                        && processes[i].remainingTime < shortestRemainingTime) {
                    shortestRemainingTime = processes[i].remainingTime;
                    shortestJobIndex = i;
                }
            }

            if (shortestJobIndex == -1) {
                // No process is ready; advance time
                System.out.println("Time " + currentTime + ": No process is ready, advancing time.");
                currentTime++;
                continue;
            }

            // Context switching logic
            if (previousProcess != -1 && previousProcess != shortestJobIndex) {
                System.out.println("Time " + currentTime + ": Context switching (from Process "
                        + processes[previousProcess].name + " to Process " + processes[shortestJobIndex].name + ")");
                currentTime += contextSwitchTime;
                executionOrder.append("[CS] -> ");
            }

            // Execute the selected process for one unit of time
            Process currentProcess = processes[shortestJobIndex];
            System.out.println("Time " + currentTime + ": Executing Process " + currentProcess.name
                    + " (Remaining Time: " + currentProcess.remainingTime + ")");
            executionOrder.append(currentProcess.name).append(" -> ");
            currentProcess.remainingTime--;
            currentTime++;

            // Check if the process is completed
            if (currentProcess.remainingTime == 0) {
                currentProcess.completed = true;
                completedProcesses++;
                currentProcess.turnaroundTime = currentTime - currentProcess.arrivalTime;
                currentProcess.waitingTime = currentProcess.turnaroundTime - currentProcess.burstTime;
                totalWaitingTime += currentProcess.waitingTime;
                totalTurnaroundTime += currentProcess.turnaroundTime;

                System.out.println("Time " + currentTime + ": Process " + currentProcess.name
                        + " completed. Turnaround Time: " + currentProcess.turnaroundTime
                        + ", Waiting Time: " + currentProcess.waitingTime);
            }

            // Update the previous process
            previousProcess = shortestJobIndex;
        }

        // Output results
        System.out.println("\nExecution Order: " + executionOrder.substring(0, executionOrder.length() - 4));

        System.out.println("\nProcess Details:");
        for (Process process : processes) {
            System.out.println("Name:" + process.name + " " + "Color:" + process.color + " "
                    + "Arrival:" + process.arrivalTime + " " + "Burst:" + process.burstTime
                    + " " + "Waiting:" + process.waitingTime + " " + "Turnaround:" + process.turnaroundTime);
        }

        System.out.println("\nAverage Waiting Time:" + totalWaitingTime / n);
        System.out.println("\nAverage Turnaround Time: " + totalTurnaroundTime / n);

        scanner.close();
    }
}
