package com.codecool.dungeoncrawl.logic.music;

import com.codecool.dungeoncrawl.Main;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;

public class MusicPlayer {

    public static String[] monsterSounds = {
            "/monsters/1.wav",
            "/monsters/2.wav",
            "/monsters/3.wav",
            "/monsters/4.wav",
            "/monsters/5.wav",
            "/monsters/6.wav",
            "/monsters/7.wav",
            "/monsters/8.wav",
            "/monsters/9.wav",
            "/monsters/10.wav"
    };
    public static String opening = "/mainSound.wav";
    public static String stepSound = "/footStepSound.wav";

    public static String bossSound = "/bossSound.wav";
    public static void playSound(String fileName,float volume) {
        try {
            Clip clip = AudioSystem.getClip();
            AudioInputStream inputStream = AudioSystem.getAudioInputStream(
                    Main.class.getResourceAsStream(fileName));
            clip.open(inputStream);
            setVolume(volume,clip);
            clip.start();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
    public static void setVolume(float volume, Clip clip) {
        if (volume < 0f || volume > 1f)
            throw new IllegalArgumentException("Volume not valid: " + volume);
        FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
        gainControl.setValue(20f * (float) Math.log10(volume));
    }
}