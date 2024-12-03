package ecostruxure.rate.calculator.bll.profile;

import ecostruxure.rate.calculator.be.Profile;

public interface ProfileObserver {
    void update(Profile profile);
}
