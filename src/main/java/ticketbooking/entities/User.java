package ticketbooking.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class User {
    private String name;
    private String password;
    @JsonProperty("hashed_password")
    private String hashedPassword;
    @JsonProperty("tickets_booked")
    private List<Ticket> ticketsBooked;
    @JsonProperty("user_id")
    private String userId;

    public User() {
    }

    public User(String name, String password, String hashedPassword, List<Ticket> ticketsBooked, String userId) {
        this.name = name;
        this.password = password;
        this.hashedPassword = hashedPassword;
        this.ticketsBooked = ticketsBooked;
        this.userId = userId;
    }

}

