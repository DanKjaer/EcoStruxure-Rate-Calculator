package ecostruxure.rate.calculator.bll.team.observers;

import ecostruxure.rate.calculator.be.Project;
import ecostruxure.rate.calculator.be.Team;
import ecostruxure.rate.calculator.bll.team.ITeamObserver;
import ecostruxure.rate.calculator.bll.utils.RateUtils;
import ecostruxure.rate.calculator.dal.IProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectObserver implements ITeamObserver {
    @Autowired
    private IProjectRepository projectRepository;

    @Override
    public void update(Team team) {
        List<Project> projects = projectRepository.findProjectsByTeamId(team.getTeamId());
        for (Project project : projects){
            var updatedProject = RateUtils.updateProjectRates(project);
            projectRepository.save(updatedProject);
        }
    }
}
