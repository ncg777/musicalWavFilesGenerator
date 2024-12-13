package name.ncg777.musicalWavFilesGenerator;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;

import name.ncg777.maths.HadamardMatrix;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioFileFormat;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.util.List;

public class PulseTrainDiskWriter {

    public static void writeWavFile(String filePath, List<Integer> pulseTrain, float amplitude) throws IllegalArgumentException, IOException {
        // Validate the amplitude
        if (amplitude <= 0) {
            throw new IllegalArgumentException("Amplitude must be positive.");
        }

        // Validate the pulse train
        for (int value : pulseTrain) {
            if (value != -1 && value != 1) {
                throw new IllegalArgumentException("Pulse train values must be either -1 or 1.");
            }
        }

        // Audio settings
        float sampleRate = 1.0f; // Standard sample rate
        int sampleSizeInBits = 16;   // Standard bit depth for WAV
        int channels = 1;           // Mono
        boolean signed = true;
        boolean bigEndian = false;

        AudioFormat format = new AudioFormat(sampleRate, sampleSizeInBits, channels, signed, bigEndian);

        // Convert pulse train to audio samples
        byte[] audioData = new byte[pulseTrain.size() * 2]; // 2 bytes per sample (16-bit audio)
        int index = 0;
        for (int value : pulseTrain) {
            short sample = (short) (value * amplitude * Short.MAX_VALUE);
            audioData[index++] = (byte) (sample & 0xFF); // Lower byte
            audioData[index++] = (byte) ((sample >> 8) & 0xFF); // Upper byte
        }

        // Write audio data to a WAV file
        File wavFile = new File(filePath);
        if(wavFile.exists())
            wavFile.delete();
        try (AudioInputStream audioStream = new AudioInputStream(
                new java.io.ByteArrayInputStream(audioData),
                format,
                pulseTrain.size())) {

            AudioSystem.write(audioStream, AudioFileFormat.Type.WAVE, wavFile);
        }
    }
    
    private static BigInteger binToN(List<Integer> p) {
	BigInteger o = BigInteger.ZERO;
	for(int i=0;i<p.size();i++) {
	    if(p.get(i).equals(1)) {
		o = o.add(BigInteger.TWO.pow(i));
	    }
	}
	return o;
    }
    
    public static void generateWalshRhythms() {
	try {
            File mainDir = new File("./Walsh rhythms");
            if(!mainDir.exists())
        	mainDir.mkdir();
            for(int i=1;i<9;i++) {
        	var m = HadamardMatrix.getMatrix(i);
        	
        	int p = (int)Math.round(Math.pow(2.0, i));
        	int ndigits = BigInteger.TWO.pow(p).toString().length();
        	
        	File dir = new File(mainDir.getAbsolutePath() + "/" + Integer.toString(i));
        	if(!dir.exists())
        	    dir.mkdir();
        	for(int j=0;j<p;j++) {
        	    var r = m.getRow(j);
        	    String fn = dir.getAbsolutePath() + "/" + String.format("%0"+ ndigits + "d", binToN(r)) + ".wav";
        	    writeWavFile(fn,r,1.0f);
        	    System.out.println("WAV file written to " + fn);
        	}
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args) {
	generateWalshRhythms();
    }
}