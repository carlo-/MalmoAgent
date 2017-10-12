package domain;

import com.google.gson.GsonBuilder;
import com.microsoft.msr.malmo.AgentHost;
import com.microsoft.msr.malmo.TimestampedStringVector;
import javafx.util.Pair;
import main.JavaAgent;
import main.Observations;

import java.util.List;

/**
 * Created by kartasevm on 11.10.2017.
 */
public class ObservationFactory {

    private static GsonBuilder builder = new GsonBuilder();
    private static Observations observations;
    private static boolean isValid = false;

    public static void invalidate() {
        isValid = false;
        observations = null;
    }

    public static Observations getObservations(AgentHost agentHost) {
        if (isValid) return observations;
        else {
            do {
                if (agentHost == null) {
                    int i = 0;
                }
                TimestampedStringVector obs = agentHost.getWorldState().getObservations();
                if (obs.size() > 0) {
                    String text = obs.get(0).getText();
                    observations = builder.create().fromJson(obs.get(0).getText(), Observations.class);
                    Pair<List<Integer>, List<String>> x = JavaAgent.JSONToLists(text);
                    observations.items = x.getValue();
                    observations.nbItems = x.getKey();
                }
            } while (observations == null);
            isValid = true;
            return observations;
        }
    }
}
