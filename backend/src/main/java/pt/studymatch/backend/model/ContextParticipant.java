
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
    name = "context_participants",
    uniqueConstraints = @UniqueConstraint(
        columnNames = {"context_id", "student_id"}
    )
)
public class ContextParticipant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "context_id", nullable = false)
    private GroupFormationContext context;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    protected ContextParticipant() {
    }

    public ContextParticipant(
            GroupFormationContext context,
            Student student) {
        this.context = context;
        this.student = student;
    }

    public Long getId() {
        return id;
    }

    public GroupFormationContext getContext() {
        return context;
    }

    public Student getStudent() {
        return student;
    }
}
