package jiepastor.brailletouch_proto.Braille;

/**
 * The braille pattern consisting of six dots
 */

public class BraillePattern {
    private int dot1;
    private int dot2;
    private int dot3;
    private int dot4;
    private int dot5;
    private int dot6;

    public int getDot1(){
        return dot1;
    }

    public int getDot2() {
        return dot2;
    }

    public int getDot3() {
        return dot3;
    }

    public int getDot4() {
        return dot4;
    }

    public int getDot5() {
        return dot5;
    }

    public int getDot6() {
        return dot6;
    }

    public boolean isPatternEmpty(){
        return dot1 + dot2 + dot3 + dot4 + dot5 + dot6 == 0;
    }

    public void setPattern(int d1, int d2, int d3, int d4, int d5, int d6){
        dot1 = d1;
        dot2 = d2;
        dot3 = d3;
        dot4 = d4;
        dot5 = d5;
        dot6 = d6;
    }
}
