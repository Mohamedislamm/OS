package org.os;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.*;



import static org.junit.jupiter.api.Assertions.*;

class commandLineInterpreterTest {

    @Test
    void testtouchcase1() {
        String s ="test22";
        commandLineInterpreter.touch(s);
        assertTrue(Files.exists(Paths.get(s)));

    }


    // file already exist
    @Test
    void testtouchcase2() {
        String s ="test22";
        commandLineInterpreter.touch(s);
        assertTrue(Files.exists(Paths.get(s)));
        ByteArrayOutputStream content = new ByteArrayOutputStream();
        System.setOut(new PrintStream(content));

        commandLineInterpreter.touch(s);
        assertTrue(content.toString().contains("File already exists"));

    }


    @Test
    void testrmcase1() {

        String s ="test21";
        commandLineInterpreter.touch(s);
        commandLineInterpreter.rm(s);
        assertFalse(Files.exists(Paths.get(s)));
    }

    //file does not exist
    @Test
    void testrmcase2() {
        ByteArrayOutputStream content = new ByteArrayOutputStream();
        System.setOut(new PrintStream(content));

        commandLineInterpreter.rm("assdsa.txt");
        assertTrue(content.toString().contains("File or directory does not exist"));
    }


    // >
    @Test
    public void testRedirectcase1() throws IOException {
        String input = "echo ddkjk > testFile.txt";
        commandLineInterpreter.redirect(input);
        Path path = Paths.get("testFile.txt");
        assertTrue(Files.exists(path));


        String content = Files.readString(path).trim();
        System.out.println( content );

        assertEquals("ddkjk", content);
    }

    // >>
    @Test
    public void testRedirectcase2() throws IOException {



        String input = "echo aaaa >> testFile.txt";
        commandLineInterpreter.redirect(input);
        Path path = Paths.get("testFile.txt");
        assertTrue(Files.exists(path));


        String content = Files.readString(path).trim();
        System.out.println( content );

        assertTrue(content.contains("ddkjk"));
        assertTrue(content.contains("aaaa"));
    }


    @Test
    public void testRedirectcase3() throws IOException {



        String input = "bhgh aaaa >> testFile.txt";

        ByteArrayOutputStream content = new ByteArrayOutputStream();
        System.setOut(new PrintStream(content));
        commandLineInterpreter.redirect(input);
        assertTrue(content.toString().contains("Unknown command:"));

    }




}