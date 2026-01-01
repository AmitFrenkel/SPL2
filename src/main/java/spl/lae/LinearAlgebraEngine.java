package spl.lae;

import parser.*;
import memory.*;
import scheduling.*;

import java.util.List;

public class LinearAlgebraEngine {

    private SharedMatrix leftMatrix = new SharedMatrix();
    private SharedMatrix rightMatrix = new SharedMatrix();
    private TiredExecutor executor;

    public LinearAlgebraEngine(int numThreads) {
        executor = new TiredExecutor(numThreads);
    }

    public ComputationNode run(ComputationNode computationRoot) {
        // TODO: resolve computation tree step by step until final matrix is produced
        computationRoot.associativeNesting();
        ComputationNode res = computationRoot.findResolvable();
        try{
            while(res != null){
                loadAndCompute(res);
                res = computationRoot.findResolvable();
            }
        }finally{
            try{
                executor.shutdown();
            } catch (InterruptedException e) {
            }
        }
        return computationRoot;
    }

    public void loadAndCompute(ComputationNode node) {
        // TODO: load operand matrices
        // TODO: create compute tasks & submit tasks to executor
        List<Runnable> tasks= null;
        switch(node.getNodeType()){
            case ADD:
                if (node.getChildren().size() != 2) {
                    throw new IllegalArgumentException("Add operation requires two operands.");
                }
                leftMatrix = new SharedMatrix(node.getChildren().get(0).getMatrix());
                rightMatrix = new SharedMatrix(node.getChildren().get(1).getMatrix());
                if (leftMatrix.length() != rightMatrix.length() ||
                    leftMatrix.get(0).length() != rightMatrix.get(0).length()) {
                    throw new IllegalArgumentException("Matrices dimensions do not match for addition.");
                    
                }
                else
                    tasks = createAddTasks();
                break;
            case MULTIPLY:
                if (node.getChildren().size() != 2) {
                    throw new IllegalArgumentException("Multiply operation requires two operands.");
                }
                leftMatrix = new SharedMatrix(node.getChildren().get(0).getMatrix()); 
                rightMatrix = new SharedMatrix();
                rightMatrix.loadColumnMajor(node.getChildren().get(1).getMatrix());
                if (leftMatrix.get(0).length() != rightMatrix.get(0).length()) {
                    throw new IllegalArgumentException("Matrices dimensions do not match for multiplication.");
                }
                else
                    tasks = createMultiplyTasks();
                break;
            case NEGATE:
                if (node.getChildren().size() > 1) {
                    throw new IllegalArgumentException("Negate operation takes only one operand.");
                }
                leftMatrix = new SharedMatrix(node.getChildren().get(0).getMatrix());
                tasks = createNegateTasks();
                break;
            case TRANSPOSE:
                if (node.getChildren().size() > 1) {
                    throw new IllegalArgumentException("Transpose operation takes only one operand.");
                }
                leftMatrix = new SharedMatrix(node.getChildren().get(0).getMatrix());
                tasks = createTransposeTasks();
                break;
        }
        executor.submitAll(tasks);
        node.resolve(leftMatrix.readRowMajor());
    }

    public List<Runnable> createAddTasks() {
        List<Runnable> tasks = new java.util.ArrayList<>();
        for(int i=0; i < leftMatrix.length(); i++){
            final int index = i;
            tasks.add(() -> {leftMatrix.get(index).add(rightMatrix.get(index));});
        }
        return tasks;
    }

    public List<Runnable> createMultiplyTasks() {
        List<Runnable> tasks = new java.util.ArrayList<>();
        final SharedMatrix A = leftMatrix;
        final SharedMatrix B = rightMatrix;  
        final int m = A.length(); 
        final int n = B.length(); 
        final double[][] result = new double[m][n];
        for (int i = 0; i < m; i++) { 
        final int row = i;
        tasks.add(() -> {
            SharedVector vecA = A.get(row);
            for (int j = 0; j < n; j++) {
                result[row][j] = vecA.dot(B.get(j));
            }
        });
    }
    leftMatrix = new SharedMatrix(result);
    return tasks;
    }

    public List<Runnable> createNegateTasks() {
        List<Runnable> tasks = new java.util.ArrayList<>();
        for(int i=0; i < leftMatrix.length(); i++){
            final int index = i;
            tasks.add(() -> {leftMatrix.get(index).negate();});
        }
        return tasks;
    }

    public List<Runnable> createTransposeTasks() {
        List<Runnable> tasks = new java.util.ArrayList<>();
        for(int i=0; i < leftMatrix.length(); i++){
            final int index = i;
            tasks.add(() -> {leftMatrix.get(index).transpose();});
        }
        return tasks;
    }

    public String getWorkerReport() {
        return executor.getWorkerReport();
    }
}
