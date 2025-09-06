package ticketbooking.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import lombok.Setter;
import ticketbooking.entities.Ticket;
import ticketbooking.entities.Train;
import ticketbooking.entities.User;
import ticketbooking.util.UserServiceUtil;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class UserBookingService {

    @Setter
    private User user;
    private List<User> userList;

    // Writable file location for runtime storage
    private static final String USERS_PATH = "data/user.json";

    // Default seed file in resources (read-only)
    private static final String SEED_PATH = "localDB/user.json";

    private final ObjectMapper objectMapper;

    public UserBookingService(User user) throws IOException {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
        this.user = user;
        loadUsersFromFile();
    }

    public UserBookingService() throws IOException {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
        loadUsersFromFile();
    }

    private void loadUsersFromFile() throws IOException {
        Path runtimePath = Paths.get(USERS_PATH);

        if (Files.exists(runtimePath)) {
            // ✅ Load existing users from writable file
            userList = objectMapper.readValue(runtimePath.toFile(), new TypeReference<List<User>>() {});
        } else {
            // ✅ Load seed file from resources
            InputStream input = getClass().getClassLoader().getResourceAsStream(SEED_PATH);
            if (input == null) {
                userList = new ArrayList<>();
            } else {
                userList = objectMapper.readValue(input, new TypeReference<List<User>>() {});
            }
            // Save to runtime file for persistence
            saveUserListToFile();
        }
    }

    private Optional<User> findUser(User userToFind) {
        return userList.stream().filter(u -> {
            boolean match = userToFind.getName().equals(u.getName());
            boolean pass = UserServiceUtil.checkPassword(userToFind.getPassword(), u.getHashedPassword());
            return match && pass;
        }).findFirst();
    }

    public Boolean login() {
        Optional<User> userToFind = findUser(user);
        return userToFind.isPresent();
    }

    public Boolean signUp(User user1) {
        try {
            userList.add(user1);
            saveUserListToFile();
            return Boolean.TRUE;
        } catch (IOException ex) {
            ex.printStackTrace();
            return Boolean.FALSE;
        }
    }

    private void saveUserListToFile() throws IOException {
        Path path = Paths.get(USERS_PATH);
        Files.createDirectories(path.getParent());

        objectMapper.writerWithDefaultPrettyPrinter().writeValue(path.toFile(), userList);
    }

    public List<Ticket> fetchBookings() {
        Optional<User> foundUser = findUser(user);
        if (foundUser.isPresent()) {
        return foundUser.get().getTicketsBooked();

        }
        else {
            System.out.println("No user found");
            return Collections.emptyList();
        }
    }

    public Boolean cancelBooking(String ticketId) {
        if (ticketId == null || ticketId.isEmpty()) {
            System.out.println("Ticket Id is empty or null");
            return Boolean.FALSE;
        }

        // Find the full user object from userList
        Optional<User> existingUserOpt = userList.stream()
                .filter(u -> u.getName().equals(user.getName()))
                .findFirst();

        if (existingUserOpt.isEmpty()) {
            System.out.println("User not found");
            return Boolean.FALSE;
        }

        User existingUser = existingUserOpt.get();

        // Remove ticket
        boolean removed = existingUser.getTicketsBooked().removeIf(ticket -> ticket.getTicketId().equals(ticketId));

        if (removed) {
            // Persist changes
            try {
                saveUserListToFile();
            } catch (IOException e) {
                e.printStackTrace();
                return Boolean.FALSE;
            }

            System.out.println("Booking has been cancelled with id: " + ticketId);
            return Boolean.TRUE;
        } else {
            System.out.println("No ticket found with id: " + ticketId);
            return Boolean.FALSE;
        }
    }


    public Boolean addBooking(User user, Train train, int row, int col, String source, String dest) {
        if (user == null || train == null) return Boolean.FALSE;

        // Create a Ticket object
        Ticket ticket = new Ticket();
        ticket.setTicketId(UUID.randomUUID().toString());
        ticket.setTrainId(train.getTrainId());
        ticket.setSeatNumber(String.valueOf(row) + "-"+ String.valueOf(col));
        ticket.setSource(source);  // or actual source
        ticket.setDestination(dest); // or actual dest
        ticket.setUserId(user.getUserId());

        user.getTicketsBooked().add(ticket);

        // Update user list and persist
        Optional<User> existingUser = userList.stream()
                .filter(u -> u.getName().equals(user.getName()))
                .findFirst();

        existingUser.ifPresent(u -> u.setTicketsBooked(user.getTicketsBooked()));

        try {
            saveUserListToFile();
        } catch (IOException e) {
            e.printStackTrace();
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }

}
