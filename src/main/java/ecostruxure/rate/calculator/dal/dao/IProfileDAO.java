package ecostruxure.rate.calculator.dal.dao;

import ecostruxure.rate.calculator.be.Country;
import ecostruxure.rate.calculator.be.Geography;
import ecostruxure.rate.calculator.be.Profile;
import ecostruxure.rate.calculator.be.Team;
import ecostruxure.rate.calculator.dal.transaction.TransactionContext;

import java.math.BigDecimal;
import java.util.List;

public interface IProfileDAO {
    Profile create(Profile profile) throws Exception;
    List<Profile> all() throws Exception;

    List<Profile> allWithUtilization() throws Exception;

    List<Profile> allWithUtilizationByTeam(int teamId) throws Exception;

    Profile get(int id) throws Exception;

    Profile get(TransactionContext context, int id) throws Exception;

    List<Profile> allByCountry(Country country) throws Exception;
    List<Profile> allByCountry(String countryCode) throws Exception;

    List<Profile> allByTeam(Team team) throws Exception;
    List<Profile> allByTeam(int teamId) throws Exception;

    List<Profile> allByGeography(Geography geography) throws Exception;
    List<Profile> allByGeography(int geographyId) throws Exception;

    BigDecimal getTotalCostAllocation(int id) throws Exception;

    BigDecimal getTotalHourAllocation(int id) throws Exception;

    BigDecimal getProfileCostAllocationForTeam(int profileId, int teamId) throws Exception;

    BigDecimal getProfileHourAllocationForTeam(int profileId, int teamId) throws Exception;

    BigDecimal getProfileCostAllocationForTeam(TransactionContext context, int profileId, int teamId) throws Exception;

    BigDecimal getProfileHourAllocationForTeam(TransactionContext context, int profileId, int teamId) throws Exception;

    List<Team> getTeams(Profile profile) throws Exception;

    boolean update(Profile profile) throws Exception;

    boolean update(TransactionContext context, Profile profile) throws Exception;

    List<Team> getTeams(TransactionContext context, Profile profile) throws Exception;

    boolean archive(Profile profile, boolean shouldArchive) throws Exception;

    boolean archive(List<Profile> profiles) throws Exception;
}
