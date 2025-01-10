package com.ghao.apps.gyro.util;

import java.util.Arrays;

// See [MyFFT]
public class FastFourierTransformer {

    public static void transform(double[] inputData) {
        int dataSize = inputData.length;
        if (dataSize == 1) {
            return;
        }

        double[] evenData = extractEvenElements(inputData);
        double[] oddData = extractOddElements(inputData);

        transform(evenData);
        transform(oddData);

        combineEvenAndOddData(inputData, evenData, oddData);
    }

    private static double[] extractEvenElements(double[] inputData) {
        int halfSize = inputData.length / 2;
        double[] evenData = new double[halfSize];

        for (int i = 0; i < halfSize; i++) {
            evenData[i] = inputData[2 * i];
        }

        return evenData;
    }

    private static double[] extractOddElements(double[] inputData) {
        int halfSize = inputData.length / 2;
        double[] oddData = new double[halfSize];

        for (int i = 0; i < halfSize; i++) {
            oddData[i] = inputData[2 * i + 1];
        }

        return oddData;
    }

    private static void combineEvenAndOddData(double[] inputData, double[] evenData, double[] oddData) {
        int halfSize = inputData.length / 2;

        for (int i = 0; i < halfSize; i++) {
            double angle = -2 * Math.PI * i / inputData.length;
            double realPart = Math.cos(angle) * oddData[i];
            double imaginaryPart = Math.sin(angle) * oddData[i];

            inputData[i] = evenData[i] + realPart;
            inputData[i + halfSize] = evenData[i] - realPart;
        }
    }

    public static double[] realPart(double[] complexData) {
        return Arrays.stream(complexData).filter(x -> x % 2 == 0).toArray();
    }

    public static double[] imaginaryPart(double[] complexData) {
        return Arrays.stream(complexData).filter(x -> x % 2 != 0).toArray();
    }
}
