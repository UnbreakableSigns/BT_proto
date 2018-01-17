package jiepastor.brailletouch_proto.Braille;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Contains the Set of keys of a character
 */

public class KeySet {
    class Key {
        private Character character;
        private BraillePattern pattern = new BraillePattern();

        public Key(int[] pattern, char key){
            character = key;
            this.pattern.setPattern(pattern[0],pattern[1],pattern[2],pattern[3],pattern[4],pattern[5]);
        }

        public Character getCharacterKey() {
            return character;
        }

        public boolean isSameCharacter(BraillePattern p){
            return Arrays.equals(pattern.getPattern(),p.getPattern());
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

    public Character getBrailleCharacter(int mode, BraillePattern pattern){
        switch(mode){
            case LOWER_CASE_MODE:
                for (Key key : alphabet) {
                    if(key.isSameCharacter(pattern))
                        return key.getCharacterKey();
                }
                break;
            case UPPER_CASE_MODE:
                for (Key key : alphabet) {
                    if(key.isSameCharacter(pattern))
                        return Character.toUpperCase(key.getCharacterKey());
                }
                break;
            case NUMBER_MODE:
                for (Key key : numbers) {
                    if(key.isSameCharacter(pattern))
                        return key.getCharacterKey();
                }
                break;
            case SYMBOL_MODE:
                for (Key key : symbols) {
                    if(key.isSameCharacter(pattern))
                        return key.getCharacterKey();
                }
                break;
        }
        return '\u0000';
    }
}
