package hr.fer.zari.midom.utils.decode;

public class WDPCMPredictor implements Predictor{

    @Override
    public int predict(int tr, int tc, PGMImage image) {
        return tc == 0 ? 0 : image.getPixel(tr, tc - 1);
    }
}
