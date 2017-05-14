package hr.fer.zari.midom.utils.decode;

public class GradWestPredictor implements Predictor {
    @Override
    public void predict_array(int[] buffer, String filepath){}
    @Override
    public int predict(int tr, int tc, PGMImage image) {
        int prediction;
        int Columns = image.getColumns();
        // border cases
        if (tc == 0 && tr == 0) {
            prediction = 0;
        } else if (tc == 0) {
            //prediction = image.getPixel((tr-1) * Columns + tc);
            prediction = image.getPixel((tr-1) * Columns);
        } else if (tr < 2) {
            prediction = image.getPixel(tr * Columns + tc-1);
        } else {
            int nPixel = image.getPixel((tr-1) * Columns + tc);
            int nnPixel = image.getPixel((tr-2) * Columns + tc);
            int predTemp = 2 * nPixel - nnPixel;
            // resolve unwanted cases
            if(predTemp < 0) {
                predTemp = 0;
            } else if (predTemp > 255) {
                predTemp = 255;
            }

            prediction = (int) predTemp;

        }

        return prediction;
    }
}
