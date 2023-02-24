package dev.xfj;

import dev.xfj.format.pmm.PMMFile;
import dev.xfj.format.pmx.PMXFile;
import dev.xfj.parsing.PMMParser;
import dev.xfj.parsing.PMXParser;
import dev.xfj.writer.PMMWriter;
import dev.xfj.writer.PMXWriter;

import java.io.IOException;
import java.nio.file.Path;

public class Main {
    public static void main(String[] args) throws IOException {
        PMMParser pmmParser = new PMMParser(Path.of("test_in_01.pmm"));
        PMMFile pmmFile = pmmParser.parse();
        PMMWriter pmmWriter = new PMMWriter(pmmFile, true);
        pmmWriter.write(Path.of("test.pmm"));
        int pmmFileSizeIn = pmmWriter.getPmmFileSizeIn();
        int pmmFileSizeOut = pmmWriter.getPmmFileSizeOut();
        System.out.println(String.format("Read:  %1$s bytes from file", pmmFileSizeIn));
        System.out.println(String.format("Wrote: %1$s bytes to buffer", pmmFileSizeOut));

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