package ticketbooking;

import ticketbooking.entities.Train;
import ticketbooking.entities.User;
import ticketbooking.entities.Ticket;
import ticketbooking.services.TrainService;
import ticketbooking.services.UserBookingService;
import ticketbooking.util.UserServiceUtil;

import java.io.IOException;
import java.util.*;

public class Main {

    public static void main(String[] args) {
        System.out.println("Running Train Booking System");
        Scanner scanner = new Scanner(System.in);

        UserBookingService userBookingService;
        TrainService trainService;

        try {
            userBookingService = new UserBookingService();
            trainService = new TrainService();
        } catch (IOException e) {
            System.out.println("IO Error: " + e.getMessage());
            return;
        }

        User loggedInUser = null;
        Train trainSelectedForBooking = null;
        String selectedSource = null;
        String selectedDestination = null;

        int option = 0;

        while (option != 9) {
            System.out.println("\nChoose option:");
            System.out.println("1. Sign up");
            System.out.println("2. Login");
            System.out.println("3. Fetch Bookings");
            System.out.println("4. Search & Select Train");
            System.out.println("5. Book a Seat");
            System.out.println("6. Cancel my Booking");
            System.out.println("7. Logout");
            System.out.println("8. Exit the App");

            String input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                System.out.println("Input cannot be empty!");
                continue;
            }

            try {
                option = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input! Please enter a valid number.");
                continue;
            }

            switch (option) {
                case 1 -> {
                    String username = readNonEmptyInput(scanner, "Enter username to sign up:");
                    String password = readNonEmptyInput(scanner, "Enter password to sign up:");

                    User newUser = new User(username, password,
                            UserServiceUtil.hashPassword(password),
                            new ArrayList<>(), UUID.randomUUID().toString());

                    if (userBookingService.signUp(newUser)) {
                        System.out.println("User successfully signed up!");
                    } else {
                        System.out.println("Sign up failed!");
                    }
                }

                case 2 -> {
                    String username = readNonEmptyInput(scanner, "Enter username to login:");
                    String password = readNonEmptyInput(scanner, "Enter password:");

                    User userToLogin = new User(username, password,
                            UserServiceUtil.hashPassword(password),
                            new ArrayList<>(), UUID.randomUUID().toString());

                    userBookingService.setUser(userToLogin);
                    if (userBookingService.login()) {
                        loggedInUser = userToLogin;
                        System.out.println("Login successful!");
                    } else {
                        System.out.println("Login unsuccessful! Please try again.");
                    }
                }

                case 3 -> {
                    if (loggedInUser == null) {
                        System.out.println("You must login first!");
                        break;
                    }
                    List<Ticket> bookings = userBookingService.fetchBookings();
                    if (bookings.isEmpty()) {
                        System.out.println("No bookings found.");
                    } else {
                        System.out.println("Your bookings:");
                        bookings.forEach(ticket -> System.out.println("Ticket ID: " + ticket.getTicketId()
                                + ", Train: " + ticket.getTrainId()
                                + ", Seat: [" + ticket.getSeatNumber() + "]"
                                + ", From: " + ticket.getSource()
                                + ", To: " + ticket.getDestination()));
                    }
                }

                case 4 -> {
                    if (loggedInUser == null) {
                        System.out.println("You must login first!");
                        break;
                    }
                    selectedSource = readNonEmptyInput(scanner, "Enter source station:");
                    selectedDestination = readNonEmptyInput(scanner, "Enter destination station:");

                    List<Train> trains = trainService.getTrains(selectedSource, selectedDestination);
                    if (trains.isEmpty()) {
                        System.out.println("No trains found for this route.");
                        trainSelectedForBooking = null;
                        break;
                    }

                    System.out.println("Available trains:");
                    for (int i = 0; i < trains.size(); i++) {
                        Train t = trains.get(i);
                        System.out.println((i + 1) + ". Train ID: " + t.getTrainId());
                    }

                    while (true) {
                        String trainChoiceStr = readNonEmptyInput(scanner, "Select a train by typing 1,2,3...");
                        try {
                            int choice = Integer.parseInt(trainChoiceStr) - 1;
                            if (choice >= 0 && choice < trains.size()) {
                                trainSelectedForBooking = trains.get(choice);
                                System.out.println("Selected Train: " + trainSelectedForBooking.getTrainId()
                                        + " from " + selectedSource + " to " + selectedDestination);
                                break;
                            } else {
                                System.out.println("Invalid train selection!");
                            }
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid train selection!");
                        }
                    }
                }

                case 5 -> {
                    if (loggedInUser == null) {
                        System.out.println("You must login first!");
                        break;
                    }
                    if (trainSelectedForBooking == null) {
                        System.out.println("Please select a train first!");
                        break;
                    }

                    System.out.println("Selected Train: " + trainSelectedForBooking.getTrainId()
                            + " | Route: " + selectedSource + " -> " + selectedDestination);

                    List<List<Integer>> seats = trainService.fetchSeats(trainSelectedForBooking);
                    System.out.println("Seat layout (0 = available, 1 = booked):");
                    for (int r = 0; r < seats.size(); r++) {
                        for (int c = 0; c < seats.get(r).size(); c++) {
                            System.out.print(seats.get(r).get(c) + " ");
                        }
                        System.out.println();
                    }

                    int row = readIntInput(scanner, "Enter row:");
                    int col = readIntInput(scanner, "Enter column:");

                    boolean booked = trainService.bookTicket(trainSelectedForBooking, row, col);
                    if (booked) {
                        System.out.println("Seat booked successfully!");
                        userBookingService.addBooking(loggedInUser, trainSelectedForBooking, row, col, selectedSource, selectedDestination);
                    } else {
                        System.out.println("Seat already booked or invalid!");
                    }
                }

                case 6 -> System.out.println("Cancel booking feature not yet implemented");

                case 7 -> {
                    if (loggedInUser != null) {
                        System.out.println("User " + loggedInUser.getName() + " logged out successfully!");
                        loggedInUser = null;
                        trainSelectedForBooking = null;
                        selectedSource = null;
                        selectedDestination = null;
                    } else {
                        System.out.println("No user is logged in!");
                    }
                }

                case 8 -> {
                    System.out.println("Exiting the App...");
                    break;
                }

                default -> System.out.println("Invalid option! Try again.");
            }
            if (option == 8) break;
        }
    }

    private static String readNonEmptyInput(Scanner scanner, String prompt) {
        String input;
        while (true) {
            System.out.println(prompt);
            input = scanner.nextLine().trim();
            if (!input.isEmpty()) break;
            System.out.println("Input cannot be empty!");
        }
        return input;
    }

    private static int readIntInput(Scanner scanner, String prompt) {
        while (true) {
            String input = readNonEmptyInput(scanner, prompt);
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number!");
            }
        }
    }
}
