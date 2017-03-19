package cn.apiclub.captcha.audio;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import cn.apiclub.captcha.audio.noise.NoiseProducer;
import cn.apiclub.captcha.audio.noise.RandomNoiseProducer;
import cn.apiclub.captcha.audio.producer.RandomNumberVoiceProducer;
import cn.apiclub.captcha.audio.producer.VoiceProducer;
import cn.apiclub.captcha.text.producer.NumbersAnswerProducer;
import cn.apiclub.captcha.text.producer.TextProducer;

/**
 * A builder for generating a CAPTCHA audio/answer pair.
 *
 * <p>
 * Example for generating a new CAPTCHA:
 * </p>
 * 
 * <pre>
 * AudioCaptcha ac = new AudioCaptcha.Builder()
 *   .addAnswer()
 *   .addNoise()
 *   .build();
 * </pre>
 * <p>
 * Note that the <code>build()</code> method must always be called last. Other
 * methods are optional.
 * </p>
 *
 * @author <a href="mailto:james.childers@gmail.com">James Childers</a>
 *
 */
public final class AudioCaptcha {

    public static final String NAME = "audioCaptcha";
    private static final Random RAND = new SecureRandom();

    private Builder _builder;

    private AudioCaptcha(Builder builder) {
        _builder = builder;
    }

    public static class Builder {

        private String _answer = "";
        private Sample _challenge;
        private List<VoiceProducer> _voiceProds;
        private List<NoiseProducer> _noiseProds;

        public Builder() {
            _voiceProds = new ArrayList<VoiceProducer>();
            _noiseProds = new ArrayList<NoiseProducer>();
        }

        public Builder addAnswer() {
            return addAnswer(new NumbersAnswerProducer());
        }

        public Builder addAnswer(TextProducer ansProd) {
            _answer += ansProd.getText();

            return this;
        }

        public Builder addVoice() {
            _voiceProds.add(new RandomNumberVoiceProducer());

            return this;
        }

        public Builder addVoice(VoiceProducer vProd) {
            _voiceProds.add(vProd);

            return this;
        }

        public Builder addNoise() {
            return addNoise(new RandomNoiseProducer());
        }

        public Builder addNoise(NoiseProducer noiseProd) {
            _noiseProds.add(noiseProd);

            return this;
        }

        public AudioCaptcha build() {
            // Make sure we have at least one voiceProducer
            if (_voiceProds.size() == 0) {
                addVoice();
            }

            // Convert answer to an array
            char[] ansAry = _answer.toCharArray();

            // Make a List of Samples for each character
            VoiceProducer vProd;
            List<Sample> samples = new ArrayList<Sample>();
            Sample sample;
            for (int i = 0; i < ansAry.length; i++) {
                // Create Sample for this character from one of the
                // VoiceProducers
                vProd = _voiceProds.get(RAND.nextInt(_voiceProds.size()));
                sample = vProd.getVocalization(ansAry[i]);
                samples.add(sample);
            }

            // 3. Add noise, if any, and return the result
            if (_noiseProds.size() > 0) {
                NoiseProducer nProd = _noiseProds.get(RAND.nextInt(_noiseProds
                        .size()));
                _challenge = nProd.addNoise(samples);

                return new AudioCaptcha(this);
            }

            _challenge = Mixer.append(samples);

            return new AudioCaptcha(this);
        }

        @Override public String toString() {
            StringBuffer sb = new StringBuffer();
            sb.append("[Answer: ");
            sb.append(_answer);
            sb.append("]");

            return sb.toString();
        }
    }

    public boolean isCorrect(String answer) {
        return answer.equals(_builder._answer);
    }

    public String getAnswer() {
        return _builder._answer;
    }

    public Sample getChallenge() {
        return _builder._challenge;
    }

    @Override public String toString() {
        return _builder.toString();
    }
}
