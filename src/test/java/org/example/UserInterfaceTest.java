package org.example;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.swing.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
class UserInterfaceTest {

    private UserInterface userInterface;

    @BeforeEach
    void setUp() throws IOException {
        userInterface = new UserInterface();
        userInterface.processTextFile("C:\\Users\\25650\\Desktop\\软件工程\\实验\\lab1\\test2.txt");
    }
    @Test
    public void test1() {
        String result = userInterface.queryBridgeWords("hello", "world");
        assertEquals("No \"hello\" and \"world\" in the graph!", result);
    }
    @Test
    public void test2() {
        String result = userInterface.queryBridgeWords("to", "world");
        assertEquals("No \"world\" in the graph!", result);
    }
    @Test
    public void test3() {
        String result = userInterface.queryBridgeWords("to", "fox");
        assertEquals("No bridge words from \"to\" to \"fox\"!", result);
    }
    @Test
    public void test4() {
        String result = userInterface.queryBridgeWords("rest", "catch");
        assertEquals("The bridge words from \"rest\" to \"catch\" is: under", result);
    }
    @Test
    public void test5() {
        String result = userInterface.queryBridgeWords("to", "under");
        assertEquals("The bridge words from \"to\" to \"under\" are: rest, catch", result);
    }
    @Test
    public void test6() {
        String result = userInterface.queryBridgeWords("world", "to");
        assertEquals("No \"world\" in the graph!", result);
    }
}