package hr.fer.zari.midom.utils.decode;

public class MEDPredictor implements Predictor{

    int north;
    int west;
    int northWest;

    // predicted pixel value
    int prediction;

    @Override
    public void predict_array(int[] buffer, String filepath){}
    @Override
    public void predict_arrayDCM(int[] buffer, String filepath, String path){}
    @Override
    public int predict(int tr, int tc, PGMImage image) {
        int Columns = image.getColumns();
        if(tc==0&&tr==0) {
            prediction = 0;
        }

        else if(tr==0) {
            //prediction = image.getPixel(tr * Columns + tc - 1);
            prediction = image.getPixel(tc - 1);
        }

        else if(tc==0) {
            //prediction = image.getPixel((tr - 1) * Columns + tc);
            prediction = image.getPixel((tr - 1) * Columns);
        }

        else {
            north = image.getPixel((tr - 1) * Columns + tc);
            northWest = image.getPixel((tr - 1) * Columns + tc - 1);
            west = image.getPixel(tr * Columns + tc - 1);
            prediction = med(north, west, northWest);
        }

        if(prediction > image.getMaxGray()){
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
