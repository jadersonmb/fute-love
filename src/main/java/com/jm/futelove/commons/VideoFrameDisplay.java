package com.jm.futelove.commons;

import org.bytedeco.opencv.opencv_core.Mat;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class VideoFrameDisplay {
    private JFrame frame;
    private JLabel imageLabel;

    public VideoFrameDisplay() {
        SwingUtilities.invokeLater(() -> {
            frame = new JFrame("Video Processing with Face Detection");
            imageLabel = new JLabel();
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 600);
            frame.add(imageLabel);
            frame.setVisible(true);
        });
    }

    public void updateImage(BufferedImage img) {
        SwingUtilities.invokeLater(() -> {
            if (imageLabel != null) {
                ImageIcon icon = new ImageIcon(img);
                imageLabel.setIcon(icon);
                frame.pack();
            } else {
                System.err.println("imageLabel is not initialized");
            }
        });
    }

    public void showInFrame(Mat mat) {
        SwingUtilities.invokeLater(() -> {
            JFrame mediaFrame = new JFrame("Media");
            mediaFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            mediaFrame.setSize(800, 600);

            BufferedImage bufferedImage = matToBufferedImage(mat);
            ImageIcon imageIcon = new ImageIcon(bufferedImage);
            JLabel label = new JLabel("", imageIcon, JLabel.CENTER);

            mediaFrame.add(label);
            mediaFrame.setVisible(true);
        });
    }

    private static BufferedImage matToBufferedImage(Mat mat) {
        int width = mat.cols();
        int height = mat.rows();
        int channels = mat.channels();
        BufferedImage bufferedImage;

        if (channels == 1) {
            bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
        } else {
            bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
        }

        byte[] data = new byte[width * height * channels];
        mat.data().get(data);
        bufferedImage.getRaster().setDataElements(0, 0, width, height, data);

        return bufferedImage;
    }
}

