package layer1;

import java.util.ArrayList;

public class Compiler {

    public static String compile(String code){
        ArrayList<Token> tokenArrayList = new ArrayList<>();

        int i = 0;

        int line = 1;
        int lineStart = 0;


        // tokenization
        while (i < code.length()) {
            if(code.charAt(i) == '\n'){
                line++;
                lineStart = ++i;
                continue;
            }else if(code.charAt(i) == '/' && code.charAt(i+1) == '/'){
                while(i < code.length() && code.charAt(i) != '\n'){
                    i++;
                }
            }
            Token.parseRet t = Token.parse(code.substring(i));
            if(t != null && t.len() != 0){
                t.tok().setPos(line, i - lineStart + 1);
                i += t.len();
                tokenArrayList.add(t.tok());
                continue;
            }

            i++;
        }

        Token[] tokens = tokenArrayList.toArray(new Token[0]);


        // TEMPORARY: outputs a String showing the tokenized code
        String s = "";

        line = tokenArrayList.get(0).getLine();
        int nTabs = 0;
        for (Token t : tokenArrayList) {
            if(t.getVal().toString().equals("}"))
                nTabs--;
            if(t.getLine() > line) {
                s += "\n";
                for (int j = 0; j < nTabs; j++) {
                    s += "\t";
                }
                line = t.getLine();
            }

            s += t.getVal() + " ";

            if(t.getVal().toString().equals("{"))
                nTabs++;

        }


        // simple syntax checking
        boolean infunc = false;

        for (i = 0; i < tokens.length; i++) {
            System.out.println(i);
            if(tokens[i].getVal().toString().equals("import")){ // imports
                int j = i+1;
                // import lib1, lib2, ... , libn {
                while(!tokens[j].isStatic){
                    if(!tokens[j].isStatic && tokens[j].getVal().getClass().equals(String.class))
                        j++;
                    if(tokens[j].isStatic && tokens[j].getVal().equals(","))
                        j++;
                    else
                        break;

                }

                if(!tokens[j].isStatic || !tokens[j].getVal().equals("{")) {
                    System.err.println("Syntax error in Line " + tokens[j].getLine() + ", column " + tokens[j].getColumn() + ":\n\t'import' has to be followed by Code Block or comma-separated List of dynamic libraries.");
                    System.exit(0);
                }
                j++;

                // import lines ('name(#)=>#' | 'name()=>#' | 'name(#)' | name())
                while (j < tokens.length && !(tokens[j].isStatic && tokens[j].getVal().equals("}"))) {

                    j = checkFuncDef(tokens, j);

                    if(tokens[j].getLine() == tokens[j-1].getLine()){
                        System.err.println("Syntax error in Line " + tokens[j].getLine() + ", column " + tokens[j].getColumn() + ":\n\t" +
                                "expected new line,\n\t\tfound '" + tokens[j].getVal() + "'");
                        System.exit(0);
                    }
                }

                i = j+1;
            }

            // function definition
            if(tokens[i].isStatic && tokens[i].getVal().equals("func")){
                i++;
                checkFuncDef(tokens, i);
                infunc = true;
            }

            // var definitios
            if(tokens[i].isStatic && (
                    tokens[i].getVal().equals("byte")
                        || tokens[i].getVal().equals("char")
                        || tokens[i].getVal().equals("word")
                        || tokens[i].getVal().equals("short")
                        || tokens[i].getVal().equals("int")
                        || tokens[i].getVal().equals("long")
            )){
                if(tokens[++i].isStatic){
                    if(tokens[i].getVal().equals("arg"))
                        i++;
                    else {
                        System.err.println("Syntax error in line " + tokens[i].getLine() + ", column " + tokens[i].getColumn() + ":\n\t" +
                                "expected variable name or 'arg' declaration, found '" + tokens[i].getVal() + "'");
                        System.exit(0);
                    }
                }
                if(tokens[i].isStatic || !tokens[i].getVal().getClass().equals(String.class)){
                    System.err.println("Syntax error in line" + tokens[i].getLine() + ", column " + tokens[i].getColumn() + ":\n\t" +
                            "Expected variable name, found '" + tokens[i].getVal() + "'");
                }
                i++;
            }



        }


        // parsing
        ArrayList<Node> parseStack = new ArrayList<>();

        for (Token t : tokens) {
            parseStack.add(new Node(t));
        }

        return s;
    }

    public static int checkFuncDef(Token[] tokens, int i){
        // name
        if(tokens[i].isStatic || !tokens[i].getVal().getClass().equals(String.class)){
            System.err.println("Syntax error in Line " + tokens[i].getLine() + ", column " + tokens[i].getColumn() + ":\n\t" +
                    "expected function name, found, '" + tokens[i].getVal() + "'");
            System.exit(0);
        }

        // (
        if(!tokens[++i].isStatic || !tokens[i].getVal().equals("(")){
            System.err.println("Syntax error in Line " + tokens[i].getLine() + ", column " + tokens[i].getColumn() + ":\n\t" +
                    "expected '(', found, '" + tokens[i].getVal() + "'");
            System.exit(0);
        }

        // #
        if (!tokens[++i].isStatic){
            if(!tokens[i].getVal().getClass().equals(Long.class)){
                System.err.println("Syntax error in Line " + tokens[i].getLine() + ", column " + tokens[i].getColumn() + ":\n\t" +
                        "expected argument size or ')', found, '" + tokens[i].getVal() + "'");
                System.exit(0);
            }
        }

        // )
        if(!tokens[++i].isStatic || !tokens[i].getVal().equals(")")){
            if(tokens[i].getVal().getClass().equals(Long.class)){
                System.err.println("Syntax error in Line " + tokens[i].getLine() + ", column " + tokens[i].getColumn() + ":\n\t" +
                        "expected ')', found, '" + tokens[i].getVal() + "'");
                System.exit(0);
            }
        }

        // => #
        if(tokens[i].getLine() == tokens[++i].getLine() && !(tokens[i].isStatic && tokens[i].getVal().equals("{"))){
            if(!tokens[i].isStatic || !tokens[i].getVal().equals("=>")){
                if(tokens[i].getVal().getClass().equals(Long.class)){
                    System.err.println("Syntax error in Line " + tokens[i].getLine() + ", column " + tokens[i].getColumn() + ":\n\t" +
                            "expected '=>', found, '" + tokens[i].getVal() + "'");
                    System.exit(0);
                }
            }
            // #
            if(tokens[++i].isStatic || !tokens[i].getVal().getClass().equals(Long.class)){
                System.err.println("Syntax error in Line " + tokens[i].getLine() + ", column " + tokens[i].getColumn() + ":\n\t" +
                        "expected return size, found, '" + tokens[i].getVal() + "'");
                System.exit(0);
            }
        }

        return ++i;
    }

    public static void parseImports(ArrayList<Node> parseStack){
        for (int i = 0; i < parseStack.size(); i++) {
            if(parseStack.get(i).t.getVal().equals("import")){
                if(parseStack.get(++i).)
            }
        }
    }

}
