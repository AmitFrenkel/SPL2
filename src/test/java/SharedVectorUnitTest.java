import memory.SharedVector;
import memory.VectorOrientation;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SharedVectorUnitTest {

    @Test
    void testStandardOperations() {
        double[] data = {1.0, 2.0, 3.0};
        SharedVector v = new SharedVector(data, VectorOrientation.ROW_MAJOR);
        assertEquals(3, v.length());
        assertEquals(2.0, v.get(1));
        assertEquals(VectorOrientation.ROW_MAJOR, v.getOrientation());
    }

    @Test
    void testTransposeFlip() {
        SharedVector v = new SharedVector(new double[]{1, 2}, VectorOrientation.ROW_MAJOR);
        v.transpose();
        assertEquals(VectorOrientation.COLUMN_MAJOR, v.getOrientation());
        v.transpose();
        assertEquals(VectorOrientation.ROW_MAJOR, v.getOrientation());
    }

    @Test
    void testEmptyVector() {
        double[] data = {};
        SharedVector v = new SharedVector(data, VectorOrientation.ROW_MAJOR);
        assertEquals(0, v.length(), "Empty vector should have length 0");
        v.negate();
        v.transpose();
        assertEquals(VectorOrientation.COLUMN_MAJOR, v.getOrientation());
    }

    @Test
    void testSingleElementVector() {
        double[] data = {42.0};
        SharedVector v = new SharedVector(data, VectorOrientation.ROW_MAJOR);
        assertEquals(1, v.length());
        assertEquals(42.0, v.get(0));
        v.negate();
        assertEquals(-42.0, v.get(0));
    }

    @Test
    void testAddDimensionMismatch() {
        SharedVector v1 = new SharedVector(new double[]{1.0, 2.0}, VectorOrientation.ROW_MAJOR);
        SharedVector v2 = new SharedVector(new double[]{1.0, 2.0, 3.0}, VectorOrientation.ROW_MAJOR);
        v1.add(v2);
        assertEquals(1.0, v1.get(0), "Vector should not change on dimension mismatch");
        assertEquals(2.0, v1.get(1));
    }

    @Test
    void testDotDimensionMismatch() {
        SharedVector v1 = new SharedVector(new double[]{1.0}, VectorOrientation.ROW_MAJOR);
        SharedVector v2 = new SharedVector(new double[]{1.0, 2.0}, VectorOrientation.ROW_MAJOR);
        double result = v1.dot(v2);
        assertEquals(0.0, result, "Dot product should be 0.0 on dimension mismatch");
    }

    @Test
    void testDotProductWithEmptyVector() {
        SharedVector v1 = new SharedVector(new double[]{}, VectorOrientation.ROW_MAJOR);
        SharedVector v2 = new SharedVector(new double[]{}, VectorOrientation.ROW_MAJOR);
        double result = v1.dot(v2);
        assertEquals(0.0, result, "Dot product of empty vectors should be 0.0");
    }

    @Test
    void testAddEmptyVectors() {
        SharedVector v1 = new SharedVector(new double[]{}, VectorOrientation.ROW_MAJOR);
        SharedVector v2 = new SharedVector(new double[]{}, VectorOrientation.ROW_MAJOR);
        assertDoesNotThrow(() -> v1.add(v2));
        assertEquals(0, v1.length());
    }

    @Test
    void testNegateEmptyVector() {
        SharedVector v = new SharedVector(new double[]{}, VectorOrientation.ROW_MAJOR);
        assertDoesNotThrow(() -> v.negate());
        assertEquals(0, v.length());
    }

    @Test
    void testTransposeEmptyVector() {
        SharedVector v = new SharedVector(new double[]{}, VectorOrientation.ROW_MAJOR);
        assertDoesNotThrow(() -> v.transpose());
        assertEquals(VectorOrientation.COLUMN_MAJOR, v.getOrientation());
    }
}