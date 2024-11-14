package ecostruxure.rate.calculator.dal.dao;

import ecostruxure.rate.calculator.be.Country;
import ecostruxure.rate.calculator.be.Geography;
import ecostruxure.rate.calculator.be.Profile;
import ecostruxure.rate.calculator.be.Team;
import ecostruxure.rate.calculator.dal.transaction.TransactionContext;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public interface IProfileDAO {
    Profile create(Profile profile) throws Exception;
    List<Profile> all() throws Exception;

    List<Profile> allWithUtilization() throws Exception;

    List<Profile> allWithUtilizationByTeam(UUID teamId) throws Exception;

    Profile get(UUID id) throws Exception;

    Profile get(TransactionContext context, UUID id) throws Exception;

    List<Profile> allByCountry(Country country) throws Exception;
    List<Profile> allByCountry(String countryCode) throws Exception;

    List<Profile> allByTeam(Team team) throws Exception;
    List<Profile> allByTeam(UUID teamId) throws Exception;

    List<Profile> allByGeography(Geography geography) throws Exception;
    List<Profile> allByGeography(int geographyId) throws Exception;

    BigDecimal getTotalCostAllocation(UUID id) throws Exception;

    BigDecimal getTotalHourAllocation(UUID id) throws Exception;

    BigDecimal getProfileCostAllocationForTeam(UUID profileId, UUID teamId) throws Exception;

    BigDecimal getProfileHourAllocationForTeam(UUID profileId, UUID teamId) throws Exception;

    BigDecimal getProfileCostAllocationForTeam(TransactionContext context, UUID profileId, UUID teamId) throws Exception;

    BigDecimal getProfileHourAllocationForTeam(TransactionContext context, UUID profileId, UUID teamId) throws Exception;

    List<Team> getTeams(Profile profile) throws Exception;

    boolean update(Profile profile) throws Exception;

    boolean update(TransactionContext context, Profile profile) throws Exception;

    List<Team> getTeams(TransactionContext context, Profile profile) throws Exception;

    boolean archive(UUID profileId, boolean shouldArchive) throws Exception;

    boolean archive(List<Profile> profiles) throws Exception;

    void updateAllocation(UUID profileId, BigDecimal costAllocation, BigDecimal hourAllocation) throws SQLException;
}
