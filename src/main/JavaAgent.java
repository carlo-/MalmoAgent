package main;// --------------------------------------------------------------------------------------------------
//  Copyright (c) 2016 Microsoft Corporation
//  
//  Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
//  associated documentation files (the "Software"), to deal in the Software without restriction,
//  including without limitation the rights to use, copy, modify, merge, publish, distribute,
//  sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
//  furnished to do so, subject to the following conditions:
//  
//  The above copyright notice and this permission notice shall be included in all copies or
//  substantial portions of the Software.
//  
//  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
//  NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
//  NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
//  DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
//  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
// --------------------------------------------------------------------------------------------------

// To compile:  javac -cp MalmoJavaJar.jar main.JavaAgent.java
// To run:      java -cp MalmoJavaJar.jar:. main.JavaAgent  (on Linux)
//              java -cp MalmoJavaJar.jar;. main.JavaAgent  (on Windows)

// To run from the jar file without compiling:   java -cp MalmoJavaJar.jar:main.JavaAgent.jar -Djava.library.path=. main.JavaAgent (on Linux)
//                                               java -cp MalmoJavaJar.jar;main.JavaAgent.jar -Djava.library.path=. main.JavaAgent (on Windows)

import com.google.gson.GsonBuilder;
import com.microsoft.msr.malmo.*;
import domain.ActionFactory;
import domain.actions.SelectItem;
import domain.fluents.IsAt;
import domain.fluents.LookingAt;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;

public class JavaAgent {

    // start point must be lower than or equal to end point, for findBlock
    public static final int X_START_OBSERVATION = -50;
    public static final int Y_START_OBSERVATION = 0;
    public static final int Z_START_OBSERVATION = -50;
    public static final int X_STOP_OBSERVATION = 50;
    public static final int Y_STOP_OBSERVATION = 0;
    public static final int Z_STOP_OBSERVATION = 50;
    public static final int X_OBSERVATION_SIZE = Math.abs(X_START_OBSERVATION - X_STOP_OBSERVATION) + 1;
    public static final int Y_OBSERVATION_SIZE = Math.abs(Y_START_OBSERVATION - Y_STOP_OBSERVATION) + 1;
    public static final int Z_OBSERVATION_SIZE = Math.abs(Z_START_OBSERVATION - Z_STOP_OBSERVATION) + 1;

    static {
        System.loadLibrary("MalmoJava"); // attempts to load MalmoJava.dll (on Windows) or libMalmoJava.so (on Linux)
    }

    private static GsonBuilder builder = new GsonBuilder();
    private static ActionFactory factory;
    private static Planner planner;


    public static void main(String argv[]) throws Exception {
        AgentHost agent_host = createAgentHost(argv);
        MissionSpec my_mission = createMissionSpec(agent_host);
        MissionRecordSpec my_mission_record = createMissionRecords();
        WorldState world_state = startMission(agent_host, my_mission, my_mission_record);

        Thread.sleep(1000);
        agent_host.sendCommand("jump 1");
        Thread.sleep(1000);
        agent_host.sendCommand("jump 0");
        Thread.sleep(2000);
        /*System.out.println("swap");
        agent_host.sendCommand("swapInventoryItems 0 1");*/

        planner = createGoalAgent(agent_host);
        planner.execute();

        System.out.println("Mission has stopped.");
    }

    private static Planner createGoalAgent(AgentHost agent_host) throws InterruptedException {
        WorldState world_state;
        world_state = agent_host.getWorldState();
        TimestampedStringVector observations = world_state.getObservations();

        while (observations.size() < 0) {
            observations = world_state.getObservations();
            Thread.sleep(50);
        }

        String text = observations.get(0).getText();
        Observations unmarshalled = builder.create().fromJson(text, Observations.class);
        Pair<List<Integer>, List<String>> x = JSONToLists(text);
        unmarshalled.items = x.getValue();
        unmarshalled.nbItems = x.getKey();
        return new Planner(new IsAt(15.5f, unmarshalled.YPos, 15.5f), agent_host);
    }

    private static AgentHost createAgentHost(String[] argv) {
        AgentHost agent_host = new AgentHost();
        try {
            StringVector args = new StringVector();
            args.add("main.JavaAgent");
            for (String arg : argv)
                args.add(arg);
            agent_host.parse(args);
        } catch (Exception e) {
            System.err.println("ERROR: " + e.getMessage());
            System.err.println(agent_host.getUsage());
            System.exit(1);
        }
        if (agent_host.receivedArgument("help")) {
            System.out.println(agent_host.getUsage());
            System.exit(0);
        }
        return agent_host;
    }

    private static MissionSpec createMissionSpec(AgentHost agent_host) throws Exception {
        MissionSpec my_mission = new MissionSpec(createMissionXml(), true);
        my_mission.forceWorldReset();
        my_mission.startAt(0.5f, 229.0f, 0.5f);
        my_mission.timeLimitInSeconds(100000000.0f);
        my_mission.requestVideo(1024, 800);
        my_mission.observeGrid(X_START_OBSERVATION, Y_START_OBSERVATION, Z_START_OBSERVATION, X_STOP_OBSERVATION, Y_STOP_OBSERVATION, Z_STOP_OBSERVATION, "CellObs");
        my_mission.allowAllDiscreteMovementCommands();
        my_mission.allowAllAbsoluteMovementCommands();
        my_mission.drawSphere(20, 226, 20, 6, "stone");
        my_mission.observeFullInventory();
        drawTree(my_mission, -15, 20);
        drawTree(my_mission, -16, 23);
        drawTree(my_mission, -13, 21);
        drawTree(my_mission, -12, 25);
        drawTree(my_mission, -14, 27);
        return my_mission;
    }

    private static void drawTree(MissionSpec my_mission, int x, int z) {
        my_mission.drawLine(x, 226, z, x, 230, z, "log");
    }

    private static MissionRecordSpec createMissionRecords() {
        MissionRecordSpec my_mission_record = new MissionRecordSpec("./saved_data.tgz");
        my_mission_record.recordCommands();
        my_mission_record.recordMP4(20, 400000);
        my_mission_record.recordRewards();
        my_mission_record.recordObservations();
        return my_mission_record;
    }

    private static WorldState startMission(AgentHost agent_host, MissionSpec my_mission, MissionRecordSpec my_mission_record) {
        WorldState world_state = null;
        try {
            agent_host.startMission(my_mission, my_mission_record);
        } catch (MissionException e) {
            System.err.println("Error starting mission: " + e.getMessage());
            System.err.println("Error code: " + e.getMissionErrorCode());
            // We can use the code to do specific error handling, eg:
            if (e.getMissionErrorCode() == MissionException.MissionErrorCode.MISSION_INSUFFICIENT_CLIENTS_AVAILABLE) {
                // Caused by lack of available Minecraft clients.
                System.err.println("Is there a Minecraft client running?");
            }
            System.exit(1);
        }

        System.out.print("Waiting for the mission to start");
        do {
            System.out.print(".");
            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {
                System.err.println("User interrupted while waiting for mission to start.");
                return world_state;
            }
            world_state = agent_host.getWorldState();
            for (int i = 0; i < world_state.getErrors().size(); i++)
                System.err.println("Error: " + world_state.getErrors().get(i).getText());
        } while (!world_state.getIsMissionRunning());
        System.out.println("");
        return world_state;
    }

    private final static String P1 = "InventorySlot_";
    private final static String P2 = "_size";
    private final static String P3 = "_item";

    public static Pair<List<Integer>, List<String>> JSONToLists(String text) {
        int sizeChars = P1.length() + P2.length() +3;
        List<Integer> out1 = new ArrayList<>();
        for (int i = 0; i <= 40; ++i) {
            if (i == 10) sizeChars++;
            int index = text.indexOf(P1 + i + P2)+ sizeChars;
            boolean test;
            String current = "";
            do {
                char c = text.charAt(index++);
                test = c != ',';
                if (test) current += c;
            } while (test);
            out1.add(Integer.valueOf(current));
        }

        List<String> out2 = new ArrayList<>();
        for (int i = 0; i <= 40; ++i) {
            if (i == 10) sizeChars++;
            int index = text.indexOf(P1 + i + P3) + sizeChars;
            boolean test;
            String current = "";
            do {
                char c = text.charAt(index++);
                test = c != '"';
                if (test) current += c;
            } while (test);
            out2.add(current);
        }

        return new Pair<>(out1, out2);
    }

    public static String createMissionXml() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n" +
                "    <Mission xmlns=\"http://ProjectMalmo.microsoft.com\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +
                "        <About>\n" +
                "            <Summary>Lets build a house</Summary>\n" +
                "        </About>\n" +
                "\n" +
                "        <ServerSection>\n" +
                "            <ServerHandlers>\n" +
                "                <FlatWorldGenerator generatorString=\"3;7,220*1,5*3,2;3;,biome_1\"/>" +
                "                <ServerQuitFromTimeUp timeLimitMs=\"3000000\" description=\"out_of_time\"/>\n" +
                "                <ServerQuitWhenAnyAgentFinishes />\n" +
                "            </ServerHandlers>\n" +
                "        </ServerSection>\n" +
                "\n" +
                "        <AgentSection mode=\"Survival\">\n" +
                "            <Name>Rover</Name>\n" +
                "            <AgentStart>\n" +
                "            <Inventory>" +
                "                 <InventoryItem slot=\"0\" type=\"diamond_pickaxe\"/>" +
                "                 <InventoryItem slot=\"1\" type=\"wooden_pickaxe\"/>" +
                "            </Inventory>" +
                "            </AgentStart>\n" +
                "            <AgentHandlers>\n" +
                "                <ContinuousMovementCommands/>\n" +
                "                <ObservationFromFullStats/>\n" +
                "            </AgentHandlers>            \n" +
                "        </AgentSection>\n" +
                "    </Mission>\n";
    }

}
