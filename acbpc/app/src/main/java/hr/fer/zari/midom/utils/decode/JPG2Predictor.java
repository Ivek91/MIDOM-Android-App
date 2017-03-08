package hr.fer.zari.midom.utils.decode;

public class JPG2Predictor implements Predictor{

    @Override
    public int predict(int tr, int tc, PGMImage image) {
        if (tr == 0) {
            return 0;
        }
        // return north
        return image.getPixel(tr-1, tc);
    }
}
