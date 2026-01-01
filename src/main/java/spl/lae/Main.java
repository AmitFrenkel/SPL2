package spl.lae;
import java.io.IOException;
import java.text.ParseException;

import parser.*;
import scheduling.TiredExecutor;

public class Main {
    public static void main(String[] args) throws IOException {
      // TODO: main
      if (args.length <3) {
          System.out.println("missing args");
          return;
      }
      int numberOfThreads = Integer.parseInt(args[0]);
      String filePath = args[1];
      String outputPath = args[2];
      InputParser parser = new InputParser();
      try{
          ComputationNode rootNode = parser.parse(filePath);
          LinearAlgebraEngine lae = new LinearAlgebraEngine(numberOfThreads);
          ComputationNode resultNode = lae.run(rootNode);
          OutputWriter.write(resultNode.getMatrix(), outputPath);
          System.out.println(lae.getWorkerReport());
      } catch (ParseException e) {
          System.out.println("An error occurred: " + e.getMessage());
      }catch(Exception e){
          OutputWriter.write(e.getMessage(), outputPath);
      }
    }
}