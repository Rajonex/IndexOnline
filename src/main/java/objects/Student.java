package objects;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.mongodb.DBRef;
import org.bson.types.ObjectId;
import org.glassfish.jersey.linking.Binding;
import org.glassfish.jersey.linking.InjectLink;
import org.glassfish.jersey.linking.InjectLinks;
import org.glassfish.jersey.server.Uri;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Reference;
import rest.StudentService;
import utils.ObjectIdJaxbAdapter;

import javax.ws.rs.core.Link;
import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.Date;
import java.util.List;

@Entity("students")
@XmlRootElement(name = "student")
public class Student {

    @InjectLinks({
//            @InjectLink(/*resource = StudentService.class, */value="/students/{index}", rel = "self"),
            @InjectLink(resource = StudentService.class, style = InjectLink.Style.ABSOLUTE, rel = "self", method = "getStudentByIndex"),
            @InjectLink(resource = StudentService.class, style = InjectLink.Style.ABSOLUTE, rel = "parent"), // tam gdzie sa operacje restowe
//            @InjectLink(resource = StudentService.class, rel = "grades", method = "getStudentsGrades")
    })
    @XmlElement(name = "link")
    @XmlElementWrapper(name = "links")
    @XmlJavaTypeAdapter(Link.JaxbAdapter.class)
    List<Link> links;


    @XmlTransient
    @Id
//    @XmlJavaTypeAdapter(ObjectIdJaxbAdapter.class)
    private ObjectId id;


    //    @XmlAttribute
    private int index;
    private String firstname;
    private String lastname;
    @JsonFormat(shape = JsonFormat.Shape.STRING,
            pattern = "yyyy-MM-dd", timezone = "CET")
    private Date birthsday;
    private List<Grade> grades;

    @XmlTransient
    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public Date getBirthsday() {
        return birthsday;
    }

    public void setBirthsday(Date birthsday) {
        this.birthsday = birthsday;
    }

    public List<Grade> getGrades() {
        return grades;
    }

    public void setGrades(List<Grade> grades) {
        this.grades = grades;
    }

//    public int addGrade(Grade grade) {
////        boolean isNotOriginal = grades.stream().anyMatch(x -> x.getId() == grade.getId());
////        if (!isNotOriginal) {
//        gradeId++;
//        grade.setId(gradeId);
//        grade.setIndex(index); // do zakomentowania
//        grades.add(grade);
//        return gradeId;
////            return true;
////        }
////        return false;
//    }

    public Student() {
    }

    public Student(int index, String firstname, String lastname, Date birthsday, List<Grade> grades) {
        this.index = index;
        this.firstname = firstname;
        this.lastname = lastname;
        this.birthsday = birthsday;
        this.grades = grades;
    }
}
