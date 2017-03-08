package hr.fer.zari.midom.utils.decode;

public class NEDPCMPredictor implements Predictor {

    @Override
    public int predict(int tr, int tc, PGMImage image) {
        if (tr == 0 && tc == 0) {
            return 0;
        } else if (tr == 0) {
            return image.getPixel(tr, tc - 1);
        } else if (tc == (image.getColumns() - 1)) {
            return image.getPixel(tr - 1, tc);
        } else {
            return image.getPixel(tr - 1, tc + 1);
        }
    }
}
