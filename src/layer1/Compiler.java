package layer1;

import java.util.ArrayList;

public class Compiler {

    public static String compile(String code){
        ArrayList<Token> tokenArrayList = new ArrayList<>();

        int i = 0;

        int line = 0;
        int lineStart = 0;


        // tokenization
        while (i < code.length()) {
            if(code.charAt(i) == '\n'){
                line++;
                lineStart = ++i;
            }else if(code.charAt(i) == '/' && code.charAt(i+1) == '/'){
                while(i < code.length() && code.charAt(i) != '\n'){
                    i++;
                }
            }
            Token.parseRet t = Token.parse(code.substring(i));
            if(t != null && t.len() != 0){
                t.tok().setPos(line, i - lineStart);
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


        // syntax checking
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
                    if(tokens[j].isStatic){
                        System.err.println("Syntax error in Line " + tokens[j].getLine() + ", column " + tokens[j].getColumn() + ":\n\t" +
                                "Function definition expected,\n\t\tfound '" + tokens[j].getVal() + "'");
                        System.exit(0);
                    }
                    j++;
                    if(!tokens[j].isStatic || !tokens[j].getVal().equals("(")){
                        System.err.println("Syntax error in Line " + tokens[j].getLine() + ", column " + tokens[j].getColumn() + ":\n\t" +
                                "expected '(',\n\t\tfound '" + tokens[j].getVal() + "'");
                        System.exit(0);
                    }
                    j++;
                    if(!tokens[j].isStatic){ // argument size defined
                        if(!tokens[j].getVal().getClass().equals(Long.class)){
                            System.err.println("Syntax error in Line " + tokens[j].getLine() + ", column " + tokens[j].getColumn() + ":\n\t" +
                                    "expected ')' or argument size,\n\t\tfound '" + tokens[j].getVal() + "'");
                            System.exit(0);
                        }
                    }
                    j++;
                    if(!tokens[j].getVal().equals(")")){
                        System.err.println("Syntax error in Line " + tokens[j].getLine() + ", column " + tokens[j].getColumn() + ":\n\t" +
                            "expected ')',\n\t\tfound '" + tokens[j].getVal() + "'");
                        System.exit(0);
                    }
                    j++;

                    if(tokens[j].isStatic){
                        if(tokens[j].getVal().equals("}"))
                            break;
                        if(!tokens[j].getVal().equals("=>") || tokens[j+1].isStatic || !tokens[j+1].getVal().getClass().equals(Long.class)){
                            System.err.println("Syntax error in Line " + tokens[j].getLine() + ", column " + tokens[j].getColumn() + ":\n\t" +
                                    "expected return size declaration or new line (=> #),\n\t\tfound '" + tokens[j].getVal() + "'");
                            System.exit(0);
                        }
                        j += 2;
                    }
                    if(tokens[j].getLine() == tokens[j-1].getLine()){
                        System.err.println("Syntax error in Line " + tokens[j].getLine() + ", column " + tokens[j].getColumn() + ":\n\t" +
                                "expected return size declaration or new line (=> #),\n\t\tfound '" + tokens[j].getVal() + "'");
                        System.exit(0);
                    }
                }

                i = j;
            }
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
            if(tokens[i].getVal().getClass().equals(Long.class)){
                System.err.println("Syntax error in Line " + tokens[i].getLine() + ", column " + tokens[i].getColumn() + ":\n\t" +
                        "expected argument size or ')', found, '" + tokens[i].getVal() + "'");
                System.exit(0);
            }
        }

        if(!tokens[++i].isStatic || !)

        return i;
    }

}
