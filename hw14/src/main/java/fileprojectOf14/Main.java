package fileprojectOf14;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws IOException {

        String myAddress = "C://Users//User//Desktop//List.txt";
        File file = new File(myAddress);
        FileInputStream fileInputStream = new FileInputStream(file);

        Scanner scanner = new Scanner(fileInputStream);

        while (scanner.hasNextLine())
            System.out.println(scanner.nextLine());
    }
}
