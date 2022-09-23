package layer0;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public abstract class VarTemplate {
    public final String name;

    public abstract Var parse(String s);
    public String getString(String s){
        for (int i = s.length(); i >= 0; i--) {
            if (parse(s.substring(0, i)) != null)
                return s.substring(0, i);
        }
        return null;
    }
    
    public static ArrayList<VarTemplate> templates = new ArrayList<>();

    public VarTemplate(String name){
        this.name = name;
        templates.add(this);
    }

    public static class NullVar extends VarTemplate{

        public NullVar() {
            super("nulltype");
        }

        @Override
        public Var parse(String s) {
            if(s.isEmpty())return new Var("null", "null", new HashMap<>());
            return null;
        }
    }

    public static class PatternVar extends VarTemplate{

        String pattern;

        public PatternVar(String name, String pattern){
            super(name);
            this.pattern = pattern;
        }

        @Override
        public Var parse(String s) {
            Map<String, Object> vars = new HashMap<>();

            if(pattern.length() == 0 || s.length() == 0)
                return null;

            int j = 0;


            int optionalDepth = 0;
            Map<String, Object>[] optionalVars = new Map[20];
            int[] optionalJ = new int[20];

            for (int i = 0; i < pattern.length(); i++) {
                while (i < pattern.length() && pattern.charAt(i) == ' ') i++;
                while (j < s.length() && s.charAt(j) == ' ') j++;

                if(i >= pattern.length() && j >= s.length())
                    break;
                else if(i >= pattern.length() || j >= s.length()) {
                    return null;
                }

                switch (pattern.charAt(i)){
                    case '\\':
                        i++;
                        if(pattern.charAt(i) != s.charAt(j++)) {
                            if (optionalDepth > 0) {
                                j = optionalJ[--optionalDepth];
                                int d = 0;
                                while (d != 0 || pattern.charAt(i) != '}') {
                                    if (pattern.charAt(i) == '{') d++;
                                    else if (pattern.charAt(i) == '}') d--;
                                    i++;
                                }
                            } else {
                                return null;
                            }
                        }
                        continue;
                    case '<':
                        String varName = pattern.substring(++i, pattern.indexOf('>', i));

                        i = pattern.indexOf('(', i) +1;

                        String[] types = pattern.substring(i, pattern.indexOf(')', i)).split(",");


                        i = pattern.indexOf(')', i);

                        boolean found = false;

                        for (String t : types) {
                            for (VarTemplate template: templates) {
                                if(template.name.equals(t.strip())){
                                    String varStr = template.getString(s.substring(j));
                                    if(varStr != null) {
                                        Var v = template.parse(varStr);
                                        if(optionalDepth == 0) {
                                            vars.put(varName, v);
                                        }
                                        else {
                                            optionalVars[optionalDepth-1].put(varName, v);
                                        }
                                        found = true;
                                        j += varStr.length();
                                        break;
                                    }
                                }
                            }
                            if(found)
                                break;
                        }
                        if(!found) {
                            if (optionalDepth > 0){
                                j = optionalJ[--optionalDepth];
                                int d = 0;
                                while (d != 0 || pattern.charAt(i) != '}'){
                                    if(pattern.charAt(i) == '{') d++;
                                    else if(pattern.charAt(i) == '}') d--;
                                    i++;
                                }
                            } else {
                                return null;
                            }
                        }

                        break;
                    case '{':
                        optionalVars[optionalDepth] = new HashMap<>();
                        optionalJ[optionalDepth] = j;
                        optionalDepth++;
                        break;
                    case '}':
                        optionalDepth--;
                        if (optionalDepth < 0){
                            return null;
                        }
                        vars.putAll(optionalVars[optionalDepth]);
                        break;
                    default:
                        if (optionalDepth > 0){
                            j = optionalJ[--optionalDepth];
                            int d = 0;
                            while (d != 0 || pattern.charAt(i) != '}'){
                                if(pattern.charAt(i) == '{') d++;
                                else if(pattern.charAt(i) == '}') d--;
                                i++;
                            }
                        } else {
                            return null;
                        }
                }
            }

            return new Var(this.name, "pattern", vars);
        }
    }

    public static class VarEnum extends VarTemplate{

        Var[] elements;

        public VarEnum(String name, Var[] elements){
            super(name);
            this.elements = elements;
        }

        @Override
        public Var parse(String s) {
            for (Var v : elements){
                if(v.name().equals(s))
                    return v;
            }

            return null;
        }

        @Override
        public String getString(String s){
            for (Var v : elements) {
                if(s.startsWith(v.name()))
                    return v.name();
            }

            return null;
        }
    }

    public static class NumVar extends VarTemplate{
        public NumVar() {
            super("n");
        }

        @Override
        public Var parse(String s) {
            try {
                return new Var("num", "num", new HashMap<>() {{
                    put("val", Integer.parseInt(s));
                }});
            }catch (NumberFormatException e){
                return null;
            }
        }
    }
}
