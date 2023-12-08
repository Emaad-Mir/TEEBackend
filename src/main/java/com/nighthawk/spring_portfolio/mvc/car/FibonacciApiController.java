package com.nighthawk.spring_portfolio.mvc.car;

import org.springframework.web.bind.annotation.*;


import java.util.HashMap;
import java.util.Map;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/api/fibonacci")
public class FibonacciApiController {

    abstract static class FibonacciAlgorithm {
        abstract void fibonacci(int length);
    }

    static class ForLoopFibonacci extends FibonacciAlgorithm {
        @Override
        void fibonacci(int length) {
            long a = 0, b = 1, c, i;
            for (i = 2; i < length; i++) {
                c = a + b;
                a = b;
                b = c;
            }
        }
    }

    static class WhileLoopFibonacci extends FibonacciAlgorithm {
        @Override
        void fibonacci(int length) {
            long a = 0, b = 1;
            int i = 2;
            while (i < length) {
                long temp = a + b;
                a = b;
                b = temp;
                i++;
            }
        }
    }

    static class RecursionFibonacci extends FibonacciAlgorithm {
        @Override
        void fibonacci(int length) {
            finishRecursion(length);
        }

        private long finishRecursion(int n) {
            if (n <= 1) {
                return n;
            }
            return finishRecursion(n - 1) + finishRecursion(n - 2);
        }
    }

    static class MatrixFibonacci extends FibonacciAlgorithm {
        @Override
        void fibonacci(int length) {
            matrixRecursive(length);
        }

        private long matrixRecursive(int n) {
            if (n == 0) {
                return 0;
            }
            long a = 0, b = 1, temp;
            for (int i = 2; i <= n; i++) {
                temp = a + b;
                a = b;
                b = temp;
            }
            return a;
        }
    }

    @GetMapping("/speeds")
    public Map<String, Integer> getAlgorithmSpeeds(@RequestParam(required = false) Integer length) {
        int size = (length != null && length > 0) ? length : 40;

        Map<String, Integer> algorithmSpeeds = new HashMap<>();

        algorithmSpeeds.put("forLoopFibonacci", measureFibonacciTime(new ForLoopFibonacci(), size));
        algorithmSpeeds.put("whileLoopFibonacci", measureFibonacciTime(new WhileLoopFibonacci(), size));
        algorithmSpeeds.put("recursionFibonacci", measureFibonacciTime(new RecursionFibonacci(), size));
        algorithmSpeeds.put("matrixFibonacci", measureFibonacciTime(new MatrixFibonacci(), size));

        return algorithmSpeeds;
    }

    private void runFibonacciAlgorithm(FibonacciAlgorithm algorithm, int size) {
        algorithm.fibonacci(size);
    }

    private int measureFibonacciTime(FibonacciAlgorithm algorithm, int size) {
        long startTime = System.nanoTime();
        runFibonacciAlgorithm(algorithm, size);
        long endTime = System.nanoTime();
        return (int) ((endTime - startTime) / 1_000_000);  // Convert nanoseconds to milliseconds
}
}