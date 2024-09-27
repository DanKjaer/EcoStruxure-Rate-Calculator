package ecostruxure.rate.calculator.gui.component.modals.addteam;

import ecostruxure.rate.calculator.be.Profile;
import ecostruxure.rate.calculator.be.Team;
import ecostruxure.rate.calculator.be.TeamProfile;
import ecostruxure.rate.calculator.bll.service.GeographyService;
import ecostruxure.rate.calculator.bll.service.ProfileService;
import ecostruxure.rate.calculator.bll.service.TeamService;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import org.apache.poi.ss.formula.functions.T;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class AddTeamInteractor {
    private final AddTeamModel model;
    private TeamService teamService;
    private Team team;

    private ProfileService profileService;
    private GeographyService geographyService;
    private List<AddProfileItemModel> profileItemModels;

    public AddTeamInteractor(AddTeamModel model, Runnable onFetchError) {
        this.model = model;

        try {
            teamService = new TeamService();
            profileService = new ProfileService();
            geographyService = new GeographyService();
        } catch (Exception e) {
            onFetchError.run();
        }

        StringBinding nameValidityBinding = Bindings.createStringBinding(() -> {
            String name = model.nameProperty().get();
            return name != null && !name.trim().isEmpty() ? "" : "Invalid Name";
        }, model.nameProperty());

        model.nameIsValidProperty().bind(nameValidityBinding.isEmpty());
        model.okToAddProperty().bind(Bindings.createBooleanBinding(
                this::isDataValid,
                model.nameProperty()
        ));
    }

    private boolean isDataValid() {
        String name = model.nameProperty().get();
        return name != null && !name.trim().isEmpty() && model.profilesFetchedProperty().get();
    }

    public Boolean fetchProfiles() {
        try {
            List<Profile> profiles = profileService.allWithUtilization();
            profileItemModels = convertToProfileItemModels(profiles);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private List<AddProfileItemModel> convertToProfileItemModels(List<Profile> profiles) throws Exception {
        List<AddProfileItemModel> profileItemModels = new ArrayList<>();
        TeamProfile teamProfile = new TeamProfile();
        team = new Team();
        UUID teamId = team.getTeamId();
        team.setTeamId(teamId);
        teamProfile.setTeam(team);
        teamProfile.setTeamId(teamId);

        for (Profile profile : profiles) {
            AddProfileItemModel profileItemModel = new AddProfileItemModel();
            BigDecimal costAllocation = teamProfile.getCostAllocation();
            BigDecimal hourAllocation = teamProfile.getHourAllocation();

            profileItemModel.UUIDProperty().set(profile.getProfileId());
            profileItemModel.nameProperty().set(profile.getName());

            profileItemModel.currentCostAllocationProperty().set(new BigDecimal(100).subtract(costAllocation != null ? costAllocation : BigDecimal.ZERO));
            profileItemModel.setCostAllocationProperty().set(BigDecimal.ZERO);
            profileItemModel.currentHourAllocationProperty().set(new BigDecimal(100).subtract(hourAllocation != null ? costAllocation : BigDecimal.ZERO));
            profileItemModel.setHourAllocationProperty().set(BigDecimal.ZERO);

            profileItemModel.locationProperty().set(geographyService.getByCountryId(profile.getCountryId()).name());
            profileItemModel.teamIdProperty().set(teamId);
            profileItemModels.add(profileItemModel);
        }

        return profileItemModels;
    }

    public void updateModel() {
        model.profiles().setAll(profileItemModels);
        model.nameProperty().set("");
        model.profilesFetchedProperty().set(true);
    }

//
//    private Profile createProfileFromModel(AddProfileItemModel model, UUID teamId) {
//        Profile profile = new Profile();
//        UUID profileId = model.UUIDProperty().get();
//
//        TeamProfile teamProfile = new TeamProfile();
//        teamProfile.setTeam(team);
//        teamProfile.setTeamId(teamId);
//        try {
//            profile.setProfileId(profileId);
//            profile.setName((model.nameProperty().get()));
//
//            BigDecimal costAllocation = model.setCostAllocationProperty().get();
//            BigDecimal hourAllocation = model.setHourAllocationProperty().get();
//
//            teamService.setAllocation(teamId, profileId, costAllocation, hourAllocation);
//
//            teamProfile.setCostAllocation(costAllocation);
//            teamProfile.setHourAllocation(hourAllocation);
//
//            //List<Profile> profiles = new ArrayList<>();
//            //teamService.assignProfiles(teamProfile.getTeam(), profiles);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return profile;
//    }


    private Profile createProfileFromModel(AddProfileItemModel model) {
        Profile profile = new Profile();
        UUID profileId = model.UUIDProperty().get();

            profile.setProfileId(profileId);
            profile.setName((model.nameProperty().get()));

        return profile;
    }


    public boolean saveTeamWithProfiles() {
        try {
            Team tempTeam = new Team.Builder()
                    .name(model.nameProperty().get())
                    .markup(BigDecimal.ZERO)
                    .grossMargin(BigDecimal.ZERO)
                    .build();
            Team team = saveTeamWithoutProfiles(tempTeam);

            List<Profile> profiles = new ArrayList<>();
            List<TeamProfile> teamProfiles = new ArrayList<>();

            for(AddProfileItemModel addProfileItemModel : model.profiles()) {
                if(addProfileItemModel.selectedProperty().get()) {
                    Profile profile = createProfileFromModel(addProfileItemModel);
                    profiles.add(profile);

                    TeamProfile teamProfile = new TeamProfile();
                    teamProfile.setTeam(team);
                    teamProfile.setTeamId(team.getTeamId());
                    teamProfile.setProfile(profile);
                    teamProfile.setProfileId(profile.getProfileId());
                    teamProfile.setCostAllocation(addProfileItemModel.setCostAllocationProperty().get());
                    teamProfile.setHourAllocation(addProfileItemModel.setHourAllocationProperty().get());
                    teamProfiles.add(teamProfile);
                }
            }

            teamService.storeTeamProfiles(tempTeam, teamProfiles);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

//    public boolean saveTeamWithProfiles() {
//        try {
//            Team tempTeam = new Team.Builder()
//                    .name(model.nameProperty().get())
//                    .markup(BigDecimal.ZERO)
//                    .grossMargin(BigDecimal.ZERO)
//                    .build();
//            Team team = saveTeamWithoutProfiles(tempTeam);
//
//            List<Profile> profiles = model.profiles().stream()
//                    .filter(addProfileItemModel -> addProfileItemModel.selectedProperty().get())
//                    .map(addProfileItemModel -> createProfileFromModel(addProfileItemModel, team.getTeamId()))
//                    .collect(Collectors.toList());
//
//            teamService.assignProfiles(team, profiles);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return true;
//    }

    public Team saveTeamWithoutProfiles(Team _team) throws Exception {
        Team team = new Team.Builder()
                .name(_team.getName())
                .markup(_team.getMarkup())
                .grossMargin(_team.getGrossMargin())
                .build();
        return teamService.create(team);
    }

//    public void saveTeamProfile(AddProfileItemModel model, UUID teamId) {
//        Profile profile = new Profile();
//        UUID profileId = model.UUIDProperty().get();
//        TeamProfile teamProfile = new TeamProfile();
//        teamProfile.setTeamId(teamId);
//        try {
//            profile.setProfileId(profileId);
//            profile.setName(model.nameProperty().get());
//
//            BigDecimal costAllocation = model.setCostAllocationProperty().get();
//            BigDecimal hourAllocation = model.setHourAllocationProperty().get();
//
//            teamService.setCostAllocation(teamId, profileId, costAllocation);
//            teamService.setHourAllocation(teamId, profileId, hourAllocation);
//
//            teamProfile.setCostAllocation(costAllocation);
//            teamProfile.setHourAllocation(hourAllocation);
//
//            List<Profile> profiles = new ArrayList<>();
//            profiles.add(profile);
//            teamService.assignProfiles(teamProfile.getTeam(), profiles);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }


//    public Boolean addTeam() {
//        try {
//            Team tempTeam = new Team.Builder()
//                    .name(model.nameProperty().get())
//                    .markup(BigDecimal.ZERO)
//                    .grossMargin(BigDecimal.ZERO)
//                    .build();
//
//            List<Profile> profiles = model.profiles().stream()
//                    .filter(addProfileItemModel -> addProfileItemModel.selectedProperty().get())
//                    .map(addProfileItemModel -> createProfileFromModel(addProfileItemModel, tempTeam.getTeamId()))
//                    .collect(Collectors.toList());
//
//            teamService.create(tempTeam, profiles);
//            System.out.println("addTeam teamId: " + tempTeam.getTeamId());
//            return true;
//        } catch (Exception e) {
//            e.printStackTrace();
//            return false;
//        }
//    }
}
