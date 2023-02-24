package dev.xfj;

import dev.xfj.format.pmx.PMXFile;
import dev.xfj.parsing.PMXParser;
import dev.xfj.writer.PMXWriter;

import java.io.IOException;
import java.nio.file.Path;

public class Main {
    public static void main(String[] args) throws IOException {
        PMXParser pmxParser = new PMXParser(Path.of("test_in_01.pmx"));
        PMXFile pmxFile = pmxParser.parse();
        PMXWriter pmxWriter = new PMXWriter(pmxFile, true);
        pmxWriter.write(Path.of("test.pmx"));
        int pmxFileSizeIn = pmxWriter.getPmxFileSizeIn();

        int pmxFileSizeOut = pmxWriter.getPmxFileSizeOut();
        System.out.println(String.format("Read:  %1$s bytes from file", pmxFileSizeIn));
        System.out.println(String.format("Wrote: %1$s bytes to buffer", pmxFileSizeOut));
    }
}