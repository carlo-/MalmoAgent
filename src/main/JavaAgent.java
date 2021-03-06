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

import com.microsoft.msr.malmo.*;
import domain.Action;
import domain.AtomicFluent;
import domain.BlockType;
import domain.fluents.BlockAt;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;

public class JavaAgent {

    public static final ObservationGrid CELL_PLANE = new ObservationGrid(-50, -2, -50, 50, 2, 50);
    public static final ObservationGrid CELL_BOX = new ObservationGrid(-5, -2, -5, 5, 2, 5);
    private final static String P1 = "InventorySlot_";
    private final static String P2 = "_size";
    private final static String P3 = "_item";
    private static Planner planner;
    private final static List<AtomicFluent> HOUSE_SHAPE = buildWalls(BlockType.planks, 0.5f, 226f, 0.5f, 3.5f, 227f, 3.5f);
    private final static List<AtomicFluent> COMPOSITE_WALL;

    static {
        COMPOSITE_WALL = buildRectangularParallelepiped(BlockType.cobblestone, 0.5f, 226, 0.5f,
                0.5f, 227, 0.5f);
        COMPOSITE_WALL.addAll(buildRectangularParallelepiped(BlockType.planks, 1.5f, 226, 0.5f,
                1.5f, 227, 0.5f));
    }

    static {
        System.loadLibrary("MalmoJava"); // attempts to load MalmoJava.dll (on Windows) or libMalmoJava.so (on Linux)
    }

    public static void main(String argv[]) throws Exception {
        AgentHost agent_host = createAgentHost(argv);
        MissionSpec my_mission = createMissionSpec(agent_host);
        MissionRecordSpec my_mission_record = createMissionRecords();
        WorldState world_state = startMission(agent_host, my_mission, my_mission_record);

        planner = createGoalAgent(agent_host);
        planner.execute();

        Observations observations = ObservationFactory.getObservations(agent_host);

        List<AtomicFluent> acts = buildRoof(BlockType.planks, 0.5f, observations.YPos - 1, 0.5f,
                3.5f, observations.YPos, 3.5f);

        acts.removeIf(pred -> ((BlockAt)pred).distanceFrom(1.5f,228f,1.5f) == 0);

        planner = new Planner(acts, agent_host);
        planner.execute();

        acts = new ArrayList<>();
        acts.add(new BlockAt(1.5f, 228f, 1.5f, BlockType.planks));

        planner = new Planner(acts, agent_host);
        planner.execute();


        System.out.println("Mission has stopped.");
    }

    private static Planner createGoalAgent(AgentHost agent_host) throws InterruptedException {
        Observations observations = ObservationFactory.getObservations(agent_host);
        return new Planner(buildWalls(BlockType.planks, 0.5f, observations.YPos - 1, 0.5f,
                3.5f, observations.YPos, 4.5f),
                agent_host);
        // return new Planner(COMPOSITE_WALL, agent_host);
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
        my_mission.startAt(0.5f, 227.0f, 0.5f);
        my_mission.timeLimitInSeconds(100000000.0f);
        my_mission.requestVideo(1024, 800);
        my_mission.observeGrid(CELL_PLANE.getXStartObservation(), CELL_PLANE.getYStartObservation(), CELL_PLANE.getZStartObservation(),
                CELL_PLANE.getXEndObservation(), CELL_PLANE.getYEndObservation(), CELL_PLANE.getZEndObservation(), "CellPlane");
        my_mission.observeGrid(CELL_BOX.getXStartObservation(), CELL_BOX.getYStartObservation(), CELL_BOX.getZStartObservation(),
                CELL_BOX.getXEndObservation(), CELL_BOX.getYEndObservation(), CELL_BOX.getZEndObservation(), "CellBox");
        my_mission.allowAllDiscreteMovementCommands();
        my_mission.allowAllAbsoluteMovementCommands();
        my_mission.allowAllInventoryCommands();
        //my_mission.drawSphere(0, 227, 20, 2, "stone");
        //my_mission.drawSphere(10, 227, 20, 2, "stone");
        my_mission.drawCuboid(0, 227, 20, 7, 228, 20, "stone");
        //drawStoneSource(my_mission);
        my_mission.observeFullInventory();
        drawTree(my_mission, -15, 20);
        drawTree(my_mission, -16, 18);
        drawTree(my_mission, -13, 18);
        drawTree(my_mission, -16, 23);
        drawTree(my_mission, -13, 21);
        drawTree(my_mission, -12, 25);
        drawTree(my_mission, -14, 27);
        return my_mission;
    }

    private static void drawTree(MissionSpec my_mission, int x, int z) {
        my_mission.drawLine(x, 227, z, x, 228, z, "log");
    }

    private static void drawStoneSource(MissionSpec my_mission) {
        my_mission.drawLine(0, 227, 10, 10, 227, 12, "stone");
    }

    private static MissionRecordSpec createMissionRecords() {
        MissionRecordSpec my_mission_record = new MissionRecordSpec("./saved_data.tgz");
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

    public static Pair<List<Integer>, List<String>> JSONToLists(String text) {
        int sizeChars = P1.length() + P2.length() + 3;
        List<Integer> out1 = new ArrayList<>();
        for (int i = 0; i <= 40; ++i) {
            if (i == 10) sizeChars++;
            int index = text.indexOf(P1 + i + P2) + sizeChars;
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
                "            <Summary>Let's build a house</Summary>\n" +
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
                "                 <InventoryItem slot=\"1\" type=\"diamond_pickaxe\"/>" +
                "                 <InventoryItem slot=\"2\" type=\"diamond_axe\"/>" +
                "            </Inventory>" +
                "            </AgentStart>\n" +
                "            <AgentHandlers>\n" +
                "               <ObservationFromNearbyEntities>\n" +
                "                   <Range name=\"Entities\" xrange=\"10\" yrange=\"10\" zrange=\"10\"/>\n" +
                "               </ObservationFromNearbyEntities>" +
                "                <ContinuousMovementCommands/>\n" +
                "                <SimpleCraftCommands/>\n" +
                "                <ObservationFromFullStats/>\n" +
                "            </AgentHandlers>            \n" +
                "        </AgentSection>\n" +
                "    </Mission>\n";
    }

    public static void checkArgs(float fromX, float fromY, float fromZ, float toX, float toY, float toZ) {
        if (fromX > toX || fromY > toY || fromZ > toZ) {
            throw new IllegalArgumentException("from cannot be bigger than to!");
        }
    }

    public static List<AtomicFluent> buildHouse(BlockType type, float fromX, float fromY, float fromZ, float toX, float toY, float toZ) {
        checkArgs(fromX, fromY, fromZ, toX, toY, toZ);
        //build floor
        List<AtomicFluent> out = buildRectangularParallelepiped(type, fromX, fromY, fromZ, toX, fromY, toZ);
        //build roof
        out.addAll(buildRectangularParallelepiped(type, fromX, toY, fromZ, toX, toY, toZ));
        //build 2 walls without the top/bottom
        out.addAll(buildRectangularParallelepiped(type, fromX, fromY + 1, fromZ, toX, toY - 1, fromZ));
        out.addAll(buildRectangularParallelepiped(type, fromX, fromY + 1, toZ, toX, toY - 1, toZ));
        //build last 2 walls
        out.addAll(buildRectangularParallelepiped(type, fromX, fromY + 1, fromZ + 1, fromX, toY - 1, toZ - 1));
        out.addAll(buildRectangularParallelepiped(type, toX, fromY + 1, fromZ + 1, toX, toY - 1, toZ - 1));
        //empty the inside of the house
        out.addAll(buildRectangularParallelepiped(BlockType.air, fromX + 1, fromY + 1, fromZ + 1, toX - 1, toY - 1, toZ - 1));
        //Maybe planner should figure that out itself later? Then we can avoid running back and forth
        return out;
    }

    public static List<AtomicFluent> buildRectangularParallelepiped(BlockType type, float fromX, float fromY, float fromZ, float toX, float toY, float toZ) {
        checkArgs(fromX, fromY, fromZ, toX, toY, toZ);
        List<AtomicFluent> out = new ArrayList<>();
        for (float x = fromX; x <= toX; ++x) {
            for (float z = fromZ; z <= toZ; ++z) {
                for (float y = fromY; y <= toY; ++y) {
                    out.add(new BlockAt(x, y, z, type));
                }
            }
        }
        return out;
    }

    public static List<AtomicFluent> buildWalls(BlockType type, float fromX, float fromY, float fromZ, float toX, float toY, float toZ) {
        checkArgs(fromX, fromY, fromZ, toX, toY, toZ);

        // Build first long wall
        List<AtomicFluent> out = buildRectangularParallelepiped(BlockType.cobblestone,fromX, fromY, fromZ, toX, fromY, fromZ);
        out.addAll(buildRectangularParallelepiped(BlockType.planks,fromX, fromY+1, fromZ, toX, toY+1, fromZ));

        // Build second long wall
        out.addAll(buildRectangularParallelepiped(BlockType.cobblestone,fromX, fromY, toZ, toX, fromY, toZ));
        out.addAll(buildRectangularParallelepiped(BlockType.planks,fromX, fromY+1, toZ, toX, toY+1, toZ));

        // Build small wall
        out.addAll(buildRectangularParallelepiped(BlockType.cobblestone,toX, fromY, fromZ+1, toX, fromY, toZ-1));
        out.addAll(buildRectangularParallelepiped(BlockType.planks,toX, fromY+1, fromZ+1, toX, toY+1, toZ-1));

        // Build small wall with door
        out.addAll(buildRectangularParallelepiped(BlockType.cobblestone,fromX, fromY, fromZ+2, fromX, fromY, toZ-1));
        out.addAll(buildRectangularParallelepiped(BlockType.planks,fromX, fromY+1, fromZ+2, fromX, toY+1, toZ-1));


        /*
        // Build roof (partial atm for debugging)
        out.addAll(buildRectangularParallelepiped(
                BlockType.cobblestone,fromX, toY+1, fromZ, toX, toY+1, fromZ+1));
         */

        return out;
    }

    public static List<AtomicFluent> buildRoof(BlockType type, float fromX, float fromY, float fromZ, float toX, float toY, float toZ) {
        return buildRectangularParallelepiped(type,fromX, toY+1, fromZ, toX, toY+1, toZ);
    }
}
