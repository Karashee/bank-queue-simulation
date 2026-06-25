# Bank Queue Simulation

**Author:** Andrew Karanja

## Assignment Description

This project implements a single-server bank queue simulation system that models customer arrivals, queueing, and service at a bank facility. The simulation generates 100 customers with random inter-arrival times and service times, computes detailed queue metrics for each customer, and produces comprehensive summary statistics to evaluate system performance.

The simulation uses:
- **Inter-arrival times:** Uniformly distributed between 1 and 8 minutes
- **Service times:** Uniformly distributed between 1 and 6 minutes
- **Random seed:** 42 (for reproducibility)

Results are presented through three channels:
1. Formatted terminal output showing customer-level metrics and summary statistics
2. CSV file export (`simulation_results.csv`) for further analysis
3. Java Swing visualization with line graphs

## Simulation Logic

The simulation follows discrete-event queue theory principles:

### Customer Arrival
- First customer arrives at time equal to their inter-arrival time
- Subsequent customers arrive based on: `Arrival[i] = Arrival[i-1] + InterArrival[i]`

### Service Scheduling
The single-server queue operates with these rules:
- **Service Start Time:** `max(Arrival Time, Previous Service End Time)`
  - If server is idle (customer arrives after previous service ends), service starts immediately
  - If server is busy, customer waits until previous service completes
  
- **Service End Time:** `Service Start Time + Service Time`

- **Waiting Time:** `Service Start Time - Arrival Time`
  - Time customer spends in queue before service begins
  
- **Time in System:** `Waiting Time + Service Time`
  - Total time from arrival to service completion
  
- **Server Idle Time:** `max(0, Arrival Time - Previous Service End Time)`
  - Time server remains idle between customers

### Queue Formulas

All metrics are computed using standard queueing theory formulas:

```
For Customer i:
  arrivalTimes[i] = arrivalTimes[i-1] + interArrivalTimes[i]
  serviceStartTimes[i] = max(arrivalTimes[i], serviceEndTimes[i-1])
  serviceEndTimes[i] = serviceStartTimes[i] + serviceTimes[i]
  waitingTimes[i] = serviceStartTimes[i] - arrivalTimes[i]
  timeInSystem[i] = waitingTimes[i] + serviceTimes[i]
  serverIdleTimes[i] = max(0, arrivalTimes[i] - serviceEndTimes[i-1])
```

## Queue Statistics Computed

The simulation computes 13 comprehensive summary statistics:

1. **Total Simulation Time:** Service end time of the last customer
2. **Total Waiting Time:** Sum of all customer waiting times
3. **Average Waiting Time:** Total waiting time ÷ 100
4. **Total Service Time:** Sum of all customer service times
5. **Average Service Time:** Total service time ÷ 100
6. **Average Time in System:** Sum of all customer times in system ÷ 100
7. **Total Server Idle Time:** Sum of all server idle time values
8. **Server Utilization:** (Total service time ÷ Total simulation time) × 100%
9. **Probability Customer Waits:** Count of customers with positive waiting time ÷ 100
10. **Maximum Waiting Time:** Largest waiting time among all customers
11. **Maximum Time in System:** Largest time in system among all customers
12. **Average Customers in Queue:** Total waiting time ÷ Total simulation time (Little's Law)
13. **Average Customers in System:** Sum of times in system ÷ Total simulation time (Little's Law)

## How to Compile and Run

### Prerequisites
- Java Development Kit (JDK) 8 or higher
- Terminal/Command Prompt

### Compilation
```bash
cd bank-queue-simulation
javac BankQueueSimulation.java
```

### Execution
```bash
java BankQueueSimulation
```

The program will:
1. Display a detailed table of all 100 customers in the terminal
2. Display summary statistics below the table
3. Create a CSV file named `simulation_results.csv` in the current directory
4. Launch a visualization window with two line graphs

### Running Property-Based Tests (Optional)
```bash
javac BankQueueSimulationTest.java
java BankQueueSimulationTest
```

This runs 13 property-based tests to validate the correctness of the simulation logic.

## Visualization

The Java Swing visualization window displays two line graphs:

1. **Waiting Time per Customer (Top Panel)**
   - X-axis: Customer number (1-100)
   - Y-axis: Waiting time in minutes
   - Shows how long each customer waited in queue before service
   - Helps identify patterns in queue congestion

2. **Time in System per Customer (Bottom Panel)**
   - X-axis: Customer number (1-100)
   - Y-axis: Time in system in minutes
   - Shows total time each customer spent from arrival to service completion
   - Combines waiting time and service time

Both graphs use blue lines connecting data points and include:
- Graph title and axis labels
- Scaled axes with tick marks and value labels
- Auto-scaling to fit data range

The visualization helps identify:
- Periods of high congestion (peaks in waiting time)
- Customer experience variability
- Trends over the simulation period

## Example Output

### Terminal Output (Sample)
```
========================================
BANK QUEUE SIMULATION RESULTS
========================================

Customer Inter-Arrival Arrival     Service     Start       End         Waiting     In System   Idle        
------------------------------------------------------------------------------------------------------------------------
1        4.73         4.73        3.71        4.73        8.44        0.00        3.71        0.00        
2        6.39         11.12       4.98        11.12       16.10       0.00        4.98        2.68        
3        2.09         13.21       2.68        16.10       18.78       2.89        5.57        0.00        
...
100      3.24         457.32      3.82        459.15      462.97      1.83        5.65        0.00        

========================================
SUMMARY STATISTICS
========================================

Total Simulation Time:           462.97 minutes
Total Waiting Time:              183.45 minutes
Average Waiting Time:            1.83 minutes
Total Service Time:              351.28 minutes
Average Service Time:            3.51 minutes
Average Time in System:          5.34 minutes
Total Server Idle Time:          111.69 minutes
Server Utilization:              75.87%
Probability Customer Waits:      0.57
Maximum Waiting Time:            8.92 minutes
Maximum Time in System:          13.45 minutes
Avg Customers in Queue:          0.40
Avg Customers in System:         1.15

========================================
```

### CSV File
The `simulation_results.csv` file contains all customer data in comma-separated format, suitable for import into Excel or other analysis tools.

### Visualization Window
A graphical window displays two line graphs showing waiting time and time in system trends across all 100 customers.

## Project Structure

```
bank-queue-simulation/
├── README.md                          # This file
├── BankQueueSimulation.java           # Main simulation program
├── BankQueueSimulationTest.java       # Property-based tests (optional)
└── simulation_results.csv             # Generated output file
```

## Implementation Details

- **Language:** Pure Java (JDK 8+)
- **Dependencies:** None (uses only Java standard library)
- **Build Tool:** None required (uses javac/java directly)
- **Random Seed:** 42 (ensures reproducible results)
- **Customers Simulated:** 100
- **Output Format:** 2 decimal places for all numeric values

## Notes

- The simulation is deterministic (fixed seed) for testing and verification
- All times are in minutes
- The visualization window must be closed to terminate the program
- CSV file is overwritten on each run
- Property-based tests validate 13 correctness properties of the simulation
