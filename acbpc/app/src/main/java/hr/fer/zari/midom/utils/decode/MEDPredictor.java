package hr.fer.zari.midom.utils.decode;

public class MEDPredictor implements Predictor{

    int north;
    int west;
    int northWest;

    // predicted pixel value
    int prediction;

    @Override
    public int predict(int tr, int tc, PGMImage image) {

        if(tc==0&&tr==0) {
            prediction = 0;
        }

        else if(tr==0) {
            prediction = image.getPixel(tr, tc - 1);
        }

        else if(tc==0) {
            prediction = image.getPixel(tr - 1, tc);
        }

        else {
            north = image.getPixel(tr - 1, tc);
            northWest = image.getPixel(tr - 1, tc - 1);
            west = image.getPixel(tr, tc - 1);
            prediction = med(north, west, northWest);
        }

        if(prediction>image.getMaxGray()){
            prediction = image.getMaxGray();
        }

        return prediction;

    }

    private int med(int a, int b, int c) {
        int median;
        int max_ab = (a > b) ? a:b;
        int min_ab = (a > b) ? b:a;
        if (c >= max_ab)
            median = min_ab;
        else if (c <= min_ab)
            median = max_ab;
        else
            median = a + b - c;

        return median;
    }
}
