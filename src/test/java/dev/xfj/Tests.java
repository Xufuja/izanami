package dev.xfj;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class Tests {
    @Test
    @DisplayName("Test")
    public void test() {
        System.out.println("Hello World!");
    }
}
