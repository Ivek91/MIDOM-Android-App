package hr.fer.zari.midom.utils.decode;

import android.util.Log;

import java.io.File;
import java.util.Arrays;
import com.imebra.*;

import static com.imebra.bitDepth_t.depthU16;
import static com.imebra.bitDepth_t.depthU8;
import static com.imebra.imageQuality_t.veryHigh;
import static hr.fer.zari.midom.utils.Constants.ZIP_EXTRACT;

public class CBPredictor implements Predictor {

	/* Used to load the 'native-lib' library on application startup. */
	static {
		System.loadLibrary("test-lib");
	}

	private final int PRED_NUM = 7;
	public static final boolean CB_SPC_CORRECTION = false;
	private long[] penaltiesM = new long[PRED_NUM];
	private CellPixelData[] cellM;
	public VectorDistMeasure vectorDistM;
	public BlendPenaltyType penTypeM;
	private boolean cumPenaltiesM;
	//private boolean penComputedM = false;
	private int radiusM;
	private int cellSizeM;
	private int vectorSizeM;
	private int xBorderM;
	private int yBorderM;
	private int spcCorrectionM = 0;
//	private int thresholdM;

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
       // this.thresholdM = threshold;
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

	public void predict_array(int[] buffer, String filepath){
		PGMImage decodedImage = new PGMImage();
		decodedImage.setDimension(buffer[0], buffer[1]);
		decodedImage.setMaxGray(buffer[2]);
		int prediction;
		int[] originVector = new int[vectorSizeM];
		int[] currVector = new int[vectorSizeM];
		for (int k = 0; k < buffer[1]; k++) {
			for (int j = 0; j < buffer[0]; j++) {
				if(j==0&&k==0) {
					prediction = 0;
				}
                else if(j==0) {
                    //prediction = image.getPixel((tr - 1) * Columns + tc);
                    prediction = buffer[(k - 1) * buffer[0] + 3];
                }
				else if(k==0) {
					//prediction = image.getPixel(tr * Columns + tc - 1);
					prediction = buffer[j - 1 + 3];
				}
				else if (j < xBorderM || (j > buffer[0] - xBorderM) || k < yBorderM) {
					//prediction = new MEDPredictor().predict(k, j, decodedImage);

						int north = buffer[(k - 1) * buffer[0] + j + 3];
						int northWest = buffer[(k - 1) * buffer[0] + j - 1 + 3];
						int west = buffer[k * buffer[0] + j - 1 + 3];
						prediction = med(north, west, northWest);

					if(prediction > 255){
						prediction = 255;
					}
				}
				else {
					resetCell();
					//searchTheWindow(k, j, decodedImage);

					/* searchTheWindow */


					if (j==xBorderM) {
						for (int i = 0; i < vectorSizeM; i++) {
							int x = j + offsetsSM[i][0];
							int y = k + offsetsSM[i][1];
							originVector[i] = buffer[y * buffer[0] + x + 3];
						}
					}
					else{
						originVector[4] = originVector[0];
						originVector[5] = originVector[1];
						originVector[1] = originVector[2];
						originVector[2] = originVector[3];
						originVector[0] = buffer[(k + offsetsSM[0][1]) * buffer[0] + j
								+ offsetsSM[0][0] + 3];
						originVector[3] = buffer[(k + offsetsSM[3][1]) * buffer[0] + j
								+ offsetsSM[3][0] + 3];
					}

					for(int y = -radiusM; y <= 0; y++){
						for(int x = -radiusM; x < radiusM; x++){
							if (x >= 0 && y == 0) {
								break;
							}

							int pX = j + x;
							int pY = k + y;
							long dist = 0;

							if (vectorDistM == VectorDistMeasure.L2){
								if(x == -radiusM){
									for (int i = 0; i < vectorSizeM; i++) {
										currVector[i] = buffer[(pY + offsetsSM[i][1]) * buffer[0] + pX
												+ offsetsSM[i][0] + 3];
										//dist += calcDistance(originVector[i],currVector[i]);
										dist += (originVector[i] - currVector[i]) * (originVector[i] - currVector[i]);
									}
								}
								else{
									currVector[4] = currVector[0];
									currVector[5] = currVector[1];
									currVector[1] = currVector[2];
									currVector[2] = currVector[3];
									currVector[0] = buffer[(pY + offsetsSM[0][1]) * buffer[0] + pX
											+ offsetsSM[0][0] + 3];
									currVector[3] = buffer[(pY + offsetsSM[3][1]) * buffer[0] + pX
											+ offsetsSM[3][0] + 3];
									for (int i = 0; i < vectorSizeM; i++) {
										dist += (originVector[i] - currVector[i]) * (originVector[i] - currVector[i]);
									}
								}
							}
							else if (vectorDistM == VectorDistMeasure.L1) {
								for (int i = 0; i < vectorSizeM; i++) {
									currVector[i] = buffer[(pY + offsetsSM[i][1]) * buffer[0] + pX
											+ offsetsSM[i][0] +3];
									dist += Math.abs(originVector[i] - currVector[i]);
								}
							}
							else if (vectorDistM == VectorDistMeasure.LINF) {
								for (int i = 0; i < vectorSizeM; i++) {
									currVector[i] = buffer[(pY + offsetsSM[i][1]) * buffer[0] + pX
											+ offsetsSM[i][0] +3];
									int distTmp = Math.abs(originVector[i] - currVector[i]);
									if (distTmp > dist)
										dist = distTmp;
								}
							}
							else if (vectorDistM == VectorDistMeasure.WL2) {
								for (int i = 0; i < vectorSizeM; i++) {
									currVector[i] = buffer[(pY + offsetsSM[i][1]) * buffer[0] + pX
											+ offsetsSM[i][0] +3];
									if (i == 0 || i == 2) {
										dist += 2 * ((originVector[i] - currVector[i]) * (originVector[i] - currVector[i]));
									} else {
										dist += (originVector[i] - currVector[i])
												* (originVector[i] - currVector[i]);
									}
								}
							}
							//long dist = calcDistance(originVector, currVector);
							CellPixelData pixel = new CellPixelData(x, y, dist);
							updateCell(pixel);
						}
					}

					computePenalties(k, j, decodedImage);

					if (CB_SPC_CORRECTION) {
						spcCorrectionM = 0;
						for (CellPixelData cpd : cellM) {
							int xOff = cpd.getxOff();
							int yOff = cpd.getyOff();
							int cellPred = blendPredictors(k + yOff, j + xOff, decodedImage);
							int cellPixel = decodedImage.getPixel((k + yOff) * buffer[0] + j + xOff);
							spcCorrectionM += cellPixel - cellPred;
						}
						spcCorrectionM /= cellSizeM;
					}

					prediction = blendPredictors(k, j, decodedImage) + spcCorrectionM;

				}
				if (prediction > 255) {
					prediction = 255;
				}
				//renewedPixel = buffer[k * buffer[0] + j + 3] + prediction;
				buffer[k * buffer[0] + j + 3] += prediction;
				decodedImage.setPixel(k, j, buffer[k * buffer[0] + j + 3]);
			}
		}
		Log.e("dekodiranje", "Saving file to " + filepath);
		decodedImage.setFilePath(filepath);
		decodedImage.writeImage();
	}

	public void predict_arrayDCM(int[] buffer, String filepath, String path){
		PGMImage decodedImage = new PGMImage();
		decodedImage.setDimension(buffer[0], buffer[1]);
		decodedImage.setMaxGray(buffer[2]);
		int prediction;
		int[] originVector = new int[vectorSizeM];
		int[] currVector = new int[vectorSizeM];
		for (int k = 0; k < buffer[1]; k++) {
			for (int j = 0; j < buffer[0]; j++) {
				if(j==0&&k==0) {
					prediction = 0;
				}
				else if(j==0) {
					//prediction = image.getPixel((tr - 1) * Columns + tc);
					prediction = buffer[(k - 1) * buffer[0] + 3];
				}
				else if(k==0) {
					//prediction = image.getPixel(tr * Columns + tc - 1);
					prediction = buffer[j - 1 + 3];
				}
				else if (j < xBorderM || (j > buffer[0] - xBorderM) || k < yBorderM) {
					//prediction = new MEDPredictor().predict(k, j, decodedImage);

					int north = buffer[(k - 1) * buffer[0] + j + 3];
					int northWest = buffer[(k - 1) * buffer[0] + j - 1 + 3];
					int west = buffer[k * buffer[0] + j - 1 + 3];
					prediction = med(north, west, northWest);

					if(prediction > buffer[2]){
						prediction = buffer[2];
					}
				}
				else {
					resetCell();
					//searchTheWindow(k, j, decodedImage);

					/* searchTheWindow */


					if (j==xBorderM) {
						for (int i = 0; i < vectorSizeM; i++) {
							int x = j + offsetsSM[i][0];
							int y = k + offsetsSM[i][1];
							originVector[i] = buffer[y * buffer[0] + x + 3];
						}
					}
					else{
						originVector[4] = originVector[0];
						originVector[5] = originVector[1];
						originVector[1] = originVector[2];
						originVector[2] = originVector[3];
						originVector[0] = buffer[(k + offsetsSM[0][1]) * buffer[0] + j
								+ offsetsSM[0][0] + 3];
						originVector[3] = buffer[(k + offsetsSM[3][1]) * buffer[0] + j
								+ offsetsSM[3][0] + 3];
					}

					for(int y = -radiusM; y <= 0; y++){
						for(int x = -radiusM; x < radiusM; x++){
							if (x >= 0 && y == 0) {
								break;
							}

							int pX = j + x;
							int pY = k + y;
							long dist = 0;

							if (vectorDistM == VectorDistMeasure.L2){
								if(x == -radiusM){
									for (int i = 0; i < vectorSizeM; i++) {
										currVector[i] = buffer[(pY + offsetsSM[i][1]) * buffer[0] + pX
												+ offsetsSM[i][0] + 3];
										//dist += calcDistance(originVector[i],currVector[i]);
										dist += (originVector[i] - currVector[i]) * (originVector[i] - currVector[i]);
									}
								}
								else{
									currVector[4] = currVector[0];
									currVector[5] = currVector[1];
									currVector[1] = currVector[2];
									currVector[2] = currVector[3];
									currVector[0] = buffer[(pY + offsetsSM[0][1]) * buffer[0] + pX
											+ offsetsSM[0][0] + 3];
									currVector[3] = buffer[(pY + offsetsSM[3][1]) * buffer[0] + pX
											+ offsetsSM[3][0] + 3];
									for (int i = 0; i < vectorSizeM; i++) {
										dist += (originVector[i] - currVector[i]) * (originVector[i] - currVector[i]);
									}
								}
							}
							else if (vectorDistM == VectorDistMeasure.L1) {
								for (int i = 0; i < vectorSizeM; i++) {
									currVector[i] = buffer[(pY + offsetsSM[i][1]) * buffer[0] + pX
											+ offsetsSM[i][0] +3];
									dist += Math.abs(originVector[i] - currVector[i]);
								}
							}
							else if (vectorDistM == VectorDistMeasure.LINF) {
								for (int i = 0; i < vectorSizeM; i++) {
									currVector[i] = buffer[(pY + offsetsSM[i][1]) * buffer[0] + pX
											+ offsetsSM[i][0] +3];
									int distTmp = Math.abs(originVector[i] - currVector[i]);
									if (distTmp > dist)
										dist = distTmp;
								}
							}
							else if (vectorDistM == VectorDistMeasure.WL2) {
								for (int i = 0; i < vectorSizeM; i++) {
									currVector[i] = buffer[(pY + offsetsSM[i][1]) * buffer[0] + pX
											+ offsetsSM[i][0] +3];
									if (i == 0 || i == 2) {
										dist += 2 * ((originVector[i] - currVector[i]) * (originVector[i] - currVector[i]));
									} else {
										dist += (originVector[i] - currVector[i])
												* (originVector[i] - currVector[i]);
									}
								}
							}
							//long dist = calcDistance(originVector, currVector);
							CellPixelData pixel = new CellPixelData(x, y, dist);
							updateCell(pixel);
						}
					}

					computePenalties(k, j, decodedImage);

					if (CB_SPC_CORRECTION) {
						spcCorrectionM = 0;
						for (CellPixelData cpd : cellM) {
							int xOff = cpd.getxOff();
							int yOff = cpd.getyOff();
							int cellPred = blendPredictors(k + yOff, j + xOff, decodedImage);
							int cellPixel = decodedImage.getPixel((k + yOff) * buffer[0] + j + xOff);
							spcCorrectionM += cellPixel - cellPred;
						}
						spcCorrectionM /= cellSizeM;
					}

					prediction = blendPredictors(k, j, decodedImage) + spcCorrectionM;

				}
				if (prediction > buffer[2]) {
					prediction = buffer[2];
				}
				//renewedPixel = buffer[k * buffer[0] + j + 3] + prediction;
				buffer[k * buffer[0] + j + 3] += prediction;
				decodedImage.setPixel(k, j, buffer[k * buffer[0] + j + 3]);
			}
		}
		Log.e("DCM", "GOTOVO DEKODIRANJE");
		// We specify the transfer syntax and the charset
		//com.imebra.DataSet dataSet = new com.imebra.DataSet("1.2.840.10008.1.2.1");
		com.imebra.DataSet loadedDataSet = com.imebra.CodecFactory.load(filepath);
		long bitsAllocated = loadedDataSet.getSignedLong(new com.imebra.TagId(0x28, 0x100), 0);
		if (bitsAllocated == 8) {
			bitDepth_t depth = depthU8;
			Image image = new Image(buffer[0], buffer[1], depth, "MONOCHROME2", 8);
			Log.e("DCM", "KREIRANA SLIKA");

			WritingDataHandlerNumeric dataHandler = image.getWritingDataHandler();
			Log.e("DCM", "DATA HANDLER");
			// Set all the pixels to red
			for (long scanY = 0; scanY != buffer[0]; scanY++) {
				for (long scanX = 0; scanX != buffer[1]; scanX++) {
					dataHandler.setUnsignedLong((scanY * buffer[1] + scanX), buffer[(int) scanY * buffer[1] + (int) scanX + 3]);
					//dataHandler.setUnsignedLong((scanY * buffer[1] + scanX) * 3 + 1, 0);
					//dataHandler.setUnsignedLong((scanY * buffer[1] + scanX) * 3 + 2, 0);
				}
			}

			dataHandler.delete();


			Log.e("DCM", "UCITANI PIXELI U SLIKU");
			loadedDataSet.setImage(0, image, veryHigh);
			//loadedDataSet.setImage(0, image,veryHigh);
			Log.e("DCM", "ROWS " + String.valueOf(buffer[0]));
			Log.e("DCM", "COLUMNS " + String.valueOf(buffer[1]));
			//com.imebra.CodecFactory.save(dataSet, path, com.imebra.codecType_t.dicom);
			com.imebra.CodecFactory.save(loadedDataSet, path, com.imebra.codecType_t.dicom);
		}
		if (bitsAllocated == 16) {
			bitDepth_t depth = depthU16;
			Image image = new Image(buffer[0], buffer[1], depth, "MONOCHROME2", 16);
			Log.e("DCM", "KREIRANA SLIKA");

			WritingDataHandlerNumeric dataHandler = image.getWritingDataHandler();
			Log.e("DCM", "DATA HANDLER");
			// Set all the pixels to red
			for (long scanY = 0; scanY != buffer[0]; scanY++) {
				for (long scanX = 0; scanX != buffer[1]; scanX++) {
					dataHandler.setUnsignedLong((scanY * buffer[1] + scanX), buffer[(int) scanY * buffer[1] + (int) scanX + 3]);
					//dataHandler.setUnsignedLong((scanY * buffer[1] + scanX) * 3 + 1, 0);
					//dataHandler.setUnsignedLong((scanY * buffer[1] + scanX) * 3 + 2, 0);
				}
			}

			dataHandler.delete();


			Log.e("DCM", "UCITANI PIXELI U SLIKU");
			loadedDataSet.setImage(0, image, veryHigh);
			//loadedDataSet.setImage(0, image,veryHigh);
			Log.e("DCM", "ROWS " + String.valueOf(buffer[0]));
			Log.e("DCM", "COLUMNS " + String.valueOf(buffer[1]));
			//com.imebra.CodecFactory.save(dataSet, path, com.imebra.codecType_t.dicom);
			com.imebra.CodecFactory.save(loadedDataSet, path, com.imebra.codecType_t.dicom);
		}
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

	public int predict(int tr, int tc, PGMImage pgmP) {
		return 0;
	}
/*	public int predict(int tr, int tc, PGMImage pgmP) {
		int prediction = 0;
		int Columns = pgmP.getColumns();
		if (tc < xBorderM || (tc > Columns - xBorderM) || tr < yBorderM) {
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
					int cellPixel = pgmP.getPixel((tr + yOff) * Columns + tc + xOff);
					spcCorrectionM += cellPixel - cellPred;
				}
				spcCorrectionM /= cellSizeM;
			}

			prediction = blendPredictors(tr, tc, pgmP) + spcCorrectionM;
		}

		return prediction > 255 ? 255 : prediction;
	}
	*/
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

	/*private void searchTheWindow(int tr, int tc, PGMImage pgmP) {
		int Columns = pgmP.getColumns();
		int[] originVector = new int[vectorSizeM];
		int[] currVector = new int[vectorSizeM];


		for (int i = 0; i < vectorSizeM; i++) {
			int x = tc + offsetsSM[i][0];
			int y = tr + offsetsSM[i][1];
			originVector[i] = pgmP.getPixel(y * Columns + x);
		}

		for(int y = -radiusM; y <= 0; y++){
			for(int x = -radiusM; x < radiusM; x++){
				if (x >= 0 && y == 0) {
					break;
				}

				int pX = tc + x;
				int pY = tr + y;
				long dist = 0;
                if (vectorDistM == VectorDistMeasure.L2){
				    for (int i = 0; i < vectorSizeM; i++) {
					    currVector[i] = pgmP.getPixel((pY + offsetsSM[i][1]) * Columns + pX
					    		+ offsetsSM[i][0]);
						//dist += calcDistance(originVector[i],currVector[i]);
                        dist += (originVector[i] - currVector[i]) * (originVector[i] - currVector[i]);
                    }
                }
                else if (vectorDistM == VectorDistMeasure.L1) {
                    for (int i = 0; i < vectorSizeM; i++) {
                        currVector[i] = pgmP.getPixel((pY + offsetsSM[i][1]) * Columns + pX
                                + offsetsSM[i][0]);
                        dist += Math.abs(originVector[i] - currVector[i]);
                    }
                }
                else if (vectorDistM == VectorDistMeasure.LINF) {
                    for (int i = 0; i < vectorSizeM; i++) {
                        currVector[i] = pgmP.getPixel((pY + offsetsSM[i][1]) * Columns + pX
                                + offsetsSM[i][0]);
                        int distTmp = Math.abs(originVector[i] - currVector[i]);
                        if (distTmp > dist)
                            dist = distTmp;
                    }
                }
                else if (vectorDistM == VectorDistMeasure.WL2) {
                    for (int i = 0; i < vectorSizeM; i++) {
                        currVector[i] = pgmP.getPixel((pY + offsetsSM[i][1]) * Columns + pX
                                + offsetsSM[i][0]);
                        if (i == 0 || i == 2) {
                            dist += 2 * ((originVector[i] - currVector[i]) * (originVector[i] - currVector[i]));
                        } else {
                            dist += (originVector[i] - currVector[i])
                                    * (originVector[i] - currVector[i]);
                        }
                    }
                }
				//long dist = calcDistance(originVector, currVector);
				CellPixelData pixel = new CellPixelData(x, y, dist);
				updateCell(pixel);
			}
        }
	}
*/

	/* NDK test calcDistance method */
	public native long calcDistance(int originVector, int currVector);

/*	private long calcDistance(int[] originVector, int[] currVector) {
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
*/
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
		int Columns = pgmP.getColumns();
		for (CellPixelData cpd : cellM) {
			int xOff = cpd.getxOff();
			int yOff = cpd.getyOff();

			int pixel = pgmP.getPixel((tr + yOff) * Columns + tc + xOff);


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
