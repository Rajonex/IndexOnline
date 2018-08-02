package app;

import morphia.DatabaseManager;
import objects.Course;
import objects.ExceptionHandler;
import objects.Grade;
import objects.Student;
import org.bson.types.ObjectId;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.linking.DeclarativeLinkingFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.mongodb.morphia.Datastore;
import utils.CustomHeaders;
import utils.DateParamConverterProvider;

import javax.ws.rs.core.Application;
import java.io.IOException;
import java.net.URI;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

import java.util.Date;
import java.util.List;

public class Main {

    // uruchomienie bazy danych: bin\mongod --dbpath ./data/db/ --port 8004

    public static final String BASE_URI = "http://localhost:8000/";

    /**
     * Starts Grizzly HTTP server exposing JAX-RS resources defined in this application.
     *
     * @return Grizzly HTTP server.
     */
    public static HttpServer startServer() {
        // create a resource config that scans for JAX-RS resources and providers
        // in com.example.rest package
//        final ResourceConfig rc = new ResourceConfig().packages("rest");
        ResourceConfig rc = null;

        rc = new ResourceConfig().packages("rest").packages("org.glassfish.jersey.examples.linking").register(DeclarativeLinkingFeature.class);
        rc.register(new ExceptionHandler());
        rc.register(new DateParamConverterProvider("yyyy-MM-dd"));
        rc.register(new CustomHeaders());

        return GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), rc);
    }

    /**
     * Main method.
     *
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        final HttpServer server = startServer();
        System.out.println("Server started...");

        /**
         * DODANIE STUDENTA
         */
//        Course course = new Course("Angielski", "Anna Dlugosz");
//        course.setId(new ObjectId("5ad8f4a94ea7422ae4ebf59c"));
//        Grade grade = new Grade(1, 3.5, new Date(4169489465489L), course, 100);
//
//        List<Grade> gradeList = new ArrayList<>();
//        gradeList.add(grade);
//        Student student = new Student(100, "Maciej", "Ix", new Date(464546541231L), gradeList);
//        Datastore datastore = DatabaseManager.getInstance();
//        datastore.save(student);


    }
}
