package pt.studymatch.backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "students")
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "student_number", nullable = false, unique = true, length = 30)
    private String studentNumber;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(name = "institutional_email", nullable = false, unique = true, length = 200)
    private String institutionalEmail;

    @Column(name = "enrolled_year", nullable = false)
    private Integer enrolledYear;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "degree_course_id", nullable = false)
    private DegreeCourse degreeCourse;

    protected Student() { }

    public Student(String studentNumber, String name, String institutionalEmail,
                   Integer enrolledYear, DegreeCourse degreeCourse) {
        this.studentNumber = studentNumber;
        this.name = name;
        this.institutionalEmail = institutionalEmail;
        this.enrolledYear = enrolledYear;
        this.degreeCourse = degreeCourse;
    }

    public Long getId() { return id; }
    public String getStudentNumber() { return studentNumber; }
    public String getName() { return name; }
    public String getInstitutionalEmail() { return institutionalEmail; }
    public Integer getEnrolledYear() { return enrolledYear; }
    public DegreeCourse getDegreeCourse() { return degreeCourse; }

    public void setName(String name) { this.name = name; }
    public void setInstitutionalEmail(String institutionalEmail) { this.institutionalEmail = institutionalEmail; }
    public void setEnrolledYear(Integer enrolledYear) { this.enrolledYear = enrolledYear; }
    public void setDegreeCourse(DegreeCourse degreeCourse) { this.degreeCourse = degreeCourse; }
}