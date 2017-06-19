package hr.fer.zari.midom.utils.decode;

public class JPG2Predictor implements Predictor{

    @Override
    public void predict_array(int[] buffer, String filepath){}
    @Override
    public void predict_arrayDCM(int[] buffer, String filepath, String path){}
    @Override
    public int predict(int tr, int tc, PGMImage image) {
        if (tr == 0) {
            return 0;
        }
        // return north
        int Columns = image.getColumns();
        return image.getPixel((tr-1) * Columns + tc);
    }
}
