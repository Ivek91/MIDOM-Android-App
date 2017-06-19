package hr.fer.zari.midom.utils.decode;

public class NEDPCMPredictor implements Predictor {

    @Override
    public void predict_array(int[] buffer, String filepath){}
    @Override
    public void predict_arrayDCM(int[] buffer, String filepath, String path){}
    @Override
    public int predict(int tr, int tc, PGMImage image) {
        int Columns = image.getColumns();
        if (tr == 0 && tc == 0) {
            return 0;
        } else if (tr == 0) {
            //return image.getPixel(tr * Columns + tc - 1);
            return image.getPixel(tc - 1);
        } else if (tc == (image.getColumns() - 1)) {
            return image.getPixel((tr - 1) * Columns + tc);
        } else {
            return image.getPixel((tr - 1) * Columns + tc + 1);
        }
    }
}
