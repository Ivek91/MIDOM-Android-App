package hr.fer.zari.midom.utils.decode;

public interface Predictor {
    public int predict(int tr, int tc, PGMImage image);
    public void predict_array(int[] buffer, String filepath);
    public void predict_arrayDCM(int[] buffer, String filepath, String path);
}
