package objects;


import org.bson.types.ObjectId;
import org.glassfish.jersey.linking.InjectLink;
import org.glassfish.jersey.linking.InjectLinks;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import rest.CourseService;
import utils.ObjectIdJaxbAdapter;

import javax.ws.rs.core.Link;
import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.List;

@Entity("courses")
@XmlRootElement
public class Course {
    @InjectLinks({
            @InjectLink(resource = CourseService.class, style = InjectLink.Style.ABSOLUTE, rel ="self", method = "getCourseById"),
            @InjectLink(resource = CourseService.class, style = InjectLink.Style.ABSOLUTE, rel ="parent")

    })
    @XmlElement(name="link")
    @XmlElementWrapper(name = "links")
    @XmlJavaTypeAdapter(Link.JaxbAdapter.class)
    List<Link> links;

//    @XmlAttribute
    @Id
    @XmlJavaTypeAdapter(ObjectIdJaxbAdapter.class)
    private ObjectId id;
    private String name;
    private String lecturer;

    @XmlTransient
    public ObjectId getId() {
        return id;
    }



    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLecturer() {
        return lecturer;
    }

    public void setLecturer(String lecturer) {
        this.lecturer = lecturer;
    }

    public Course(){}

    public Course(String name, String lecturer) {
        this.name = name;
        this.lecturer = lecturer;
    }
}
