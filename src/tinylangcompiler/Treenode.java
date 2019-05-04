package tinylangcompiler;

import java.util.ArrayList;

class Treenode {
    
    private Treenode parent;
    Treenode sibling;
    ArrayList <Treenode> children = new ArrayList();
    private static int nodeID = 0;
    private String nodeName = "Node ";
    String desc;

    public Treenode(String d) {
        this.desc = d;
        this.nodeName = nodeName + nodeID + "" ;
        nodeID ++;
    }

    void addchild(Treenode x){
        children.add(x);
    }

    public void setSibling(Treenode sibling) {
        this.sibling = sibling;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getNodeName() {
        return nodeName;
    }

    public String getDesc() {
        return desc;
    }

}