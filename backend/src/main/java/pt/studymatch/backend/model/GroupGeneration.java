
package pt.studymatch.backend.model;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "group_generations")
public class GroupGeneration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "context_id", nullable = false)
    private GroupFormationContext context;

    @Column(name = "generated_at", nullable = false, updatable = false)
    private Instant generatedAt;

    @Column(name = "algorithm_version", nullable = false, length = 50)
    private String algorithmVersion;

    @Column(nullable = false, length = 100)
    private String strategy;

    @Column(length = 2000)
    private String parameters;

    protected GroupGeneration() {
    }

    public GroupGeneration(
            GroupFormationContext context,
            String algorithmVersion,
            String strategy,
            String parameters) {

        this.context = context;
        this.algorithmVersion = algorithmVersion;
        this.strategy = strategy;
        this.parameters = parameters;
    }

    @PrePersist
    protected void onCreate() {
        if (generatedAt == null) {
            generatedAt = Instant.now();
        }
    }

    public Long getId() {
        return id;
    }

    public GroupFormationContext getContext() {
        return context;
    }

    public Instant getGeneratedAt() {
        return generatedAt;
    }

    public String getAlgorithmVersion() {
        return algorithmVersion;
    }

    public String getStrategy() {
        return strategy;
    }

    public String getParameters() {
        return parameters;
    }
}
