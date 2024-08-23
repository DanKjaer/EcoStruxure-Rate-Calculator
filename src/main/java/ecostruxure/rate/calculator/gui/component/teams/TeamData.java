package ecostruxure.rate.calculator.gui.component.teams;

import ecostruxure.rate.calculator.be.Profile;

import java.util.List;
import java.util.UUID;

public record TeamData(UUID teamId, List<Profile> profiles) { }