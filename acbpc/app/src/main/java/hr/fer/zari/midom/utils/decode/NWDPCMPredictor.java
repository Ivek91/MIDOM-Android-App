package hr.fer.zari.midom.utils.decode;

public class NWDPCMPredictor implements Predictor {

    @Override
    public int predict(int tr, int tc, PGMImage image) {
        if (tc == 0 && tr == 0) {
            return 0;
        } else if (tc == 0) {
            return image.getPixel(tr - 1, tc);
        } else if (tr == 0) {
            return image.getPixel(tr, tc - 1);
        } else {
            return image.getPixel(tr - 1, tc - 1);
        }
    }
}
