package ChatServer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by user-pc on 09.07.2016.
 */
public class JsonParser {
    static  Object parseFromJson(InputStream is, Class clazz) throws IOException {
        Object obj=null;
        int sz = is.available();
        if (sz > 0) {
            byte[] buf = new byte[is.available()];
            is.read(buf);

            Gson gson = new GsonBuilder().create();
            obj = gson.fromJson(new String(buf), clazz);
        }
        return obj;
    }
}
