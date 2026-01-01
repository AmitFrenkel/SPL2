package memory;

public class SharedMatrix {

    private volatile SharedVector[] vectors = {}; // underlying vectors

    public SharedMatrix() {
        // TODO: initialize empty matrix
    }

    public SharedMatrix(double[][] matrix) {
        loadRowMajor(matrix);
    }

    public void loadRowMajor(double[][] matrix) {
        if (matrix != null) {
            vectors = new SharedVector[matrix.length];
            for (int i = 0; i < matrix.length; i++) {
                vectors[i] = new SharedVector(matrix[i], VectorOrientation.ROW_MAJOR);
            }

        }
    }

    public void loadColumnMajor(double[][] matrix) {
        double[][] result = new double[matrix[0].length][matrix.length];
        for(int i=0; i < matrix[0].length; i++) {
            for(int j=0; j < matrix.length; j++) {
                result[i][j] = matrix[j][i];
            }
        }
        this.vectors = new SharedVector[result.length];
        for (int i = 0; i < result.length; i++) {
            vectors[i] = new SharedVector(result[i], VectorOrientation.COLUMN_MAJOR);
        }
    }

    public double[][] readRowMajor() {
        if (length() > 0) {
            acquireAllVectorReadLocks(vectors);
            if(getOrientation() == VectorOrientation.ROW_MAJOR){
                double[][] result = new double[length()][vectors[0].length()];
                for (int i = 0; i < vectors.length; i++) {
                    for (int j = 0; j < vectors[i].length(); j++) {
                        result[i][j] = vectors[i].get(j);
                    }
                }
                releaseAllVectorReadLocks(vectors);
                return result;
            }
            else{
                double[][] result = new double[vectors[0].length()][length()];
                for(int i=0; i < vectors[0].length(); i++) {
                    for(int j=0; j < vectors.length; j++) {
                        result[i][j] = vectors[j].get(i);
                    }
                }
                releaseAllVectorReadLocks(vectors);
                return result;
            }
        }
        return new double[0][0];
    }

    public SharedVector get(int index) {
        vectors[index].readLock();
        SharedVector vec = vectors[index];
        vectors[index].readUnlock();
        return vec;
    }

    public int length() {
        return vectors.length;
    }

    public VectorOrientation getOrientation() {
        vectors[0].readLock();
        VectorOrientation ori = vectors[0].getOrientation();
        vectors[0].readUnlock();
        return ori;
    }

    private void acquireAllVectorReadLocks(SharedVector[] vecs) {
        for (SharedVector vec : vecs) {
            vec.readLock();
        }
    }

    private void releaseAllVectorReadLocks(SharedVector[] vecs) {
        for (SharedVector vec : vecs) {
            vec.readUnlock();
        }
    }

    private void acquireAllVectorWriteLocks(SharedVector[] vecs) {
        for (SharedVector vec : vecs) {
            vec.writeLock();
        }
    }

    private void releaseAllVectorWriteLocks(SharedVector[] vecs) {
        for (SharedVector vec : vecs) {
            vec.writeUnlock();
        }
    }
}
