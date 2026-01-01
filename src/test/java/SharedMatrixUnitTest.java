import memory.SharedMatrix;
import memory.VectorOrientation;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SharedMatrixUnitTest {
    @Test
    void testLoadAndReadRowMajor() {
        double[][] data = {{1.0, 2.0}, {3.0, 4.0}};
        SharedMatrix m = new SharedMatrix(data);

        assertEquals(2, m.length());
        double[][] result = m.readRowMajor();
        
        assertArrayEquals(data[0], result[0]);
        assertArrayEquals(data[1], result[1]);
    }

    @Test
    void testLoadColumnMajor() {
        double[][] data = {{1.0, 2.0, 3.0}, {4.0, 5.0, 6.0}};
        SharedMatrix m = new SharedMatrix();
        m.loadColumnMajor(data);
        assertEquals(3, m.length()); 
        assertEquals(VectorOrientation.COLUMN_MAJOR, m.getOrientation());
        double[][] result = m.readRowMajor();
        assertEquals(1.0, result[0][0]);
        assertEquals(3.0, result[0][2]);
        assertEquals(6.0, result[1][2]);
    }

    @Test
    void testEmptyMatrixCreation() {
        double[][] emptyData = {}; 
        SharedMatrix m = new SharedMatrix(emptyData);
        assertEquals(0, m.length());        
    }

    @Test
    void testMatrixWithNoRows() {
        double[][] data = {}; 
        SharedMatrix m = new SharedMatrix(data);
        assertEquals(0, m.length());
        double[][] result = m.readRowMajor();
        assertEquals(0, result.length);
    }

    @Test
    void testMatrixWithRowsButNoColumns() {
        double[][] data = {{}, {}, {}}; 
        SharedMatrix m = new SharedMatrix(data);
        assertEquals(3, m.length());
        assertEquals(0, m.get(0).length());
        double[][] result = m.readRowMajor();
        assertEquals(3, result.length);
        assertEquals(0, result[0].length);
    }
}