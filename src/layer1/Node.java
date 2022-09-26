package layer1;

import java.util.ArrayList;

public class Node {

    public static class Import extends Node{

        String[] libs;
        FuncNode[] funcs;

        public Import(String[] libs, FuncNode[] funcs){
            this.libs = libs;
            this.funcs = funcs;
        }
    }

    public static class FuncNode extends Node {

        public String name;

        public Line[] lines;
        public boolean extern;
    }

    public static class Line extends Node {

    }

}
