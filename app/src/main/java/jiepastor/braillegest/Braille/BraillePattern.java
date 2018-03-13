package jiepastor.braillegest.Braille;

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

    public boolean setStringPattern(String string){
        for(int i=0;i<6;i++){
            if (!setDot(Integer.parseInt(String.valueOf(string.charAt(i))), i))
                return false;
        }
        return true;
    }

    public int[] getPattern(){
        return new int[]{dot1,dot2,dot3,dot4,dot5,dot6};
    }

    public boolean setDot(int val, int pos){
        boolean isValidArgs = false;
        if ((val == 1 || val == 0) && (pos >= 0 && pos <= 5)) {
            switch(pos){
                case 0: dot1 = val; break;
                case 1: dot2 = val; break;
                case 2: dot3 = val; break;
                case 3: dot4 = val; break;
                case 4: dot5 = val; break;
                case 5: dot6 = val; break;
                default: return false;
            }

            isValidArgs = true;
        }
        return isValidArgs;
    }

    public boolean setPattern(int d1, int d2, int d3, int d4, int d5, int d6){
        boolean isValidArg = d1 + d2 + d3 + d4 + d5 + d6 <= 6 && d1 + d2 + d3 + d4 + d5 + d6 >= 0;

        if(isValidArg)
        {
            dot1 = d1;
            dot2 = d2;
            dot3 = d3;
            dot4 = d4;
            dot5 = d5;
            dot6 = d6;
        }

        return isValidArg;
    }

    public boolean isPatternEmpty(){
        return dot1 + dot2 + dot3 + dot4 + dot5 + dot6 == 0;
    }

    public boolean interpretString(String curString){
        char[] array = curString.toCharArray();

        if (array.length!=6)
            return false;

        try {
            for (int i = 0; i < 6; i++) {
                setDot(Integer.parseInt(String.valueOf(array[i])), i);
            }
        }
        catch (Exception e){
            return false;
        }

        return true;
    }

    @Override
    public String toString(){
        return dot1 + "" + dot2+ "" + dot3+ "" + dot4+ "" + dot5+ "" + dot6 + "";
    }

}
