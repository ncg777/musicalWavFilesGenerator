package name.ncg777.musicalWavFilesGenerator;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;

import name.ncg777.maths.music.pcs12.Pcs12;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioFileFormat;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class ForteWavDiskWriter {

    public static void writeWavFile(String filePath, List<Integer> pitches) throws IllegalArgumentException, IOException {

        // Audio settings
        float sampleRate = 1.0f; // Standard sample rate
        int sampleSizeInBits = 16;   // Standard bit depth for WAV
        int channels = 1;           // Mono
        boolean signed = true;
        boolean bigEndian = false;

        AudioFormat format = new AudioFormat(sampleRate, sampleSizeInBits, channels, signed, bigEndian);

        // Convert pulse train to audio samples
        byte[] audioData = new byte[pitches.size() * 2]; // 2 bytes per sample (16-bit audio)
        int index = 0;
        for (int value : pitches) {
            short sample = (short) ((((float)value) / 12.0f) * Short.MAX_VALUE);
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
                pitches.size())) {

            AudioSystem.write(audioStream, AudioFileFormat.Type.WAVE, wavFile);
            System.out.println("WAV file written to " + filePath);
        }
    }
    
    public static void generateForteWavs() {
	try {
            File mainDir = new File("./Wavs/Forte Numbers");
            if(!mainDir.exists())
        	mainDir.mkdir();
            
            var dict = Pcs12.getForteChordDict().keySet();
            
            for(var c : dict) {
        	var n = Pcs12.parseForte(c);
        	writeWavFile(mainDir.getAbsoluteFile() + "/" + c + ".wav",n.asSequence());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args) {
	generateForteWavs();
    }
}