package hr.fer.zari.midom.utils.decode;

public class GradWestPredictor implements Predictor {

    @Override
    public int predict(int tr, int tc, PGMImage image) {
        int prediction;
        // border cases
        if (tc == 0 && tr == 0) {
            prediction = 0;
        } else if (tc == 0) {
            prediction = image.getPixel(tr-1, tc);
        } else if (tr < 2) {
            prediction = image.getPixel(tr, tc-1);
        } else {
            int nPixel = image.getPixel(tr-1, tc);
            int nnPixel = image.getPixel(tr-2, tc);
            int predTemp = 2 * nPixel - nnPixel;
            // resolve unwanted cases
            if(predTemp < 0) {
                predTemp = 0;
            } else if (predTemp > image.getMaxGray()) {
                predTemp = image.getMaxGray();
            }

            prediction = (int) predTemp;

        }

        return prediction;
    }
}
