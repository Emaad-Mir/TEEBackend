package com.nighthawk.spring_portfolio.mvc.car;

import org.springframework.web.bind.annotation.*;

import com.nighthawk.spring_portfolio.mvc.betting.Betting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/api/fibonacci")
public class FibonacciApiController {

    abstract public class FibonacciAlgorithm {
        protected int iterations;
        protected String complexity;
        abstract void fibonacci(int length);
        public int measureFibonacciTime(int size) {
            long startTime = System.nanoTime();
            fibonacci(size);
            long endTime = System.nanoTime();
            return (int) (endTime - startTime) * 10;
        }
        public int getIterations() {
            return iterations;
        }
        public String getComplexity() {
            return complexity;
        }
    }

    public class ForLoopFibonacci extends FibonacciAlgorithm {
        @Override
        void fibonacci(int length) {
            int a = 0, b = 1;
            iterations = 0;
            complexity = "O(n)";
            for (int i = 1; i <= length; ++i) {
                iterations++;
                int c = a + b;
                a = b;
                b = c;
            }
        }
    }

    public class WhileLoopFibonacci extends FibonacciAlgorithm {
        @Override
        void fibonacci(int length) {
            complexity = "O(n)";
            iterations = 0;
            int a = 0, b = 1;
            int i = 1;
            while (i <= length) {
                iterations++;
                int c = a + b;
                a = b;
                b = c;
                i++;
            }
        }
    }

    public class RecursionFibonacci extends FibonacciAlgorithm {
        @Override
        void fibonacci(int length) {
            complexity = "O(n^2)";
            iterations = 0;
            finishRecursion(length);
        }

        private long finishRecursion(int n) {
            int f[]= new int[n + 2];
            int i;
 
            f[0] = 0;
            f[1] = 1;
 
            for (i = 2; i <= n; i++) {
                iterations++;
                f[i] = f[i - 1] + f[i - 2];
        }
 
        return f[n];
        }
    }

    public class MatrixFibonacci extends FibonacciAlgorithm {
        @Override
        void fibonacci(int length) {
            complexity = "O(log(n))";
            iterations = 0;
            matrixRecursive(length);
        }

        private long matrixRecursive(int n) {
            if (n == 0) {
                return 0;
            }
            long a = 0, b = 1, temp;
            for (int i = 2; i <= n; i++) {
                iterations++;
                temp = a + b;
                a = b;
                b = temp;
            }
            return a;
        }
    }

        @GetMapping("/speeds")
        public Map<String, Map<String, Integer>> getAlgorithmSpeeds(@RequestParam(required = false) Integer length) {
            int size = (length != null && length > 0) ? length : 35;

            Map<String, Map<String, Integer>> algorithmSpeeds = new HashMap<>();

            algorithmSpeeds.put("forLoopFibonacci", getAlgorithmInfo(new ForLoopFibonacci(), size));
            algorithmSpeeds.put("whileLoopFibonacci", getAlgorithmInfo(new WhileLoopFibonacci(), size));
            algorithmSpeeds.put("recursionFibonacci", getAlgorithmInfo(new RecursionFibonacci(), size));
            algorithmSpeeds.put("matrixFibonacci", getAlgorithmInfo(new MatrixFibonacci(), size));

            return algorithmSpeeds;
    }

    private Map<String, Integer> getAlgorithmInfo(FibonacciAlgorithm algorithm, int size) {
        Map<String, Integer> algorithmInfo = new HashMap<>();

        algorithmInfo.put("time", algorithm.measureFibonacciTime(size)); 
        algorithmInfo.put("iterations", algorithm.getIterations());
        return algorithmInfo;
    }

    @GetMapping("/bet")
    public Map<String, Object> betOnFibonacciRace(@RequestParam(required = true) int betAmount,
                                                  @RequestParam(required = true) int startingPoints) {
        Betting game = new Betting(startingPoints);
    
        Map<String, Map<String, Integer>> info = getAlgorithmSpeeds(null);

        Map<String, Integer> speeds = new HashMap<>();
        info.forEach((algorithm, analytics) -> speeds.put(algorithm, analytics.get("time")));
    
        // Randomly select a Fibonacci algorithm
        List<String> algorithms = new ArrayList<>(speeds.keySet());
        String selectedAlgorithm = algorithms.get(new Random().nextInt(algorithms.size()));
    
        String fastestAlgorithm = speeds.entrySet().stream()
                .min(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("none");
    
        boolean isGuessCorrect = selectedAlgorithm.equals(fastestAlgorithm);
    
        game.placeBet(betAmount, isGuessCorrect);
    
        Map<String, Object> result = new HashMap<>();
        result.put("message", game.getResultMessage());
        result.put("newScore", game.getPoints());
        result.put("selectedAlgorithm", selectedAlgorithm);
    
        Map<String, String> fibonacciFacts = getFibonacciFacts();
        result.put("fastestAlgorithm", fastestAlgorithm);
        result.put("funFact", fibonacciFacts.get(fastestAlgorithm));
    
        return result;
    }
    
    private Map<String, String> getFibonacciFacts() {
        Map<String, String> facts = new HashMap<>();
        facts.put("forLoopFibonacci", "For Loop Fibonacci is efficient for small sequences but becomes less efficient as the sequence grows due to its linear time complexity.");
        facts.put("whileLoopFibonacci", "While Loop Fibonacci is similar to the for-loop version but uses a while loop, making it slightly more versatile in certain contexts.");
        facts.put("recursionFibonacci", "Recursion Fibonacci is elegant but can be less efficient due to its exponential time complexity, especially for large sequences.");
        facts.put("matrixFibonacci", "Matrix Fibonacci uses matrix multiplication to calculate Fibonacci numbers, providing a more efficient approach for larger sequences.");
        return facts;
    }
}