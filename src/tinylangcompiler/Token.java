package tinylangcompiler;

import java.util.ArrayList;

public class Token {
    private String type;
    private String value;
    
    private static int i = 0;
    
    public static ArrayList<Token> tokens = new ArrayList<Token>();
    
    public static void addToken(String type, String value){
        Token tempToken = new Token();
        
        tempToken.type = type;
        tempToken.value = value;
        tokens.add(tempToken);
    }
    
    @Override
    public String toString(){
        return this.value + " : " + this.type;
    }
    
    public static Token getNextToken(){
        if( i < tokens.size() ){
            return tokens.get(i);
        }
        return null;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    public static int getI() {
        return i;
    }

    public static void setI(int i) {
        Token.i = i;
    }

    public static void incI() {
        i++;
    }

    public static void decI() {
        i--;
    }
}
