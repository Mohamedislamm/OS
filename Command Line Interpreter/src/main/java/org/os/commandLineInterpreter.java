package org.os;

import java.util.*;
import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;





public class commandLineInterpreter{





    public static void touch(String input) {
        try {
            File myfile = new File( input);
            if (myfile.createNewFile()) {
                System.out.println("File created:" + myfile.getName());
            } else {
                System.out.println("File already exists");
            }
        } catch (IOException e) {
            System.out.println("An error occured");
            e.printStackTrace();
        }
    }

    public static void rm(String input) {
        Path path = Paths.get(input);

        // Check if the file or directory exists
        if (!Files.exists(path)) {
            System.out.println("File or directory does not exist: " + input);
            return;
        }

        try {
            // Check if it's a directory
            if (Files.isDirectory(path)) {
                // Recursively delete the directory and its contents
                Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                        Files.delete(file);  // Delete the file
                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                        Files.delete(dir);  // Delete the directory after files have been deleted
                        return FileVisitResult.CONTINUE;
                    }
                });
                System.out.println("Directory and its contents deleted successfully: " + input);
            } else {
                // It's a file, so just delete it
                Files.delete(path);
                System.out.println("File deleted successfully: " + input);
            }

        } catch (IOException e) {
            System.err.println("Failed to delete: " + input + " - " + e.getMessage());
        }
    }


    public static void redirect(String input) {
        String[] parts;
        boolean append = false;

        if (input.contains(">>")) {
            parts = input.split(">>");
            append = true;  // Set append mode for ">>"
        } else if (input.contains(">")) {
            parts = input.split(">");
        } else {
            System.out.println("Invalid command format: " + input);
            return;
        }

        if (parts.length < 2) {
            System.out.println("Invalid command format: " + input);
            return;
        }

        String command = parts[0].trim();
        String name = parts[1].trim();

        try (PrintWriter myPrintWriter = new PrintWriter(new FileWriter(name, append))) {
            if (command.startsWith("echo")) {
                String message = command.substring(5).trim();  // Get the text after "echo "
                myPrintWriter.println(message);  // Print the message and add a new line
            } else {
                System.out.println("Unknown command: " + command);
            }
        } catch (IOException e) {
            System.out.println("Error writing to file: " + e.getMessage());
        }
    }




    public static void main(String[] args) {
        Scanner myObj = new Scanner(System.in);
        String input;

        while (true) {
            System.out.print("CLI> ");
            input = myObj.nextLine().trim();


            if (input.equals("exit")) {
                System.out.println("Exiting the CLI...");
                break;
            }
            else if(input.equals("help")){

                System.out.println("""
                rm: Removes each given fileor directory.
                touch:Creates a file with each given Name. 
                > : Redirects the output of the first command to be written to a file.
                >> : Like > but appends to the file if it exists.

                    
                """);

            }


            else if (input.startsWith("touch")) {
                touch(input.split("\\s+")[1]);
            }


            else if (input.contains(">")) {

                redirect(input);
            }


            else if (input.startsWith("rm")) {
                rm(input.split("\\s+")[1]);
            }

            // Handle unknown commands
            else {
                System.out.println("Unknown command: " + input);
            }
        }

        myObj.close();  // Close the scanner
    }
}



 