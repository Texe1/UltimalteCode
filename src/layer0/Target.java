package layer0;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Locale;
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

        if(sub.charAt(0) != '(')
            return new byte[0];

        String sizeStr = sub.substring(1, sub.indexOf(')'));
        int sz = 0;
        try{
            sz = Integer.parseInt(sizeStr);
        }catch (NumberFormatException e){
            e.printStackTrace();
            return new byte[0];
        }

        int i = sub.indexOf('{');
        int j = sub.indexOf('}');

        try{
            long l = parseNum(sub.substring(i+1, j), v);
            byte[] ret = new byte[sz/8];


            for (i = sz/8 - 1; i >= 0; i --){
                ret[sz/8 - 1 - i] = (byte) ((l >> (8*i)) & 0xff);
            }
            return ret;
        }catch(Exception e){
            return new byte[0];
        }
    }

    interface BinaryOp{
        long calc(long a, long b);
    }

    interface UnaryOp{
        long calc(long a);
    }

    interface BinaryBoolOp{
        boolean calc(boolean a, boolean b);
    }

    interface UnaryBoolOp{
        boolean calc(boolean a);
    }

    static Map<Character, BinaryOp> binOps = new LinkedHashMap<>(){{
        put('|', (a, b) -> {
            System.out.println(a + " | " + b + " = " + (a|b));
            return a | b;
        });
        put('&', (a, b) -> a & b);
        put('^', (a, b) -> a ^ b);
        put('+', (a, b) -> a + b);
        put('-', (a, b) -> a - b);
        put('*', (a, b) -> a * b);
        put('/', (a, b) -> a / b);
        put('%', (a, b) -> a % b);
//        put('<', (a, b) -> a << b);
//        put('>', (a, b) -> a >> b);
    }};

    static Map<Character, UnaryOp> unOps = new LinkedHashMap<>(){{
        put('-', (a) -> -a);
        put('v', (a) -> (long)Math.sqrt(a));
    }};

    long parseNum(String s, Var v) throws Exception {


        s = s.strip();

        System.out.println(s);

        if(s.equals("none")) {
            throw new Exception("none");
        }

        if(s.charAt(0) == '(' && s.charAt(s.length()-1) == ')'){
            int scope = 0, i = 0;
            for (; i < s.length(); i++) {
                if(s.charAt(i) == '(')
                    scope++;
                else if(s.charAt(i) == ')')
                    scope--;

                if(scope == 0)
                    break;
            }
            if(i >= s.length()-1) {
                return parseNum(s.substring(1, s.length() - 1), v);
            }
        }

        for (Map.Entry<Character, BinaryOp> op : binOps.entrySet()) {
            if(s.contains("" + op.getKey())){
                int i = s.indexOf(op.getKey());
                while(i >= 0) {
                    if (s.substring(0, i).isBlank()) {
                        continue;
                    }
                    int scope = 0;

                    for (int j = 0; j < i; j++) {
                        if (s.charAt(j) == '(')
                            scope++;
                        else if (s.charAt(j) == ')')
                            scope--;
                    }

                    if (scope != 0) {
                        i = s.indexOf(op.getKey(), i+1);
                        if(i < 0)
                            break;
                        continue;
                    }

                    return op.getValue().calc(
                            parseNum(s.substring(0, i).strip(), v),
                            parseNum(s.substring(i + 1).strip(), v)
                    );
                }
            }
        }

        if(s.contains("<<")){

            System.out.println("<<");

            // checking if outside parentheses
            String[] strsToCheck = s.split("<<");

            int scope = 0;
            int i = 0;


            for (; i < strsToCheck.length-1; i++) {
                for (int j = 0; j < strsToCheck[i].length(); j++) {
                    if(strsToCheck[i].charAt(j) == '(')
                        scope++;
                    if(strsToCheck[i].charAt(j) == ')')
                        scope--;
                }
                System.out.println(scope);
                if(scope == 0)
                    break;
            }


            System.out.println(scope);
            if(scope == 0) {


                String s1 = strsToCheck[0];

                for (int j = 1; j <= i; j++) {
                    s1 += "<<" + strsToCheck[j];
                }

                String s2 = strsToCheck[i + 1];

                for (int j = i + 2; j < strsToCheck.length; j++) {
                    s2 += "<<" + strsToCheck[j];
                }


                System.out.println("<<");

                return parseNum(s1, v) << parseNum(s2, v);
            }
        }

        if(s.contains(">>")){
            // checking if outside parentheses
            String[] strsToCheck = s.split(">>");

            int scope = 0;
            int i = 0;

            for (; i < strsToCheck.length; i++) {
                for (int j = 0; j < strsToCheck[i].length(); j++) {
                    if(strsToCheck[i].charAt(j) == '(')
                        scope++;
                    if(strsToCheck[i].charAt(j) == ')')
                        scope--;
                }
                if(scope == 0)
                    break;
            }

            if(scope == 0) {


                String s1 = strsToCheck[0];

                for (int j = 1; j <= i; j++) {
                    s1 += ">>" + strsToCheck[j];
                }

                String s2 = strsToCheck[i + 1];

                for (int j = i + 2; j < strsToCheck.length; j++) {
                    s2 += ">>" + strsToCheck[j];
                }

                return parseNum(s1, v) << parseNum(s2, v);
            }
        }

        for (Map.Entry<Character, UnaryOp> op : unOps.entrySet()) {
            if(s.startsWith("" + op.getKey())){

                return op.getValue().calc(parseNum(s.substring(1).strip(), v));
            }
        }

        // TODO ternary operator(evlis expression)
        if(s.contains("?")){
            System.out.println('?');
            if(s.indexOf(':', s.indexOf('?')) < 0){
                throw new IllegalArgumentException("Ternary operator: missing ':' in '" + s + "'");
            }

            System.out.println("Checking " + s.substring(0, s.indexOf('?')));

            boolean condition = parseCondition(s.substring(0, s.indexOf('?')), v);

            if(condition){
                long n = parseNum(s.substring(s.indexOf('?')+1, s.indexOf(':', s.indexOf('?'))).strip(), v);
                return n;
            }else{
                String value = s.substring(s.indexOf(':', s.indexOf('?')) +1).strip();
                if (value.indexOf('(') == 0 && value.indexOf(')') > 0){
                    value = value.substring(1, value.indexOf(')'));
                }

                return parseNum(value, v);
            }
        }

        if(s.charAt(0) == '<' && s.charAt(s.length()-1) == '>'){// var

            String sub = s.strip();
            Object value =  v.get(s.contains("[") ? s.substring(1, s.indexOf('[')) : s.substring(1, s.length()-1));

            while (sub.indexOf('[', 1) >= 0){
                sub = sub.substring(sub.indexOf('[', 1));
                value = ((Var) value).get(sub.substring(1, sub.indexOf(']')));
            }

            if(value.getClass().equals(Long.class)){
                return (long) value;
            }else throw new IllegalArgumentException("'" + s + "' is not a numeric value");

        }

        try{
            return Long.parseLong(s.strip());
        }catch(NumberFormatException e){
            throw new IllegalArgumentException("Could not parse: '" + s + "'");
        }
    }

    static Map<String, BinaryBoolOp> binBoolOps = new LinkedHashMap<>(){{
        put("||", (a, b) -> a || b);
        put("&&", (a, b) -> a && b);
        put("^^", (a, b) -> a ^ b);
    }};

    boolean parseCondition(String s, Var v) throws Exception {
        s = s.strip();

        // TODO == >= <= != [<>]

        if(s.charAt(0) == '(' && s.indexOf(')') == s.length()-1){
            return parseCondition(s.substring(1, s.length()-1), v);
        }

        for (Map.Entry<String, BinaryBoolOp> e: binBoolOps.entrySet()){
            if(s.contains(e.getKey())){
                // checking if outside parentheses
                String[] strsToCheck = s.split(e.getKey());

                int scope = 0;
                int i = 0;

                for (; i < strsToCheck.length; i++) {
                    for (int j = 0; j < strsToCheck[i].length(); j++) {
                        if(strsToCheck[i].charAt(j) == '(')
                            scope++;
                        if(strsToCheck[i].charAt(j) == ')')
                            scope--;
                    }
                    if(scope == 0)
                        break;
                }

                if(scope != 0)
                    continue;


                String s1 = strsToCheck[0];

                for (int j = 1; j <= i; j++) {
                    s1 += e.getKey() + strsToCheck[j];
                }

                String s2 = strsToCheck[i+1];

                for (int j = i+2; j < strsToCheck.length; j++) {
                    s2 += e.getKey() + strsToCheck[j];
                }

                return e.getValue().calc(parseCondition(s1, v), parseCondition(s2, v));
            }
        }

        if(s.contains("==")){
            String[] strsToCheck = s.split("==");

            int scope = 0;
            int i = 0;

            for (; i < strsToCheck.length; i++) {
                for (int j = 0; j < strsToCheck[i].length(); j++) {
                    if(strsToCheck[i].charAt(j) == '(')
                        scope++;
                    if(strsToCheck[i].charAt(j) == ')')
                        scope--;
                }
                if(scope == 0)
                    break;
            }


            if(scope == 0){
                String s1 = strsToCheck[0];

                for (int j = 0; j < i; j++) {
                    s1 += "==" + strsToCheck[j + 1];
                }

                String s2 = strsToCheck[i+1];

                for (int j = i+2; j < strsToCheck.length; j++) {
                    s2 += "==" + strsToCheck[j];
                }

                long l1 = parseNum(s1, v), l2 = parseNum(s2, v);

                if(l1 == l2) {
                    return true;
                }
                return false;
            }

        }

        if(s.charAt(0) == '!'){
            return !parseCondition(s.strip().substring(1), v);
        }


        if(s.contains("is")) {

            String varStr = s.split("is", 2)[0].strip();
            String type = s.split("is", 2)[1].strip();

            String sub = varStr;
            Object value = v.get(s.contains("[") ? varStr.substring(0, varStr.indexOf('[')) : varStr);

            while (sub.indexOf('[', 1) >= 0){
                sub = sub.substring(sub.indexOf('[', 1));
                value = ((Var) value).get(sub.substring(1, sub.indexOf(']')));
            }

            if(value == null){
                return type.equals("null");
            }

            if(!value.getClass().equals(Var.class)){
                System.out.println(value.getClass().getName());

                if(type.toLowerCase(Locale.ROOT).equals("int"))
                    return value.getClass().equals(Long.class);
                if(type.toLowerCase(Locale.ROOT).equals("str"))
                    return value.getClass().equals(String.class);
                if(type.toLowerCase(Locale.ROOT).equals("float"))
                    return value.getClass().equals(Float.class);
                if(type.toLowerCase(Locale.ROOT).equals("double"))
                    return value.getClass().equals(Double.class);
                return false;
            }
            System.out.println(varStr + " is " + (((Var) value).type().equals(type) ? "" : "not ") + type);
            return ((Var) value).type().equals(type);
        }

        if(s.equals("true"))
            return true;

        if(s.equals("false"))
            return false;

        return false;
    }
}
