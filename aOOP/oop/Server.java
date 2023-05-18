import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class Server {

    public static void main(String[] args) {
        try {
            // Create a server socket
            ServerSocket serverSocket = new ServerSocket(1024);
            System.out.println("Server started");
    
            while (true) {
                // Wait for a client to connect
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected");
    
                // Read the data sent by the client
                ObjectInputStream objectInputStream = new ObjectInputStream(clientSocket.getInputStream());
                Map<String, List<String>> preferences = (Map<String, List<String>>) objectInputStream.readObject();
                objectInputStream.close();
                clientSocket.close();
    
                // Generate the best fit of destinations for each student using a genetic algorithm
                Map<String, List<String>> bestFitDestinations = generateBestFitDestinations(preferences);
    
                // Print the best fit of destinations for each student
                System.out.println("Best fit of destinations:");
                for (String studentID : bestFitDestinations.keySet()) {
                    System.out.println("Student ID: " + studentID);
                    System.out.println("Best fit of destinations:");
                    for (String destination : bestFitDestinations.get(studentID)) {
                        System.out.println(destination);
                    }
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    
    /**
 * Generates the best fit of destinations for each student using a genetic algorithm.
 * @param preferences The student preferences.
 * @return The best fit of destinations for each student.
 */
private static Map<String, List<String>> generateBestFitDestinations(Map<String, List<String>> preferences) {
    // Define the genetic algorithm parameters
    int populationSize = 50;
    int maxGenerations = 100;
    double mutationRate = 0.1;
    int tournamentSize = 5;

    // Create the initial population
    List<Individual> population = new ArrayList<>();
    for (int i = 0; i < populationSize; i++) {
        population.add(new Individual(preferences));
    }

    // Evolve the population
    for (int i = 0; i < maxGenerations; i++) {
        // Select the parents using tournament selection
        List<Individual> parents = new ArrayList<>();
        for (int j = 0; j < populationSize; j++) {
            parents.add(tournamentSelection(population, tournamentSize));
        }

        // Crossover the parents to create the offspring
        List<Individual> offspring = new ArrayList<>();
        for (int j = 0; j < populationSize; j++) {
            Individual parent1 = parents.get(j);
            Individual parent2 = parents.get((j + 1) % populationSize);
            offspring.add(crossover(parent1, parent2));
        }

        // Mutate the offspring
        for (Individual individual : offspring) {
            mutate(individual, mutationRate);
        }

        // Replace the population with the offspring
        population = offspring;
    }

    // Get the best fit of destinations for each student
    Map<String, List<String>> bestFitDestinations = new HashMap<>();
    for (String studentID : preferences.keySet()) {
        Individual bestIndividual = null;
        double bestFitness = Double.NEGATIVE_INFINITY;
        for (Individual individual : population) {
            if (individual.getFitness(preferences.get(studentID)) > bestFitness) {
                bestIndividual = individual;
                bestFitness = individual.getFitness(preferences.get(studentID));
            }
        }
        bestFitDestinations.put(studentID, bestIndividual.getDestinations());
    }
    return bestFitDestinations;
}

        
        /**
         * Performs tournament selection on the population to select the fittest individual.
         * @param population The population to select from.
         * @param tournamentSize The size of the tournament.
         * @return The fittest individual.
         */
        private static Individual tournamentSelection(List<Individual> population, int tournamentSize) {
            // Create a tournament
            List<Individual> tournament = new ArrayList<>();
            for (int i = 0; i < tournamentSize; i++) {
                tournament.add(population.get((int) (Math.random() * population.size())));
            }
        
            // Find the fittest individual in the tournament
            Individual fittest = null;
            double bestFitness = Double.NEGATIVE_INFINITY;
            for (Individual individual : tournament) {
                if (individual.getFitness() > bestFitness) {
                    fittest = individual;
                    bestFitness = individual.getFitness();
                }
            }
            return fittest;
        }
        
        /**
         * Performs crossover on two individuals to create an offspring.
         * @param parent1 The first parent.
         * @param parent2 The second parent.
         * @return The offspring.
         */
        private static Individual crossover(Individual parent1, Individual parent2) {
            // Create the offspring
            Individual offspring = new Individual(parent1.getPreferences());
        
            // Choose a random crossover point
            int crossoverPoint = (int) (Math.random() * parent1.getDestinations().size());
        
            // Copy the first part of the first parent's destinations to the offspring
            for (int i = 0; i < crossoverPoint; i++) {
                offspring.getDestinations().set(i, parent1.getDestinations().get(i));
            }
        
            // Copy the second part of the second parent's destinations to the offspring
            for (int i = crossoverPoint; i < offspring.getDestinations().size(); i++) {
                if (!offspring.getDestinations().contains(parent2.getDestinations().get(i))) {
                    offspring.getDestinations().set(i, parent2.getDestinations().get(i));
                }
            }
        
            return offspring;
        }
        
        /**
         * Performs mutation on an individual.
         * @param individual The individual to mutate.
         * @param mutationRate The mutation rate.
         */
        private static void mutate(Individual individual, double mutationRate) {
            // Mutate each destination with the given probability
            for (int i = 0; i < individual.getDestinations().size(); i++) {
                if (Math.random() < mutationRate) {
                    // Swap the destination with another random one
                    int randomIndex = (int) (Math.random() * individual.getDestinations().size());
                    String temp = individual.getDestinations().get(i);
                    individual.getDestinations().set(i, individual.getDestinations().get(randomIndex));
                    individual.getDestinations().set(randomIndex, temp);
                }
            }
        }
        
        /**
 * Represents an individual in the population.
 */
private static class Individual {
    private Map<String, List<String>> preferences;
    private List<String> destinations;
    private double fitness;

    public Individual(Map<String, List<String>> preferences) {
        this.preferences = preferences;
        this.destinations = new ArrayList<>(); // initialize the destinations list
        List<String> allDestinations = new ArrayList<>();
        for (List<String> studentPreferences : preferences.values()) {
            allDestinations.addAll(studentPreferences);
        }

        Set<String> uniqueDestinations = new LinkedHashSet<>(allDestinations);
        destinations.addAll(uniqueDestinations);
        Collections.shuffle(destinations);
        calculateFitness();
    }
    
    // rest of the class



/**
 * Gets the individual's preferences.
 * @return The individual's preferences.
 */
public Map<String, List<String>> getPreferences() {
    return preferences;
}

/**
 * Gets the individual's list of destinations.
 * @return The individual's list of destinations.
 */
public List<String> getDestinations() {
    return destinations;
}

/**
 * Gets the individual's fitness.
 * @return The individual's fitness.
 */
public double getFitness() {
    return fitness;
}

/**
 * Calculates the individual's fitness based on the preferences of all the students.
 */
public void calculateFitness() {
    double totalFitness = 0.0;

    // Calculate the fitness for each student
    for (String studentID : preferences.keySet()) {
        List<String> studentPreferences = preferences.get(studentID);

        // Calculate the student's fitness
        double studentFitness = 0.0;
        for (String destination : destinations) {
            if (studentPreferences.contains(destination)) {
                studentFitness += 1.0 / (double) destinations.indexOf(destination);
            }
        }

        totalFitness += studentFitness;
    }

    fitness = totalFitness;
}

/**
 * Gets the fitness of the individual for a specific student's preferences.
 * @param studentPreferences The student's preferences.
 * @return The fitness of the individual for the student's preferences.
 */
public double getFitness(List<String> studentPreferences) {
    double studentFitness = 0.0;
    for (String destination : destinations) {
        if (studentPreferences.contains(destination)) {
            studentFitness += 1.0 / (double) destinations.indexOf(destination);
        }
    }
    return studentFitness;
    }
}
}


    
    
                   
