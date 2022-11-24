package com.engie.csai.pc.core.models;

import com.engie.csai.pc.core.consensus.ConsensusSimulator;
import com.engie.csai.pc.core.consensus.subscriber.MessageSubscriber;
import com.engie.csai.pc.core.listener.EndMetrics;
import com.engie.csai.pc.core.listener.ICommitteeListener;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import me.tongfei.progressbar.ProgressBar;
import org.barfuin.texttree.api.DefaultNode;
import org.barfuin.texttree.api.TextTree;
import org.barfuin.texttree.api.TreeOptions;
import org.barfuin.texttree.api.color.NodeColor;
import org.barfuin.texttree.api.style.TreeStyles;

@Builder
@Getter
public class Committee
    implements
    MessageSubscriber {
    private int capacity;
    private String category;
    private int initialQuota;
    private int dataSizeMax;
    private Consensus consensus;
    private List<ProcessorNode> processorNodes;
    private ProcessorQueue processorQueue;
    private List<Client> clients;
    private int timeSpent = 0;
    private int numberOfMessages = 0;

    private int numberOfClients;

    private List<ICommitteeListener> listeners = new ArrayList<ICommitteeListener>();

    public void registerListener(ICommitteeListener listener){
        listeners.add(listener);
    }

    public void setDisplayTree(
        boolean displayTree) {
        this.displayTree = displayTree;
        if(displayTree){
            progressBar.close();
        }
    }

    private boolean displayTree;

    private int nodeColorIndex;

    @Setter
    private long start;

    @Setter
    private ProgressBar progressBar;
    private Network network;

    private int counter = 0;

    public void subscribe(
        ConsensusSimulator simulator) {
        simulator.subscribe(
            this);
    }

    private NodeColor getColor() {
        String categoryNumberAsString = getCategory().substring(
            getCategory().indexOf(
                " ") + 1);
        final var categoryNumber = Integer.parseInt(
            categoryNumberAsString);
        switch (categoryNumber) {
            case 1:
                return NodeColor.DarkBlue;
            case 2:
                return NodeColor.LightGreen;
            case 3:
                return NodeColor.LightCyan;
            case 4:
                return NodeColor.LightMagenta;
            case 5:
                return NodeColor.LightYellow;
            default:
                return NodeColor.LightGray;
        }
    }

    @Override

    public void onMsgReceived(
        String msg) {
        checkMsg(
            msg);
        executeProtocol(displayTree);
        if(!displayTree) {
            progressBar.step();
//            final var totalTime = System.currentTimeMillis() - start;
//            final var meanTimePerRequest = 1.0 * totalTime / (numberOfMessages * 1.0);
//            String meanTime = getMeanValueAsFormattedString(
//                meanTimePerRequest);

//            progressBar.setExtraMessage("Time= " + getTotalTimeAsFormattedString(totalTime) +"ms. Mean time/request= " + meanTime +" ms/tx");
            if(numberOfMessages == progressBar.getMax()){
                progressBar.close();
//                listeners.forEach(l -> l.notifyFinished(numberOfMessages, totalTime));

            }
        }
    }

    @Override
    public void onMsgReceived(EndMetrics metrics) {

    }

    private static String getTotalTimeAsFormattedString(
        double totalTime) {
        DecimalFormat df = new DecimalFormat(
            "#########");
        String mt = df.format(
            totalTime);
        int fixStringLength = 7;
        int deltaLength = fixStringLength - mt.length();
        StringBuilder completion = new StringBuilder();
        for (int i = 0; i < deltaLength; i++) {
            completion.append(
                " ");
        }
        return completion + mt;
    }

    private static String getMeanValueAsFormattedString(
        double totalTime) {
        DecimalFormat df = new DecimalFormat(
            "####.0##");
        String mt = df.format(
            totalTime);
        String left = mt.replaceAll(
            "^([0-9]*)([\\.,])([0-9]*)0*$",
            "$1");
        String completionLeft = getCompletion(
            left,3);
        String right = mt.replaceAll(
            "^([0-9]*)([\\.,])([0-9]*)$",
            "$3");
        String completionRight = getCompletion(
            right,3);
        return completionLeft + mt + completionRight;
    }

    private static String getCompletion(
        String left, int fixStringLength) {
        int deltaLength = fixStringLength - left.length();
        StringBuilder completion = new StringBuilder();
        for (int i = 0; i < deltaLength; i++) {
            completion.append(
                " ");
        }
        return completion.toString();
    }

    private void checkMsg(
        String msg) {
        numberOfMessages++;
        String timeString = msg.substring(
            msg.indexOf(
                "Total time: "),
            msg.indexOf(
                "Total time: ") + 30);
        String[] splitTimeString = timeString.split(
            " ");
        timeSpent += Integer.parseInt(
            splitTimeString[2]);
    }

    private void executeProtocol(boolean slowMotion) {
        ProcessorNode leaderPeer = selectLeaderPeer();
        leaderPeer.reduceActualQuota();
        switchPeer(leaderPeer);
        try {
            if(slowMotion) {
                displayTree();
                Thread.sleep(
                    500);
            }
            else{
//                Thread.sleep(
//                    1000);

                //    displayTree();
            }
        } catch (
            InterruptedException e) {
            throw new RuntimeException(
                e);
        }
    }

    public void displayTree() {
        ClearConsole();
        TreeOptions options = new TreeOptions();
        options.setStyle(
            TreeStyles.ASCII_ROUNDED);
        System.out.print(
            TextTree.newInstance(
                    options)
                .render(
                    network.getTree()));
        System.out.flush();

    }

    public static void ClearConsole() {
        try {
            String operatingSystem = System.getProperty(
                "os.name"); //Check the current operating system

            if (operatingSystem.contains(
                "Windows")) {
                ProcessBuilder pb = new ProcessBuilder(
                    "cmd",
                    "/c",
                    "cls");
                Process startProcess = pb.inheritIO()
                    .start();
                startProcess.waitFor();
            } else {
                ProcessBuilder pb = new ProcessBuilder(
                    "clear");
                Process startProcess = pb.inheritIO()
                    .start();

                startProcess.waitFor();
            }
        } catch (
            Exception e) {
            System.out.println(
                e);
        }
    }

    public ProcessorNode selectLeaderPeer() {

        Random r = new Random();
        if (processorNodes.isEmpty()) {
            return null;
        }
        return processorNodes.get(
            r.nextInt(
                processorNodes.size()));
    }

    public ProcessorNode switchPeer(
        ProcessorNode nodeToSwitch) {
        if (nodeToSwitch.getCurrentQuota()==0) {

            // remove the nodeToSwitch from committee
            nodeToSwitch.setCurrentQuota(
                initialQuota);
            // get a waiting nodeToSwitch in the queue and remove it from the queue
            if (!processorQueue.getProcessorNodes()
                .isEmpty()) {
                int candidateNodeIndex = new Random().nextInt(
                    processorQueue.getProcessorNodes()
                        .size());
                var candidateNode = processorQueue.getProcessorNodes()
                    .get(
                        candidateNodeIndex);
                processorQueue.getProcessorNodes()
                    .remove(
                        candidateNode);
                processorNodes.remove(
                    nodeToSwitch);
                processorNodes.add(
                    candidateNode);

                // insert the initial committee nodeToSwitch in the queue
                processorQueue.getProcessorNodes()
                    .add(
                        nodeToSwitch);
                // insert the waiting nodeToSwitch in the committee
                return candidateNode;
            }
            return null;
        }
        return nodeToSwitch;
    }

    public int getTimeSpent() {
        return timeSpent;
    }

    public int getNumberOfMessages() {
        return numberOfMessages;
    }

    public DefaultNode getTree() {
        final var pText = this.category + " - Seats(" + this.getProcessorNodes()
            .size() + "/" + capacity + ") - Initial quota(" + initialQuota + ")" + " - Maximum " + "data size(" + dataSizeMax + ")" + " - Consensus(" + consensus + ")";
        DefaultNode committeeTree = new DefaultNode(
            pText);
        committeeTree.setColor(
            getColor());
        DefaultNode processorNodesTree = new DefaultNode(
            "Processor nodes");
        processorNodes.forEach(
            node -> processorNodesTree.addChild(
                node.getTree(
                    getColor())));
        committeeTree.addChild(
            processorNodesTree);
        DefaultNode processorQueueTree =
            new DefaultNode(
                "Processor queue - Capacity(" + processorQueue.getCapacity() + ")");
        processorQueue.getProcessorNodes()
            .stream()
            .forEach(
                node -> processorQueueTree.addChild(
                    node.getTree(
                        NodeColor.LightRed)));
        committeeTree.addChild(
            processorQueueTree);
        processorQueueTree.setColor(
            NodeColor.LightRed);

        return committeeTree;
    }

    public enum Consensus {
        PBFT,
        PAXOS,
        RAFT
    }
}
