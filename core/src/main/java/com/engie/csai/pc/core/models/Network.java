package com.engie.csai.pc.core.models;

import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.barfuin.texttree.api.DefaultNode;
import org.barfuin.texttree.api.Node;

@Builder
@Getter
@Setter
public class Network {
    private List<Committee> committees;

    public Node getTree(){
        DefaultNode tree = new DefaultNode("root");
        committees.stream().forEach(committee -> tree.addChild(committee.getTree()));
        return tree;
    }
}
