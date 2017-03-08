package hr.fer.zari.midom.utils.decode;

import java.util.Arrays;

public class CBPredictor implements Predictor {

	private final int PRED_NUM = 7;
	public static final boolean CB_SPC_CORRECTION = false;
	private long[] penaltiesM = new long[PRED_NUM];
	private CellPixelData[] cellM;
	public VectorDistMeasure vectorDistM;
	public BlendPenaltyType penTypeM;
	private boolean cumPenaltiesM;
	private boolean penComputedM = false;
	private int radiusM;
	private int cellSizeM;
	private int vectorSizeM;
	private int xBorderM;
	private int yBorderM;
	private int spcCorrectionM = 0;
	private int thresholdM;
	//private TestPredictorsOld predictors;
	private Predictor[] predictorSetM = new Predictor[PRED_NUM];

	private static final int[][] offsetsSM = {
            { -1, 0 }, // W
			{ -1, -1 }, // NW
			{ 0, -1 }, // N
			{ 1, -1 }, // NE
			{ -2, 0 }, // WW
			{ -2, -1 }, // WWN
			{ -2, -2 }, // NNWW
			{ -1, -2 }, // NNW
			{ 0, -2 }, // NN
			{ 1, -2 } // NNE
	};

	public CBPredictor(VectorDistMeasure vectorDist, BlendPenaltyType penType,
					   int radius, int cellSize, int vSize, int threshold,
					   boolean cumPenalties) {

        this.vectorDistM = vectorDist;
        this.penTypeM = penType;
        this.radiusM = radius;
        this.cellSizeM = cellSize;
        this.vectorSizeM = vSize;
        this.cumPenaltiesM = cumPenalties;
        this.thresholdM = threshold;
        this.cellM = new CellPixelData[cellSizeM];


		if (vectorSizeM < 1 && vectorSizeM > 10) {
			throw new IllegalArgumentException(
					"Vector size must be in [1,10] interval");
		} else if (vectorSizeM <= 4) {
			xBorderM = yBorderM = radius + 1;
		} else {
			xBorderM = yBorderM = radius + 2;
		}

		this.predictorSetM[0] = new WDPCMPredictor();
		this.predictorSetM[1] = new JPG2Predictor();
		this.predictorSetM[2] = new NWDPCMPredictor();
		this.predictorSetM[3] = new NEDPCMPredictor();
		this.predictorSetM[4] = new PlanePredictor();
		this.predictorSetM[5] = new GradWestPredictor();
		this.predictorSetM[6] = new GradNorthPredictor();
	}

	public int predict(int tr, int tc, PGMImage pgmP) {
		int prediction = 0;

		if (tc < xBorderM || (tc > pgmP.getColumns() - xBorderM) || tr < yBorderM) {
			prediction = new MEDPredictor().predict(tr, tc, pgmP);
		} else {
			resetCell();
			searchTheWindow(tr, tc, pgmP);
			computePenalties(tr, tc, pgmP);


			if (CB_SPC_CORRECTION) {
				spcCorrectionM = 0;
				for (CellPixelData cpd : cellM) {
					int xOff = cpd.getxOff();
					int yOff = cpd.getyOff();
					int cellPred = blendPredictors(tr + yOff, tc + xOff, pgmP);
					int cellPixel = pgmP.getPixel(tr + yOff, tc + xOff);
					spcCorrectionM += cellPixel - cellPred;
				}
				spcCorrectionM /= cellSizeM;
			}

			prediction = blendPredictors(tr, tc, pgmP) + spcCorrectionM;
		}

		return prediction > pgmP.getMaxGray() ? pgmP.getMaxGray() : prediction;
	}
/*
	public int predict(int tr, int tc, int prevError, PGMImage pgmP) {
		int prediction = 0;
		if (tc < xBorderM || (tc > pgmP.getColumns() - xBorderM)
				|| tr < yBorderM) {
			prediction = new MEDPredictor().predict(tr, tc, pgmP);
		} else {
			if (prevError > thresholdM || (!penComputedM)) {
				resetCell();
				searchTheWindow(tr, tc, pgmP);
				computePenalties(tr, tc, pgmP);
				penComputedM = true;
			}

			if (CB_SPC_CORRECTION) {
				spcCorrectionM = 0;

				for (CellPixelData cpd : cellM) {
					int xOff = cpd.getxOff();
					int yOff = cpd.getyOff();
					int cellPred = blendPredictors(tr + yOff, tc + xOff, pgmP);
					int cellPixel = pgmP.getPixel(tr + yOff, tc + xOff);
					spcCorrectionM += cellPixel - cellPred;
				}
				spcCorrectionM /= cellSizeM;
			}

			prediction = blendPredictors(tr, tc, pgmP) + spcCorrectionM;
		}

		return prediction > pgmP.getMaxGray() ? pgmP.getMaxGray() : prediction;
	}*/

	private void resetCell() {
		for (int i=0; i<cellSizeM; i++) {
			cellM[i] = new CellPixelData(Integer.MAX_VALUE, Integer.MAX_VALUE, Long.MAX_VALUE);
			cellM[i].setxOff(Integer.MAX_VALUE);
			cellM[i].setyOff(Integer.MAX_VALUE);
			cellM[i].setDist(Long.MAX_VALUE);
		}
	}

	private void searchTheWindow(int tr, int tc, PGMImage pgmP) {
		int[] originVector = new int[vectorSizeM];
		int[] currVector = new int[vectorSizeM];

		for (int i = 0; i < vectorSizeM; i++) {
			int x = tc + offsetsSM[i][0];
			int y = tr + offsetsSM[i][1];
			originVector[i] = pgmP.getPixel(y, x);
		}

		for(int y = -radiusM; y <= 0; y++){
			for(int x = -radiusM; x < radiusM; x++){
				if (x >= 0 && y == 0) {
					break;
				}

				int pX = tc + x;
				int pY = tr + y;

				for (int i = 0; i < vectorSizeM; i++) {
					currVector[i] = pgmP.getPixel(pY + offsetsSM[i][1], pX
							+ offsetsSM[i][0]);
				}

				long dist = calcDistance(originVector, currVector);

				CellPixelData pixel = new CellPixelData(x, y, dist);

				updateCell(pixel);
			}
		}
	}

	private long calcDistance(int[] originVector, int[] currVector) {
		long distance = 0;

		for (int i = 0; i < vectorSizeM; i++) {
			if (vectorDistM == VectorDistMeasure.L1) {
				distance += Math.abs(originVector[i] - currVector[i]);
			} else if (vectorDistM == VectorDistMeasure.L2) {
				distance += (originVector[i] - currVector[i])
						* (originVector[i] - currVector[i]);
			} else if (vectorDistM == VectorDistMeasure.WL2) {
				if (i == 0 || i == 2) {
					distance += 2 * ((originVector[i] - currVector[i]) * (originVector[i] - currVector[i]));
				} else {
					distance += (originVector[i] - currVector[i])
							* (originVector[i] - currVector[i]);
				}
			} else if (vectorDistM == VectorDistMeasure.LINF) {
				int distTmp = Math.abs(originVector[i] - currVector[i]);
				if (distTmp > distance)
					distance = distTmp;
			}
		}

		return distance;
	}

	private void updateCell(CellPixelData pixel) {
		int indexMax = 0;
		long maxDistance = 0;

		for (int i = 0; i < cellM.length; i++) {
			if (cellM[i].getDist() > maxDistance) {
				indexMax = i;
				maxDistance = cellM[i].getDist();
			}
		}

		if (pixel.getDist() < maxDistance)
			cellM[indexMax] = pixel;
	}


	private void computePenalties(int tr, int tc, PGMImage pgmP) {
		if (!cumPenaltiesM) {
			resetPenalties();
		}

		for (CellPixelData cpd : cellM) {
			int xOff = cpd.getxOff();
			int yOff = cpd.getyOff();

			int pixel = pgmP.getPixel(tr + yOff, tc + xOff);


			for (int predictor = 0; predictor < PRED_NUM; predictor++)
			{
				// Prediction of the predictor
				int prediction = predictorSetM[predictor].predict(tr + yOff, tc + xOff, pgmP);
				// Prediction error of the predictor
				int error = pixel - prediction;

				if((penTypeM == BlendPenaltyType.SSQR) || (penTypeM == BlendPenaltyType.MSQR)) {
					penaltiesM[predictor] += (error * error);
				} else if ((penTypeM == BlendPenaltyType.SABS) || (penTypeM == BlendPenaltyType.MABS)) {
					penaltiesM[predictor] += Math.abs(error);
				}

			}

		}
	}


	public int getCorrection() {
		return spcCorrectionM;
	}

	private void resetPenalties() {
		Arrays.fill(penaltiesM, 0);
	}

	// private void resetHistory() {
	// penComputedM = false;
	// resetPenalties();
	// nerrM = 0;
	// werrM = 0;
	// spcCorrectionM = 0;
	// nerrM = werrM = 0;
	// }

	private int blendPredictors(int tr, int tc, PGMImage image) {
		int[] predictions = new int[PRED_NUM];

		for(int predictor = 0; predictor < PRED_NUM; predictor++)
		{
			predictions[predictor] = predictorSetM[predictor].predict(tr, tc, image);

			// Shortcut - if ideal predictor exists in terms of the penalty
			// calculation (if predictor's penalty is zero - idela predictor)
			if(penaltiesM[predictor] == 0)
			{
				return predictions[predictor];
			}
		}

		float sum = 0;
		float predTemp = 0;

		for (int i = 0; i < PRED_NUM; i++) {
			sum += 1.0 / penaltiesM[i];
			predTemp += (1.0 / penaltiesM[i]) * predictions[i];
		}

		return (int) (predTemp / sum );
	}

	@Override
	public String toString() {
		return "CBPredictor " + vectorDistM.toString() + " "
				+ penTypeM.toString() + (cumPenaltiesM ? " *" : "");
	}

	public enum VectorDistMeasure {
		L1, L2, WL2, LINF
	}

	public enum BlendPenaltyType {
		SSQR, MSQR, SABS, MABS
	}

	class CellPixelData {

		private int xOff;
		private int yOff;
		private long dist;

		public CellPixelData(int xOff, int yOff, long dist) {
			//xOff = Integer.MAX_VALUE;
			//yOff = Integer.MAX_VALUE;
			//dist = Long.MAX_VALUE;
            setxOff(xOff);
            setyOff(yOff);
            setDist(dist);
		}



		public void setxOff(int xOff1) {
			xOff = xOff1;
		}

        public void setyOff(int yOff1) {
            yOff = yOff1;
        }

        public void setDist(long dist1) {
            dist = dist1;
        }

        public int getxOff() {
            return xOff;
        }
		public int getyOff() {
			return yOff;
		}

		public long getDist() {
			return dist;
		}



	}

}
