
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
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
        name = "cold_start_answers",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"student_id", "competency_id"}
        )
)
public class ColdStartAnswer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "competency_id", nullable = false)
    private Competency competency;

    @Column(nullable = false)
    private Integer rating;

    public ColdStartAnswer() {
    }

    public ColdStartAnswer(
            Student student,
            Competency competency,
            Integer rating) {

        this.student = student;
        this.competency = competency;
        this.rating = rating;
    }

    public Long getId() {
        return id;
    }

    public Student getStudent() {
        return student;
    }

    public Competency getCompetency() {
        return competency;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }
}
