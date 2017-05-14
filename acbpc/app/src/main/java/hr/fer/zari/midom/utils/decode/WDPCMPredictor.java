package hr.fer.zari.midom.utils.decode;

public class WDPCMPredictor implements Predictor{

    @Override
    public void predict_array(int[] buffer, String filepath){}
    @Override
    public int predict(int tr, int tc, PGMImage image) {
        int Columns = image.getColumns();
        return tc == 0 ? 0 : image.getPixel(tr * Columns + tc - 1);
    }
}
