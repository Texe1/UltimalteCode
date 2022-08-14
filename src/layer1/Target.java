package layer1;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public record Target(String s) {
    public byte[] inject(Var v){
        ArrayList<Byte> bytes = new ArrayList<>();

        for (int i = 0; i < s.length(); i++) {
            while (s.charAt(i) == ' ')i++;

            switch (s.charAt(i)){
                case '(':
                    if(s.indexOf(')', i) > 0 && s.indexOf('{', i) > s.indexOf(')', i)){
                        int start = i;
                        i = s.indexOf('}', s.indexOf('{', i));
                        byte[] newBytes = parsePart(start, i+1, v);
                        for (byte b : newBytes) {
                            bytes.add(b);
                        }
                    }
                    else{
                        throw new IllegalArgumentException("At index " + i + ": could not find ')' before next '{'");
                    }
            }
        }

        byte[] ret = new byte[bytes.size()];
        for (int i = 0; i < ret.length; i++) {
            ret[i] = bytes.get(i);
        }
        
        return ret;
    }

    public byte[] parsePart(int start, int end, Var v){
        String sub = s.substring(start, end).strip();

        System.out.println(sub);

        if(sub.charAt(0) != '(')
            return new byte[0];

        String sizeStr = sub.substring(1, sub.indexOf(')'));
        int sz = 0;
        try{
            sz = Integer.parseInt(s);
        }catch (NumberFormatException e){
            return new byte[0];
        }

        int i = sub.indexOf('{');
        
        return new byte[0];
    }

    interface BinaryOp{
        long calc(long a, long b);
    }

    interface UnaryOp{
        long calc(long a);
    }


    static Map<Character, BinaryOp> binOps = new HashMap<>(){{
        put('|', (a, b) -> a | b);
        put('&', (a, b) -> a & b);
        put('^', (a, b) -> a ^ b);
        put('+', (a, b) -> a + b);
        put('-', (a, b) -> a - b);
        put('*', (a, b) -> a * b);
        put('/', (a, b) -> a / b);
        put('%', (a, b) -> a %  b);
    }};

    static Map<Character, UnaryOp> unOps = new HashMap<>(){{
        put('-', (a) -> -a);
        put('v', (a) -> (long)Math.sqrt(a));
    }};

    long parseNum(String s, Var v){
        System.out.println(s);
        for (Map.Entry<Character, BinaryOp> op : binOps.entrySet()) {
            if(s.contains("" + op.getKey())){
                int i = s.indexOf(op.getKey());
                if(!s.substring(0, i).isBlank()){
                    continue;
                }
                return op.getValue().calc(
                        parseNum(s.substring(0, i).strip(), v),
                        parseNum(s.substring(i +1).strip(), v)
                        );
            }
        }

        for (Map.Entry<Character, UnaryOp> op : unOps.entrySet()) {
            if(s.startsWith("" + op.getKey())){
                return op.getValue().calc(parseNum(s.substring(1), v));
            }
        }

        if(s.charAt(0) == '<' && s.charAt(s.length()-1) == '>'){// var

        }

        return 0;
    }
}
