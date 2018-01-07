package jiepastor.brailletouch_proto;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jiepastor on 12/31/2017.
 */

public class KeySet {
    class Key {
        private Character character;
        int dot1, dot2, dot3, dot4, dot5, dot6;

        public Key(int[] pattern, char key){
            character = key;
            dot1 = pattern[0];
            dot2 = pattern[1];
            dot3 = pattern[2];
            dot4 = pattern[3];
            dot5 = pattern[4];
            dot6 = pattern[5];
        }


        public Character getCharacter() {
            return character;
        }

        public boolean isSameCharacter(int[] pattern){
            return pattern[0] == dot1 && pattern[1] == dot2 && pattern[2]==dot3 && pattern[3]==dot4 && pattern[4]==dot5 && pattern[5]==dot6;
        }
    }

    private List<Key> alphabet = new ArrayList<>();
    private List<Key> numbers = new ArrayList<>();
    private List<Key> symbols = new ArrayList<>();

    public static final int LOWER_CASE_MODE = 0;
    public static final int UPPER_CASE_MODE = 1;
    public static final int NUMBER_MODE = 2;
    public static final int SYMBOL_MODE = 3;

    public KeySet(){
        alphabet.add( new Key(new int[]{1,0,0,0,0,0},'a') );
        alphabet.add( new Key(new int[]{1,1,0,0,0,0},'b') );
        alphabet.add( new Key(new int[]{1,0,0,1,0,0},'c') );
        alphabet.add( new Key(new int[]{1,0,0,1,1,0},'d') );
        alphabet.add( new Key(new int[]{1,0,0,0,1,0},'e') );
        alphabet.add( new Key(new int[]{1,1,0,1,0,0},'f') );
        alphabet.add( new Key(new int[]{1,1,0,1,1,0},'g') );
        alphabet.add( new Key(new int[]{1,1,0,0,1,0},'h') );
        alphabet.add( new Key(new int[]{0,1,0,1,0,0},'i') );
        alphabet.add( new Key(new int[]{0,1,0,1,1,0},'j') );
        alphabet.add( new Key(new int[]{1,0,1,0,0,0},'k') );
        alphabet.add( new Key(new int[]{1,1,1,0,0,0},'l') );
        alphabet.add( new Key(new int[]{1,0,1,1,0,0},'m') );
        alphabet.add( new Key(new int[]{1,0,1,1,1,0},'n') );
        alphabet.add( new Key(new int[]{1,0,1,0,1,0},'o') );
        alphabet.add( new Key(new int[]{1,1,1,1,0,0},'p') );
        alphabet.add( new Key(new int[]{1,1,1,1,1,0},'q') );
        alphabet.add( new Key(new int[]{1,1,1,0,1,0},'r') );
        alphabet.add( new Key(new int[]{0,1,1,1,0,0},'s') );
        alphabet.add( new Key(new int[]{0,1,1,1,1,0},'t') );
        alphabet.add( new Key(new int[]{1,0,1,0,0,1},'u') );
        alphabet.add( new Key(new int[]{1,1,1,0,0,1},'v') );
        alphabet.add( new Key(new int[]{0,1,0,1,1,1},'w') );
        alphabet.add( new Key(new int[]{1,0,1,1,0,1},'x') );
        alphabet.add( new Key(new int[]{1,0,1,1,1,1},'y') );
        alphabet.add( new Key(new int[]{1,0,1,0,1,1},'z') );
        numbers.add( new Key(new int[]{0,1,0,1,1,0},'0') );
        numbers.add( new Key(new int[]{1,0,0,0,0,0},'1') );
        numbers.add( new Key(new int[]{1,1,0,0,0,0},'2') );
        numbers.add( new Key(new int[]{1,0,0,1,0,0},'3') );
        numbers.add( new Key(new int[]{1,0,0,1,1,0},'4') );
        numbers.add( new Key(new int[]{1,0,0,0,1,0},'5') );
        numbers.add( new Key(new int[]{1,1,0,1,0,0},'6') );
        numbers.add( new Key(new int[]{1,1,0,1,1,0},'7') );
        numbers.add( new Key(new int[]{1,1,0,0,1,0},'8') );
        numbers.add( new Key(new int[]{0,1,0,1,0,0},'9') );
        symbols.add( new Key(new int[]{0,1,0,0,0,0},',') );
        symbols.add( new Key(new int[]{0,1,1,0,0,0},';') );
        symbols.add( new Key(new int[]{0,1,0,0,1,0},':') );
        symbols.add( new Key(new int[]{0,1,0,0,1,1},'.') );
        symbols.add( new Key(new int[]{0,1,1,0,1,0},'!') );
        symbols.add( new Key(new int[]{0,1,1,0,0,1},'?') );
    }

    public Character getCharacter(int mode, int[] pattern){
        switch(mode){
            case LOWER_CASE_MODE:
                for (Key key : alphabet) {
                    if(key.isSameCharacter(pattern))
                        return key.getCharacter();
                }
                break;
            case UPPER_CASE_MODE:
                for (Key key : alphabet) {
                    if(key.isSameCharacter(pattern))
                        return Character.toUpperCase(key.getCharacter());
                }
                break;
            case NUMBER_MODE:
                for (Key key : numbers) {
                    if(key.isSameCharacter(pattern))
                        return key.getCharacter();
                }
                break;
            case SYMBOL_MODE:
                for (Key key : symbols) {
                    if(key.isSameCharacter(pattern))
                        return key.getCharacter();
                }
                break;
        }
        return '\u0000';
    }
}
