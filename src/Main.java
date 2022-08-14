import layer1.Target;
import layer1.Var;
import layer1.VarTemplate;

import java.util.HashMap;

public class Main {
    public static void main(String[] args) {
        String code = "rax = rcx";

        String regs = "i, sz\nrax; 0, 64\nrcx; 1, 64\nrdx; 2, 64\nrbx; 3, 64\neax; 0, 32";

        String pattern = "<dst>(r) \\= <src>(r, m)";

        VarTemplate.VarEnum registers = new VarTemplate.VarEnum("r", new Var[]{
                new Var("rax",
                        new HashMap<>(){{
                            put("index", 0);
                            put("size", 64);
                        }}),
                new Var("rcx",
                        new HashMap<>(){{
                            put("index", 1);
                            put("size", 64);
                        }})
        });

        VarTemplate memTemplate = new VarTemplate.PatternVar("m", "\\[<b>(r) {\\+ <i>(r){\\* <s>(n)}}\\]");
        new VarTemplate.NumVar();

        VarTemplate template = new VarTemplate.PatternVar("mov r, r/m", pattern);

        Var v = template.parse("rax = [rcx + rax * 4565]");
        //System.out.println(v);

        Target t = new Target("(n8){0}(n64){20 | <src[b][index]>}");
        t.inject(v);
    }
}
