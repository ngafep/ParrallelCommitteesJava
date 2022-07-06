package com.engie.csai.pc.core.models;

import java.util.List;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.barfuin.texttree.api.DefaultNode;
import org.barfuin.texttree.api.color.NodeColor;

@Builder
@Getter
@Setter
public class ProcessorNode {

    private int nodeIndex;

    private static int currentNodeIndex = 0;
    private synchronized int getNodeIndex(){
        if(nodeIndex == 0){
            nodeIndex = getCurrentNodeIndex();
        }
        return nodeIndex;
    }

    private static synchronized int getCurrentNodeIndex(){
        return currentNodeIndex ++;
    }
    private int currentQuota;
    private List<Replication> replications;
    private UUID id;

    private Committee committee;

    private static final int REDUCTION_QUOTA = 1;

    private int colorIndex = 0;


    public void reduceActualQuota() {
        currentQuota = Math.max(0, currentQuota - REDUCTION_QUOTA);
    }

    public DefaultNode getTree() {
        return getTree(NodeColor.values()[getColor()]);
    }
    public DefaultNode getTree(NodeColor color) {
        final var defaultNode = new DefaultNode("Node-" + getNodeIndex()+" - ID: "+
            getFullId() + " - Current Quota(" + currentQuota + ")");
        defaultNode.setColor(
            color);
//        defaultNode.setAnnotation(id.toString().substring(0,3));
//        defaultNode.setAnnotationColor(NodeColor.values()[getColor()]);
        return defaultNode;
    }

    public String getFullId(){
        return id+"-"+committee.getCategory();
    }

    public int getColor(){
        if(colorIndex == 0){
            colorIndex = (int)(Math.random()*NodeColor.values().length-2) + 2;
        }
        return colorIndex;
    }
}
