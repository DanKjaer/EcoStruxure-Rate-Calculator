package ecostruxure.rate.calculator.be;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
public class TeamProfile {

    @Id
    @GeneratedValue
    private UUID teamProfileId;

    @ManyToOne
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    @ManyToOne
    @JoinColumn(name = "profile_id", nullable = false)
    private Profile profile;

    @Column(precision = 5, scale = 2, nullable = false)
    private BigDecimal allocationPercentage;

    @Column(precision = 15, scale = 2)
    private BigDecimal allocatedCost; // Pre-calculated in DB

    // Getters and Setters
}