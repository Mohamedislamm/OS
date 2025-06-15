package org.os;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.


import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.*;

import static java.lang.Integer.*;

class parkingSystem {
    public int parkingSpots = 4;
    public Semaphore semaphore = new Semaphore(parkingSpots);
    int totalcars = 0;// Total number of cars served
    int carsInParking = 0; // Cars currently in parking
    int c1 = 0, c2 = 0, c3 = 0;// Cars served by each gate


    class car extends Thread {
        public int gate;
        public int car;
        public int ArrTime;
        public int Duration;

        car(int gate, int car, int ArrTime, int Duration) {
            this.gate = gate;
            this.car = car;
            this.ArrTime = ArrTime;
            this.Duration = Duration;
        }


        @Override
        public void run() {
            try {

                Thread.sleep(ArrTime * 1000);
                Thread.sleep(1);
                // Convert arrival time to milliseconds
                // Check if the parking lot is full (semaphore permits are zero)
                if (semaphore.availablePermits() == 0) {
                    System.out.println("Car " + car + " from Gate " + gate + " waiting for a spot.");
                }

                // Acquire a parking spot
                semaphore.acquire();
                totalcars++; // Increment total number of cars served
                carsInParking++; // Increment current cars in parking

                // Update the number of cars served by the gate
                if (gate == 1) c1++;
                if (gate == 2) c2++;
                if (gate == 3) c3++;

                System.out.println("Car " + car + " from Gate " + gate + " parked. (Parking Status: " + (parkingSpots - semaphore.availablePermits()) + " spots occupied)");


                // Simulate parking duration
                Thread.sleep(Duration * 1000); // Convert parking duration to milliseconds

                // Release the parking spot and leave the parking lot
                semaphore.release();
                carsInParking--; // Decrement current cars in parking
                System.out.println("Car " + car + " from Gate " + gate + " left after " + Duration + " units of time. (Parking Status: " + (parkingSpots - semaphore.availablePermits()) + " spots occupied)");


            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    class gate1 extends Thread {
        int car;
        int ArrTime;
        int Duration;


        gate1(int car, int ArrTime, int Duration) {
            this.car = car;
            this.ArrTime = ArrTime;
            this.Duration = Duration;
        }


        @Override
        public void run() {
            try {
                // Simulate car arrival time
                Thread.sleep(ArrTime * 1000); // Convert arrival time to milliseconds
                System.out.println("Car " + car + " from Gate " + 1 + " arrived at time " + ArrTime);


            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    class gate2 extends Thread {
        int car;
        int ArrTime;
        int Duration;


        gate2(int car, int ArrTime, int Duration) {
            this.car = car;
            this.ArrTime = ArrTime;
            this.Duration = Duration;
        }


        @Override
        public void run() {
            try {
                // Simulate car arrival time
                Thread.sleep(ArrTime * 1000); // Convert arrival time to milliseconds
                System.out.println("Car " + car + " from Gate " + 2 + " arrived at time " + ArrTime);


            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }



    class gate3 extends Thread {
        int car;
        int ArrTime;
        int Duration;


        gate3(int car, int ArrTime, int Duration) {
            this.car = car;
            this.ArrTime = ArrTime;
            this.Duration = Duration;
        }


        @Override
        public void run() {
            try {
                // Simulate car arrival time
                Thread.sleep(ArrTime * 1000); // Convert arrival time to milliseconds
                System.out.println("Car " + car + " from Gate " + 3 + " arrived at time " + ArrTime);


            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }



    void semulate(String filename) throws InterruptedException {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            List<Thread> carThreads = new ArrayList<>();
            List<Thread> gate1Threads = new ArrayList<>();
            List<Thread> gate2Threads = new ArrayList<>();
            List<Thread> gate3Threads = new ArrayList<>();
            while ((line = br.readLine()) != null) {
                // Split the line by commas
                String[] parts = line.split(",");

                // Extract the gate number from "Gate X"
                int gate = Integer.parseInt(parts[0].trim().split(" ")[1]); // This gets the X after "Gate"

                // Extract the car number from "Car X"
                int car = Integer.parseInt(parts[1].trim().split(" ")[1]); // This gets the X after "Car"

                // Extract the arrival time from "ArrTime X"
                int arrivTime = Integer.parseInt(parts[2].trim().split(" ")[1]); // This gets the X after "ArrTime"

                // Extract the duration from "Duration X"
                int Duration = Integer.parseInt(parts[3].trim().split(" ")[1]); // This gets the X after "Duration"

                // Start a car thread for each car
                car c = new car(gate, car, arrivTime, Duration);
                carThreads.add(c);

                if (gate == 1) {
                    gate1 g1 = new gate1(car, arrivTime, Duration);
                    gate1Threads.add(g1);
                    g1.start();
                    c.start();
                } else if (gate == 2) {
                    gate2 g2 = new gate2(car, arrivTime, Duration);
                    gate2Threads.add(g2);
                    g2.start();
                    c.start();
                } else if (gate == 3) {
                    gate3 g3 = new gate3(car, arrivTime, Duration);
                    gate3Threads.add(g3);
                    g3.start();
                    c.start();
                }
            }
            for(Thread t: gate1Threads) {
                t.join();
            }
            for(Thread t: gate2Threads) {
                t.join();
            }
            for(Thread t: gate3Threads) {
                t.join();
            }
            for (Thread t : carThreads) {
                t.join();
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    void print() {
        System.out.println("\n...");
        System.out.println("Total Cars Served:" + totalcars);
        System.out.println("Current Cars in Parking: " + (carsInParking));
        System.out.println("Details:");
        System.out.println("- Gate 1 served " + c1 + " cars.");
        System.out.println("- Gate 2 served " + c2 + " cars.");
        System.out.println("- Gate 3 served " + c3 + " cars.");
    }

}


public class Main {
    public static void main(String[] args) throws InterruptedException {
        parkingSystem obj = new parkingSystem();
        obj.semulate("test.txt.txt");
        obj.print();


    }
}