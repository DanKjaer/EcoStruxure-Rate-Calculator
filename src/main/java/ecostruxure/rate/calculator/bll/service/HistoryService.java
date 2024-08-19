package ecostruxure.rate.calculator.bll.service;

import ecostruxure.rate.calculator.be.*;
import ecostruxure.rate.calculator.dal.dao.*;
import ecostruxure.rate.calculator.dal.db.HistoryDAO;

import java.util.List;
import java.util.UUID;

public class HistoryService {
    private final IHistoryDAO historyDAO;
    public HistoryService() throws Exception {
        this.historyDAO = new HistoryDAO();
    }

    public List<ProfileHistory> getProfileHistory(UUID profileId) throws Exception {
        return historyDAO.getProfileHistory(profileId);
    }

    public List<TeamHistory> getTeamHistory(int teamId) throws Exception {
        return historyDAO.getTeamHistory(teamId);
    }
}
