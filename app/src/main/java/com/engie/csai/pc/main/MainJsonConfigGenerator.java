package com.engie.csai.pc.main;

import java.util.ArrayList;
import java.util.List;

public class MainJsonConfigGenerator {

    public static void main(String[] args) {

        displayConfig(500, 30, 3, 20);

    }

    private static void displayConfig(int numberOfCommittees, int numberOfRequests, int numberOfClients,
                                      int numberOfReplicas){
        System.out.println("""
                {
                  "networkConfig":\s
                  [
                                
                """+ String.join(",", generate(numberOfCommittees, numberOfRequests, numberOfClients,
                numberOfReplicas))+ """
                  ]
                }
                                
                """);
    }

    private static List<String> generate(int numberOfCommittees, int numberOfRequests, int numberOfClients,
                                         int numberOfReplicas){
        List<String> committees = new ArrayList<>();
        for(int committeeIndex = 1; committeeIndex<= numberOfCommittees; committeeIndex++){
            committees.add(generateOneCommittee("category "+committeeIndex,numberOfRequests,numberOfClients, numberOfReplicas));
        }
        return committees;
    }

    private static String generateOneCommittee(String name, int numberOfRequests, int numberOfClients,
                                               int numberOfReplicas){
        return String.format("""
                {
                      "name": "%s",
                      "numberOfQuota": 3,
                      "numberOfInitialTokens": 100,
                      "numberOfRequests": %d,
                      "capacity": 10,
                      "pql": 1000,
                      "maxDataSize": 2,
                      "numberOfClients": %d,
                      "numberOfReplicas": %d
                }
                    """,name, numberOfRequests,numberOfClients, numberOfReplicas);
    }
}
