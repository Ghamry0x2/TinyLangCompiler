package tinylangcompiler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class Scanner {
    
    private File f1 = new File("tiny_sample_code.txt");
    private FileReader fr;
    PrintWriter pw;
        
    private static ArrayList reservedWords = new ArrayList();
    private static ArrayList specialChar = new ArrayList();
    
    private enum STATES{START, INCOMMENT, INNUM, INID, INASSIGN, DONE}; 
    
    private STATES state = STATES.START;
    private char c;
    private boolean readTheSameCharAgain = false;
        
    private String tempComment = "";
    private String tempNum = "";
    private String tempID = "";
    private String tempAssign = "";

    Scanner() throws IOException {
        this.fr = new FileReader(f1);
        pw = new PrintWriter("scanner_output.txt");
        
        initiallizeReservedWordsAndSpecialChars();
        
        init();
        
        pw.close();
    }
    
    public void init() throws IOException {
        
        do {
            
            if(!readTheSameCharAgain){
                c = (char)fr.read();                
            }
            
            switch(state){
                case START: 
                    start(); break;
                case INCOMMENT:
                    inComment(); break;
                case INNUM: 
                    inNum(); break;
                case INID: 
                    inID(); break;
                case INASSIGN: 
                    inAssign(); break;
                case DONE: 
                    done(); break;
            }
        }
        while( fr.ready() );
        
        //Checking the last character in the input stream
        if ( "".equals(tempID) && Character.isLetter(c) ){
            tempID = Character.toString(c);
        }
        else if ( "".equals(tempNum) && Character.isDigit(c) ){
            tempNum = Character.toString(c);
        }
        if ( "".equals(tempAssign) && c == '=' ){
            tempAssign = Character.toString(c);
        }
        
        
        done();
        pw.close();
    }

    private void start() {
        if( c == ' ' || c == '\n' || c == '\t' || c == '\r' ){
            readTheSameCharAgain = false;
            state = STATES.START;
        }
        else if( c == '{' ){
            readTheSameCharAgain = false;
            state = STATES.INCOMMENT;
        }
        else if( Character.isDigit(c) ){
            readTheSameCharAgain = true;
            state = STATES.INNUM;            
        }
        else if( Character.isLetter(c) ){
            readTheSameCharAgain = true;
            state = STATES.INID;            
        }
        else if( c == ':' ){
            readTheSameCharAgain = false;
            state = STATES.INASSIGN;            
        }
        else{
            readTheSameCharAgain = true;
            state = STATES.DONE;   
        }
    }

    private void inComment() {
        if( c != '}' ){
            readTheSameCharAgain = false;
            tempComment = tempComment + c;
            state = STATES.INCOMMENT;            
        }
        else{
            readTheSameCharAgain = false;
            state = STATES.DONE;
        }
    }

    private void inNum() {
        if( Character.isDigit(c) ){
            tempNum = tempNum + Character.toString(c);
            readTheSameCharAgain = false;
            state = STATES.INNUM;            
        }
        else{
            readTheSameCharAgain = true;
            state = STATES.DONE;
        }        
    }

    private void inID() {
        if( Character.isLetter(c) ){
            tempID = tempID + Character.toString(c);
            readTheSameCharAgain = false;
            state = STATES.INID;            
        }
        else{
            readTheSameCharAgain = true;
            state = STATES.DONE;
        }          
    }

    private void inAssign() {
        if( c == '=' ){
            tempAssign = ":" + Character.toString(c);
            readTheSameCharAgain = false;
            state = STATES.DONE;            
        }
        else{
            tempAssign = ":";
            readTheSameCharAgain = true;
            state = STATES.DONE;
        }          
    }

    private void done() {
        
        readTheSameCharAgain = true;
        
        // 1. Print token
        if( !"".equals(tempComment) ){
            readTheSameCharAgain = false;
//            pw.println(tempComment + " : comment");
        }
        else if( !"".equals(tempNum) ){
            pw.println(tempNum + " : number");
            Token.addToken("number", tempNum);
        }
        else if( !"".equals(tempID) ){
            if( reservedWords.contains(tempID) ){
                pw.println(tempID + " : reserved word");   
                Token.addToken(tempID, tempID);             
            }
            else{
                pw.println(tempID + " : identifier");
                Token.addToken("identifier", tempID);       
            }
        }
        else if( ":=".equals(tempAssign) ){
            pw.println(tempAssign + " : special symbol");
            Token.addToken(":=", tempAssign);   
        }
        else if ( ":".equals(tempAssign) ){
            readTheSameCharAgain = true;
            pw.println(tempAssign + " : unrecognized");
            Token.addToken("unrecognized", tempAssign);  
        }
        else if ( specialChar.contains(c) ){
            readTheSameCharAgain = false;
            pw.println(c + " : special symbol");
            Token.addToken(Character.toString(c), Character.toString(c));   
        }
        else{
            readTheSameCharAgain = false;
            pw.println(c + " : unrecognized");
            Token.addToken("unrecognized", Character.toString(c));   
        }
        
        // 2. Set all temps to empty strings
        tempComment = "";
        tempNum = "";
        tempID = "";
        tempAssign = "";
        
        
        // 3. Change your state
        state = STATES.START;
    }
    
    private static void initiallizeReservedWordsAndSpecialChars(){
        reservedWords.add("if");
        reservedWords.add("then");
        reservedWords.add("else");
        reservedWords.add("end");
        reservedWords.add("repeat");
        reservedWords.add("until");
        reservedWords.add("read");
        reservedWords.add("write");
        
        specialChar.add('+');
        specialChar.add('-');
        specialChar.add('*');
        specialChar.add('/');
        specialChar.add('<');
//        specialChar.add('>');
        specialChar.add('=');
        specialChar.add('(');
        specialChar.add(')');
        specialChar.add(';');
//        specialChar.add(':');
    }
    
    public static boolean doesReservedWordsContain(String word){
        return reservedWords.contains(word);
    }
    
    public static boolean doesSpecialCharactersContain(String word){
        return specialChar.contains(word);
    }
}
