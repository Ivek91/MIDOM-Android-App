package hr.fer.zari.midom.utils.decode;

public class PlanePredictor implements Predictor{

    @Override
    public void predict_array(int[] buffer, String filepath){}
    @Override
    public void predict_arrayDCM(int[] buffer, String filepath, String path){}
    @Override
    public int predict(int tr, int tc, PGMImage image) {
        int Columns = image.getColumns();
        int north = image.getPixel((tr - 1) * Columns + tc);
        int west = image.getPixel(tr * Columns + tc - 1);
        int northWest = image.getPixel((tr - 1) * Columns + tc - 1);

        if ((north + west - northWest) > image.getMaxGray()) {
            return image.getMaxGray();
        } else if ((north + west - northWest) < 0) {
            return 0;
        }
        else{
            return (north + west - northWest);
        }
    }
}
