package Fcai;

import java.util.*;

class Process {
    String name, color;
    int arrivalTime, burstTime, remainingTime, priority, waitingTime, turnaroundTime;
    int quantum;

    public Process(String name, String color, int arrivalTime, int burstTime, int priority) {
        this.name = name;
        this.color = color;
        this.arrivalTime = arrivalTime;
        this.burstTime = burstTime;
        this.remainingTime = burstTime;
        this.priority = priority;
        this.quantum = (int) Math.ceil(burstTime / 5.0); // Initial quantum calculation
    }
}

public class FCAIScheduler {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Input: Number of processes
        System.out.print("Enter the number of processes: ");
        int n = scanner.nextInt();

        // Input: Process details
        Process[] processes = new Process[n];
        for (int i = 0; i < n; i++) {
            System.out.println("\nEnter details for Process " + (i + 1) + ":");
            System.out.print("Name: ");
            String name = scanner.next();
            System.out.print("Color: ");
            String color = scanner.next();
            System.out.print("Arrival Time: ");
            int arrivalTime = scanner.nextInt();
            System.out.print("Burst Time: ");
            int burstTime = scanner.nextInt();
            System.out.print("Priority: ");
            int priority = scanner.nextInt();

            processes[i] = new Process(name, color, arrivalTime, burstTime, priority);
        }

        // Input: Context switching time
        System.out.print("\nEnter context switching time: ");
        int contextSwitchTime = scanner.nextInt();

        // Calculate V1 and V2
        double V1 = calculateV1(processes);
        double V2 = calculateV2(processes);

        // Print initial FCAI factors
        System.out.println("\nInitial Calculations:");
        System.out.printf("V1 = %.2f, V2 = %.2f\n", V1, V2);
        System.out.println("Process\tBurst Time\tArrival Time\tPriority\tQuantum\tInitial FCAI Factor");
        for (Process p : processes) {
            double factor = calculateFCAIFactor(p, V1, V2);
            System.out.printf("%s\t%d\t\t%d\t\t%d\t\t%d\t\t%.2f\n", p.name, p.burstTime, p.arrivalTime, p.priority, p.quantum, factor);
        }

        // Simulate FCAI Scheduling
        simulateFCAIScheduling(processes, V1, V2, contextSwitchTime);

        scanner.close();
    }

    // Simulation of FCAI Scheduling
    private static void simulateFCAIScheduling(Process[] processes, double V1, double V2, int contextSwitchTime) {
        int currentTime = 0, completed = 0;
        double totalWaitingTime = 0, totalTurnaroundTime = 0;
        PriorityQueue<Process> queue = new PriorityQueue<>(Comparator.comparingDouble(p -> calculateFCAIFactor(p, V1, V2)));

        // Add processes to the queue when they arrive
        List<Process> arrivedProcesses = new ArrayList<>();
        while (completed < processes.length) {
            // Add processes that have arrived
            for (Process p : processes) {
                if (!arrivedProcesses.contains(p) && p.arrivalTime <= currentTime) {
                    arrivedProcesses.add(p);
                    queue.add(p);
                }
            }

            // If no process is ready, increment time
            if (queue.isEmpty()) {
                currentTime++;
                continue;
            }

            // Pick the process with the smallest FCAI factor
            Process current = queue.poll();
            double factorBefore = calculateFCAIFactor(current, V1, V2);

            // Execute the process for its quantum or remaining time
            int executionTime = Math.min(current.quantum, current.remainingTime);
            current.remainingTime -= executionTime;
            currentTime += executionTime;

            // Calculate the FCAI factor after execution
            double factorAfter = calculateFCAIFactor(current, V1, V2);

            // Print execution details
            System.out.printf("Time %d–%d: %s executed for %d units, Remaining: %d, Quantum: %d -> %d, FCAI: %.2f -> %.2f\n",
                    currentTime - executionTime, currentTime, current.name, executionTime, current.remainingTime,
                    current.quantum, current.remainingTime > 0 ? current.quantum + 2 : current.quantum, factorBefore, factorAfter);

            // Update quantum or mark process as completed
            if (current.remainingTime > 0) {
                current.quantum += (executionTime == current.quantum) ? 2 : (current.quantum - executionTime);
                queue.add(current); // Re-add process to queue
            } else {
                current.turnaroundTime = currentTime - current.arrivalTime;
                current.waitingTime = current.turnaroundTime - current.burstTime;
                totalWaitingTime += current.waitingTime;
                totalTurnaroundTime += current.turnaroundTime;
                completed++;
                System.out.printf("Process %s completed: Turnaround Time = %d, Waiting Time = %d\n", current.name, current.turnaroundTime, current.waitingTime);
            }

            // Add context switch time
            currentTime += contextSwitchTime;
        }

        // Print summary
        System.out.println("\nSummary:");
        for (Process p : processes) {
            System.out.printf("Process %s: Waiting Time = %d, Turnaround Time = %d\n", p.name, p.waitingTime, p.turnaroundTime);
        }
        System.out.printf("\nAverage Waiting Time = %.2f\n", totalWaitingTime / processes.length);
        System.out.printf("Average Turnaround Time = %.2f\n", totalTurnaroundTime / processes.length);
    }

    // Helper methods to calculate V1, V2, and FCAI Factor
    private static double calculateV1(Process[] processes) {
        int maxArrival = Arrays.stream(processes).mapToInt(p -> p.arrivalTime).max().orElse(1);
        return maxArrival / 10.0;
    }

    private static double calculateV2(Process[] processes) {
        int maxBurst = Arrays.stream(processes).mapToInt(p -> p.burstTime).max().orElse(1);
        return maxBurst / 10.0;
    }

    private static double calculateFCAIFactor(Process p, double V1, double V2) {
        return Math.ceil((10 - p.priority) + (p.arrivalTime / V1) + (p.remainingTime / V2));
    }
}
