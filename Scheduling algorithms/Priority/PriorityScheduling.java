package Priority;

import java.util.*;

class Process {
    String name;
    int arrivalTime, burstTime, priority, waitingTime, turnaroundTime, completionTime;

    public Process(String name, int arrivalTime, int burstTime, int priority) {
        this.name = name;
        this.arrivalTime = arrivalTime;
        this.burstTime = burstTime;
        this.priority = priority;
    }
}

public class PriorityScheduling {
    public static void main(String[] args) {
        // Flag for auto test case
        boolean useAutoTestCase = true; // Set to true to use the auto test case, false for user input

        List<Process> processes = new ArrayList<>();

        if (useAutoTestCase) {
            // Auto Test Case
            processes.add(new Process("P1", 0, 3, 1)); // Arrival time: 0, Burst time: 5, Priority: 2
            processes.add(new Process("P2", 1, 4, 2)); // Arrival time: 1, Burst time: 3, Priority: 1
            processes.add(new Process("P3", 2, 2, 3)); // Arrival time: 2, Burst time: 4, Priority: 3


            System.out.println("Auto Test Case:");
        } else {
            // User Input (comment this block if using auto test case)
            Scanner scanner = new Scanner(System.in);

            System.out.print("Enter the number of processes: ");
            int n = scanner.nextInt();

            // User Input for each process
            for (int i = 0; i < n; i++) {
                System.out.println("Enter details for Process " + (i + 1) + ":");
                System.out.print("Name: ");
                String name = scanner.next();
                System.out.print("Arrival Time: ");
                int arrivalTime = scanner.nextInt();
                System.out.print("Burst Time: ");
                int burstTime = scanner.nextInt();
                System.out.print("Priority (lower value means higher priority): ");
                int priority = scanner.nextInt();

                processes.add(new Process(name, arrivalTime, burstTime, priority));
            }

            scanner.close(); // Close scanner when user input is used
        }

        // Execute the priority scheduling
        executePriorityScheduling(processes);
    }

    private static void executePriorityScheduling(List<Process> processes) {
        // Sort by arrival time, then by priority
        processes.sort(Comparator.comparingInt((Process p) -> p.arrivalTime)
                .thenComparingInt(p -> p.priority));

        int currentTime = 0;
        double totalWaitingTime = 0;
        double totalTurnaroundTime = 0;
        StringBuilder executionOrder = new StringBuilder(); // To track execution order

        // Process execution
        for (Process process : processes) {
            if (currentTime < process.arrivalTime) {
                currentTime = process.arrivalTime;  // Wait for the process to arrive
            }

            process.completionTime = currentTime + process.burstTime;
            process.waitingTime = currentTime - process.arrivalTime;
            process.turnaroundTime = process.completionTime - process.arrivalTime;

            totalWaitingTime += process.waitingTime;
            totalTurnaroundTime += process.turnaroundTime;

            currentTime += process.burstTime;  // Update current time after process execution

            // Add process name to the execution order
            executionOrder.append(process.name).append(" -> ");
        }

        // Output execution order in the old format (with "->" separator)
        System.out.println("\nExecution Order: " + executionOrder.substring(0, executionOrder.length() - 4));

        // Output process details in tabular form
        System.out.printf("\n%-15s %-15s %-15s %-15s %-15s\n", "Process", "Arrival Time", "Burst Time", "Waiting Time", "Turnaround Time");
        for (Process process : processes) {
            System.out.printf("%-15s %-15d %-15d %-15d %-15d\n",
                    process.name, process.arrivalTime, process.burstTime, process.waitingTime, process.turnaroundTime);
        }

        // Calculating and displaying averages
        System.out.printf("\nAverage Waiting Time: %.2f\n", totalWaitingTime / processes.size());
        System.out.printf("Average Turnaround Time: %.2f\n", totalTurnaroundTime / processes.size());
    }
}
