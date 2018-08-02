package objects;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.glassfish.jersey.linking.InjectLink;
import org.glassfish.jersey.linking.InjectLinkNoFollow;
import org.glassfish.jersey.linking.InjectLinks;
import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Reference;
import rest.StudentService;

import javax.ws.rs.core.Link;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.beans.Transient;
import java.util.Date;
import java.util.List;

@Embedded
@XmlRootElement
public class Grade {
    @InjectLinkNoFollow
    @InjectLinks({
            @InjectLink(resource = StudentService.class, style = InjectLink.Style.ABSOLUTE, rel = "self", method = "getGrade"),
//            @InjectLink(resource = StudentService.class, rel="parent", method = "getStudentsGrades"),
            @InjectLink(resource = StudentService.class, style = InjectLink.Style.ABSOLUTE, rel = "student", method = "getStudentByIndex")

    })
    @XmlElement(name = "link")
    @XmlElementWrapper(name = "links")
    @XmlJavaTypeAdapter(Link.JaxbAdapter.class)
    List<Link> links;

//    @InjectLinkNoFollow


    private int id;
    private double value;
    @JsonFormat(shape = JsonFormat.Shape.STRING,
            pattern = "yyyy-MM-dd", timezone = "CET")

    private Date date;
    @Reference
    private Course course;


    /**
     * DO ZAKOMENTOWANIA
     */
    @XmlTransient
    private int index;

    @XmlTransient
    public int getIndex() {
//        return studentIndex;
        return index;
    }

    public void setIndex(int studentIndex) {
        this.index = studentIndex;
    }


//    public void setStudentIndex(int studentIndex) {
//        this.studentIndex = studentIndex;
//    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = (float) (0.5 * Math.round(value * 2));
        if (this.value > 5)
            this.value = 5;
        if (this.value < 2)
            this.value = 2;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public Grade() {
    }

//    public Grade(int id, double value, Date date, Course course) {
//        this.id = id;
//        this.value = value;
//        this.date = date;
//        this.course = course;
//    }

    public Grade(int id, double value, Date date, Course course, int studentIndex) {
        this.id = id;
        this.value = value;
        this.date = date;
        this.course = course;
        this.index = studentIndex;
    }
}
