package com.ilyasov;

import com.ilyasov.service.Service;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        Service service = new Service();
        Scanner scanner = new Scanner(System.in);
        System.out.println("Commands:" + '\n' +
                "1: Build XML file with data from Database." + '\n' +
                "2: Syncronize Database with XML file." + '\n' +
                "3: Exit.");
        while (true) {
            System.out.println("Input command: 1, 2 or 3:");
            int n = scanner.nextInt();
            switch (n) {
                case 1: {
                    service.buildXml();
                    System.out.println("Builded!");
                    break;
                }
                case 2: {
                    service.syncronizeWithXml();
                    System.out.println("Synchronized!");
                    break;
                }
                case 3: {
                    System.out.println("Good bye!");
                    System.exit(0);
                }
            }
        }
    }
}
