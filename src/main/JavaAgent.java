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
import domain.actions.MoveTo;
import domain.fluents.IsAt;

public class JavaAgent {


    static {
        System.loadLibrary("MalmoJava"); // attempts to load MalmoJava.dll (on Windows) or libMalmoJava.so (on Linux)
    }

    private static GsonBuilder builder = new GsonBuilder();
    private static ActionFactory factory;
    private static Agent agent;


    public static void main(String argv[]) throws InterruptedException {
        AgentHost agent_host = createAgentHost(argv);
        MissionSpec my_mission = createMissionSpec(agent_host);
        MissionRecordSpec my_mission_record = createMissionRecords();
        WorldState world_state = startMission(agent_host, my_mission, my_mission_record);

        Thread.sleep(500);
        agent_host.sendCommand("jump 1");
        Thread.sleep(500);
        agent_host.sendCommand("jump 0");
        Thread.sleep(500);

        agent = createGoalAgent(agent_host);

        do {
            world_state = agent_host.getWorldState();
            TimestampedStringVector observations = world_state.getObservations();

            if (observations.size() > 0) {
                Observations unmarshalled = builder.create().fromJson(observations.get(0).getText(), Observations.class);
                System.out.println("X: " + unmarshalled.XPos + "  Y:" + unmarshalled.YPos + "  Z:" + unmarshalled.ZPos + "  Yaw:" + unmarshalled.Yaw + "  Pitch:" + unmarshalled.Pitch);
                agent.execute(unmarshalled);
                Thread.sleep(50);
            }
            if (world_state == null) return;
        } while (world_state.getIsMissionRunning());

        System.out.println("Mission has stopped.");
    }

    private static Agent createGoalAgent(AgentHost agent_host) throws InterruptedException {
        WorldState world_state;
        world_state = agent_host.getWorldState();
        TimestampedStringVector observations = world_state.getObservations();

        while (observations.size() < 0) {
            observations = world_state.getObservations();
            Thread.sleep(50);
        }

        Observations unmarshalled = builder.create().fromJson(observations.get(0).getText(), Observations.class);
        return new Agent(new IsAt(0, unmarshalled.YPos, 0), factory = new ActionFactory(agent_host));
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

    private static MissionSpec createMissionSpec(AgentHost agent_host) {
        MissionSpec my_mission = new MissionSpec();
        my_mission.forceWorldReset();
        //my_mission.setWorldSeed("3;minecraft:bedrock,59*minecraft:stone,3*minecraft:dirt,minecraft:grass;1;");
        my_mission.timeLimitInSeconds(100000000.0f);
        my_mission.requestVideo(1024, 800);
        my_mission.observeGrid(3, -1, 3, -3, -1, -3, "CellObs");
        my_mission.startAt(0, 230, -50);
        my_mission.allowAllDiscreteMovementCommands();
        my_mission.allowAllAbsoluteMovementCommands();
        my_mission.allowAllContinuousMovementCommands();
        return my_mission;
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

}
