package net.wendal.nutzbook.util;

import org.nustaq.serialization.FSTConfiguration;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.plugins.cache.dao.CacheResult;
import org.nutz.plugins.cache.dao.impl.convert.AbstractCacheSerializer;

public class FstCacheSerializer extends AbstractCacheSerializer {
    
    static FSTConfiguration conf = FSTConfiguration.createDefaultConfiguration();

    private static final Log log = Logs.get();

    public Object from(Object obj) {
        if (obj == null)
            return NULL_OBJ;
        try {
            log.debug("from fst ....");
            return conf.asByteArray(obj);
        } catch (Exception e) {
            log.info("Object to bytes fail", e);
            return null;
        }
    }

    public Object back(Object obj) {
        if (obj == null)
            return null;
        if (isNULL_OBJ(obj))
            return CacheResult.NULL;
        try {
            return conf.asObject((byte[])obj);
        } catch (Exception e) {
            log.info("bytes to Object fail", e);
            return null;
        }
    }

}
