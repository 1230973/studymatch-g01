
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
    name = "student_groups",
    uniqueConstraints = @UniqueConstraint(
        columnNames = {"generation_id", "group_number"}
    )
)
public class StudentGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "generation_id", nullable = false)
    private GroupGeneration generation;

    @Column(name = "group_number", nullable = false)
    private Integer groupNumber;

    @Column(name = "balance_score")
    private Double balanceScore;

    @Column(length = 2000)
    private String explanation;

    protected StudentGroup() {
    }

    public StudentGroup(
            GroupGeneration generation,
            Integer groupNumber,
            Double balanceScore,
            String explanation) {

        this.generation = generation;
        this.groupNumber = groupNumber;
        this.balanceScore = balanceScore;
        this.explanation = explanation;
    }

    public Long getId() {
        return id;
    }

    public GroupGeneration getGeneration() {
        return generation;
    }

    public Integer getGroupNumber() {
        return groupNumber;
    }

    public Double getBalanceScore() {
        return balanceScore;
    }

    public String getExplanation() {
        return explanation;
    }
}
