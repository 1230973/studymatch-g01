
package pt.studymatch.backend.model;

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
    name = "group_memberships",
    uniqueConstraints = @UniqueConstraint(
        columnNames = {"generation_id", "student_id"}
    )
)
public class GroupMembership {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "generation_id", nullable = false)
    private GroupGeneration generation;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_group_id", nullable = false)
    private StudentGroup studentGroup;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    protected GroupMembership() {
    }

    public GroupMembership(
            GroupGeneration generation,
            StudentGroup studentGroup,
            Student student) {

        this.generation = generation;
        this.studentGroup = studentGroup;
        this.student = student;
    }

    public Long getId() {
        return id;
    }

    public GroupGeneration getGeneration() {
        return generation;
    }

    public StudentGroup getStudentGroup() {
        return studentGroup;
    }

    public Student getStudent() {
        return student;
    }
}
