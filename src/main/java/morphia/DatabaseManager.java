package morphia;

import com.mongodb.MongoClient;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;

public class DatabaseManager {
    private static Datastore datastore;

    private DatabaseManager(){

    }

    public static Datastore getInstance()
    {
        if(datastore == null)
        {
            final Morphia morphia = new Morphia();
            morphia.mapPackage("morphia");
            datastore = morphia.createDatastore(new MongoClient("localhost", 8004), "Database");
            datastore.ensureIndexes();
        }

        return datastore;
    }
}
