package hr.fer.zari.midom.utils.decode;

public class PlanePredictor implements Predictor{

    @Override
    public int predict(int tr, int tc, PGMImage image) {
        int north = image.getPixel(tr - 1, tc);
        int west = image.getPixel(tr, tc - 1);
        int northWest = image.getPixel(tr - 1, tc - 1);

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
