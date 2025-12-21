package memory;

import java.util.concurrent.locks.ReadWriteLock;

public class SharedVector {

    private double[] vector;
    private VectorOrientation orientation;
    private ReadWriteLock lock = new java.util.concurrent.locks.ReentrantReadWriteLock();

    public SharedVector(double[] vector, VectorOrientation orientation) {
       this.vector = vector;
       this.orientation = orientation;
    }

    public double get(int index) {
        readLock();
        double value = vector[index];
        readUnlock();
        return value;
    }

    public int length() {
        readLock();
        int len = vector.length;
        readUnlock();
        return len;
    }

    public VectorOrientation getOrientation() {
        readLock();
        VectorOrientation ori = orientation;
        readUnlock();
        return ori;
    }

    public void writeLock() {
        lock.writeLock().lock();
    }

    public void writeUnlock() {
        lock.writeLock().unlock();
    }

    public void readLock() {
        lock.readLock().lock();
    }

    public void readUnlock() {
        lock.readLock().unlock();
    }

    public void transpose() {
        readLock();
        if (orientation == VectorOrientation.ROW_MAJOR) {
            readUnlock();
            writeLock();
            orientation = VectorOrientation.COLUMN_MAJOR;
            writeUnlock();
        } else {
            readUnlock();
            writeLock();
            orientation = VectorOrientation.ROW_MAJOR;
            writeUnlock();
        }
    }

    public void add(SharedVector other) {
        readLock();
        if (other.length() == this.length()) {
            readUnlock();
            writeLock();
            for (int i = 0; i < vector.length; i++) {
                vector[i] += other.get(i);
            }
            writeUnlock();
        }
    }

    public void negate() {
        writeLock();
        for (int i = 0; i < vector.length; i++) {
            vector[i] = -vector[i];
        }
        writeUnlock();
    }

    public double dot(SharedVector other) {
        double result = 0.0;
        readLock();
        if(this.orientation != other.orientation && this.length() == other.length()) {
            readUnlock();
            writeLock();
            for (int i = 0; i < vector.length; i++) {
                result += this.vector[i] * other.get(i);
            }
            writeUnlock();
        }
        return result;   
    }

    public void vecMatMul(SharedMatrix matrix) {
        readLock();
        if (matrix.getOrientation() == VectorOrientation.ROW_MAJOR && this.length() == matrix.length()) {
            readUnlock();
            writeLock();
            double[] temp = this.vector.clone();
            for(int i=0; i < matrix.length(); i++) {
                
            }
            
        }
    }
}
