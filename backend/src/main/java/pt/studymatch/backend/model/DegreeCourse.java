package pt.studymatch.backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "degree_courses")
public class DegreeCourse {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 30)
    private String code;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(name = "total_credits", nullable = false)
    private Integer totalCredits;

    protected DegreeCourse() { }

    public DegreeCourse(String code, String name, Integer totalCredits) {
        this.code = code;
        this.name = name;
        this.totalCredits = totalCredits;
    }

    public Long getId() { return id; }
    public String getCode() { return code; }
    public String getName() { return name; }
    public Integer getTotalCredits() { return totalCredits; }
}
