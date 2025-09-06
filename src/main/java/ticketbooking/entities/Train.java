package ticketbooking.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

@Setter
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
@Builder
public class Train {

    @JsonProperty("train_id")
    private String trainId;
    @JsonProperty("train_no")
    private String trainNo;

    private List<List<Integer>> seats;
    @JsonProperty("station_times")
    private Map<String, String> stationTimes;

    private List<String> stations;

    public Train(){}

    public Train(String trainId, String trainNo, List<List<Integer>> seats, Map<String, String> stationTimes, List<String> stations){
        this.trainId = trainId;
        this.trainNo = trainNo;
        this.seats = seats;
        this.stationTimes = stationTimes;
        this.stations = stations;
    }

    public String getTrainInfo(){
        return String.format("Train ID: %s Train No: %s", trainId, trainNo);
    }

}