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
        if (other != null &&other.length() == this.length()) {
            writeLock();
            other.readLock();
            for (int i = 0; i < vector.length; i++) {
                vector[i] += other.get(i);
            }
            other.readUnlock();
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
        
        if(other != null && this.length() == other.length()) {
            writeLock();
            other.readLock();
            for (int i = 0; i < vector.length; i++) {
                result += this.vector[i] * other.get(i);
            }
            other.readUnlock();
            writeUnlock();
        }
        
        return result;   
    }

    public void vecMatMul(SharedMatrix matrix) {
        writeLock();
        if (matrix != null && matrix.getOrientation() == VectorOrientation.ROW_MAJOR && this.length() == matrix.length()) {
            double[] temp = new double[vector.length];
            for(int i=0; i < vector.length; i++) {
                temp[i] = 0;
            }
            for(int i=0; i < matrix.length(); i++) {
                matrix.get(i).readLock();
                for(int j=0; j < matrix.get(i).length(); j++) {
                    temp[i] += vector[j] * matrix.get(j).get(i);
                }
                matrix.get(i).readUnlock();
            }
            for(int i=0; i < vector.length; i++) {
                this.vector[i] = temp[i];
            }
            writeUnlock();
            return;
        }
        else if(matrix != null && matrix.getOrientation() == VectorOrientation.COLUMN_MAJOR && 
        this.length() == matrix.get(0).length()) {
            double[] temp = new double[vector.length];
            for(int i=0; i < vector.length; i++) {
                temp[i] = 0;
            }
            for(int i=0; i < matrix.length(); i++) {
                matrix.get(i).readLock();
                for(int j=0; j < matrix.get(i).length(); j++) {
                    temp[i] += vector[j] * matrix.get(i).get(j);
                }
                matrix.get(i).readUnlock();
            }
            for(int i=0; i < vector.length; i++) {
                this.vector[i] = temp[i];
            }
            writeUnlock();
            return;
        }
    }
}
