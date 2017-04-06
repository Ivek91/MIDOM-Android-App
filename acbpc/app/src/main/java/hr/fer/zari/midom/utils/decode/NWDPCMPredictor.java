package hr.fer.zari.midom.utils.decode;

public class NWDPCMPredictor implements Predictor {

    @Override
    public int predict(int tr, int tc, PGMImage image) {
        int Columns = image.getColumns();
        if (tc == 0 && tr == 0) {
            return 0;
        } else if (tc == 0) {
            //return image.getPixel((tr - 1) * Columns + tc);
            return image.getPixel((tr - 1) * Columns);
        } else if (tr == 0) {
            //return image.getPixel(tr * Columns + tc - 1);
            return image.getPixel(tc - 1);
        } else {
            return image.getPixel((tr - 1) * Columns + tc - 1);
        }
    }
}
