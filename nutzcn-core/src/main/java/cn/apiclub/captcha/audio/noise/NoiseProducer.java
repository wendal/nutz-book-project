package cn.apiclub.captcha.audio.noise;

import java.util.List;

import cn.apiclub.captcha.audio.Sample;

public interface NoiseProducer {
    public Sample addNoise(List<Sample> target);
}
