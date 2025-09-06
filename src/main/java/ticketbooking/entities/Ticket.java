package  ticketbooking.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class Ticket{

    private String ticketId;

    private String userId;

    private String source;

    private String destination;

    private String dateOfTravel;

    private String trainId;

    private String seatNumber;

    public Ticket(){}

    public Ticket(String ticketId, String userId, String source, String destination, String dateOfTravel, String trainId){
        this.ticketId = ticketId;
        this.userId = userId;
        this.source = source;
        this.destination = destination;
        this.dateOfTravel = dateOfTravel;
        this.trainId = trainId;
    }

    public String getTicketInfo(){
        return String.format("Ticket ID: %s belongs to User %s from %s to %s on %s", ticketId, userId, source, destination, dateOfTravel);
    }

}