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
        ComputationNode res = computationRoot.findResolvable();
        while(res != null){
            loadAndCompute(res);
            res = computationRoot.findResolvable();
        }
        return computationRoot;
    }

    public void loadAndCompute(ComputationNode node) {
        // TODO: load operand matrices
        // TODO: create compute tasks & submit tasks to executor
        List<Runnable> tasks= null;
        switch(node.getNodeType()){
            case ADD:
                leftMatrix = new SharedMatrix(node.getChildren().get(0).getMatrix());
                rightMatrix = new SharedMatrix(node.getChildren().get(1).getMatrix());
                tasks = createAddTasks();
                break;
            case MULTIPLY:
                leftMatrix = new SharedMatrix(node.getChildren().get(0).getMatrix());
                rightMatrix = new SharedMatrix(node.getChildren().get(1).getMatrix());  
                tasks = createMultiplyTasks();
                break;
            case NEGATE:
                tasks = createNegateTasks();
                break;
            case TRANSPOSE:
                tasks = createTransposeTasks();
                break;
        }
        executor.submitAll(tasks);
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
        for(int i=0; i < leftMatrix.length(); i++){
            final int index = i;
            tasks.add(() -> {leftMatrix.get(index).dot(rightMatrix.get(index));});
        }
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
