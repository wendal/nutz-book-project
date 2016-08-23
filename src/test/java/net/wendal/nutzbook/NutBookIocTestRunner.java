package net.wendal.nutzbook;

import org.junit.runners.model.InitializationError;
import org.nutz.mock.NutTestRunner;

public class NutBookIocTestRunner extends NutTestRunner {

    public NutBookIocTestRunner(Class<?> klass) throws InitializationError {
        super(klass);
    }

    protected Class<?> getMainModule() {
        return MainModule.class;
    }
}
