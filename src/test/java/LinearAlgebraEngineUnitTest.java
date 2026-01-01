import spl.lae.LinearAlgebraEngine;
import parser.ComputationNode;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class LinearAlgebraEngineUnitTest {
    @Test
    void testSuccessAdd() {
        double[][] a = {{1, 2}};
        double[][] b = {{3, 4}};
        ComputationNode nodeA = new ComputationNode(a);
        ComputationNode nodeB = new ComputationNode(b);
        List<ComputationNode> children = new ArrayList<>();
        children.add(nodeA);
        children.add(nodeB);
        ComputationNode root = new ComputationNode("+", children);
        LinearAlgebraEngine lae = new LinearAlgebraEngine(2);
        ComputationNode result = lae.run(root);
        assertArrayEquals(new double[]{4.0, 6.0}, result.getMatrix()[0]);
    }

    @Test
    void testAdditionMismatchThrows() {
        double[][] a = {{1, 2}};
        double[][] b = {{1, 2, 3}};
        ComputationNode nodeA = new ComputationNode(a);
        ComputationNode nodeB = new ComputationNode(b);
        List<ComputationNode> children = new ArrayList<>();
        children.add(nodeA);
        children.add(nodeB);
        ComputationNode root = new ComputationNode("+", children);
        LinearAlgebraEngine lae = new LinearAlgebraEngine(2);
        try{
            lae.run(root);
            fail("Expected IllegalArgumentException not thrown");}
        catch(IllegalArgumentException e){
        }
    }

    @Test
    void testMultiplicationMismatchThrows() {
        double[][] a = {{1, 2}};
        double[][] b = {{1}, {2}, {3}};
        ComputationNode nodeA = new ComputationNode(a);
        ComputationNode nodeB = new ComputationNode(b);
        List<ComputationNode> children = new ArrayList<>();
        children.add(nodeA);
        children.add(nodeB);
        ComputationNode root = new ComputationNode("*", children);
        LinearAlgebraEngine lae = new LinearAlgebraEngine(2);
        try{
            lae.run(root);
            fail("Expected IllegalArgumentException not thrown");}
        catch(IllegalArgumentException e){
        }
    }

    @Test
    void testScalarMatrixOperations() {
        double[][] a = {{2.0}};
        double[][] b = {{3.0}};
        ComputationNode nodeA = new ComputationNode(a);
        ComputationNode nodeB = new ComputationNode(b);
        List<ComputationNode> children = new ArrayList<>();
        children.add(nodeA);
        children.add(nodeB);
        ComputationNode root = new ComputationNode("*", children);
        LinearAlgebraEngine lae = new LinearAlgebraEngine(1);
        ComputationNode result = lae.run(root);
        assertEquals(6.0, result.getMatrix()[0][0]);
    }

    @Test
    void testTranspose1x1() {
        double[][] a = {{5.0}};
        ComputationNode nodeA = new ComputationNode(a);
        List<ComputationNode> children = new ArrayList<>();
        children.add(nodeA);
        ComputationNode root = new ComputationNode("T", children);
        LinearAlgebraEngine lae = new LinearAlgebraEngine(2);
        ComputationNode result = lae.run(root);
        assertEquals(5.0, result.getMatrix()[0][0]);
    }

    
}