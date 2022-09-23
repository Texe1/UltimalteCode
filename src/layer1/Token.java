package layer1;

import jdk.jshell.execution.JdiDefaultExecutionControl;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class Token<T> {

    boolean isStatic;

    public static HashMap<Character, Integer> intBases = new HashMap<>(){{
        put('b', 2);
        put('t', 3);
        put('q', 4);
        put('p', 5);
        put('s', 6);
        put('h', 7);
        put('o', 8);
        put('n', 9);
        put('u', 11);
        put('d', 12);
        put('x', 16);
        put('v', 20);
    }};

    private static String[] staticTypes;

    static {
        File f = new File("rsc/layer1/staticTokens.txt");

        if(!f.exists() || !f.canRead()) {
            System.err.println("Could not " + (f.exists() ? "read" : "find") + " staticTokens.txt");
            System.exit(-1);
        }

        try {
            Scanner sc = new Scanner(f);
            ArrayList<String> lines = new ArrayList<>();

            while(sc.hasNextLine())
                lines.add(sc.nextLine());

            staticTypes = lines.toArray(new String[0]);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.err.println("Could not read staticTokens.txt");
        }

    }

    private T value;

    private int line, column;

    public void setPos(int line, int column) {
        this.line = line;
        this.column = column;
    }

    public int getColumn() {
        return column;
    }

    public int getLine() {
        return line;
    }

    public T getVal(){
        return value;
    }

    public Token(T val){
        this(val, false);
    }

    public Token(T val, boolean isStatic){
        this.value = val;
        this.isStatic = isStatic;
    }

    public static parseRet parse(String s) {
        for (String type : staticTypes) {
            if(s.startsWith(type)){
                return new parseRet(new Token<String>(type), type.length());
            }
        }

        char c = s.charAt(0);
        s = s.split("\n")[0];

        if(s.matches("[a-zA-Z](([a-zA-Z_\\-0-9])?)+(.?)+")) {
            for (int i = s.length(); i > 0; i--){
                if(s.substring(0, i).matches("[a-zA-Z](([a-zA-Z_\\-0-9])?)+")){
                    return new parseRet(new Token<String>(s.substring(0, i)), i);
                }
            }
        }

        if(s.matches("(0[btqpshonudxv].+)|([0-9])+(.)+")){
            int base = 10;
            int i = 0;

            if(s.charAt(0) == '0' && (s.charAt(1) < '0' || s.charAt(1) > '9') && intBases.containsKey(s.charAt(1))){
                base = intBases.get(s.charAt(1));
                i = 2;
            }

            long l = 0;
            for (; i < s.length(); i++) {
                char digit = s.charAt(i);
                int digitVal =
                        (digit >= '0' && digit <= '9') ? digit - '0' : (
                                (digit >= 'a' && digit <= 'z') ? digit - 'a' + 10 : (
                                        (digit >= 'A' && digit <= 'Z') ? digit - 'A' + (base > 36 ? 36 : 10) : -1
                                        )
                        );
                if(digitVal >= 0){
                    l = l*base + digitVal;
                }else{
                    if(i >= 2 || base == 10){
                        return new parseRet(new Token<Long>(l), i);
                    }
                }
            }
            return new parseRet(new Token<Long>(l), i);
        }

        return null;
    }

    public record parseRet(Token tok, int len){}
}
