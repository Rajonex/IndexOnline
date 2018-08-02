package rest;


import com.mongodb.BasicDBObject;
import com.mongodb.WriteResult;
import morphia.DatabaseManager;
import objects.Course;
import objects.Grade;
import objects.Student;
import org.bson.BSON;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Key;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import org.mongodb.morphia.query.UpdateResults;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

@Path("/courses")
public class CourseService {


    //
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public List<Course> getAllCourses(@QueryParam("name") String name, @QueryParam("lecturer") String lecturer) {
        Datastore datastore = DatabaseManager.getInstance();
        Query<Course> query = datastore.createQuery(Course.class);
        if(lecturer != null)
        {
            query.field("lecturer").containsIgnoreCase(lecturer);
        }

        if(name != null)
        {
            query.field("name").containsIgnoreCase(name);
        }

        return query.asList();
    }


    @POST
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response addCourse(Course course) {
//        int result = CourseDB.addCourse(course);
//        if (!result) {
//            return Response.status(Response.Status.NOT_MODIFIED).build();
//        }

        Datastore datastore = DatabaseManager.getInstance();
        Key<Course> key = datastore.save(course);
        String result = key.getId().toString();
//        System.out.println(key.getId());
        if(ObjectId.isValid(result)) {
            course.setId(new ObjectId(result));
        }

        URI uri = null;
        try {
            uri = new URI("/courses/" + result);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        return Response.created(uri).entity(course).build();
//        return Response.status(Response.Status.CREATED).location(uri).build();
    }


    @Path("/{id}")
    @DELETE
    public Response deleteCourse(@PathParam("id") String id) {
        Datastore datastore = DatabaseManager.getInstance();
        if(ObjectId.isValid(id)) {
            Course deletedCourse = datastore.get(Course.class, new ObjectId(id));


            boolean resultResponse = false;
            if(deletedCourse != null) {
                Query<Student> query = datastore.createQuery(Student.class);

                UpdateOperations<Student> updateOperations = datastore.createUpdateOperations(Student.class).disableValidation().removeAll("grades", new BasicDBObject("course.$id", deletedCourse.getId()));


                UpdateResults updateResults = datastore.update(query, updateOperations);

                WriteResult result = datastore.delete(deletedCourse);
                resultResponse = result.getN()>0 ? true : false;
            }


//        boolean result = CourseDB.deleteCourseById(id);
            if (resultResponse) {
                return Response.status(Response.Status.OK).build();
            }
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    //
    @Path("/{id}")
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getCourseById(@PathParam("id") String id) {

//        Query<Course> query = datastore.getByKey(Course.class, new Key<Course>(Course.class, "Course", id));


//        Query<Course> query = datastore.createQuery(Course.class);
//        List<Course> courses = query.filter("id", new ObjectId(id)).asList();
//        Course course = null;
//        if(courses != null && courses.size() > 0)
//        {
//            course = courses.get(0);
//        }

        if (ObjectId.isValid(id)) {
            Datastore datastore = DatabaseManager.getInstance();
            Course course = datastore.get(Course.class, new ObjectId(id));

            if (course != null) {
                return Response.ok(course).build();
            }
        }

        return Response.status(Response.Status.NOT_FOUND).build();
    }
//
    @Path("/{id}")
    @PUT
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Response updateCourse(@PathParam("id") String id, Course course) {


        if (ObjectId.isValid(id)) {
            Datastore datastore = DatabaseManager.getInstance();
            Course courseToUpdate = datastore.get(Course.class, new ObjectId(id));

            if (courseToUpdate != null) {
                UpdateOperations<Course> updateOperations = datastore.createUpdateOperations(Course.class).set("name", course.getName()).set("lecturer", course.getLecturer());
                datastore.update(courseToUpdate, updateOperations);
            return Response.status(Response.Status.OK).build();
            }
        }


//        Course courseToChange = CourseDB.getCourseById(id);
//        if (courseToChange != null) {
////            courseToChange.setId(course.getId());
//            courseToChange.setLecturer(course.getLecturer());
//            courseToChange.setName(course.getName());
//            return Response.status(Response.Status.OK).build();
//        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }


}
