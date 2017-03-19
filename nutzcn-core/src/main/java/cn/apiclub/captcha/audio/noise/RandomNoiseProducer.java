package cn.apiclub.captcha.audio.noise;

import java.security.SecureRandom;
import java.util.List;
import java.util.Random;

import cn.apiclub.captcha.audio.Mixer;
import cn.apiclub.captcha.audio.Sample;
import cn.apiclub.captcha.util.FileUtil;

/**
 * Adds noise to a {@link Sample} from one of the given <code>noiseFiles</code>.
 * By default this noise comes from one of three files, all located in
 * <code>/sounds/noises/</code>: <code>radio_tuning.wav</code>,
 * <code>restaurant.wav</code>, and <code>swimming.wav</code>. This can be
 * overridden by passing the location of your own sound files to the
 * constructor, e.g.:
 * 
 * <pre>
 * String myFiles = { &quot;/mysounds/noise1.wav&quot;, &quot;/mysounds/noise2.wav&quot; };
 * NoiseProducer myNp = new RandomNoiseProducer(myFiles);
 * </pre>
 * 
 * @author <a href="mailto:james.childers@gmail.com">James Childers</a>
 * 
 */
public class RandomNoiseProducer implements NoiseProducer {

    private static final Random RAND = new SecureRandom();
    private static final String[] DEFAULT_NOISES = {
            "/sounds/noises/radio_tuning.wav", 
            "/sounds/noises/restaurant.wav",
            "/sounds/noises/swimming.wav", };

    private final String _noiseFiles[];

    public RandomNoiseProducer() {
        this(DEFAULT_NOISES);
    }

    public RandomNoiseProducer(String[] noiseFiles) {
        _noiseFiles = noiseFiles;
    }

    /**
     * Append the given <code>samples</code> to each other, then add random
     * noise to the result.
     * 
     */
    @Override public Sample addNoise(List<Sample> samples) {
        Sample appended = Mixer.append(samples);
        String noiseFile = _noiseFiles[RAND.nextInt(_noiseFiles.length)];
        Sample noise = FileUtil.readSample(noiseFile);

        // Decrease the volume of the noise to make sure the voices can be heard
        return Mixer.mix(appended, 1.0, noise, 0.6);
    }

    @Override public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("[Noise files: ");
        sb.append(_noiseFiles);
        sb.append("]");

        return sb.toString();
    }
}
