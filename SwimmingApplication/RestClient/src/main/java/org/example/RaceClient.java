package org.example;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.Scanner;

public class RaceClient {
    private static final String URL = "http://localhost:8080/swimming/api/races";
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static final CloseableHttpClient httpClient = HttpClients.createDefault();

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        System.out.println("\n=== Race REST Client (Java) ===");

        while (running) {
            System.out.println("\n--- Menu ---");
            System.out.println("1. Get all races");
            System.out.println("2. Get race by ID");
            System.out.println("3. Create race");
            System.out.println("4. Update race");
            System.out.println("5. Delete race");
            System.out.println("0. Exit");
            System.out.print("\nSelect an option: ");

            int option = scanner.nextInt();
            scanner.nextLine();  // consume newline

            try {
                switch (option) {
                    case 1:
                        getAllRaces();
                        break;
                    case 2:
                        System.out.print("Enter race ID: ");
                        int id = scanner.nextInt();
                        scanner.nextLine();
                        getRaceById(id);
                        break;
                    case 3:
                        createRace(scanner);
                        break;
                    case 4:
                        updateRace(scanner);
                        break;
                    case 5:
                        System.out.print("Enter race ID to delete: ");
                        int deleteId = scanner.nextInt();
                        scanner.nextLine();
                        deleteRace(deleteId);
                        break;
                    case 0:
                        running = false;
                        System.out.println("Exiting...");
                        break;
                    default:
                        System.out.println("Invalid option!");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
                e.printStackTrace();
            }
        }

        scanner.close();
        httpClient.close();
    }

    private static void getAllRaces() throws IOException {
        System.out.println("\nFetching all races...");
        HttpGet request = new HttpGet(URL);
        request.addHeader("Accept", "application/json");

        try (CloseableHttpResponse response = httpClient.execute(request)) {
            printResponseStatus(response);

            if (response.getStatusLine().getStatusCode() == 200) {
                String json = EntityUtils.toString(response.getEntity());
                Race[] races = gson.fromJson(json, Race[].class);

                System.out.println("\n--- All Races ---");
                if (races.length == 0) {
                    System.out.println("No races found.");
                } else {
                    for (Race race : races) {
                        System.out.println(race);
                    }
                    System.out.println("\nTotal races: " + races.length);
                }
            }
        }
    }

    private static void getRaceById(int id) throws IOException {
        System.out.println("\nFetching race with ID: " + id);
        HttpGet request = new HttpGet(URL + "/" + id);
        request.addHeader("Accept", "application/json");

        try (CloseableHttpResponse response = httpClient.execute(request)) {
            printResponseStatus(response);

            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == 200) {
                String json = EntityUtils.toString(response.getEntity());
                Race race = gson.fromJson(json, Race.class);
                System.out.println("\n--- Race Details ---");
                System.out.println(race);
            } else if (statusCode == 404) {
                System.out.println("Race not found with ID: " + id);
            }
        }
    }

    private static void createRace(Scanner scanner) throws IOException {
        System.out.println("\nCreating a new race...");
        Race race = new Race();

        System.out.print("Enter Style (e.g., freestyle, butterfly): ");
        race.setStyle(scanner.nextLine());

        System.out.print("Enter Distance (meters): ");
        race.setDistance(scanner.nextInt());
        scanner.nextLine();

        System.out.print("Enter Number of Participants: ");
        race.setNrOfParticipants(scanner.nextInt());
        scanner.nextLine();

        HttpPost request = new HttpPost(URL);
        request.addHeader("Content-Type", "application/json");
        request.addHeader("Accept", "application/json");

        String json = gson.toJson(race);
        request.setEntity(new StringEntity(json));

        try (CloseableHttpResponse response = httpClient.execute(request)) {
            printResponseStatus(response);

            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == 200 || statusCode == 201) {
                String responseJson = EntityUtils.toString(response.getEntity());
                Race createdRace = gson.fromJson(responseJson, Race.class);
                System.out.println("\n--- Created Race ---");
                System.out.println(createdRace);
                System.out.println("\nRace created successfully" );
            } else {
                System.out.println("Failed to create race. Status: " + statusCode);
            }
        }
    }

    private static void updateRace(Scanner scanner) throws IOException {
        System.out.print("\nEnter race ID to update: ");
        int id = scanner.nextInt();
        scanner.nextLine();

        HttpGet getRequest = new HttpGet(URL + "/" + id);
        getRequest.addHeader("Accept", "application/json");

        try (CloseableHttpResponse getResponse = httpClient.execute(getRequest)) {
            printResponseStatus(getResponse);

            int statusCode = getResponse.getStatusLine().getStatusCode();
            if (statusCode == 404) {
                System.out.println("Race not found with ID: " + id);
                return;
            } else if (statusCode != 200) {
                System.out.println("Error getting race: " + statusCode);
                return;
            }

            String json = EntityUtils.toString(getResponse.getEntity());
            Race race = gson.fromJson(json, Race.class);
            System.out.println("\nCurrent race details: " + race);

            System.out.println("\nEnter new values (leave empty to keep current value):");

            System.out.print("Enter Style (current: " + race.getStyle() + "): ");
            String style = scanner.nextLine();
            if (!style.trim().isEmpty()) {
                race.setStyle(style);
            }

            System.out.print("Enter Distance (current: " + race.getDistance() + "): ");
            String distInput = scanner.nextLine();
            if (!distInput.trim().isEmpty()) {
                try {
                    race.setDistance(Integer.parseInt(distInput));
                } catch (NumberFormatException e) {
                    System.out.println("Invalid distance input, keeping current value.");
                }
            }

            System.out.print("Enter Number of Participants (current: " + race.getNrOfParticipants() + "): ");
            String nrOfParticipantsInput = scanner.nextLine();
            if (!nrOfParticipantsInput.trim().isEmpty()) {
                try {
                    race.setNrOfParticipants(Integer.parseInt(nrOfParticipantsInput));
                } catch (NumberFormatException e) {
                    System.out.println("Invalid number of participants input, keeping current value.");
                }
            }

            HttpPut updateRequest = new HttpPut(URL + "/" + id);
            updateRequest.addHeader("Content-Type", "application/json");
            updateRequest.addHeader("Accept", "application/json");

            String updateJson = gson.toJson(race);
            updateRequest.setEntity(new StringEntity(updateJson));

            try (CloseableHttpResponse updateResponse = httpClient.execute(updateRequest)) {
                printResponseStatus(updateResponse);

                int updateStatusCode = updateResponse.getStatusLine().getStatusCode();
                if (updateStatusCode == 200) {
                    String responseJson = EntityUtils.toString(updateResponse.getEntity());
                    Race updatedRace = gson.fromJson(responseJson, Race.class);
                    System.out.println("\n--- Updated Race ---");
                    System.out.println(updatedRace);
                    System.out.println("\nRace updated successfully!");
                } else {
                    System.out.println("Failed to update race. Status: " + updateStatusCode);
                }
            }
        }
    }

    private static void deleteRace(int id) throws IOException {
        System.out.println("\nDeleting race with ID: " + id);
        HttpDelete request = new HttpDelete(URL + "/" + id);

        try (CloseableHttpResponse response = httpClient.execute(request)) {
            printResponseStatus(response);

            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == 204) {
                System.out.println("Race deleted successfully!");
            } else if (statusCode == 404) {
                System.out.println("Race not found with ID: " + id);
            } else {
                System.out.println("Failed to delete race. Status: " + statusCode);
            }
        }
    }

    private static void printResponseStatus(CloseableHttpResponse response) {
        int statusCode = response.getStatusLine().getStatusCode();
        String reason = response.getStatusLine().getReasonPhrase();
        System.out.println("Response: " + statusCode + " " + reason);
    }

    static class Race {
        private Integer id;
        private String style;
        private Integer distance;
        private Integer nrOfParticipants;

        public Integer getId() { return id; }
        public void setId(Integer id) { this.id = id; }

        public String getStyle() { return style; }
        public void setStyle(String style) { this.style = style; }

        public Integer getDistance() { return distance; }
        public void setDistance(Integer distance) { this.distance = distance; }

        public Integer getNrOfParticipants() { return nrOfParticipants; }
        public void setNrOfParticipants(Integer nrOfParticipants) { this.nrOfParticipants = nrOfParticipants; }

        @Override
        public String toString() {
            return "Race{id=" + id + ", style='" + style + '\'' +
                    ", distance=" + distance +
                    ", nrOfParticipants=" + nrOfParticipants + '}';
        }
    }
}
