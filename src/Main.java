import layer1.Token;

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws Exception {


        Scanner sc = new Scanner(new File("rsc/layer1/code.uc"));

        String s = "";
        while (sc.hasNextLine()){
            s += "\n" + sc.nextLine();
        }

        ArrayList<Token> tokens = new ArrayList<>();

        int i = 0;

        int line = 0;
        int column = -1;

        while (i < s.length()) {
            column++;
            if(s.charAt(i) == '\n'){
                line++;
                column = 0;
                i++;
            }else if(s.charAt(i) == '/' && s.charAt(i+1) == '/'){
                while(i < s.length() && s.charAt(i) != '\n'){
                    i++;
                }
            }
            Token.parseRet t = Token.parse(s.substring(i));
            if(t != null && t.len() != 0){
                i += t.len();
                t.tok().setPos(line, column);
                tokens.add(t.tok());
                continue;
            }

            i++;
        }

        System.out.println("Tokens:");

        line = tokens.get(0).getLine();

        int nTabs = 0;

        for (Token t : tokens) {
            if(t.getVal().toString().equals("}"))
                nTabs--;
            if(t.getLine() > line) {
                System.out.println();
                for (int j = 0; j < nTabs; j++) {
                    System.out.print("\t");
                }
                line = t.getLine();
            }

            System.out.print(t.getVal() + " ");
            if(t.getVal().toString().equals("{"))
                nTabs++;

        }

    }

    public static void s(String s){
        s = s.substring(1);
    }
}
