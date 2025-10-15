package org.os;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;
import java.util.Scanner;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class Commands {

    public void help() {
        System.out.println("Available Commands:");
        System.out.println("pwd                  : Display current directory");
        System.out.println("cd <dir>             : Change to the specified directory");
        System.out.println("ls                   : List files in the current directory");
        System.out.println("ls -a                : List all files, including hidden files, in the current directory");
        System.out.println("ls -r                : Recursively list all files and directories");
        System.out.println("mkdir <dir>          : Create a new directory with the specified name");
        System.out.println("rmdir <dir>          : Remove an empty directory with the specified name");
        System.out.println("touch <file>         : Create a new file with the specified name");
        System.out.println("mv <src> <dest>      : Move or rename a file or directory from <src> to <dest>");
        System.out.println("rm <file>            : Remove a file with the specified name");
        System.out.println("cat <file>           : Display the content of the specified file");
        System.out.println("echo <text> > <file> : Redirects the output of 'echo' to a file (overwrites)");
        System.out.println("echo <text> >> <file>: Redirects the output of 'echo' to a file (appends)");
        System.out.println("grep <pattern> <file>: Search for a pattern in the specified file and display matching lines");
        System.out.println("zip <archive> <files> : Create a zip archive containing specified files/directories");
        System.out.println("unzip <archive> [dir] : Extract files from a zip archive to current or specified directory");
        System.out.println("exit                 : Terminate the command line interpreter");
        System.out.println("help                 : Display this help message");
    }





    public void ls(Path currentDirectory) {
        // Basic `ls` - lists only visible files and directories in the current directory
        try (Stream<Path> paths = Files.list(currentDirectory)) {
            paths.filter(path -> !path.getFileName().toString().startsWith("."))
                    .forEach(path -> {
                        if (Files.isDirectory(path)) {
                            System.out.println(path.getFileName() + "/");
                        } else {
                            System.out.println(path.getFileName());
                        }
                    });
        } catch (IOException e) {
            System.out.println("ls: Error reading directory");
        }
    }

    public void lsa(Path currentDirectory) {
        // `ls -a` - lists all files, including hidden files
        try (Stream<Path> paths = Files.list(currentDirectory)) {
            paths.forEach(path -> {
                if (Files.isDirectory(path)) {
                    System.out.println(path.getFileName() + "/");
                } else {
                    System.out.println(path.getFileName());
                }
            });
        } catch (IOException e) {
            System.out.println("ls -a: Error reading directory");
        }
    }

    public void lsr(Path currentDirectory) {
        // `ls -r` - recursively lists all files and directories
        try {
            Files.walk(currentDirectory).forEach(path -> {
                if (Files.isDirectory(path)) {
                    System.out.println(path + "/");
                } else {
                    System.out.println("  " + path);
                }
            });
        } catch (IOException e) {
            System.out.println("ls -r: Error reading directory");
        }
    }

    public void mkdir(String[] command, Path currentDirectory) {
        // Check if the directory name argument is provided
        if (command.length < 2) {
            System.out.println("mkdir: Missing directory argument (name)");
            return; // Exit if no directory name is given
        }

        // Resolve the directory path to create
        Path dirPath = currentDirectory.resolve(command[1]);

        // Check if the parent directory exists
        if (!Files.exists(currentDirectory)) {
            System.out.println("mkdir: Parent directory does not exist");
            return; // Exit if the parent directory doesn't exist
        }

        try {
            // Attempt to create the directory
            Files.createDirectory(dirPath);
            System.out.println("Directory created: " + dirPath);
        } catch (FileAlreadyExistsException e) {
            // Handle case where the directory already exists
            System.out.println("mkdir: Failed to create directory '" + command[1] + "': A directory with the same name already exists.");
        } catch (IOException e) {
            // Handle any other I/O errors during directory creation
            System.out.println("mkdir: An error occurred while creating the directory '" + command[1] + "'.");
        }
    }

    public void rmdir(String[] command, Path currentDirectory) {
        // Check if the directory argument is provided
        if (command.length < 2) {
            System.out.println("rmdir: Missing directory argument (name)");
            return; // Exit if no directory name is given
        }

        // Resolve the directory path to delete
        Path dirToDelete = currentDirectory.resolve(command[1]).normalize();

        // Check if the directory exists
        if (Files.notExists(dirToDelete)) {
            System.out.println("rmdir: Directory does not exist: '" + command[1] + "'");
            return; // Exit if the directory doesn't exist
        }

        // Check if the path is actually a directory
        if (!Files.isDirectory(dirToDelete)) {
            System.out.println("rmdir: Not a directory: '" + command[1] + "'");
            return; // Exit if the path is not a directory
        }

        try {
            // Attempt to delete the directory (only if it's empty)
            Files.delete(dirToDelete);
            System.out.println("Directory deleted: " + dirToDelete);
        } catch (DirectoryNotEmptyException e) {
            // Handle case where the directory is not empty
            System.out.println("rmdir: Directory is not empty: " + command[1]);
        } catch (IOException e) {
            // Handle any other I/O errors during deletion
            System.out.println("rmdir: Error deleting directory: " + command[1]);
        }
    }

    public void cat(String[] command, Path currentDirectory) {
        if (command.length < 2) {
            System.out.println("cat: Missing file argument");
            return;
        }
        Path filePath = currentDirectory.resolve(command[1]);
        try {
            Files.lines(filePath).forEach(System.out::println);
        } catch (IOException e) {
            System.out.println("cat: Failed to read file " + command[1]);
        }
    }

    public void touch(String fileName, Path currentDirectory) {
        Path filePath = currentDirectory.resolve(fileName);
        try {
            if (Files.exists(filePath)) {
                System.out.println("File already exists: " + fileName);
            } else {
                Files.createFile(filePath);
                System.out.println("File created: " + fileName);
            }
        } catch (IOException e) {
            System.out.println("An error occurred while creating the file: " + fileName);
            e.printStackTrace();
        }
    }

    public void rm(String fileName, Path currentDirectory) {
        Path path = currentDirectory.resolve(fileName);
        if (Files.notExists(path)) {
            System.out.println("File or directory does not exist: " + fileName);
            return;
        }

        try {
            if (Files.isDirectory(path)) {
                Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                        Files.delete(file);
                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                        Files.delete(dir);
                        return FileVisitResult.CONTINUE;
                    }
                });
                System.out.println("Directory and its contents deleted successfully: " + fileName);
            } else {
                Files.delete(path);
                System.out.println("File deleted successfully: " + fileName);
            }
        } catch (IOException e) {
            System.err.println("Failed to delete: " + fileName + " - " + e.getMessage());
        }
    }

    public void redirect(String input) {
        String[] parts;
        boolean append = input.contains(">>");

        if (append) {
            parts = input.split(">>");
        } else {
            parts = input.split(">");
        }

        if (parts.length < 2) {
            System.out.println("Invalid command format: " + input);
            return;
        }

        String command = parts[0].trim();
        String fileName = parts[1].trim();
        Path filePath = Paths.get(fileName);

        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath.toFile(), append))) {
            if (command.startsWith("echo")) {
                String message = command.substring(5).trim();
                writer.println(message);
                System.out.println("Message written to file: " + fileName);
            } else {
                System.out.println("Unknown command: " + command);
            }
        } catch (IOException e) {
            System.out.println("Error writing to file: " + e.getMessage());
        }
    }

    public void pwd(Path currentDirectory) {
        System.out.println(currentDirectory);
    }

    public Path cd(String[] command, Path currentDirectory) {
        if (command.length < 2) {
            System.out.println("cd: Missing directory argument");
            return currentDirectory;
        }
        Path newPath = currentDirectory.resolve(command[1]).normalize();
        if (Files.isDirectory(newPath)) {
            return newPath;
        } else {
            System.out.println("cd: No such directory: " + command[1]);
            return currentDirectory;
        }
    }

    public void mv(String[] command, Path currentDirectory) {
        if (command.length < 3) {
            System.out.println("mv: Missing source or destination argument");
            return;
        }
        Path sourcePath = currentDirectory.resolve(command[1]);
        Path destinationPath = currentDirectory.resolve(command[2]);
        try {
            Files.move(sourcePath, destinationPath);
            System.out.println("Moved " + sourcePath + " to " + destinationPath);
        } catch (IOException e) {
            System.out.println("mv: Failed to move " + command[1]);
        }
    }

    public void grep(String[] command, Path currentDirectory) {
        if (command.length < 3) {
            System.out.println("grep: Missing pattern or file argument");
            return;
        }
        String pattern = command[1];
        Path filePath = currentDirectory.resolve(command[2]);

        try (Stream<String> lines = Files.lines(filePath)) {
            lines.filter(line -> line.contains(pattern))
                    .forEach(System.out::println);
        } catch (IOException e) {
            System.out.println("grep: Failed to read file " + command[2]);
        }
    }

    public void zip(String[] command, Path currentDirectory) {
        if (command.length < 3) {
            System.out.println("zip: Usage: zip <archive_name> <file1> [file2] ...");
            return;
        }

        String archiveName = command[1];
        if (!archiveName.endsWith(".zip")) {
            archiveName += ".zip";
        }

        Path archivePath = currentDirectory.resolve(archiveName);

        try (ZipOutputStream zipOut = new ZipOutputStream(Files.newOutputStream(archivePath))) {
            // Add each file/directory to the zip
            for (int i = 2; i < command.length; i++) {
                Path sourcePath = currentDirectory.resolve(command[i]);
                if (Files.exists(sourcePath)) {
                    addToZip(sourcePath, zipOut, "");
                    System.out.println("Added to archive: " + command[i]);
                } else {
                    System.out.println("Warning: " + command[i] + " does not exist, skipping...");
                }
            }
            System.out.println("Zip archive created: " + archiveName);
        } catch (IOException e) {
            System.out.println("zip: Error creating archive: " + e.getMessage());
        }
    }

    private void addToZip(Path sourcePath, ZipOutputStream zipOut, String basePath) throws IOException {
        if (Files.isDirectory(sourcePath)) {
            // Add directory entry
            String dirName = basePath + sourcePath.getFileName().toString() + "/";
            zipOut.putNextEntry(new ZipEntry(dirName));
            zipOut.closeEntry();

            // Add all files in the directory
            try (Stream<Path> paths = Files.list(sourcePath)) {
                paths.forEach(path -> {
                    try {
                        addToZip(path, zipOut, dirName);
                    } catch (IOException e) {
                        System.err.println("Error adding " + path + " to zip: " + e.getMessage());
                    }
                });
            }
        } else {
            // Add file entry
            String fileName = basePath + sourcePath.getFileName().toString();
            zipOut.putNextEntry(new ZipEntry(fileName));
            Files.copy(sourcePath, zipOut);
            zipOut.closeEntry();
        }
    }

    public void unzip(String[] command, Path currentDirectory) {
        if (command.length < 2) {
            System.out.println("unzip: Usage: unzip <archive_name> [destination_directory]");
            return;
        }

        String archiveName = command[1];
        Path archivePath = currentDirectory.resolve(archiveName);

        if (!Files.exists(archivePath)) {
            System.out.println("unzip: Archive not found: " + archiveName);
            return;
        }

        // Determine destination directory
        Path destDir = currentDirectory;
        if (command.length > 2) {
            destDir = currentDirectory.resolve(command[2]);
            try {
                Files.createDirectories(destDir);
            } catch (IOException e) {
                System.out.println("unzip: Error creating destination directory: " + e.getMessage());
                return;
            }
        }

        try (ZipInputStream zipIn = new ZipInputStream(Files.newInputStream(archivePath))) {
            ZipEntry entry;
            while ((entry = zipIn.getNextEntry()) != null) {
                Path filePath = destDir.resolve(entry.getName());
                
                // Create parent directories if they don't exist
                Files.createDirectories(filePath.getParent());

                if (entry.isDirectory()) {
                    Files.createDirectories(filePath);
                    System.out.println("Created directory: " + entry.getName());
                } else {
                    Files.copy(zipIn, filePath, StandardCopyOption.REPLACE_EXISTING);
                    System.out.println("Extracted: " + entry.getName());
                }
                zipIn.closeEntry();
            }
            System.out.println("Extraction completed successfully!");
        } catch (IOException e) {
            System.out.println("unzip: Error extracting archive: " + e.getMessage());
        }
    }

}
