// --------------------------------------------------------------------------------------------------
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

// To compile:  javac -cp MalmoJavaJar.jar JavaExamples_run_mission.java
// To run:      java -cp MalmoJavaJar.jar:. JavaExamples_run_mission  (on Linux)
//              java -cp MalmoJavaJar.jar;. JavaExamples_run_mission  (on Windows)

// To run from the jar file without compiling:   java -cp MalmoJavaJar.jar:JavaExamples_run_mission.jar -Djava.library.path=. JavaExamples_run_mission (on Linux)
//                                               java -cp MalmoJavaJar.jar;JavaExamples_run_mission.jar -Djava.library.path=. JavaExamples_run_mission (on Windows)

import com.microsoft.msr.malmo.*;

public class JavaExamples_run_mission {
    static {
        System.loadLibrary("MalmoJava"); // attempts to load MalmoJava.dll (on Windows) or libMalmoJava.so (on Linux)
    }


    public static void main(String argv[]) {
        AgentHost agent_host = createAgentHost(argv);
        MissionSpec my_mission = createMissionSpec(agent_host);
        MissionRecordSpec my_mission_record = createMissionRecords();
        WorldState world_state = startMission(agent_host, my_mission, my_mission_record);
        // main loop:
        do {
            world_state = doStuff(agent_host);
            if (world_state == null) return;
        } while (world_state.getIsMissionRunning());

        System.out.println("Mission has stopped.");
    }

    private static MissionSpec createMissionSpec(AgentHost agent_host) {
        MissionSpec my_mission = new MissionSpec();
        my_mission.createDefaultTerrain();
        my_mission.forceWorldReset();
        my_mission.setWorldSeed("3;minecraft:bedrock,59*minecraft:stone,3*minecraft:dirt,minecraft:grass;1;");
        my_mission.timeLimitInSeconds(100000000.0f);
        my_mission.requestVideo(1920, 1080);
        my_mission.observeGrid(900, -1, 1000, 1000, -1, 1000, "CellObs");
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

    private static AgentHost createAgentHost(String[] argv) {
        AgentHost agent_host = new AgentHost();
        try {
            StringVector args = new StringVector();
            args.add("JavaExamples_run_mission");
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

    private static WorldState doStuff(AgentHost agent_host) {
        WorldState world_state;

        try {
            agent_host.sendCommand("pitch 0");
            agent_host.sendCommand("move 0.5");
            agent_host.sendCommand("turn 1");
            Thread.sleep(1000);
            agent_host.sendCommand("move 0");
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {
            Thread.sleep(500);
        } catch (InterruptedException ex) {
            System.err.println("User interrupted while mission was running.");
            return null;
        }
        world_state = agent_host.getWorldState();
        System.out.print("video,observations,rewards received: ");
        TimestampedStringVector observations = world_state.getObservations();
        System.out.println(observations.get(0).getText());
        System.out.print(world_state.getNumberOfVideoFramesSinceLastState() + ",");
        System.out.print(world_state.getNumberOfObservationsSinceLastState() + ",");
        System.out.println(world_state.getNumberOfRewardsSinceLastState());
        for (int i = 0; i < world_state.getRewards().size(); i++) {
            TimestampedReward reward = world_state.getRewards().get(i);
            System.out.println("Summed reward: " + reward.getValue());
        }
        for (int i = 0; i < world_state.getErrors().size(); i++) {
            TimestampedString error = world_state.getErrors().get(i);
            System.err.println("Error: " + error.getText());
        }
        return world_state;
    }

//    private Observations jsonToJavaExample(String obsString) throws JAXBException {
//        JAXBContext jc = JAXBContext.newInstance(Observations.class);
//        Unmarshaller unmarshaller = jc.createUnmarshaller();
//        unmarshaller.setProperty(UnmarshallerProperties.MEDIA_TYPE, "application/json");
//        unmarshaller.setProperty(UnmarshallerProperties.JSON_INCLUDE_ROOT, true);
//        StreamSource json = new StreamSource(
//                new StringReader(obsString));
//        return unmarshaller.unmarshal(json, Observations.class).getValue();
//
//    }
}
