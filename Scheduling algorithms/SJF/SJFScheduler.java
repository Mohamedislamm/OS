package SJF;

import java.util.Scanner;

class Process {
    String name, color;
    int arrivalTime, burstTime, waitingTime, turnaroundTime;
    boolean completed;

    public Process(String name, String color, int arrivalTime, int burstTime) {
        this.name = name;
        this.color = color;
        this.arrivalTime = arrivalTime;
        this.burstTime = burstTime;
        this.waitingTime = 0;
        this.turnaroundTime = 0;
        this.completed = false;
    }
}

public class SJFScheduler {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Input the number of processes
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

        // Initialize variables for scheduling
        int currentTime = 0, completedProcesses = 0;
        double totalWaitingTime = 0, totalTurnaroundTime = 0;
        StringBuilder executionOrder = new StringBuilder();

        // SJF scheduling
        while (completedProcesses < n) {
            int shortestJobIndex = -1;
            int shortestBurstTime = Integer.MAX_VALUE;

            // Find the process with the shortest burst time among the ready processes
            for (int i = 0; i < n; i++) {
                if (!processes[i].completed && processes[i].arrivalTime <= currentTime 
                        && processes[i].burstTime < shortestBurstTime) {
                    shortestBurstTime = processes[i].burstTime;
                    shortestJobIndex = i;
                }
            }

            if (shortestJobIndex == -1) {
                // No process is ready; advance time
                currentTime++;
                continue;
            }

            // Execute the selected process
            Process currentProcess = processes[shortestJobIndex];
            executionOrder.append(currentProcess.name).append(" -> ");
            currentTime += currentProcess.burstTime;
            currentProcess.turnaroundTime = currentTime - currentProcess.arrivalTime;
            currentProcess.waitingTime = currentProcess.turnaroundTime - currentProcess.burstTime;

            // Update totals and mark process as completed
            totalWaitingTime += currentProcess.waitingTime;
            totalTurnaroundTime += currentProcess.turnaroundTime;
            currentProcess.completed = true;
            completedProcesses++;
        }

        // Output results
        System.out.println("\nExecution Order: " + executionOrder.substring(0, executionOrder.length() - 4));

        System.out.println("\nProcess Details:");
        for (Process process : processes) {
            System.out.println( "Name:" +process.name +" " +"Color:" +  process.color +" " +"Arrival:" + process.arrivalTime +" " + "Burst:" +process.burstTime + " " +"Waiting:" + process.waitingTime + " " +"Turnaround:" + process.turnaroundTime);
        }

        System.out.println("\nAverage Waiting Time:" + totalWaitingTime / n);
        System.out.println("\nAverage Turnaround Time: " + totalTurnaroundTime / n);

        scanner.close();
    }
}
