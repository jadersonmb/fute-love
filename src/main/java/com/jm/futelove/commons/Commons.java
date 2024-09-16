package com.jm.futelove.commons;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class Commons {

    public static org.bytedeco.opencv.opencv_core.Mat convertMatOpenCoreCV(org.opencv.core.Mat opencvMat) {
        int rows = opencvMat.rows();
        int cols = opencvMat.cols();
        int type = opencvMat.type();
        int channels = opencvMat.channels();
        int elemSize = (int) opencvMat.elemSize();
        int size = rows * cols * elemSize;

        ByteBuffer byteBuffer = ByteBuffer.allocate(size);
        opencvMat.get(0, 0, byteBuffer.array());

        org.bytedeco.opencv.opencv_core.Mat javaCPP_Mat = new org.bytedeco.opencv.opencv_core.Mat(rows, cols, type);

        byteBuffer.rewind();
        javaCPP_Mat.data().put(byteBuffer.array());

        return javaCPP_Mat;
    }

    public static org.opencv.core.Mat convertToOpenCVMat(org.bytedeco.opencv.opencv_core.Mat javaCPPMat) {
        int rows = javaCPPMat.rows();
        int cols = javaCPPMat.cols();
        int type = javaCPPMat.type();
        int elemSize = (int) javaCPPMat.elemSize(); // Tamanho do elemento em bytes
        int channels = javaCPPMat.channels();

        org.opencv.core.Mat opencvMat = new org.opencv.core.Mat(rows, cols, type);

        ByteBuffer byteBuffer = ByteBuffer.allocate(rows * cols * channels * elemSize);
        byteBuffer.order(ByteOrder.nativeOrder()); // Configura a ordem dos bytes para a plataforma nativa

        byteBuffer.put(javaCPPMat.data().asByteBuffer().array());

        opencvMat.put(0, 0, byteBuffer.array());

        return opencvMat;
    }

}
