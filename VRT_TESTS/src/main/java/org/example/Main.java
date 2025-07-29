package org.example;


import io.pkts.Pcap;

import java.io.File;
import java.io.IOException;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    static int count = 0;
    public static void main(String[] args) throws IOException {
        long startTime = System.nanoTime();
        final Pcap pcap = Pcap.openStream("src/main/resources/scanning.pcap");

        var things = Parser.INSTANCE.parse();

        long endTime = System.nanoTime(); // End timer
        long durationNano = endTime - startTime; // Runtime in nanoseconds
        double durationMillis = durationNano / 1_000_000.0; // Convert to ms
        System.out.println(count + " In " + durationMillis);
    }

}