import java.util.Random;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.IOException;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.HeadlessException;

/**
 * Bank Queue Simulation
 * 
 * Simulates a single-server bank queue with 100 customers.
 * Generates random inter-arrival and service times, computes queue metrics,
 * and displays results through terminal output, CSV file, and visualization.
 * 
 * Author: Andrew Karanja
 */
public class BankQueueSimulation {
    
    // Constants
    private static final int NUM_CUSTOMERS = 100;
    private static final int SEED = 42;
    
    // Random number generator
    private static Random random;
    
    // Customer metric arrays (8 arrays for 100 customers each)
    private static double[] interArrivalTimes = new double[NUM_CUSTOMERS];
    private static double[] arrivalTimes = new double[NUM_CUSTOMERS];
    private static double[] serviceTimes = new double[NUM_CUSTOMERS];
    private static double[] serviceStartTimes = new double[NUM_CUSTOMERS];
    private static double[] serviceEndTimes = new double[NUM_CUSTOMERS];
    private static double[] waitingTimes = new double[NUM_CUSTOMERS];
    private static double[] timeInSystem = new double[NUM_CUSTOMERS];
    private static double[] serverIdleTimes = new double[NUM_CUSTOMERS];
    
    public static void main(String[] args) {
        System.out.println("Starting Bank Queue Simulation...\n");
        
        // Phase 1: Initialize random number generator
        initializeRandomGenerator();
        
        // Phase 2: Generate random values (inter-arrival times and service times)
        generateRandomValues();
        
        // Phase 3: Simulate queue dynamics (compute all customer metrics)
        simulateQueue();
        
        // Phase 4: Compute summary statistics
        SummaryStatistics stats = computeSummaryStatistics();
        
        // Phase 5: Display results to terminal
        displayResults(stats);
        
        // Phase 6: Write results to CSV file
        writeCSV();
        
        // Phase 7: Launch visualization window (only in graphical environment)
        if (!java.awt.GraphicsEnvironment.isHeadless()) {
            launchVisualization();
        } else {
            System.out.println("\nNote: Visualization skipped (headless environment)");
            System.out.println("Simulation complete!");
        }
    }
    
    /**
     * Initialize the random number generator with seed 42.
     * Ensures reproducibility across simulation runs.
     */
    private static void initializeRandomGenerator() {
        random = new Random(SEED);
    }
    
    /**
     * Generate a uniform random value in the range [min, max].
     * Uses Random.nextDouble() scaled to the specified range.
     * 
     * @param min Minimum value (inclusive)
     * @param max Maximum value (inclusive)
     * @return Random double value in [min, max]
     */
    private static double generateUniform(double min, double max) {
        return min + (max - min) * random.nextDouble();
    }
    
    /**
     * Generate random inter-arrival times and service times for all customers.
     * Inter-arrival times are Uniform(1, 8) minutes.
     * Service times are Uniform(1, 6) minutes.
     */
    private static void generateRandomValues() {
        for (int i = 0; i < NUM_CUSTOMERS; i++) {
            interArrivalTimes[i] = generateUniform(1.0, 8.0);
            serviceTimes[i] = generateUniform(1.0, 6.0);
        }
    }
    
    /**
     * Simulate queue dynamics for all customers.
     * Computes arrival times, service start/end times, waiting times, 
     * time in system, and server idle times.
     */
    private static void simulateQueue() {
        // Handle first customer (index 0) as special case
        arrivalTimes[0] = interArrivalTimes[0];
        serviceStartTimes[0] = arrivalTimes[0];
        serviceEndTimes[0] = serviceStartTimes[0] + serviceTimes[0];
        waitingTimes[0] = 0.0;
        timeInSystem[0] = serviceTimes[0];
        serverIdleTimes[0] = 0.0;
        
        // Process remaining customers (indices 1-99)
        for (int i = 1; i < NUM_CUSTOMERS; i++) {
            // Calculate arrival time: previous arrival + current inter-arrival time
            arrivalTimes[i] = arrivalTimes[i - 1] + interArrivalTimes[i];
            
            // Calculate service start time: max of arrival time and previous service end time
            serviceStartTimes[i] = Math.max(arrivalTimes[i], serviceEndTimes[i - 1]);
            
            // Calculate service end time: service start + service time
            serviceEndTimes[i] = serviceStartTimes[i] + serviceTimes[i];
            
            // Calculate waiting time: service start - arrival time
            waitingTimes[i] = serviceStartTimes[i] - arrivalTimes[i];
            
            // Calculate time in system: waiting time + service time
            timeInSystem[i] = waitingTimes[i] + serviceTimes[i];
            
            // Calculate server idle time: max of 0 and (arrival - previous service end)
            serverIdleTimes[i] = Math.max(0.0, arrivalTimes[i] - serviceEndTimes[i - 1]);
        }
    }
    
    /**
     * Inner class to encapsulate summary statistics.
     * Contains all 13 aggregate metrics computed from customer data.
     */
    private static class SummaryStatistics {
        double totalSimulationTime;
        double totalWaitingTime;
        double averageWaitingTime;
        double totalServiceTime;
        double averageServiceTime;
        double averageTimeInSystem;
        double totalServerIdleTime;
        double serverUtilization;
        double probabilityCustomerWaits;
        double maxWaitingTime;
        double maxTimeInSystem;
        double avgCustomersInQueue;
        double avgCustomersInSystem;
        
        /**
         * Constructor accepting all 13 summary statistic values.
         */
        SummaryStatistics(double totalSimulationTime, double totalWaitingTime, 
                         double averageWaitingTime, double totalServiceTime,
                         double averageServiceTime, double averageTimeInSystem,
                         double totalServerIdleTime, double serverUtilization,
                         double probabilityCustomerWaits, double maxWaitingTime,
                         double maxTimeInSystem, double avgCustomersInQueue,
                         double avgCustomersInSystem) {
            this.totalSimulationTime = totalSimulationTime;
            this.totalWaitingTime = totalWaitingTime;
            this.averageWaitingTime = averageWaitingTime;
            this.totalServiceTime = totalServiceTime;
            this.averageServiceTime = averageServiceTime;
            this.averageTimeInSystem = averageTimeInSystem;
            this.totalServerIdleTime = totalServerIdleTime;
            this.serverUtilization = serverUtilization;
            this.probabilityCustomerWaits = probabilityCustomerWaits;
            this.maxWaitingTime = maxWaitingTime;
            this.maxTimeInSystem = maxTimeInSystem;
            this.avgCustomersInQueue = avgCustomersInQueue;
            this.avgCustomersInSystem = avgCustomersInSystem;
        }
    }
    
    /**
     * Display simulation results to terminal.
     * Prints customer-level metrics in a formatted table and summary statistics.
     * 
     * @param stats SummaryStatistics object containing aggregate metrics
     */
    private static void displayResults(SummaryStatistics stats) {
        // Print table header
        System.out.println("\n========================================");
        System.out.println("BANK QUEUE SIMULATION RESULTS");
        System.out.println("========================================\n");
        
        System.out.printf("%-8s %-12s %-12s %-12s %-12s %-12s %-12s %-12s %-12s%n",
                         "Customer", "Inter-Arrival", "Arrival", "Service", 
                         "Start", "End", "Waiting", "In System", "Idle");
        System.out.println("----------------------------------------" +
                          "----------------------------------------" +
                          "----------------------------------------");
        
        // Print all 100 customer rows
        for (int i = 0; i < NUM_CUSTOMERS; i++) {
            System.out.printf("%-8d %-12.2f %-12.2f %-12.2f %-12.2f %-12.2f %-12.2f %-12.2f %-12.2f%n",
                             i + 1,
                             interArrivalTimes[i],
                             arrivalTimes[i],
                             serviceTimes[i],
                             serviceStartTimes[i],
                             serviceEndTimes[i],
                             waitingTimes[i],
                             timeInSystem[i],
                             serverIdleTimes[i]);
        }
        
        // Print summary statistics section
        System.out.println("\n========================================");
        System.out.println("SUMMARY STATISTICS");
        System.out.println("========================================\n");
        
        System.out.printf("Total Simulation Time:           %.2f minutes%n", stats.totalSimulationTime);
        System.out.printf("Total Waiting Time:              %.2f minutes%n", stats.totalWaitingTime);
        System.out.printf("Average Waiting Time:            %.2f minutes%n", stats.averageWaitingTime);
        System.out.printf("Total Service Time:              %.2f minutes%n", stats.totalServiceTime);
        System.out.printf("Average Service Time:            %.2f minutes%n", stats.averageServiceTime);
        System.out.printf("Average Time in System:          %.2f minutes%n", stats.averageTimeInSystem);
        System.out.printf("Total Server Idle Time:          %.2f minutes%n", stats.totalServerIdleTime);
        System.out.printf("Server Utilization:              %.2f%%%n", stats.serverUtilization);
        System.out.printf("Probability Customer Waits:      %.2f%n", stats.probabilityCustomerWaits);
        System.out.printf("Maximum Waiting Time:            %.2f minutes%n", stats.maxWaitingTime);
        System.out.printf("Maximum Time in System:          %.2f minutes%n", stats.maxTimeInSystem);
        System.out.printf("Avg Customers in Queue:          %.2f%n", stats.avgCustomersInQueue);
        System.out.printf("Avg Customers in System:         %.2f%n", stats.avgCustomersInSystem);
        
        System.out.println("\n========================================\n");
    }
    
    /**
     * Compute summary statistics from customer arrays.
     * Calculates all 13 aggregate metrics as specified in requirements.
     * 
     * @return SummaryStatistics object containing all computed values
     */
    private static SummaryStatistics computeSummaryStatistics() {
        // 1. Total simulation time = service end time of last customer
        double totalSimulationTime = serviceEndTimes[NUM_CUSTOMERS - 1];
        
        // 2. Total waiting time = sum of all customer waiting times
        double totalWaitingTime = 0.0;
        for (int i = 0; i < NUM_CUSTOMERS; i++) {
            totalWaitingTime += waitingTimes[i];
        }
        
        // 3. Average waiting time = total waiting time / 100
        double averageWaitingTime = totalWaitingTime / NUM_CUSTOMERS;
        
        // 4. Total service time = sum of all customer service times
        double totalServiceTime = 0.0;
        for (int i = 0; i < NUM_CUSTOMERS; i++) {
            totalServiceTime += serviceTimes[i];
        }
        
        // 5. Average service time = total service time / 100
        double averageServiceTime = totalServiceTime / NUM_CUSTOMERS;
        
        // 6. Average time in system = sum of all customer times in system / 100
        double sumTimeInSystem = 0.0;
        for (int i = 0; i < NUM_CUSTOMERS; i++) {
            sumTimeInSystem += timeInSystem[i];
        }
        double averageTimeInSystem = sumTimeInSystem / NUM_CUSTOMERS;
        
        // 7. Total server idle time = sum of all server idle time values
        double totalServerIdleTime = 0.0;
        for (int i = 0; i < NUM_CUSTOMERS; i++) {
            totalServerIdleTime += serverIdleTimes[i];
        }
        
        // 8. Server utilization = (total service time / total simulation time) * 100
        double serverUtilization = (totalServiceTime / totalSimulationTime) * 100.0;
        
        // 9. Probability that a customer waits = count of customers with positive waiting time / 100
        int customersWhoWaited = 0;
        for (int i = 0; i < NUM_CUSTOMERS; i++) {
            if (waitingTimes[i] > 0.0) {
                customersWhoWaited++;
            }
        }
        double probabilityCustomerWaits = customersWhoWaited / (double) NUM_CUSTOMERS;
        
        // 10. Maximum waiting time = largest waiting time among all customers
        double maxWaitingTime = waitingTimes[0];
        for (int i = 1; i < NUM_CUSTOMERS; i++) {
            if (waitingTimes[i] > maxWaitingTime) {
                maxWaitingTime = waitingTimes[i];
            }
        }
        
        // 11. Maximum time in system = largest time in system among all customers
        double maxTimeInSystem = timeInSystem[0];
        for (int i = 1; i < NUM_CUSTOMERS; i++) {
            if (timeInSystem[i] > maxTimeInSystem) {
                maxTimeInSystem = timeInSystem[i];
            }
        }
        
        // 12. Average number of customers in queue = total waiting time / total simulation time
        double avgCustomersInQueue = totalWaitingTime / totalSimulationTime;
        
        // 13. Average number of customers in system = sum of all times in system / total simulation time
        double avgCustomersInSystem = sumTimeInSystem / totalSimulationTime;
        
        return new SummaryStatistics(totalSimulationTime, totalWaitingTime,
                                    averageWaitingTime, totalServiceTime,
                                    averageServiceTime, averageTimeInSystem,
                                    totalServerIdleTime, serverUtilization,
                                    probabilityCustomerWaits, maxWaitingTime,
                                    maxTimeInSystem, avgCustomersInQueue,
                                    avgCustomersInSystem);
    }
    
    /**
     * Write simulation results to CSV file.
     * Creates simulation_results.csv with all customer-level metrics.
     */
    private static void writeCSV() {
        try {
            PrintWriter writer = new PrintWriter(new FileWriter("simulation_results.csv"));
            
            // Write header row
            writer.println("Customer,Inter-Arrival,Arrival,Service,Start,End,Waiting,In System,Idle");
            
            // Write all 100 customer data rows
            for (int i = 0; i < NUM_CUSTOMERS; i++) {
                writer.printf("%d,%.2f,%.2f,%.2f,%.2f,%.2f,%.2f,%.2f,%.2f%n",
                             i + 1,
                             interArrivalTimes[i],
                             arrivalTimes[i],
                             serviceTimes[i],
                             serviceStartTimes[i],
                             serviceEndTimes[i],
                             waitingTimes[i],
                             timeInSystem[i],
                             serverIdleTimes[i]);
            }
            
            writer.close();
            System.out.println("CSV file 'simulation_results.csv' created successfully.");
            
        } catch (IOException e) {
            System.err.println("Error: Unable to write CSV file: " + e.getMessage());
            System.exit(1);
        }
    }
    
    /**
     * Launch Java Swing visualization window.
     * Displays line graphs for waiting time and time in system per customer.
     */
    private static void launchVisualization() {
        try {
            // Check if running in headless environment
            if (java.awt.GraphicsEnvironment.isHeadless()) {
                System.err.println("Warning: Cannot display visualization in headless environment");
                return;
            }
            
            // Create main frame
            JFrame frame = new JFrame("Bank Queue Simulation - Visualization");
            frame.setSize(800, 600);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLayout(new java.awt.GridLayout(2, 1));
            
            // Create graph panel for waiting time
            GraphPanel waitingTimePanel = new GraphPanel(waitingTimes, 
                                                        "Waiting Time per Customer",
                                                        "Customer Number",
                                                        "Waiting Time (minutes)");
            
            // Create graph panel for time in system
            GraphPanel timeInSystemPanel = new GraphPanel(timeInSystem,
                                                         "Time in System per Customer",
                                                         "Customer Number",
                                                         "Time in System (minutes)");
            
            frame.add(waitingTimePanel);
            frame.add(timeInSystemPanel);
            frame.setVisible(true);
            
        } catch (HeadlessException e) {
            System.err.println("Warning: Cannot display visualization in headless environment");
        } catch (Exception e) {
            System.err.println("Warning: Error creating visualization: " + e.getMessage());
        }
    }
    
    /**
     * Inner class for graph visualization.
     * Extends JPanel to provide custom rendering of line graphs.
     */
    private static class GraphPanel extends JPanel {
        private double[] dataPoints;
        private String graphTitle;
        private String xLabel;
        private String yLabel;
        
        /**
         * Constructor accepting data array and labels.
         * 
         * @param dataPoints Array of values to plot
         * @param graphTitle Title displayed at top of graph
         * @param xLabel Label for x-axis
         * @param yLabel Label for y-axis
         */
        GraphPanel(double[] dataPoints, String graphTitle, String xLabel, String yLabel) {
            this.dataPoints = dataPoints;
            this.graphTitle = graphTitle;
            this.xLabel = xLabel;
            this.yLabel = yLabel;
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            
            // Set background
            setBackground(java.awt.Color.WHITE);
            g.setColor(java.awt.Color.BLACK);
            
            // Get panel dimensions
            int width = getWidth();
            int height = getHeight();
            int padding = 60;
            int graphWidth = width - 2 * padding;
            int graphHeight = height - 2 * padding;
            
            // Draw title
            g.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 14));
            java.awt.FontMetrics titleMetrics = g.getFontMetrics();
            int titleWidth = titleMetrics.stringWidth(graphTitle);
            g.drawString(graphTitle, (width - titleWidth) / 2, padding / 2);
            
            // Find min and max values for scaling
            double minValue = dataPoints[0];
            double maxValue = dataPoints[0];
            for (int i = 1; i < dataPoints.length; i++) {
                if (dataPoints[i] < minValue) minValue = dataPoints[i];
                if (dataPoints[i] > maxValue) maxValue = dataPoints[i];
            }
            
            // Add 10% padding to y-axis range
            double range = maxValue - minValue;
            if (range == 0) range = 1.0; // Avoid division by zero
            minValue -= range * 0.1;
            maxValue += range * 0.1;
            
            // Draw axes
            g.drawLine(padding, height - padding, width - padding, height - padding); // x-axis
            g.drawLine(padding, padding, padding, height - padding); // y-axis
            
            // Draw axis labels
            g.setFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 12));
            java.awt.FontMetrics labelMetrics = g.getFontMetrics();
            
            // X-axis label
            int xLabelWidth = labelMetrics.stringWidth(xLabel);
            g.drawString(xLabel, (width - xLabelWidth) / 2, height - 10);
            
            // Y-axis label (rotated)
            g.drawString(yLabel, 10, padding - 20);
            
            // Draw tick marks and labels on y-axis
            int numYTicks = 5;
            for (int i = 0; i <= numYTicks; i++) {
                int y = height - padding - (i * graphHeight / numYTicks);
                double value = minValue + (i * (maxValue - minValue) / numYTicks);
                g.drawLine(padding - 5, y, padding, y);
                String label = String.format("%.1f", value);
                g.drawString(label, padding - 35, y + 5);
            }
            
            // Draw tick marks on x-axis
            int numXTicks = 10;
            for (int i = 0; i <= numXTicks; i++) {
                int x = padding + (i * graphWidth / numXTicks);
                int customerNum = i * (NUM_CUSTOMERS / numXTicks);
                g.drawLine(x, height - padding, x, height - padding + 5);
                g.drawString(String.valueOf(customerNum), x - 5, height - padding + 20);
            }
            
            // Draw line graph
            g.setColor(java.awt.Color.BLUE);
            for (int i = 0; i < dataPoints.length - 1; i++) {
                int x1 = padding + (i * graphWidth / (dataPoints.length - 1));
                int y1 = height - padding - (int)((dataPoints[i] - minValue) / (maxValue - minValue) * graphHeight);
                
                int x2 = padding + ((i + 1) * graphWidth / (dataPoints.length - 1));
                int y2 = height - padding - (int)((dataPoints[i + 1] - minValue) / (maxValue - minValue) * graphHeight);
                
                g.drawLine(x1, y1, x2, y2);
            }
        }
    }
}
