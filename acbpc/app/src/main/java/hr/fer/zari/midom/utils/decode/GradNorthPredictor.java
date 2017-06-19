package hr.fer.zari.midom.utils.decode;

public class GradNorthPredictor implements Predictor {

    @Override
    public void predict_array(int[] buffer, String filepath){}
    @Override
    public void predict_arrayDCM(int[] buffer, String filepath, String path){}
    @Override
    public int predict(int tr, int tc, PGMImage image) {
        int prediction;
        int Columns = image.getColumns();
        // border cases
        if (tr == 0 && tc == 0) {
            prediction = 0;
        } else if (tr == 0) {
            //prediction = image.getPixel(tr * Columns + tc - 1);
            prediction = image.getPixel(tc - 1);
        } else if (tc < 2) {
            prediction = image.getPixel((tr - 1) * Columns + tc);
        } else {
            int wPixel = image.getPixel(tr * Columns + tc - 1);
            int wwPixel = image.getPixel(tr * Columns + tc - 2);
            int predTemp = 2 * wPixel - wwPixel;
            // resolve unwanted cases
            if (predTemp < 0) {
                predTemp = 0;
            } else if (predTemp > image.getMaxGray()) {
                predTemp = image.getMaxGray();
            }

            prediction = (int) (predTemp);
        }

        return prediction;
    }
}
