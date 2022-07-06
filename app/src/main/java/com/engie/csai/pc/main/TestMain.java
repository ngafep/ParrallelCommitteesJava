package com.engie.csai.pc.main;

public class TestMain {
    public static void main(String[] args) throws InterruptedException {
        for (int i = 0; i < 100; i++) {
            System.out.print("\rThinking... " + i);
            ClearConsole();
            System.out.flush();
            Thread.sleep(100);
        }
    }

    public static void ClearConsole(){
        try{
            String operatingSystem = System.getProperty("os.name"); //Check the current operating system

            if(operatingSystem.contains("Windows")){
                ProcessBuilder pb = new ProcessBuilder("cmd", "/c", "cls");
                Process startProcess = pb.inheritIO().start();
                startProcess.waitFor();
            } else {
                ProcessBuilder pb = new ProcessBuilder("clear");
                Process startProcess = pb.inheritIO().start();

                startProcess.waitFor();
            }
        }catch(Exception e){
            System.out.println(e);
        }
    }


}
