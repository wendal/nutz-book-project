package cn.apiclub.captcha.text.producer;

/**
 * TextProducer implementation that will return Chinese characters.
 * 
 * @author <a href="mailto:james.childers@gmail.com">James Childers</a>
 * 
 */
public class ChineseTextProducer implements TextProducer {
    
	static final int DEFAULT_LENGTH = 5;    
    // Here's hoping none of the characters in this range are offensive.
    static final int CODE_POINT_START = 0x4E00;
    static final int CODE_POINT_END = 0x4F6F;
    private static final int NUM_CHARS = CODE_POINT_END - CODE_POINT_START;
    private static final char[] CHARS;
    
    static {
    	CHARS = new char[NUM_CHARS];
    	for (char c = CODE_POINT_START, i = 0; c < CODE_POINT_END; c++, i++) {
    		CHARS[i] = Character.valueOf(c);
    	}
    }

    private final TextProducer _txtProd;	// Decorator
    
    public ChineseTextProducer() {
    	this(DEFAULT_LENGTH);
    }
    
    public ChineseTextProducer(int length) {
    	_txtProd = new DefaultTextProducer(length, CHARS);
    }

    @Override
    public String getText() {
    	return _txtProd.getText();
    }
}
