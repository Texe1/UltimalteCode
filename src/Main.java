import layer1.Compiler;

import java.io.File;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws Exception {


        Scanner sc = new Scanner(new File("rsc/layer1/code.uc"));

        String s = "";
        while (sc.hasNextLine()){
            s += "\n" + sc.nextLine();
        }

        System.out.println("Tokens:\n" + Compiler.compile(s));

    }
}
