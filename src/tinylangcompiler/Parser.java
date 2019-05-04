package tinylangcompiler;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import static tinylangcompiler.Token.tokens;
import org.graphstream.graph.*;
import org.graphstream.graph.implementations.*;
import org.graphstream.ui.view.Viewer;

public class Parser {
    PrintWriter pw;
    Token token = Token.getNextToken();
    Graph graph = new SingleGraph("SyntaxTree"); 
    static int x,y,z = 0 ;
    
    
    Parser() throws IOException {
        pw = new PrintWriter("parser_output.txt");
        init();
        pw.close();
        
    }
    
    private void init(){
        Treenode Tree = program();
    
        drawTree(Tree);

    }
    
    private Treenode program(){
        pw.println("Program is found");
        
        Treenode nodeProgram = stmt_sequence();
        return nodeProgram;
    }
    
    private Treenode stmt_sequence(){
        pw.println("Statement Sequence is found");
        
        Treenode nodeStmtSeq = statement();
        Treenode nodeSib = nodeStmtSeq ;
        
        while( token != null && ";".equals(token.getValue()) ){
            match(";");
            token = Token.getNextToken();
            Treenode temp = statement();
            if( nodeSib != null){
            nodeSib.setSibling(temp);
            nodeSib = temp;
            }
        }
        
        if( token != null && !( "end".equals(token.getValue()) || 
                                "else".equals(token.getValue()) || 
                                "until".equals(token.getValue()) )){
                    
                                match(";");
        }
        
        return nodeStmtSeq;
    }
    
    private Treenode statement(){
        pw.println("Statement is found");
        
        if( token != null ){
            switch (token.getType()){
                case "if" : 
                    return if_stmt(); 
                case "repeat" : 
                    return repeat_stmt(); 
                case "identifier" : 
                    return assign_stmt(); 
                case "read" : 
                    return read_stmt(); 
                case "write" : 
                    return write_stmt(); 
            }
        }
        return null;
    }
    
    private Treenode if_stmt(){
        pw.println("If Statement is found"); 
        Treenode testChild = null;
        Treenode thenChild = null;
        Treenode elseChild = null;
        Treenode nodeIf = new Treenode("if");
        
        match("if");
        token = Token.getNextToken();
        
        testChild = exp();
        if (testChild != null){       
            nodeIf.addchild(testChild);
            match("then");
            token = Token.getNextToken();    
            thenChild = stmt_sequence();
            nodeIf.addchild(thenChild);
            
            if(token != null && "else".equals(token.getValue())){
                match("else");
                token = Token.getNextToken();  
                elseChild = stmt_sequence();
                nodeIf.addchild(elseChild);
            }
            
            match("end");
            token = Token.getNextToken();   
            return nodeIf;
        }
        return null;
    }
    
    private Treenode repeat_stmt(){
        pw.println("Repeat Statement is found"); 
        Treenode nodeRepeat = new Treenode("repeat");
        Treenode bodyChild = null;
        Treenode testChild = null;
        match("repeat");
        token = Token.getNextToken(); 
        
        bodyChild = stmt_sequence();
        nodeRepeat.addchild(bodyChild);
        
        match("until");
        token = Token.getNextToken();  
         
        testChild = exp();
        if ( testChild != null){
        nodeRepeat.addchild(testChild);
        return nodeRepeat;
        }
        return null;
    }
    
    private Treenode assign_stmt(){
        pw.println("Assign Statement is found"); 
        Treenode nodeAssign = new Treenode ("assign") ;
        Treenode temp = null;
        
        match("identifier");
        nodeAssign.setDesc("assign ( " + token.getValue() +" )" );
        token = Token.getNextToken();  
        
        match(":=");
        token = Token.getNextToken();  
        
        temp = exp();
        if (temp!= null){
            nodeAssign.addchild(temp);
            return nodeAssign;
        }
        
        return null;
    }
    
    private Treenode read_stmt(){
        pw.println("Read Statement is found"); 
        
        match("read");
        token = Token.getNextToken();  
        Treenode nodeRead = new Treenode ("read ( " + token.getValue() +" )" );
        
        match("identifier");
        token = Token.getNextToken();  
        
        return nodeRead;
    }
    
    private Treenode write_stmt(){
        pw.println("Write Statement is found"); 
        Treenode nodeWrite = new Treenode ("write") ;
        Treenode temp = null;
        
        match("write");
        token = Token.getNextToken();  
        
         temp = exp();
         if (temp!= null){
         nodeWrite.addchild(temp);
         return nodeWrite;
         }
         return null; //handled.
    }
    
    private Treenode exp(){
        pw.println("Exp is found"); 
        
        Treenode nodeExp = simple_exp();
        Treenode nodeOperation ;
        Treenode temp1 ;
        Treenode temp2 ;
        
        if( token != null && "<".equals(token.getValue()) ){
            match("<");
            token = Token.getNextToken();  
            nodeOperation = new Treenode("<");
            temp1 = nodeExp;
            nodeExp = nodeOperation;
            temp2 = simple_exp();
            nodeExp.addchild(temp1);
            nodeExp.addchild(temp2);
        }
        else if( token != null && "=".equals(token.getValue()) ){
            match("=");
            token = Token.getNextToken();  
            nodeOperation = new Treenode("=");
            temp1 = nodeExp;
            nodeExp = nodeOperation;
            temp2 = simple_exp();
            nodeExp.addchild(temp1);
            nodeExp.addchild(temp2);
        }
        return nodeExp;
    }
    
    private Treenode simple_exp(){
        
        Treenode nodeSimpleExp = term();
        Treenode nodeOperation ;
        Treenode temp1 ;
        Treenode temp2 ;
        
        while( token != null && ( "+".equals(token.getValue()) || "-".equals(token.getValue()) ) ){       
            switch (token.getValue()){
                case "+" : 
                    match("+"); 
                    token = Token.getNextToken(); 
                    nodeOperation = new Treenode("+");
                    temp1 = nodeSimpleExp;
                    nodeSimpleExp = nodeOperation;
                    temp2 = term(); 
                    nodeSimpleExp.addchild(temp1);
                    nodeSimpleExp.addchild(temp2);
                    break;
                    
                case "-" : 
                    match("-"); 
                    token = Token.getNextToken(); 
                    nodeOperation = new Treenode("-");
                    temp1 = nodeSimpleExp;
                    nodeSimpleExp = nodeOperation;
                    temp2 = term(); 
                    nodeSimpleExp.addchild(temp1);
                    nodeSimpleExp.addchild(temp2);
                    break;
            }
        }
        return nodeSimpleExp;
    }
    
    private Treenode term(){
        
        Treenode nodeTerm = factor();
        Treenode nodeOperation ;
        Treenode temp1 ;
        Treenode temp2 ;
        
        while( token != null && ( "*".equals(token.getValue()) || "/".equals(token.getValue()) ) ){            
            switch (token.getValue()){
                case "*" : 
                    match("*"); 
                    token = Token.getNextToken(); 
                    nodeOperation = new Treenode("*");
                    temp1 = nodeTerm;
                    nodeTerm = nodeOperation;
                    temp2 = term(); 
                    nodeTerm.addchild(temp1);
                    nodeTerm.addchild(temp2);
                    break;
                case "/" : 
                    match("/"); 
                    token = Token.getNextToken(); 
                    nodeOperation = new Treenode("/");
                    temp1 = nodeTerm;
                    nodeTerm = nodeOperation;
                    temp2 = term(); 
                    nodeTerm.addchild(temp1);
                    nodeTerm.addchild(temp2);
                    break;
            }
        }
        return nodeTerm;
    }
    
    private Treenode factor(){
        Treenode nodeFactor = null;
        
        while( token != null && !( "(".equals(token.getType()) || "number".equals(token.getType()) || "identifier".equals(token.getType()) ) ){
            match("'(' or number or identifier"); token = Token.getNextToken();
        }
        if( token != null ){
            switch (token.getType()){
                case "(" : 
                    match("("); 
                    token = Token.getNextToken(); 
                    nodeFactor = exp(); 
                    match(")"); 
                    token = Token.getNextToken(); 
                    return nodeFactor;
                    
                case "number" : 
                    match("number"); 
                    nodeFactor = new Treenode (token.getValue());
                    token = Token.getNextToken();
                    return nodeFactor;
                    
                case "identifier" : 
                    match("identifier"); 
                    nodeFactor = new Treenode (token.getValue());
                    token = Token.getNextToken(); 
                    return nodeFactor;
            }
        }
        return null;
    }
    
    public void match(String expectedTokenType){
        boolean missingSemiColonFlag = false;
        
        if( token != null && !(tokens.get(Token.getI()).getType().equals(expectedTokenType)) ){
            if( ";".equals(expectedTokenType) ){
                missingSemiColonFlag = true;                
            }
            
            if( tokens.get(Token.getI()).getType().equals("unrecognized") ){
                pw.println("ERROR: Unrecognized token (" + tokens.get(Token.getI()).getValue()+ "), "
                        + "Expected (" + expectedTokenType + ") after (" + tokens.get(Token.getI()-1).getType() + ")." );
            }
            else if( Token.getI() != 0 ){
                pw.println("ERROR: Expected (" + expectedTokenType + ") after (" + tokens.get(Token.getI()-1).getType() + ")." );
            }
            else{
                pw.println("ERROR: Unacceptable start for the program." );
            }
        }
        else if ( token != null && Scanner.doesReservedWordsContain(tokens.get(Token.getI()).getType()) ){
            pw.println(tokens.get(Token.getI()).getType());
        }
        
        if ( !missingSemiColonFlag && Token.getI() < tokens.size() ){
            Token.incI();
        }
    }
    
    public void unmatch(){
        if ( Token.getI() > 0 ) {
            Token.decI();
        }
    }
    
  
  public void drawTree(Treenode n) {
            System.setProperty("gs.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer");
            graph.addAttribute("ui.quality");
            graph.addAttribute("ui.antialias");
            Viewer viewer = graph.display();
            viewer.disableAutoLayout();
            draw(n); 
        
    }
     
  public Treenode draw(Treenode n){
    try {   
        if (n == null){
            return null;
        }
        int newy = y; 
        graph.addNode(n.getNodeName());
        Node temp = graph.getNode(n.getNodeName());
        temp.addAttribute("ui.label", n.getDesc());
        temp.setAttribute("xyz", x, newy ,z);
        if (condition1(n)){
            temp.addAttribute("ui.style", "shape:box;\n" +
                                          "fill-color: white;\n" +
                                          "stroke-mode: plain;\n" +
                                          "size: 65px, 30px;\n" + 
                                          " text-alignment: center;\n" + 
                                          "text-size: 10;");
        }
        else{ temp.addAttribute("ui.style", "shape:circle;\n" + 
                                            "fill-color: white;\n" + 
                                            "stroke-mode: plain;\n" +
                                            "size: 60px, 30px;\n" + 
                                            "text-alignment: center;\n" +
                                            "text-size: 10;");
        }
        //-----Children...
        y--;
        for (int i = 0; i < n.children.size(); i++) {
            if (i!= 0){
                x++;
            }
            Treenode n1 = draw(n.children.get(i));
            Node temp1 = graph.getNode(n1.getNodeName());
            graph.addEdge(n.getNodeName()+","+ n1.getNodeName(), temp, temp1);
        }
        //-----Siblings...    
        y = newy;
        if (n.sibling != null){
            x++;
            Treenode n1 = draw(n.sibling);
            Node temp1 = graph.getNode(n1.getNodeName());
            graph.addEdge(n.getNodeName()+","+ n1.getNodeName(), temp, temp1);
        }
    }
    catch (Exception e){
            pw.println("Error, Incorrect Code"); 
    }
    return n;
    }
  
    public boolean condition1(Treenode n){
    if (n.desc.startsWith("read") ||
        n.desc.startsWith("assign") ||
        n.desc.startsWith("write") ||
        n.desc.startsWith("if") ||
        n.desc.startsWith("repeat"))
    { 
        return true;
    }
    else 
       return false;
    }
}
