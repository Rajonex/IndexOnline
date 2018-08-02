package rest;

import com.mongodb.BasicDBObject;
import com.mongodb.WriteResult;
import morphia.DatabaseManager;
import objects.Grade;
import objects.Student;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Key;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import org.mongodb.morphia.query.UpdateResults;
import utils.GroupId;
import utils.Index;

import javax.ws.rs.*;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Path("/students")
public class StudentService {


    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public List<Student> get( @DefaultValue("-1") @QueryParam("index") int index, @QueryParam("firstname") String firstname, @QueryParam("lastname") String lastname, @QueryParam("birthdayFrom")Date birthdayFrom, @QueryParam("birthdayTo") Date birthdayTo, @QueryParam("birthday") Date birthday) {
        Datastore datastore = DatabaseManager.getInstance();
        Query<Student> query = datastore.createQuery(Student.class);
        if(index != -1)
        {
            query.field("index").equal(index);
        }
        if (firstname != null) {
            query.field("firstname").containsIgnoreCase(firstname);
        }
        if (lastname != null) {
            query.field("lastname").containsIgnoreCase(lastname);
        }
        if(birthday != null)
        {
            query.field("birthsday").equal(birthday);
        }
        if(birthdayFrom != null)
        {
            query.field("birthsday").greaterThanOrEq(birthdayFrom);
        }
        if(birthdayTo != null)
        {
            query.field("birthsday").lessThanOrEq(birthdayTo);
        }

        return query.asList();
    }

    @POST
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response addStudent(Student student) {

//        if (!result) {
//            return Response.status(Response.Status.BAD_REQUEST).build();
//        }
        Datastore datastore = DatabaseManager.getInstance();
        Query<Index> query = datastore.find(Index.class);
        UpdateOperations<Index> updateOperations = datastore.createUpdateOperations(Index.class).inc("index");
        Index autoincrementIndex = datastore.findAndModify(query, updateOperations);
        if (autoincrementIndex == null) {
            autoincrementIndex = new Index(1);
            datastore.save(autoincrementIndex);
        }
        int index = autoincrementIndex.getIndex();
        student.setIndex(index);
        if (student.getGrades() != null) {
            for (Grade studentGrade : student.getGrades()) {
                studentGrade.setIndex(index);
                Query<GroupId> queryGradeId = datastore.find(GroupId.class);
                UpdateOperations<GroupId> updateOperationsGradeId = datastore.createUpdateOperations(GroupId.class).inc("groupId");
                GroupId groupId = datastore.findAndModify(queryGradeId, updateOperationsGradeId);
                if (groupId == null) {
                    groupId = new GroupId(1);
                    datastore.save(groupId);
                }
                studentGrade.setId(groupId.getGroupId());
            }
        } else
        {
            student.setGrades(new ArrayList<>());
        }
        Key<Student> key = datastore.save(student);
        int result = autoincrementIndex.getIndex();

        URI uri = null;
        try {
            uri = new URI("/students/" + result);
        } catch (URISyntaxException er) {
            er.printStackTrace();
        }

        return Response.created(uri).entity(student).build();
//        return Response.status(Response.Status.CREATED).location(uri).build();
    }

    @GET
    @Path("/{index}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getStudentByIndex(@PathParam("index") int index) {
        Datastore datastore = DatabaseManager.getInstance();
        Query<Student> query = datastore.createQuery(Student.class).field("index").equal(index);
        Student student = query.get();

        if (student != null) {
            return Response.ok(student).build();
        }

        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @PUT
    @Path("/{index}")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response updateStudent(@PathParam("index") int index, Student student) {
        Datastore datastore = DatabaseManager.getInstance();
        Query<Student> query = datastore.createQuery(Student.class).field("index").equal(index);

        UpdateOperations<Student> updateOperations = datastore.createUpdateOperations(Student.class).set("firstname", student.getFirstname()).set("lastname", student.getLastname()).set("birthsday", student.getBirthsday());//.set("grades", student.getGrades());
        UpdateResults updateResults = datastore.update(query, updateOperations);


        System.out.println(updateResults.getUpdatedCount());
        if (updateResults.getUpdatedCount() > 0) {
            return Response.status(Response.Status.OK).build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @DELETE
    @Path("/{index}")
    public Response deleteStudent(@PathParam("index") int index) {
        Datastore datastore = DatabaseManager.getInstance();
        Query<Student> query = datastore.createQuery(Student.class).field("index").equal(index);
        WriteResult result = datastore.delete(query);

        if (result.getN() > 0) {
            return Response.status(Response.Status.OK).build();
        }
        return Response.status(Response.Status.BAD_REQUEST).build();
    }

    @GET
    @Path("/{index}/grades")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getStudentsGrades(@PathParam("index") int index, @QueryParam("id") String id, @QueryParam("subject") String subject, @DefaultValue("-1") @QueryParam("grade") double grade, @DefaultValue("0") @QueryParam("gradeDir") int dir, @QueryParam("date") Date date) {
        Datastore datastore = DatabaseManager.getInstance();
        Student student = datastore.createQuery(Student.class).field("index").equal(index).get();

        if (student != null) {
            List<Grade> grades = student.getGrades();
            if(grades == null)
            {
                grades = new ArrayList<>();
            }
            if (subject != null) {
                grades = grades.stream().filter(x -> (x.getCourse().getName().toLowerCase().contains(subject.toLowerCase()))).collect(Collectors.toList());
            }

            if(id != null)
            {
                if(ObjectId.isValid(id)) {
                    grades = grades.stream().filter(x -> (x.getCourse().getId().equals(new ObjectId(id)))).collect(Collectors.toList());
                } else{
                    grades.clear();
                }
            }

            if(date != null)
            {
                grades = grades.stream().filter(x -> (x.getDate().equals(date))).collect(Collectors.toList());
            }

            if (grade != -1) {
                grades = grades.stream().filter(x -> {
                    if (dir > 0) {
                        return x.getValue() >= grade;
                    }
                    if(dir < 0)
                    {
                        return x.getValue() <= grade;
                    }
                    return x.getValue() == grade;
                }).collect(Collectors.toList());
            }

            GenericEntity<List<Grade>> entity = new GenericEntity<List<Grade>>(grades) {
            };
            return Response.ok(entity).build();
        }

        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @POST
    @Path("/{index}/grades")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response addGrade(@PathParam("index") int index, Grade grade) {
        Datastore datastore = DatabaseManager.getInstance();
        Query<GroupId> query = datastore.find(GroupId.class);
        UpdateOperations<GroupId> updateOperations = datastore.createUpdateOperations(GroupId.class).inc("groupId");
        GroupId groupId = datastore.findAndModify(query, updateOperations);
        if (groupId == null) {
            groupId = new GroupId(1);
            datastore.save(groupId);
        }
        grade.setId(groupId.getGroupId());
        grade.setIndex(index);

        Query<Student> queryAdd = datastore.createQuery(Student.class).field("index").equal(index);
        UpdateOperations<Student> updateOperationsAdd = datastore.createUpdateOperations(Student.class).add("grades", grade);
        UpdateResults updateResults = datastore.update(queryAdd, updateOperationsAdd);


        if (updateResults.getUpdatedCount() > 0) {
            int result = grade.getId();
            URI uri = null;
            try {
                uri = new URI("students/" + index + "/grades/" + result);
            } catch (URISyntaxException er) {
                er.printStackTrace();
            }
            return Response.created(uri).entity(grade).build();
//            return Response.status(Response.Status.CREATED).location(uri).build();
        }

        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @GET
    @Path("/{index}/grades/{id}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getGrade(@PathParam("index") int index, @PathParam("id") int id) {
        Datastore datastore = DatabaseManager.getInstance();
        Student student = datastore.createQuery(Student.class).field("index").equal(index).get();

        if (student != null) {
            List<Grade> grades = student.getGrades();
            if (grades != null) {
                Optional<Grade> resultGrade = grades.stream().filter(x -> (x.getId() == id)).findFirst();
                if (resultGrade.isPresent()) {
                    return Response.ok(resultGrade.get()).build();
                }
            }

        }

        return Response.status(Response.Status.NOT_FOUND).build();

    }

    @PUT
    @Path("/{index}/grades/{id}")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response updateGrade(@PathParam("index") int index, @PathParam("id") int id, Grade grade) {
        Datastore datastore = DatabaseManager.getInstance();
        Query<Student> query = datastore.createQuery(Student.class).filter("index", index).filter("grades.id", id);
        UpdateOperations<Student> updateOperations = datastore.createUpdateOperations(Student.class).set("grades.$.value", grade.getValue()).set("grades.$.date", grade.getDate()).set("grades.$.course", grade.getCourse());


        UpdateResults updateResults = datastore.update(query, updateOperations);

        if (updateResults.getUpdatedCount() > 0) {
            return Response.status(Response.Status.OK).build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();

    }

    @DELETE
    @Path("/{index}/grades/{id}")
    public Response deleteGrade(@PathParam("index") int index, @PathParam("id") int id) {
        Datastore datastore = DatabaseManager.getInstance();
//        Query<Student> query = datastore.createQuery(Student.class).filter("grades.id", id);

        long numberGradesBefore = datastore.createQuery(Student.class).disableValidation().field("index").equal(index).field("grades.id").equal(id).enableValidation().countAll();
        Query<Student> query = datastore.createQuery(Student.class).field("index").equal(index);

        Grade deleteGrade = new Grade();
        deleteGrade.setId(id);
        UpdateOperations<Student> updateOperations = datastore.createUpdateOperations(Student.class).disableValidation().removeAll("grades", new BasicDBObject("id", id));

        UpdateResults updateResults = datastore.update(query, updateOperations);

        long numberGradesAfter = datastore.createQuery(Student.class).disableValidation().field("index").equal(index).field("grades.id").equal(id).enableValidation().countAll();


        if (numberGradesBefore - numberGradesAfter > 0) {
            return Response.status(Response.Status.OK).build();
        }
        return Response.status(Response.Status.BAD_REQUEST).build();


    }
}
