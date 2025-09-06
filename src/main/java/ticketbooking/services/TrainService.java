package ticketbooking.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import ticketbooking.entities.Train;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


public class TrainService {
    private List<Train> trainsList;

    private static final String TRAINS_PATH = "/Users/ankushyamkar/IdeaProjects/ticket-booking/src/main/java/ticketbooking/localDB/trains.json";

    private final ObjectMapper objectMapper = new ObjectMapper();

   public TrainService() throws IOException {
       trainsList = new ArrayList<>();
       InputStream input = getClass().getClassLoader().getResourceAsStream("localDB/trains.json");
       trainsList = objectMapper.readValue(input, new TypeReference<List<Train>>() {});
   }

   public void addTrain(Train train) {
       Optional<Train> alreadyExists = trainsList.stream().filter(t -> t.getTrainId().equalsIgnoreCase(train.getTrainId())).findFirst();

       if (alreadyExists.isPresent()) {
           updateTrain(train);
       }
       else {
           trainsList.add(train);
       }
   }

    public void updateTrain(Train updatedTrain) {
        // Find the index of the train with the same trainId
        OptionalInt index = IntStream.range(0, trainsList.size())
                .filter(i -> trainsList.get(i).getTrainId().equalsIgnoreCase(updatedTrain.getTrainId()))
                .findFirst();

        if (index.isPresent()) {
            // If found, replace the existing train with the updated one
            trainsList.set(index.getAsInt(), updatedTrain);
            saveTrainListToFile();
        } else {
            // If not found, treat it as adding a new train
            addTrain(updatedTrain);
        }
    }

    private void saveTrainListToFile() {
        try {
            Path path = Paths.get(TRAINS_PATH);
            Files.createDirectories(path.getParent());

            objectMapper.writerWithDefaultPrettyPrinter().writeValue(path.toFile(), trainsList);
        } catch (IOException e) {
            System.out.println("Error occurred while updating file"); // Handle the exception based on your application's requirements
        }
    }

    private boolean validTrain(Train train, String source, String destination) {
       List<String> stations= train.getStations();
       int sourceIndex = stations.indexOf(source);
       int destinationIndex = stations.indexOf(destination);

       return sourceIndex >= 0 && destinationIndex >= 0 && sourceIndex < destinationIndex;
   }

    public List<Train> getTrains(String source, String destination){
        return trainsList.stream().filter(train -> validTrain(train, source, destination)).collect(Collectors.toList());
    }

    public List<List<Integer>> fetchSeats(Train train){
        System.out.println(train.getTrainId()+ "fetching seats");
        return train.getSeats();
    }

    public Boolean bookTicket(Train train, int row, int seat){
        List<List<Integer>> seats = train.getSeats();
        if(row >=0 && row < seats.size() && seat >= 0 &&  seat < seats.get(row).size()){
            if(seats.get(row).get(seat) == 1){
                return Boolean.FALSE;
            } else{
                seats.get(row).set(seat,1);
                train.setSeats(seats);
                addTrain(train);
                return Boolean.TRUE;
            }
        } else {
            return Boolean.FALSE;
        }
    }

    public Optional<Train> getTrainById(String trainId){
       return trainsList.stream().filter(train -> train.getTrainId().equals(trainId)).findFirst();
    }

}
